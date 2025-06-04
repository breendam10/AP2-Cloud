package br.edu.ibmec.cloud.tradingbot.modelo;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ConfiguracaoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer identificador;

    @Column
    private Double percentualPerda;

    @Column
    private Double percentualLucro;

    @Column
    private Double quantidadePorOrdem;
}