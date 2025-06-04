package br.edu.ibmec.cloud.tradingbot.servico;

import br.edu.ibmec.cloud.tradingbot.dto.UsuarioDTOs.*;
import br.edu.ibmec.cloud.tradingbot.modelo.ConfiguracaoUsuario;
import br.edu.ibmec.cloud.tradingbot.modelo.Usuario;
import br.edu.ibmec.cloud.tradingbot.repositorio.ConfiguracaoUsuarioRepositorio;
import br.edu.ibmec.cloud.tradingbot.repositorio.UsuarioRepositorio;
import br.edu.ibmec.cloud.tradingbot.resposta.MensagemResposta;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class UsuarioServico {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private ConfiguracaoUsuarioRepositorio configuracaoUsuarioRepositorio;

    @Autowired
    private BinanceServico binanceServico;

    public UsuarioCriadoResposta criarUsuario(CriarUsuarioRequisicao requisicao) {
        if (usuarioRepositorio.findByNomeUsuario(requisicao.getUsuario_login()) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe");
        }

        Usuario usuario = new Usuario();
        usuario.setNomeUsuario(requisicao.getUsuario_login());
        usuario.setSenha(requisicao.getUsuario_senha());
        usuario.setChaveApiBinance(requisicao.getUsuario_binanceApiKey());
        usuario.setChaveSecretaBinance(requisicao.getUsuario_binanceSecretKey());
        usuario.setSaldoInicial(0.0);

        ConfiguracaoUsuario config = new ConfiguracaoUsuario();
        config.setPercentualLucro(requisicao.getPct_ganho());
        config.setPercentualPerda(requisicao.getPct_perda());
        config.setQuantidadePorOrdem(requisicao.getValor_compra());

        usuario.setConfiguracao(config);
        usuarioRepositorio.save(usuario);

        return new UsuarioCriadoResposta("Usuário criado com sucesso", usuario.getIdentificador());
    }

    public LoginResposta fazerLogin(LoginRequisicao requisicao) {
        Usuario usuario = usuarioRepositorio.findByNomeUsuario(requisicao.getUsuario_login());
        if (usuario == null || !usuario.getSenha().equals(requisicao.getUsuario_senha())) {
            return new LoginResposta(false, null, "Credenciais inválidas");
        }
        return new LoginResposta(true, usuario.getIdentificador(), "Login efetuado com sucesso");
    }

    public UsuarioDetalhesResposta obterUsuario(Integer usuarioId) {
        return usuarioRepositorio.findById(usuarioId)
                .map(usuario -> {
                    boolean hasKeys = usuario.getChaveApiBinance() != null && !usuario.getChaveApiBinance().isEmpty() &&
                                    usuario.getChaveSecretaBinance() != null && !usuario.getChaveSecretaBinance().isEmpty();

                    Double saldoAtual = 0.0;
                    if (hasKeys) {
                        try {
                            binanceServico.setApiKey(usuario.getChaveApiBinance());
                            binanceServico.setSecretKey(usuario.getChaveSecretaBinance());

                            saldoAtual = binanceServico.obterSaldoRealDaConta("USDT");
                        } catch (Exception e) {
                            System.err.println("Erro ao obter saldo da Binance para usuário " + usuarioId + ": " + e.getMessage());
                            saldoAtual = 0.0;
                        }
                    }

                    if (saldoAtual != null) {
                        BigDecimal bd = new BigDecimal(saldoAtual).setScale(2, RoundingMode.HALF_UP);
                        saldoAtual = bd.doubleValue();
                    }

                    return new UsuarioDetalhesResposta(usuario.getIdentificador(), usuario.getNomeUsuario(), saldoAtual, hasKeys);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    public MensagemResposta excluirUsuario(Integer usuarioId) {
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        usuarioRepositorio.delete(usuario);

        return new MensagemResposta("Usuário excluído com sucesso");
    }

    public ConfiguracaoUsuarioResposta obterConfiguracoes(Integer usuarioId) {
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (usuario.getConfiguracao() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Configurações não encontradas para este usuário");
        }

        ConfiguracaoUsuario config = usuario.getConfiguracao();
        return new ConfiguracaoUsuarioResposta(config.getQuantidadePorOrdem(), config.getPercentualLucro(), config.getPercentualPerda());
    }

    public MensagemResposta atualizarConfiguracoes(Integer usuarioId, ConfiguracaoUsuarioRequisicao requisicao) {
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        ConfiguracaoUsuario config = usuario.getConfiguracao();
        if (config == null) {
            // Se não houver configuração existente, cria uma nova
            config = new ConfiguracaoUsuario();
            usuario.setConfiguracao(config);
        }

        config.setQuantidadePorOrdem(requisicao.getValor_compra());
        config.setPercentualLucro(requisicao.getPct_ganho());
        config.setPercentualPerda(requisicao.getPct_perda());

        configuracaoUsuarioRepositorio.save(config);
        usuarioRepositorio.save(usuario);

        return new MensagemResposta("Configuração atualizada com sucesso");
    }
}