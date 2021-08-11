package br.com.bbrazsilveira.payment.v1.configuration.cnab240.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Getter
public class Parametro {

    private String nome;
    private Registro.Tipo tipoRegistro;
    private Tipo tipoParametro;
    private String formato;
    private boolean truncar;
    private int tamanho;
    private int posicaoInicial;
    private int posicaoFinal;
    private Object valor;
    private Object valorPadrao;

    public Parametro(Registro.Tipo tipoRegistro, String nome, Object valor) {
        this.nome = nome;
        this.tipoRegistro = tipoRegistro;
        this.valor = valor;
    }

    @SuppressWarnings("unchecked")
    public void setTemplate(LinkedHashMap<String, Object> tParametro) {
        // Atualiza os campos do parâmetro
        List<Integer> posicao = (List<Integer>) tParametro.get("posicao");
        this.tipoParametro = Tipo.valueOf(tParametro.get("tipo").toString());
        this.posicaoInicial = posicao.get(0);
        this.posicaoFinal = posicao.get(1);
        this.valorPadrao = tParametro.get("valorPadrao");
        this.truncar = (boolean) tParametro.getOrDefault("truncar", false);
        this.formato = tParametro.get("formato") != null ? tParametro.get("formato").toString() : null;
        this.tamanho = posicaoFinal - posicaoInicial + 1;
    }

    public String getValorCnab() {
        // Caso não seja informado o valor, ele será o valor padrão
        Object valorCnab = valor != null ? valor : valorPadrao;

        // Verifica se o valor foi informado ou se existe um valor padrão
        if (valorCnab == null) {
            throw new IllegalStateException(String.format("Parâmetro \"%s\" do registro \"%s\" não tem um valor padrão. " +
                    "Defina um valor padrão no template ou informe o valor do parâmetro.", nome, getNomeRegistro()));
        }

        StringBuilder sb = new StringBuilder(tamanho);
        String strValorCnab;

        // Converte o valor para String
        if (this.formato == null && (valorCnab instanceof String || valorCnab instanceof Integer)) {
            strValorCnab = valorCnab.toString();
        } else {
            // Verifica se o formato foi definido no template
            validarFormato();

            // Verifica o tipoParametro do valor e faz a conversão de acordo com o formato do template
            if (valorCnab instanceof Date) {
                strValorCnab = new SimpleDateFormat(formato).format((Date) valorCnab);
            } else if (valorCnab instanceof Calendar) {
                strValorCnab = new SimpleDateFormat(formato).format(((Calendar) valorCnab).getTime());
            } else if (valorCnab instanceof LocalDate) {
                strValorCnab = ((LocalDate) valorCnab).format(DateTimeFormatter.ofPattern(formato));
            } else if (valorCnab instanceof Integer || valorCnab instanceof Double || valorCnab instanceof Float || valorCnab instanceof BigDecimal) {
                strValorCnab = new DecimalFormat("#.00").format(valorCnab).replace(",", "").replace(".", "");
            } else if (valorCnab instanceof String && ((String) valorCnab).isEmpty()) {
                strValorCnab = valorCnab.toString();
            } else {
                throw new IllegalStateException(String.format("O tipoParametro \"%s\" não é suportado.", valorCnab.getClass().getName()));
            }
        }

        // Valida se campos do tipo N contém apenas dígitos
        if (tipoParametro == Tipo.N) {
            if (strValorCnab.length() > 0 && !strValorCnab.matches("[0-9]+")) {
                throw new IllegalStateException(String.format("O valor \"%s\" do parâmetro \"%s\" e registro \"%s\" contém caracteres " +
                        "não numéricos e seu tipo foi definido como \"N\".", strValorCnab, nome, getNomeRegistro()));
            }
        }

        // Verifica se o valor excede o limite do template
        if (strValorCnab.length() > tamanho) {
            // Verifica se o parâmetro permite truncar no template
            if (!truncar) {
                throw new IllegalStateException(String.format("O valor \"%s\" do parâmetro \"%s\" e registro \"%s\" excedeu o tamanho máximo de \"%d\". " +
                        "Reduza o tamanho do valor ou defina o campo \"truncar: true\" no template.", strValorCnab, nome, getNomeRegistro(), tamanho));
            }

            // Trunca o valor para não exceder o limite do template
            sb.append(strValorCnab, 0, tamanho);
        } else {
            // Concatena o valor
            sb.append(strValorCnab);

            if (tipoParametro == Tipo.A) {
                // Adiciona espaços em branco à direita
                while (sb.length() < sb.capacity()) {
                    sb.append(" ");
                }
            } else if (tipoParametro == Tipo.N) {
                // Adiciona zeros à esquerda
                while (sb.length() < sb.capacity()) {
                    sb.insert(0, "0");
                }
            }
        }

        return sb.toString();
    }

    public String getNomeRegistro() {
        return this.tipoRegistro.getNome();
    }

    private void validarFormato() {
        if (this.formato == null) {
            throw new IllegalStateException(String.format("Formato não encontrado para o parâmetro \"%s\" do registro \"%s\". " +
                    "É necessário declarar o formato quando o tipoParametro do parâmetro é %s.", nome, getNomeRegistro(), valor.getClass().getName()));
        }
    }

    private enum Tipo {
        A, N
    }
}
