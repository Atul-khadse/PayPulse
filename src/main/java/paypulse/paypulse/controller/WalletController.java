package paypulse.paypulse.controller;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import paypulse.paypulse.dto.TransferRequestDto;
import paypulse.paypulse.dto.TransferResponceDto;
import paypulse.paypulse.dto.WalletResponseDto;
import paypulse.paypulse.model.Wallet;
import paypulse.paypulse.service.WalletService;

@RestController
@RequestMapping("/api/vi/wallets")
@RequiredArr
public class WalletController {
    private final WalletService walletService;

    @GetMapping("/{id}")
    public ResponseEntity<WalletResponseDto> getWallet(@PathVariable Long id){
        return ResponseEntity.ok(walletService.getWallet());
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponceDto> transfer(
            @RequestHeader("Idempotency-key") String idempotencyKey,
            @Valid @RequestBody TransferRequestDto request){
        return ResponseEntity.ok(walletService.transfer(idempotencyKey, request));
    }

}
