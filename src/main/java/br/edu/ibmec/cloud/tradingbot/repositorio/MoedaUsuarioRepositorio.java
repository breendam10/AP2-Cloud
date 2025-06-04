package br.edu.ibmec.cloud.tradingbot.repositorio;

import br.edu.ibmec.cloud.tradingbot.modelo.MoedaUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoedaUsuarioRepositorio extends JpaRepository<MoedaUsuario, Integer> {
}