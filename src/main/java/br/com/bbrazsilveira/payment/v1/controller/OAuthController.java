package br.com.bbrazsilveira.payment.v1.controller;

import br.com.bbrazsilveira.payment.v1.domain.dto.TokenRequestDto;
import br.com.bbrazsilveira.payment.v1.domain.dto.TokenDto;
import br.com.bbrazsilveira.payment.v1.service.OAuthService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = "OAuth")
@RestController
@RequestMapping(value = "/oauth", produces = MediaType.APPLICATION_JSON_VALUE)
public class OAuthController {

    @Autowired
    private OAuthService oAuthService;

    @ApiOperation(value = "Cria um token de autenticação temporário")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Token criado", responseHeaders = @ResponseHeader(name = "Location", description = "URL do certificado em PDF")),
            @ApiResponse(code = 401, message = "Token não autorizado"),
    })
    @RequestMapping(value = "/token", method = RequestMethod.POST)
    public ResponseEntity<TokenDto> getToken(@Valid @RequestBody @ApiParam(name = "Token", value = "Existem dois tipos de autenticação: password (usuário e senha) e refresh_token (token de atualização).<br>Para mais informações do protocolo OAuth, acesse <a target=\"_blank\" href=\"https://www.oauth.com/oauth2-servers/access-tokens/\">https://www.oauth.com/oauth2-servers/access-tokens/</a>") TokenRequestDto tokenRequestDto) {
        TokenDto dto = oAuthService.getToken(tokenRequestDto);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}