package br.edu.ibmec.cloud.tradingbot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

public class BinanceApiDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PreenchimentoResposta {
        private double preco;
        private double quantidade;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BinanceOrdemResposta {
        @JsonProperty("symbol")
        private String simbolo;
        @JsonProperty("orderId")
        private String ordemId;
        @JsonProperty("status")
        private String status;
        @JsonProperty("type")
        private String tipo; // (MARKET, LIMIT)
        @JsonProperty("side")
        private String lado; // (BUY, SELL)
        @JsonProperty("executedQty")
        private double quantidadeExecutada;
        @JsonProperty("cummulativeQuoteQty")
        private double precoMedioExecucao;
        @JsonProperty("fills")
        private List<PreenchimentoResposta> fills;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TickerResposta {
        @JsonProperty("symbol")
        private String simbolo;

        @JsonProperty("price")
        private double ultimoPreco;
    }
}