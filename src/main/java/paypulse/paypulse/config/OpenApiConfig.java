package paypulse.paypulse.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "PayPulse API",
                description = "High-concurrency Wallet Ledger API",
                version = "v1.0.0"
        )
)
public class OpenApiConfig {
}

