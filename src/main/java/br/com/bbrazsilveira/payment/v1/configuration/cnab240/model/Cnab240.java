package br.com.bbrazsilveira.payment.v1.configuration.cnab240.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Cnab240 {

    private final Tipo tipo;
    private Registro header;
    private Registro trailer;
    private List<Lote> lotes;

    private Cnab240(Tipo tipo) {
        this.tipo = tipo;
        this.lotes = new ArrayList<>();
    }

    public static Cnab240 novoArquivo(Tipo tipo) {
        return new Cnab240(tipo);
    }

    public Cnab240 comHeader(Registro header) {
        this.header = header;
        return this;
    }

    public Cnab240 comLotes(List<Lote> lotes) {
        this.lotes.addAll(lotes);
        return this;
    }

    public Cnab240 comTrailer(Registro trailer) {
        this.trailer = trailer;
        return this;
    }

    public int getQuantidadeLotes() {
        return lotes.size();
    }

    public int getQuantidadeRegistros() {
        int quantidadeRegistros = 2; // Considerando que sempre teremos header e trailer do arquivo

        // Soma a quantidade de registros dos lotes
        for (Lote lote : lotes) {
            quantidadeRegistros += lote.getQuantidadeRegistros();
        }

        return quantidadeRegistros;
    }

    public enum Tipo {
        REMESSA, RETORNO
    }
}
