package br.edu.ibmec.cloud.tradingbot.modelo;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class RelatorioOrdemUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int identificador;

    @Column
    private String ordemIdBinance;

    @Column(nullable = false)
    private String simbolo;

    @Column(nullable = false)
    private double quantidade;

    @Column(nullable = false)
    private double precoCompra;

    @Column
    private double precoVenda;

    @Column(nullable = false)
    private LocalDateTime dataOperacao;

    @Column(nullable = false)
    private String status;

    @Column
    private String tpOperacao;

    @Column 
    private String tipoOrdem;

    @ManyToOne
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    private Usuario usuario;
}