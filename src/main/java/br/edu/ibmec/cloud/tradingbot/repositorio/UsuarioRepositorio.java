package br.edu.ibmec.cloud.tradingbot.repositorio;

import br.edu.ibmec.cloud.tradingbot.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer> {
    Usuario findByNomeUsuario(String nomeUsuario);
}