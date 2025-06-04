package br.edu.ibmec.cloud.tradingbot.modelo;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
public class MoedaUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer identificador;

    @Column
    private String simbolo;
}