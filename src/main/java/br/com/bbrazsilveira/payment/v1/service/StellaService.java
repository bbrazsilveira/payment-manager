package br.com.bbrazsilveira.payment.v1.service;

import br.com.caelum.stella.boleto.*;
import br.com.caelum.stella.boleto.bancos.Santander;
import br.com.caelum.stella.boleto.transformer.GeradorDeBoleto;
import br.com.bbrazsilveira.payment.v1.configuration.gcloud.GCStorage;
import br.com.bbrazsilveira.payment.v1.domain.model.banco.Convenio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static br.com.bbrazsilveira.payment.v1.domain.model.banco.Banco.CODIGO_SANTANDER;

@Service
@Transactional
public class StellaService {

    @Autowired
    private GCStorage gcStorage;

    public Boleto createBoleto(br.com.bbrazsilveira.payment.v1.domain.model.boleto.Boleto boletoEntity) {
        try {
            Convenio convenioEntity = boletoEntity.getConvenio();
            Banco banco = createBanco(boletoEntity);
            Beneficiario beneficiario = createBeneficiario(boletoEntity);
            Pagador pagador = createPagador(boletoEntity);
            Datas datas = createDatas(boletoEntity);

            return createBoleto(boletoEntity, banco, beneficiario, pagador, datas);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public byte[] exportBoleto(Boleto boleto, String templatePath) {
        try {
            Beneficiario beneficiario = boleto.getBeneficiario();

            // Define custom parameters
            Map<String, Object> parameters = new HashMap<>();
            String agenciaCodigoBeneficiario = String.format("%s/%s", beneficiario.getAgencia(), beneficiario.getNumeroConvenio());
            parameters.put("AGENCIA_COM_DIGITO_E_CODIGO_BENEFICIARIO", agenciaCodigoBeneficiario);

            // Create boleto generator
            InputStream jasperStream = getClass().getResourceAsStream(templatePath);
            GeradorDeBoleto geradorBoleto = new GeradorDeBoleto(jasperStream, parameters, boleto);

            // Export boleto as PDF
            return geradorBoleto.geraPDF();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private Datas createDatas(br.com.bbrazsilveira.payment.v1.domain.model.boleto.Boleto boletoEntity) {
        LocalDate dataDocumento = boletoEntity.getDataDocumento();
        LocalDate dataProcessamento = boletoEntity.getDataProcessamento();
        LocalDate dataVecimento = boletoEntity.getDataVencimento();

        return Datas.novasDatas()
                .comDocumento(dataDocumento.getDayOfMonth(), dataDocumento.getMonthValue(), dataDocumento.getYear())
                .comProcessamento(dataProcessamento.getDayOfMonth(), dataProcessamento.getMonthValue(), dataProcessamento.getYear())
                .comVencimento(dataVecimento.getDayOfMonth(), dataVecimento.getMonthValue(), dataVecimento.getYear());
    }

    private Beneficiario createBeneficiario(br.com.bbrazsilveira.payment.v1.domain.model.boleto.Boleto boletoEntity) {
        Convenio convenioEntity = boletoEntity.getConvenio();

        Endereco enderecoBeneficiario = Endereco.novoEndereco()
                .comLogradouro(convenioEntity.getBeneficiario().getEndereco().getEndereco().toUpperCase())
                .comBairro(convenioEntity.getBeneficiario().getEndereco().getBairro().toUpperCase())
                .comCep(convenioEntity.getBeneficiario().getEndereco().getCep())
                .comCidade(convenioEntity.getBeneficiario().getEndereco().getCidade().toUpperCase())
                .comUf(convenioEntity.getBeneficiario().getEndereco().getUf());

        return Beneficiario.novoBeneficiario()
                .comNomeBeneficiario(convenioEntity.getBeneficiario().getNome().toUpperCase())
                .comDocumento(convenioEntity.getBeneficiario().getDocumento())
                .comAgencia(convenioEntity.getAgencia()).comDigitoAgencia(convenioEntity.getAgenciaDigito())
                .comCodigoBeneficiario(convenioEntity.getContaCorrente()).comDigitoCodigoBeneficiario(convenioEntity.getContaCorrenteDigito())
                .comNumeroConvenio(convenioEntity.getNumero())
                .comCarteira(convenioEntity.getCarteira())
                .comEndereco(enderecoBeneficiario)
                .comNossoNumero(boletoEntity.getTitulo().getCodigo());
    }

    private Pagador createPagador(br.com.bbrazsilveira.payment.v1.domain.model.boleto.Boleto boletoEntity) {
        br.com.bbrazsilveira.payment.v1.domain.model.boleto.Pagador pagadorEntity = boletoEntity.getPagador();

        Endereco enderecoPagador = Endereco.novoEndereco()
                .comLogradouro(pagadorEntity.getEndereco().toUpperCase())
                .comBairro(pagadorEntity.getBairro().toUpperCase())
                .comCep(pagadorEntity.getCep())
                .comCidade(pagadorEntity.getCidade().toUpperCase())
                .comUf(pagadorEntity.getUf().toString());

        return Pagador.novoPagador()
                .comNome(pagadorEntity.getNome().toUpperCase())
                .comDocumento(pagadorEntity.getDocumento())
                .comEndereco(enderecoPagador);
    }

    private Banco createBanco(br.com.bbrazsilveira.payment.v1.domain.model.boleto.Boleto boletoEntity) {
        String codigo = boletoEntity.getConvenio().getBanco().getCodigo();
        if (codigo.equals(CODIGO_SANTANDER)) {
            return new Santander();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, String.format("Bank with code \"%s\" not implemented.", codigo));
        }
    }

    private Boleto createBoleto(br.com.bbrazsilveira.payment.v1.domain.model.boleto.Boleto boleto, Banco banco, Beneficiario beneficiario, Pagador pagador, Datas datas) {
        return Boleto.novoBoleto()
                .comBanco(banco)
                .comDatas(datas)
                .comBeneficiario(beneficiario)
                .comAceite(boleto.getAceite())
                .comEspecieMoeda(boleto.getEspecieMoeda())
                .comEspecieDocumento(boleto.getTitulo().getEspecie())
                .comPagador(pagador)
                .comValorBoleto(boleto.getTitulo().getValor())
                .comNumeroDoDocumento(boleto.getTitulo().getNumero())
                .comInstrucoes(boleto.getInstrucoes())
                .comLocaisDePagamento(boleto.getLocalPagamento().toUpperCase());
    }
}
