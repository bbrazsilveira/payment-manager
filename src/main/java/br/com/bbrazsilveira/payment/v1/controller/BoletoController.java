package br.com.bbrazsilveira.payment.v1.controller;

import br.com.bbrazsilveira.payment.v1.configuration.utils.HttpUtils;
import br.com.bbrazsilveira.payment.v1.domain.dto.BoletoRequestDto;
import br.com.bbrazsilveira.payment.v1.domain.dto.BoletoCreatedDto;
import br.com.bbrazsilveira.payment.v1.service.BoletoService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "Boletos")
@RestController
@RequestMapping(value = "/boletos", produces = MediaType.APPLICATION_JSON_VALUE)
public class BoletoController {

    @Autowired
    private BoletoService boletoService;

    @ApiOperation(value = "Cria um novo boleto")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Bearer {access_token} \nExemplo: \"Bearer eyJhbGciOiJIUzI1Ni.eyJuYW1lIjoiSm9obiBEb2UifQ.DjwRE2jZhren2Wt37t5hlVru6M\"", dataType = "string", paramType = "header", required = true)
    })
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<BoletoCreatedDto> create(@Valid @RequestBody @ApiParam(name = "Boleto", value = "Novo boleto") BoletoRequestDto boletoRequestDto) {
        BoletoCreatedDto boletoResponseDto = boletoService.create(boletoRequestDto);
        return new ResponseEntity<>(boletoResponseDto, HttpUtils.locationHeaderById(boletoResponseDto.getId().toString()), HttpStatus.CREATED);
    }
}