package br.edu.ibmec.cloud.tradingbot.modelo;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer identificador;

    @Column(unique = true, nullable = false)
    private String nomeUsuario;

    @Column(nullable = false)
    private String senha;

    @Column
    private String chaveApiBinance;

    @Column
    private String chaveSecretaBinance;

    @Column
    private Double saldoInicial;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "configuracao_id", referencedColumnName = "identificador")
    private ConfiguracaoUsuario configuracao;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "usuario_id", referencedColumnName = "identificador")
    private List<MoedaUsuario> tickers;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "usuario_id", referencedColumnName = "identificador")
    private List<RelatorioOrdemUsuario> relatoriosOrdens;
}