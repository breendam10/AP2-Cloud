package br.edu.ibmec.cloud.tradingbot.controller;

import br.edu.ibmec.cloud.tradingbot.dto.UsuarioDTOs.*;
import br.edu.ibmec.cloud.tradingbot.resposta.MensagemResposta;
import br.edu.ibmec.cloud.tradingbot.servico.UsuarioServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario") 
public class UsuarioController {

    @Autowired
    private UsuarioServico usuarioServico;

    // Criar usuário
    @PostMapping
    public ResponseEntity<UsuarioCriadoResposta> criarUsuario(@RequestBody CriarUsuarioRequisicao requisicao) {
        UsuarioCriadoResposta resposta = usuarioServico.criarUsuario(requisicao);
        return new ResponseEntity<>(resposta, HttpStatus.CREATED);
    }

    // Login de usuário
    @PostMapping("/login")
    public ResponseEntity<LoginResposta> loginUsuario(@RequestBody LoginRequisicao requisicao) {
        LoginResposta resposta = usuarioServico.fazerLogin(requisicao);
        if (resposta.isSuccess()) {
            return new ResponseEntity<>(resposta, HttpStatus.OK);
        }
        return new ResponseEntity<>(resposta, HttpStatus.UNAUTHORIZED);
    }

    // Buscar usuário
    @GetMapping("/{usuario_id}")
    public ResponseEntity<UsuarioDetalhesResposta> obterUsuario(@PathVariable("usuario_id") Integer usuarioId) {
        UsuarioDetalhesResposta usuario = usuarioServico.obterUsuario(usuarioId);
        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }

    // Excluir usuário
    @DeleteMapping("/{usuario_id}")
    public ResponseEntity<MensagemResposta> excluirUsuario(@PathVariable("usuario_id") Integer usuarioId) {
        MensagemResposta resposta = usuarioServico.excluirUsuario(usuarioId);
        return new ResponseEntity<>(resposta, HttpStatus.OK);
    }

    // Exibir configurações do usuário
    @GetMapping("/{usuario_id}/config")
    public ResponseEntity<ConfiguracaoUsuarioResposta> obterConfiguracoesUsuario(@PathVariable("usuario_id") Integer usuarioId) {
        ConfiguracaoUsuarioResposta configuracao = usuarioServico.obterConfiguracoes(usuarioId);
        return new ResponseEntity<>(configuracao, HttpStatus.OK);
    }

    // Atualizar configurações do usuário
    @PutMapping("/{usuario_id}/config")
    public ResponseEntity<MensagemResposta> atualizarConfiguracoesUsuario(@PathVariable("usuario_id") Integer usuarioId, @RequestBody ConfiguracaoUsuarioRequisicao requisicao) {
        MensagemResposta resposta = usuarioServico.atualizarConfiguracoes(usuarioId, requisicao);
        return new ResponseEntity<>(resposta, HttpStatus.OK);
    }
}