package paypulse.paypulse.dto;

import java.math.BigDecimal;

public record WalletResponseDto(
        Long id,
        Long userId,
        BigDecimal balance
) {
}
