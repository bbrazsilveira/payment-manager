package br.com.bbrazsilveira.payment.v1.configuration.web;

import br.com.bbrazsilveira.payment.v1.service.ApiKeycloakService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Configuration
public class RetrofitConfig {

    @Value("${app.keycloak.auth-server-url}")
    private String baseUrlKeycloak;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public ApiKeycloakService apiKeycloakService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrlKeycloak)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();

        return retrofit.create(ApiKeycloakService.class);
    }
}
