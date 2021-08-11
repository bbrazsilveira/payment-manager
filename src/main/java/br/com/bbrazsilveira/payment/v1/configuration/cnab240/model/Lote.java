package br.com.bbrazsilveira.payment.v1.configuration.cnab240.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Lote {

    private Registro header;
    private Registro trailer;
    private List<Detalhe> detalhes;

    private Lote() {
        this.detalhes = new ArrayList<>();
    }

    public static Lote novoLote() {
        return new Lote();
    }

    public Lote comHeader(Registro header) {
        this.header = header;
        return this;
    }

    public Lote comTrailer(Registro trailer) {
        this.trailer = trailer;
        return this;
    }

    public Lote comDetalhes(List<Detalhe> detalhes) {
        this.detalhes.addAll(detalhes);
        return this;
    }

    public int getQuantidadeRegistros() {
        int quantidadeRegistros = 2; // Considerando que sempre teremos header e trailer do lote

        // Soma a quantidade de registros dos detalhes
        for (Detalhe detalhe : detalhes) {
            quantidadeRegistros += detalhe.getQuantidadeRegistros();
        }

        return quantidadeRegistros;
    }
}
