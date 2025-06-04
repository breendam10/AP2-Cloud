package br.edu.ibmec.cloud.tradingbot.dto;
import br.edu.ibmec.cloud.tradingbot.dto.BinanceApiDTOs.PreenchimentoResposta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

public class OrdemDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrdemRequisicao {
        private String simbolo;
        private String tp_operacao; // (COMPRA,VENDA)
        private double quantidade;
        private String tipo; // (MERCADO,LIMITE)
        private Double preco;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrdemCriadaResposta {
        private String message;
        private String ordem_id;
        private String simbolo;
        private String tipo; // (MERCADO, LIMITE)
        private String tp_operacao; // (COMPRA, VENDA)
        private double quantidade;
        private double preco;
        private String status; // (EXECUTADA, PENDENTE)
        private List<PreenchimentoResposta> fills;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrdemDetalheResposta {
        private String simbolo;
        private String ordem_id;
        private double qtd_executada;
        private String tipo; // (MERCADO, LIMITE)
        private String tp_operacao; // (COMPRA, VENDA)
        private double preco;
        private String status; // (EM CARTEIRA, VENDIDA)
        private List<PreenchimentoResposta> fills;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListaOrdensResposta {
        private int total;
        private List<OrdemDetalheResposta> ordens;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrdemAbertaDetalheResposta {
        private Integer id;
        private String moeda;
        private double quantidade;
        private double preco_compra;
        private LocalDateTime data_operacao;
        private String status; // (EM CARTEIRA)
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListaOrdensAbertasResposta {
        private int total;
        private List<OrdemAbertaDetalheResposta> ordens_abertas;
    }
}