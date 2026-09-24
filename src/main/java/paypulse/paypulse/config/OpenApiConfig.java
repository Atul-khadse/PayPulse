package paypulse.paypulse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI payPulseOpenAPI(){
        return new OpenAPI()
                .info(new Info().title("PayPulse API")
                        .description("High-concurrency Wallet Ledger API")
                        .version("v1.0.0"));
        }
    }
}
