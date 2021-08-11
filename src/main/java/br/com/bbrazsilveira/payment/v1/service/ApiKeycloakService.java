package br.com.bbrazsilveira.payment.v1.service;

import br.com.bbrazsilveira.payment.v1.domain.dto.TokenDto;
import lombok.Data;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiKeycloakService {

    @FormUrlEncoded
    @POST("realms/{realm}/protocol/openid-connect/token")
    Call<TokenDto> getToken(@Path("realm") String realm, @Field("client_id") String clientId, @Field("grant_type") String grantType, @Field("username") String username, @Field("password") String password, @Field("refresh_token") String refreshToken);

    @Data
    class ResponseError {
        private String error, errorDescription;
    }
}
