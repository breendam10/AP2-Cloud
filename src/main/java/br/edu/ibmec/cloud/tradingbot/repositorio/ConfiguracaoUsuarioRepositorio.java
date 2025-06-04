package br.edu.ibmec.cloud.tradingbot.repositorio;

import br.edu.ibmec.cloud.tradingbot.modelo.ConfiguracaoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracaoUsuarioRepositorio extends JpaRepository<ConfiguracaoUsuario, Integer> {
}