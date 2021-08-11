package br.com.bbrazsilveira.payment.v1.configuration.cnab240.model;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Detalhe {

    private Map<String, Registro> segmentos;

    private Detalhe() {
        this.segmentos = new HashMap<>();
    }

    public static Detalhe novoDetalhe() {
        return new Detalhe();
    }

    public Detalhe comSegmento(String nome, Registro segmento) {
        if (!nome.startsWith("segmento")) {
            throw new IllegalStateException("Segmentos devem começar com o nome \"segmento\".");
        }

        this.segmentos.put(nome, segmento);
        return this;
    }

    public int getQuantidadeRegistros() {
        return segmentos.size();
    }
}
