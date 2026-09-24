package paypulse.paypulse.service;


import jakarta.persistence.Table;
import org.hibernate.Internal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import paypulse.paypulse.dto.TransferResponceDto;
import paypulse.paypulse.model.Wallet;
import paypulse.paypulse.repository.TransactionRecordRepository;
import paypulse.paypulse.repository.WalletRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class WalletServiceConcurrencyTest {

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    private Long walletAId;
    private Long walletBId;

    @BeforeEach
    void setUp(){
        transactionRecordRepository.deleteAll();
        walletRepository.deleteAll();

        Wallet walletA = Wallet.builder()
                .userId(101L)
                .balance(new BigDecimal("100.00"))
                .build();

        Wallet walletB = Wallet.builder()
                .userId(102L)
                .balance(new BigDecimal(("50.00")))
                .build();

        walletA = walletRepository.save(walletA);
        walletB = walletRepository.save(walletB);

        walletAId = walletA.getId();
        walletBId = walletB.getId();
    }


    @Test
    void testConcurrentTransfersDoesNotCorruptBalance() throws InterruptedException, ExecutionException{
        int threadCount = 20;
        ExecutorService executorService = Executors.newFixedTreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(1);
        List<Future<Exception>> futures = new ArrayList<>();

        for(int i = 0; i < threadCount; i++){
            futures.add(executorService.submit(() -> {
                try{
                    latch.await();
                    TransferResponceDto request = new TransferResponceDto(
                            walletAId, walletBId, new BigDecimal("5.00"));
                    walletService.transfer(UUID.randomUUID().toString(), request);
                    return null;
                } catch (Exception e){
                    return e;
                }
            }));
        }

        latch.countDown();

        for(Future<Exception> future : futures){
            Exception exception = future.get();
            if(exception != null){
                System.out.println("Thread threw exception: " + exception.getMessage());
            }
        }

        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);

        Wallet finalWalletA = walletRepository.findById(walletAId).orElseThrow();
        Wallet finalWalletB = walletRepository.findById(walletBId).orElseThrow();

        assertEquals(new BigDecimal("0.00"), finalWalletA.getBalance().stripTrailingZeros());
        assertEquals(new BigDecimal("150.00"), finalWalletB.getBalance().stripTrailingZeros());
    }

    
}
