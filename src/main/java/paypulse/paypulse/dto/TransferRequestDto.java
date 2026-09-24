package paypulse.paypulse.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequestDto(
        @NotNull(message = "Source Wallet ID is required")
        Long fromWalletId,

        @NotNull(message = "Destination wallete id is required")
        Long toWalletId,

        @NotNull(message = "Amount is requires")
        @DecimalMin(value = "0.01", message = "Amount must be Strictly positive")
        BigDecimal amount
) {
}
