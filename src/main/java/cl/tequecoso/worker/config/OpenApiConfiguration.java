package cl.tequecoso.worker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI externalWorkerOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Flowable External Worker API")
                        .description(
                                "API HTTP para exponer los mismos servicios "
                                        + "consumidos por los external workers."
                        )
                        .version("v1")
                        .contact(new Contact().name("Tequecoso")));
    }
}
