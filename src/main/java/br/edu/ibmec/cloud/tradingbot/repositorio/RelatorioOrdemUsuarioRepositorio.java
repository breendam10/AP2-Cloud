package br.edu.ibmec.cloud.tradingbot.repositorio;

import br.edu.ibmec.cloud.tradingbot.modelo.RelatorioOrdemUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelatorioOrdemUsuarioRepositorio extends JpaRepository<RelatorioOrdemUsuario, Integer> {
}