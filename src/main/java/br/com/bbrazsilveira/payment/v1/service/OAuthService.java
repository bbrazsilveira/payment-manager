package br.com.bbrazsilveira.payment.v1.service;

import br.com.bbrazsilveira.payment.v1.configuration.multitenancy.TenantContext;
import br.com.bbrazsilveira.payment.v1.domain.dto.TokenRequestDto;
import br.com.bbrazsilveira.payment.v1.domain.dto.TokenDto;
import br.com.bbrazsilveira.payment.v1.domain.model.conta.User;
import br.com.bbrazsilveira.payment.v1.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import retrofit2.Response;

import java.io.IOException;
import java.util.Base64;
import java.util.UUID;


@Service
@Transactional
public class OAuthService {

    @Value("${app.keycloak.realm}")
    private String realm;

    @Value("${app.keycloak.resource}")
    private String resource;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ApiKeycloakService apiKeycloakService;

    @Autowired
    private UserRepository userRepository;

    public TokenDto getToken(TokenRequestDto tokenRequestDto) {
        try {
            String clientId = String.format("%s_%s", resource, TenantContext.getCurrentTenant());
            Response<TokenDto> response = apiKeycloakService.getToken(realm, clientId, tokenRequestDto.getGrantType().toLowerCase(), tokenRequestDto.getUsername(), tokenRequestDto.getPassword(), tokenRequestDto.getRefreshToken()).execute();
            if (response.isSuccessful()) {
                TokenDto tokenDto = response.body();

                // Decode user id from Keycloak
                assert tokenDto != null;
                String accessToken = tokenDto.getAccessToken();
                byte[] payloadBytes = Base64.getDecoder().decode(accessToken.split("\\.")[1]);
                Payload payload = mapper.readValue(payloadBytes, Payload.class);

                // Create user if not exists
                User user = new User();
                user.setId(UUID.fromString(payload.getSub()));
                if (!userRepository.existsById(user.getId())) {
                    userRepository.save(user);
                }

                return tokenDto;
            }

            assert response.errorBody() != null;
            ApiKeycloakService.ResponseError error = mapper.readValue(response.errorBody().string(), ApiKeycloakService.ResponseError.class);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, error.getErrorDescription());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Data
    private static class Payload {
        private String sub;
    }
}
