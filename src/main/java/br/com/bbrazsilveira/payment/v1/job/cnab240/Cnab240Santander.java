package br.com.bbrazsilveira.payment.v1.job.cnab240;

import br.com.bbrazsilveira.payment.v1.configuration.cnab240.model.Cnab240;
import br.com.bbrazsilveira.payment.v1.configuration.cnab240.model.Detalhe;
import br.com.bbrazsilveira.payment.v1.configuration.cnab240.model.Lote;
import br.com.bbrazsilveira.payment.v1.configuration.cnab240.model.Registro;
import br.com.bbrazsilveira.payment.v1.domain.model.banco.Beneficiario;
import br.com.bbrazsilveira.payment.v1.domain.model.banco.Convenio;
import br.com.bbrazsilveira.payment.v1.domain.model.boleto.Boleto;
import br.com.bbrazsilveira.payment.v1.domain.model.boleto.Pagador;
import br.com.bbrazsilveira.payment.v1.domain.model.boleto.Titulo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class Cnab240Santander {

    public Cnab240 criarCnab240(int numeroArquivo, Convenio convenio, List<Boleto> boletos) {
        LocalDate dataProcessamento = LocalDate.now();
        List<Lote> lotes = new ArrayList<>();
        Beneficiario beneficiario = convenio.getBeneficiario();

        // Cria header do arquivo
        Registro headerArquivo = Registro.novoRegistro(Registro.Tipo.ARQUIVO_HEADER)
                .comParametro("numeroInscricao", beneficiario.getDocumento(), true)
                .comParametro("codigoTransmissao", convenio.getCodigoTransmissao())
                .comParametro("nomeEmpresa", beneficiario.getNome().toUpperCase())
                .comParametro("dataGeracao", dataProcessamento)
                .comParametro("numeroSequencial", numeroArquivo);

        // Cria o lote
        // Sempre trataremos o arquivo de remessa com apenas um lote para simplificar o processo
        Lote lote = criarLote(numeroArquivo, 1, convenio, boletos);
        lotes.add(lote);

        // Cria o Cnab 240
        Cnab240 cnab240 = Cnab240.novoArquivo(Cnab240.Tipo.REMESSA)
                .comHeader(headerArquivo)
                .comLotes(lotes);

        // Cria trailer do arquivo
        int quantidadeRegistrosArquivo = 2 + lote.getQuantidadeRegistros();  // 2 header e trailer do arquivo
        Registro trailerArquivo = Registro.novoRegistro(Registro.Tipo.ARQUIVO_TRAILER)
                .comParametro("quantidadeLotes", cnab240.getQuantidadeLotes())
                .comParametro("quantidadeRegistros", cnab240.getQuantidadeRegistros());

        // Adiciona trailer do arquivo
        return cnab240.comTrailer(trailerArquivo);
    }

    private Lote criarLote(int numeroArquivo, int numeroLote, Convenio convenio, List<Boleto> boletos) {
        LocalDate dataGravacao = LocalDate.now();
        List<Detalhe> detalhes = new ArrayList<>();
        Beneficiario beneficiario = convenio.getBeneficiario();

        // Cria header do lote
        Registro headerLote = Registro.novoRegistro(Registro.Tipo.LOTE_HEADER)
                .comParametro("numeroLote", numeroLote) // Preencher com ‘0001’ para o primeiro lote do arquivo
                .comParametro("numeroInscricao", beneficiario.getDocumento(), true)
                .comParametro("codigoTransmissao", convenio.getCodigoTransmissao())
                .comParametro("nomeBeneficiario", beneficiario.getNome().toUpperCase())
                .comParametro("numeroRemessa", numeroArquivo) // Identificar a sequência de envio ou devolução do arquivo
                .comParametro("dataGravacao", dataGravacao);

        // Cria detalhes
        int numeroSequencialRegistro = 1; // Sempre começar com 1
        for (Boleto boleto : boletos) {
            Detalhe detalhe = criarDetalhe(numeroLote, numeroSequencialRegistro, convenio, boleto);
            detalhes.add(detalhe);
            numeroSequencialRegistro += detalhe.getQuantidadeRegistros();
        }

        // Cria o lote
        Lote lote = Lote.novoLote()
                .comHeader(headerLote)
                .comDetalhes(detalhes);

        // Cria trailer do lote
        Registro trailerLote = Registro.novoRegistro(Registro.Tipo.LOTE_TRAILER)
                .comParametro("numeroLote", numeroLote) // Preencher com ‘0001’ para o primeiro lote do arquivo
                .comParametro("quantidadeRegistros", lote.getQuantidadeRegistros()); // Somatório dos registros do lote, incluindo header e trailer.

        // Adiciona trailer do lote
        return lote.comTrailer(trailerLote);
    }


    private Detalhe criarDetalhe(int numeroLote, int numeroSequencial, Convenio convenio, Boleto boleto) {
        Titulo titulo = boleto.getTitulo();
        Pagador pagador = boleto.getPagador();
        String[] instrucoes = boleto.getInstrucoes();

        // Cria segmento P do detalhes
        Registro segmentoP = Registro.novoRegistro(Registro.Tipo.DETALHES_SEGMENTO)
                .comParametro("numeroLote", numeroLote)
                .comParametro("numeroSequencial", numeroSequencial)
                .comParametro("codigoMovimento", "01") // 01 Entrada de título
                .comParametro("agenciaDestinataria", convenio.getAgencia())
                .comParametro("digitoAgenciaDestinataria", convenio.getAgenciaDigito())
                .comParametro("numeroConta", convenio.getContaCorrente())
                .comParametro("digitoVerificadorConta", convenio.getContaCorrenteDigito())
                .comParametro("contaCobrancaFIDC", convenio.getContaCorrente()) // Repetir conta de movimento
                .comParametro("digitoContaCobrancaFIDC", convenio.getContaCorrenteDigito()) // Repetir dígito conta de movimento
                .comParametro("identificacaoTituloBanco", titulo.getCodigo())
                .comParametro("numeroDocumento", titulo.getNumero())
                .comParametro("dataVencimento", boleto.getDataVencimento())
                .comParametro("valorNominal", titulo.getValor())
                .comParametro("dataEmissao", titulo.getDataTitulo());

        // Cria segmento Q do detalhes
        numeroSequencial += 1;
        Registro segmentoQ = Registro.novoRegistro(Registro.Tipo.DETALHES_SEGMENTO)
                .comParametro("numeroLote", numeroLote)
                .comParametro("numeroSequencial", numeroSequencial)
                .comParametro("codigoMovimento", "01") // 01 Entrada de título
                .comParametro("numeroInscricaoPagador", pagador.getDocumento(), true)
                .comParametro("nomePagador", pagador.getNome())
                .comParametro("enderecoPagador", pagador.getEndereco())
                .comParametro("bairroPagador", pagador.getBairro())
                .comParametro("cepPagador", pagador.getCep().substring(0, 5))
                .comParametro("sufixoCepPagador", pagador.getCep().substring(5))
                .comParametro("cidadePagador", pagador.getCidade())
                .comParametro("unidadeFederacaoPagador", pagador.getUf().name());

        // Cria segmento R do detalhes
        numeroSequencial += 1;
        Registro segmentoR = Registro.novoRegistro(Registro.Tipo.DETALHES_SEGMENTO)
                .comParametro("numeroLote", numeroLote)
                .comParametro("numeroSequencial", numeroSequencial)
                .comParametro("codigoMovimento", "01") // 01 Entrada de título
                .comParametro("mensagem3", instrucoes.length >= 1 ? instrucoes[0] : "")
                .comParametro("mensagem4", instrucoes.length >= 2 ? instrucoes[1] : "");

        // Cria segmento S do detalhes
        numeroSequencial += 1;
        Registro segmentoS = Registro.novoRegistro(Registro.Tipo.DETALHES_SEGMENTO)
                .comParametro("numeroLote", numeroLote)
                .comParametro("numeroSequencial", numeroSequencial)
                .comParametro("codigoMovimento", "01") // 01 Entrada de título
                .comParametro("mensagem5", instrucoes.length >= 3 ? instrucoes[2] : "")
                .comParametro("mensagem6", instrucoes.length >= 4 ? instrucoes[3] : "")
                .comParametro("mensagem7", instrucoes.length >= 5 ? instrucoes[4] : "");

        // Cria detalhes
        return Detalhe.novoDetalhe()
                .comSegmento("segmentoP", segmentoP)
                .comSegmento("segmentoQ", segmentoQ)
                .comSegmento("segmentoR", segmentoR)
                .comSegmento("segmentoS", segmentoS);
    }
}
