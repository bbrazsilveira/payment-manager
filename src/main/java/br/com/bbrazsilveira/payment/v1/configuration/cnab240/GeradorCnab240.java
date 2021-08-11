package br.com.bbrazsilveira.payment.v1.configuration.cnab240;

import br.com.bbrazsilveira.payment.v1.configuration.cnab240.model.Cnab240;
import br.com.bbrazsilveira.payment.v1.configuration.cnab240.model.Lote;
import br.com.bbrazsilveira.payment.v1.configuration.cnab240.model.Parametro;
import br.com.bbrazsilveira.payment.v1.configuration.cnab240.model.Registro;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;


@SuppressWarnings("unchecked")
public class GeradorCnab240 {

    public static byte[] gerarRemessa(InputStream template, Cnab240 cnab240) {
        try {
            // Busca e configura o template
            Yaml yaml = new Yaml();
            LinkedHashMap<String, Object> templateMap = yaml.load(template);
            LinkedHashMap<String, Object> tRemessa = (LinkedHashMap<String, Object>) templateMap.get("remessa");

            // Valida o template
            validarTemplate(tRemessa);

            // Gera a remessa no formato CNAB 240
            String remessa = gerarRemessa(cnab240, tRemessa);

            return remessa.getBytes();
        } catch (Exception e) {
            throw new IllegalStateException(String.format("Erro ao gerar remessa. %s", e.getMessage()));
        }
    }

    private static void validarTemplate(LinkedHashMap<String, Object> tRemessa) {
        try {
            // Percorre todos registros obrigatórios
            Stream.of(Registro.Tipo.values()).forEach(tTipoRegistro -> {
                String tNomeRegistro = tTipoRegistro.getNome();

                // Verifica se o template contém o registro
                if (!tRemessa.containsKey(tNomeRegistro)) {
                    throw new IllegalStateException(String.format("Template não contém o registro \"%s\".", tNomeRegistro));
                }

                // Verifica se o registro é segmentado para validar todos parâmetros
                if (tNomeRegistro.equals("detalhes")) {
                    LinkedHashMap<String, Object> segmentos = (LinkedHashMap<String, Object>) tRemessa.get("detalhes");

                    // Percorre todos segmentos do registro detalhes
                    segmentos.forEach((tNomeSegmento, tParametros) -> {
                        // Valida o nome do segmento
                        if (!tNomeSegmento.startsWith("segmento")) {
                            throw new IllegalStateException(String.format("O nome do segmento \"%s\" do registro " +
                                    "\"detalhes\" deve começar com \"segmento\".", tNomeSegmento));
                        }

                        // Valida os parâmetros do segmento
                        validarParametros((LinkedHashMap<String, Object>) tParametros, tNomeRegistro + "/" + tNomeSegmento);
                    });
                } else {
                    LinkedHashMap<String, Object> tParametros = (LinkedHashMap<String, Object>) tRemessa.get(tNomeRegistro);
                    // Valida os parâmetros do segmento
                    validarParametros(tParametros, tNomeRegistro);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Erro de validação no template.");
        }
    }

    private static void validarParametros(LinkedHashMap<String, Object> tParametros, String tNomeRegistro) {
        Set<Integer> posicoes = new HashSet<>();

        // Percorre todos parâmetros para validar as posições
        tParametros.forEach((tNomeParametro, tParametro) -> {
            // Ignora validação quando encontra o campo "opcional"
            if (tNomeParametro.equals("opcional")) {
                if (!(tParametro instanceof Boolean)) {
                    throw new IllegalStateException("O campo \"opcional\" é reservado e deve ser usado apenas " +
                            "para declarar que determinado segmento é opcional (true ou false).");
                }
                return;
            }

            List<Integer> tPosicao = (List<Integer>) ((LinkedHashMap<String, Object>) tParametro).get("posicao");
            for (int i = tPosicao.get(0); i <= tPosicao.get(1); i++) {
                // Verifica se a posição inicial do parâmetro é menor ou igual a posição final
                if (tPosicao.get(0) > tPosicao.get(1)) {
                    throw new IllegalStateException(String.format("Posição inicial no parâmetro \"%s\" do registro " +
                            "\"%s\" não pode ser maior que a posição final.", tNomeParametro, tNomeRegistro));
                }

                // Verifica a posição de algum parâmetro não está sobrescrevendo nenhum outro parâmetro
                if (posicoes.contains(i)) {
                    throw new IllegalStateException(String.format("Posição \"%d\" no parâmetro \"%s\" do registro " +
                            "\"%s\" está sobrescrevendo outra posição.", i, tNomeParametro, tNomeRegistro));
                }

                // Adiciona a posição em um Set para validar o tamanho posteriormente
                posicoes.add(i);
            }
        });

        // Verifica se o registro/segmento tem todas 240 posições preenchidas
        if (posicoes.size() != 240 || !posicoes.contains(1) || !posicoes.contains(240)) {
            throw new IllegalStateException(String.format("O registro \"%s\" não tem posições válidas. Verifique se todas " +
                    "240 posições estão sendo preenchidas, se o registro começa pela posição 1 e termina na posiçao 240.", tNomeRegistro));
        }
    }

    private static String gerarRemessa(Cnab240 cnab240, LinkedHashMap<String, Object> tRemessa) {
        // Parâmetros do cnab240
        Registro header = cnab240.getHeader();
        List<Lote> lotes = cnab240.getLotes();
        Registro trailer = cnab240.getTrailer();

        // Gera registro do header do arquivo no formato CNAB 240
        StringBuilder sb = new StringBuilder();
        LinkedHashMap<String, Object> tArquivoHeader = (LinkedHashMap<String, Object>) tRemessa.get(Registro.Tipo.ARQUIVO_HEADER.getNome());
        sb.append(gerarRegistroRemessa(tArquivoHeader, Registro.Tipo.ARQUIVO_HEADER, header)).append("\r\n");

        // Percorre todos lotes informados pelo usuário
        lotes.forEach(lote -> {
            // Gera registro do header do lote no formato CNAB 240
            LinkedHashMap<String, Object> tLoteHeader = (LinkedHashMap<String, Object>) tRemessa.get(Registro.Tipo.LOTE_HEADER.getNome());
            sb.append(gerarRegistroRemessa(tLoteHeader, Registro.Tipo.LOTE_HEADER, lote.getHeader())).append("\r\n");

            // Percorre todos detalhes informados pelo usuário
            lote.getDetalhes().forEach(detalhe -> {
                LinkedHashMap<String, Object> tSegmentos = (LinkedHashMap<String, Object>) tRemessa.get("detalhes");

                // Percorre todos segmentos do registro detalhes
                tSegmentos.forEach((tNomeSegmento, tSegmento) -> {
                    // Encontra o segmento informado pelo usuário
                    Registro segmento = detalhe.getSegmentos().get(tNomeSegmento);
                    boolean opcional = (boolean) ((LinkedHashMap<String, Object>) tSegmento).getOrDefault("opcional", false);

                    // Verifica se o segmento foi informado pelo usuário ou se ele é opcional
                    if (segmento == null && !opcional) {
                        throw new IllegalStateException(String.format("O segmento \"%s\" de \"detalhes\" não foi informado e não é opcional.", tNomeSegmento));
                    }

                    // Gera registro do segmento de detalhes no formato CNAB 240
                    sb.append(gerarRegistroRemessa((LinkedHashMap<String, Object>) tSegmento, Registro.Tipo.DETALHES_SEGMENTO, segmento)).append("\r\n");
                });
            });

            // Gera registro do trailer do lote no formato CNAB 240
            LinkedHashMap<String, Object> tLoteTrailer = (LinkedHashMap<String, Object>) tRemessa.get(Registro.Tipo.LOTE_TRAILER.getNome());
            sb.append(gerarRegistroRemessa(tLoteTrailer, Registro.Tipo.LOTE_TRAILER, lote.getTrailer())).append("\r\n");
        });

        // Gera registro do trailer do arquivo no formato CNAB 240
        LinkedHashMap<String, Object> tArquivoTrailer = (LinkedHashMap<String, Object>) tRemessa.get(Registro.Tipo.ARQUIVO_TRAILER.getNome());
        sb.append(gerarRegistroRemessa(tArquivoTrailer, Registro.Tipo.ARQUIVO_TRAILER, trailer)).append("\r\n");

        // Remove acentos e espaços em brancos do início e fim da remessa, e mantém todos caracteres em caixa alta
        return StringUtils.stripAccents(sb.toString().toUpperCase());
    }

    private static String gerarRegistroRemessa(LinkedHashMap<String, Object> tRegistro, Registro.Tipo tTipo, Registro registro) {
        StringBuilder sb = new StringBuilder();

        // Percorre todos os parâmetros do registro presentes no template
        tRegistro.forEach((tNomeParametro, tParametro) -> {
            Parametro parametro;

            // Ignora campo "opcional"
            if (tNomeParametro.equals("opcional")) {
                return;
            }

            // Verifica se o parâmetro e registro informados pelo usuário existem
            if (registro == null || registro.getParametros().get(tNomeParametro) == null) {
                // Instancia Parametro com o valor padrão do template
                parametro = new Parametro(tTipo, tNomeParametro, null);
            } else {
                // Busca a instância de Parametro informada pelo usuário
                parametro = registro.getParametros().get(tNomeParametro);
            }

            // Atualiza o parâmetro com as informações do template
            parametro.setTemplate((LinkedHashMap<String, Object>) tParametro);

            // Busca o valor formatado no padrão CNAB 240
            sb.append(parametro.getValorCnab());
        });

        return sb.toString();
    }
}
