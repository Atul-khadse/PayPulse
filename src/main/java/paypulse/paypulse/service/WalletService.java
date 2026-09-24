package paypulse.paypulse.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import paypulse.paypulse.dto.TransferRequestDto;
import paypulse.paypulse.dto.TransferResponceDto;
import paypulse.paypulse.dto.WalletResponseDto;
import paypulse.paypulse.exception.InsufficientFundsException;
import paypulse.paypulse.exception.WalletNotFoundException;
import paypulse.paypulse.model.*;
import paypulse.paypulse.repository.LedgerEntryRepository;
import paypulse.paypulse.repository.TransactionRecordRepository;
import paypulse.paypulse.repository.WalletRepository;

import java.time.Instant;

@Service
public class WalletService {
    private final WalletRepository walletRepository;
    private final TransactionRecordRepository transactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    @Transactional
    public WalletResponseDto getWallet(Long id){
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new WalletNotFoundException(id));
        return new WalletResponseDto(wallet.getId(), wallet.getUserId(), wallet.getBalance());
    }


    @Transactional
    public TransferResponceDto transfer(String idempotencyKey, TransferRequestDto request){
        Optional<TransationRecord> existingTx = transactionRepository.findByIdempotencyKey(idempotencyKey);
        if(existingTx.isPresent()){
            log.info("Indempotent request detet. returnign cached result for key: {}", idempotencyKey);
            return mapTopResponse(existingTx.get());

        }

        if(request.fromWalletId().equals(request.toWalletId())){
            throw new IllegalArgumentException("Connot tranfer funds to the same wallet");
        }

        Long firstLockId = Math.min(request.fromWalletId(), request.toWalletId());
        Long secondLockId = Math.max(request.fromWalletId(), request.toWalletId());

        Wallet firstWallet = walletRepository.findByIdForUpdate(firstLockId)
                .orElseThrow(() -> new WalletNotFoundException(firstLockId));
        Wallet secondWallet = walletRepository.findByIdForUpdate(secondLockId)
                .orElseThrow(() -> new WalletNotFoundException(secondLockId));

        Wallet fromWallet = request.fromWalletId().equals(firstLockId) ? firstWallet : secondWallet;
        Wallet toWallet = request.toWalletId().equals(firstLockId) ? firstWallet : secondWallet;

        existingTx = transactionRepository.findByIdempotencyKey(idempotencyKey);
        if (existingTx.isPresent()){
            return mapToResponse(existingTx.get());
        }

        if(fromWallet.getBalance().compareTo(request.amount()) < 0){
            throw new InsufficientFundsException("Insufficient balance in wallet ID: " + fromWallet.getId());

        }

        fromWallet.setBalance(fromWallet.getBalance().substract(request.amount()));
        toWallet.setBalance(toWallet.getBalance().add(request.amount()));

        Instant now = Instant.now();
        TransationRecord txRecord = TransationRecord.builder()
                .idempotencyKey(idempotencyKey)
                .fromWalletId(fromWallet.getId())
                .toWalletId(toWallet.getId())
                .amout(request.amount())
                .status(TransactionStatus.COMPLETED)
                .createdAt(now)
                .build();
        txRecord = transactionRepository.save(txRecord);

        LedgerEntry debitEntry = LedgerEntry.builder()
                .walletId(fromWallet.getId())
                .transation(txRecord)
                .amount(request.amount())
                .type(LedgerEntryType.DEBIT)
                .createdAt(now)
                .build();

        LedgerEntry creditEntry = LedgerEntry.builder()
                .walletId(toWallet.getId())
                .transaction(txRecord)
                .amount(request.amount())
                .type(LedgerEntryType.CREDIT)
                .createdAt(now)
                .build();


        ledgerEntryRepository.saveAll(List.of(debitEntry, creditEntry));

        log.info("Transfer completed succssfully. TxTD: {}", txRecord.getId());
        return mapToResponse(txRecord);
    }

    private TransferResponceDto mapToResponse(TransationRecord tx){
        return new TransferResponceDto(
                tx.getId().toString(),
                tx.getStatus().name(),
                tx.getFromWalletId(),
                tx.getToWalletId(),
                tx.getAmount(),
                tx.getCreateAt()
        );

    }
}
