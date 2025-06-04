package br.edu.ibmec.cloud.tradingbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UsuarioDTOs {


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CriarUsuarioRequisicao {
        private String usuario_login;
        private String usuario_senha;
        private String usuario_binanceApiKey;
        private String usuario_binanceSecretKey;
        private Double valor_compra;
        private Double pct_ganho;
        private Double pct_perda;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequisicao {
        private String usuario_login;
        private String usuario_senha;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConfiguracaoUsuarioRequisicao {
        private Double valor_compra;
        private Double pct_ganho;
        private Double pct_perda;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsuarioCriadoResposta {
        private String message;
        private Integer usuario_id;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResposta {
        private boolean success;
        private Integer usuario_id;
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsuarioDetalhesResposta {
        private Integer usuario_id;
        private String usuario_login;
        private Double usuario_saldo;
        private boolean has_binance_keys;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConfiguracaoUsuarioResposta {
        private Double valor_compra;
        private Double pct_ganho;
        private Double pct_perda;
    }
}