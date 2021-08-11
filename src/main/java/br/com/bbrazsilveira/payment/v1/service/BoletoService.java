package br.com.bbrazsilveira.payment.v1.service;


import br.com.bbrazsilveira.payment.v1.configuration.gcloud.GCStorage;
import br.com.bbrazsilveira.payment.v1.configuration.multitenancy.TenantContext;
import br.com.bbrazsilveira.payment.v1.domain.dto.BoletoRequestDto;
import br.com.bbrazsilveira.payment.v1.domain.dto.BoletoCreatedDto;
import br.com.bbrazsilveira.payment.v1.domain.model.boleto.*;
import br.com.bbrazsilveira.payment.v1.domain.model.banco.Convenio;
import br.com.bbrazsilveira.payment.v1.repository.BoletoRepository;
import br.com.bbrazsilveira.payment.v1.repository.BoletoStatusRepository;
import br.com.bbrazsilveira.payment.v1.repository.ConvenioRepository;
import com.google.cloud.storage.Blob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
public class BoletoService {

    @Value("${app.gcloud.storage-bucket}")
    private String bucket;

    @Autowired
    private GCStorage gcStorage;

    @Autowired
    private StellaService stellaService;

    @Autowired
    private BoletoRepository boletoRepository;

    @Autowired
    private BoletoStatusRepository boletoStatusRepository;

    @Autowired
    private ConvenioRepository convenioRepository;


    public BoletoCreatedDto create(@Valid BoletoRequestDto boletoRequestDto) {
        // Find convenio by id
        UUID convenioId = boletoRequestDto.getConvenioId();
        Convenio convenio = convenioRepository.findById(convenioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Convenio was not found for parameters {id=%s}.", convenioId)));

        // Create boleto entity
        Boleto boleto = createBoleto(convenio, boletoRequestDto);
        boleto.setId(UUID.randomUUID());

        // Export boleto as PDF
        br.com.caelum.stella.boleto.Boleto boletoStella = stellaService.createBoleto(boleto);
        byte[] pdfFile = stellaService.exportBoleto(boletoStella, convenio.getTemplateBoletoPath());

        // Update boleto nosso número
        String digitoNossoNumero = boletoStella.getNossoNumeroECodDocumento().split("-")[1];
        boleto.getTitulo().setCodigoDigito(digitoNossoNumero);

        // Upload boleto to GCS
        String url = uploadBoleto(boleto, pdfFile);

        // Update boleto's URL and save
        boleto.setUrl(url);
        boletoRepository.save(boleto);

        // Save boleto status
        BoletoStatus status = new BoletoStatus();
        status.setBoleto(boleto);
        status.setStatus(BoletoStatus.Status.CRIADO);
        boletoStatusRepository.save(status);

        return BoletoCreatedDto.builder()
                .id(boleto.getId())
                .url(boleto.getUrl())
                .build();
    }

    private Boleto createBoleto(Convenio convenio, BoletoRequestDto boletoRequestDto) {
        BoletoRequestDto.TituloDto tituloDto = boletoRequestDto.getTitulo();
        BoletoRequestDto.PagadorDto pagadorDto = boletoRequestDto.getPagador();

        Pagador pagador = new Pagador();
        pagador.setNome(pagadorDto.getNome().toUpperCase());
        pagador.setEndereco(pagadorDto.getEndereco().toUpperCase());
        pagador.setBairro(pagadorDto.getBairro().toUpperCase());
        pagador.setCep(pagadorDto.getCep());
        pagador.setCidade(pagadorDto.getCidade().toUpperCase());
        pagador.setUf(Pagador.UF.valueOf(pagadorDto.getUf().toString()));
        pagador.setDocumento(pagadorDto.getDocumento());
        pagador.setTipoDocumento(Pagador.TipoDocumento.valueOf(pagadorDto.getTipoDocumento().toString()));

        Titulo titulo = new Titulo();
        titulo.setNumero(tituloDto.getNumero());
        titulo.setCodigo(boletoRepository.getNextNossoNumero().toString());
        titulo.setValor(tituloDto.getValor());
        titulo.setDataTitulo(tituloDto.getDataTitulo());
        titulo.setEspecie(tituloDto.getEspecie().toString());

        Boleto boleto = new Boleto();
        boleto.setNome(boletoRequestDto.getNome());
        boleto.setConvenio(convenio);
        boleto.setTitulo(titulo);
        boleto.setPagador(pagador);
        boleto.setAceite(boletoRequestDto.getAceite());
        boleto.setEspecieMoeda(boletoRequestDto.getEspecieMoeda().toString());
        boleto.setDataDocumento(tituloDto.getDataTitulo());
        boleto.setDataProcessamento(LocalDate.now());
        boleto.setDataVencimento(boletoRequestDto.getDataVencimento());
        boleto.setLocalPagamento(boletoRequestDto.getLocalPagamento().toUpperCase());
        boleto.setInstrucao1(boletoRequestDto.getInstrucao1());
        boleto.setInstrucao2(boletoRequestDto.getInstrucao2());
        boleto.setInstrucao3(boletoRequestDto.getInstrucao3());
        boleto.setInstrucao4(boletoRequestDto.getInstrucao4());
        boleto.setInstrucao5(boletoRequestDto.getInstrucao5());

        return boleto;
    }

    private String uploadBoleto(Boleto boleto, byte[] pdfFile) {
        String filename = boleto.getNome() + ".pdf";
        String tenant = TenantContext.getCurrentTenant();
        String convenio = boleto.getConvenio().getId().toString();
        String object = String.format("payment/%s/convenios/%s/boletos/%s.pdf", tenant, convenio, boleto.getId().toString());

        Blob blob = gcStorage.create(bucket, object, "application/pdf", pdfFile, true, GCStorage.DispositionType.INLINE, filename);
        if (!blob.exists()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload file to Google Cloud Storage.");
        }

        return gcStorage.getUrl(blob);
    }
}