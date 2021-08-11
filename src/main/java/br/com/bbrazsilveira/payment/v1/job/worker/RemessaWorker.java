package br.com.bbrazsilveira.payment.v1.job.worker;

import br.com.bbrazsilveira.payment.v1.configuration.cnab240.GeradorCnab240;
import br.com.bbrazsilveira.payment.v1.configuration.cnab240.model.Cnab240;
import br.com.bbrazsilveira.payment.v1.configuration.gcloud.GCStorage;
import br.com.bbrazsilveira.payment.v1.configuration.multitenancy.TenantContext;
import br.com.bbrazsilveira.payment.v1.domain.model.PObject;
import br.com.bbrazsilveira.payment.v1.domain.model.arquivo.Arquivo;
import br.com.bbrazsilveira.payment.v1.domain.model.banco.Convenio;
import br.com.bbrazsilveira.payment.v1.domain.model.boleto.Boleto;
import br.com.bbrazsilveira.payment.v1.domain.model.boleto.BoletoStatus;
import br.com.bbrazsilveira.payment.v1.job.cnab240.Cnab240Santander;
import br.com.bbrazsilveira.payment.v1.repository.ArquivoRepository;
import br.com.bbrazsilveira.payment.v1.repository.BoletoRepository;
import br.com.bbrazsilveira.payment.v1.repository.BoletoStatusRepository;
import br.com.bbrazsilveira.payment.v1.repository.ConvenioRepository;
import com.google.cloud.storage.Blob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Transactional
public class RemessaWorker {

    @Value("${app.gcloud.storage-bucket}")
    private String bucket;

    @Value("${app.keycloak.admin-id}")
    private UUID adminId;

    @Autowired
    private ConvenioRepository convenioRepository;

    @Autowired
    private BoletoRepository boletoRepository;

    @Autowired
    private ArquivoRepository arquivoRepository;

    @Autowired
    private BoletoStatusRepository boletoStatusRepository;

    @Autowired
    private Cnab240Santander cnab240Santander;

    @Autowired
    private GCStorage gcStorage;

    public void enviarRemessas() {
        // Find all non-deleted convenio
        convenioRepository.findAllByDeletedByNull()
                .forEach(this::enviarRemessaByConvenio); // Enviar remessa by convenio
    }

    private void enviarRemessaByConvenio(Convenio convenio) {
        // Find all non-deleted boletos by convenio and status
        List<Boleto> boletos = boletoRepository.findAllActiveByConvenioIdAndStatus(convenio.getId(), BoletoStatus.Status.CRIADO.name());

        if (boletos.size() > 0) {
            // Create arquivo
            Arquivo arquivo = new Arquivo();
            arquivo.setId(UUID.randomUUID());
            arquivo.setNumero(arquivoRepository.getNextNumeroArquivo());
            arquivo.setConvenio(convenio);
            arquivo.setTipo(Arquivo.Tipo.REMESSA);

            // Generate remessa cnab240
            InputStream template = getClass().getResourceAsStream(convenio.getTemplateArquivoPath());
            Cnab240 cnab240 = cnab240Santander.criarCnab240(arquivo.getNumero(), convenio, boletos);
            byte[] remessa = GeradorCnab240.gerarRemessa(template, cnab240);

            // Save arquivo
            String url = getRemessaUrl(arquivo);
            arquivo.setUrl(url);
            arquivoRepository.save(arquivo);

            // Delete older status
            UUID[] boletosId = boletos.stream().map(PObject::getId).toArray(UUID[]::new);
            boletoStatusRepository.deleteAllByBoletos(boletosId, adminId);

            // Create and save boleto status for each boleto
            List<BoletoStatus> boletosStatus = boletos.stream()
                    .map(boleto -> new BoletoStatus(BoletoStatus.Status.AGUARDANDO_PAGAMENTO, boleto))
                    .collect(Collectors.toList());
            boletoStatusRepository.saveAll(boletosStatus);

            // Upload remessa to Google Storage
            uploadRemessa(arquivo, remessa);
        }
    }

    /**
     * Generate an url before to upload the file
     */
    private String getRemessaUrl(Arquivo arquivo) {
        String object = getRemessaObject(arquivo);
        return gcStorage.getUrl(bucket, object);
    }

    private void uploadRemessa(Arquivo arquivo, byte[] remessa) {
        String object = getRemessaObject(arquivo);
        Blob blob = gcStorage.create(bucket, object, "text/plain", remessa, false);
        if (!blob.exists()) {
            throw new RuntimeException("Failed to upload file to Google Cloud Storage.");
        }
    }

    private String getRemessaObject(Arquivo arquivo) {
        String tenant = TenantContext.getCurrentTenant();
        String filename = arquivo.getId().toString() + ".txt";
        String convenio = arquivo.getConvenio().getId().toString() ;
        return String.format("payment/%s/convenios/%s/remessas/%s", tenant, convenio, filename);
    }
}
