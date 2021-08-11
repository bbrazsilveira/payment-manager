package br.com.bbrazsilveira.payment.v1.configuration.cnab240.model;

import lombok.Getter;

import java.util.LinkedHashMap;

@Getter
public class Registro {

    private final Tipo tipo;
    private LinkedHashMap<String, Parametro> parametros;

    private Registro(Tipo tipo) {
        this.tipo = tipo;
        this.parametros = new LinkedHashMap<>();
    }

    public static Registro novoRegistro(Tipo tipo) {
        return new Registro(tipo);
    }

    public Registro comParametro(String nome, Object valor) {
        parametros.put(nome, new Parametro(this.tipo, nome, valor));
        return this;
    }

    public Registro comParametro(String nome, String valor, boolean somenteAlfanumerico) {
        if (somenteAlfanumerico) {
            parametros.put(nome, new Parametro(this.tipo, nome, valor.replaceAll("[^A-Za-z0-9]", "")));
        }
        return this;
    }

    public enum Tipo {

        ARQUIVO_HEADER("headerArquivo"),
        ARQUIVO_TRAILER("trailerArquivo"),
        LOTE_HEADER("headerLote"),
        LOTE_TRAILER("trailerLote"),
        DETALHES_SEGMENTO("detalhes");

        private String nome;

        public String getNome() {
            return this.nome;
        }

        Tipo(String nome) {
            this.nome = nome;
        }
    }
}