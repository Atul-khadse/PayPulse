package paypulse.paypulse.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TransferResponceDto(
        String transactionId,
        String status,
        Long fromWalletId,
        Long toWalletId,
        BigDecimal amount,
        Instant processAt
) {
}
