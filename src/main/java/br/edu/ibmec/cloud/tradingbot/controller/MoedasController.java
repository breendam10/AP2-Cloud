package br.edu.ibmec.cloud.tradingbot.controller;

import br.edu.ibmec.cloud.tradingbot.dto.MoedasDTOs.*;
import br.edu.ibmec.cloud.tradingbot.resposta.MensagemResposta;
import br.edu.ibmec.cloud.tradingbot.servico.MoedasServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/moedas")
public class MoedasController {

    @Autowired
    private MoedasServico moedasAtivasServico;

    // Listar pares de trading disponíveis
    @GetMapping("/pares")
    public ResponseEntity<TradingPairsResposta> listarParesDeTradingDisponiveis() {
        TradingPairsResposta resposta = moedasAtivasServico.listarParesDeTradingDisponiveis();
        return new ResponseEntity<>(resposta, HttpStatus.OK);
    }

    // Listar moedas do usuario
    @GetMapping("/{usuario_id}")
    public ResponseEntity<ListaMoedasAtivasResposta> listarMoedasAtivasDoUsuario(@PathVariable("usuario_id") Integer usuarioId) {
        ListaMoedasAtivasResposta resposta = moedasAtivasServico.listarMoedasAtivasDoUsuario(usuarioId);
        return new ResponseEntity<>(resposta, HttpStatus.OK);
    }

    // Adicionar moeda
    @PostMapping("/{usuario_id}")
    public ResponseEntity<AdicionarMoedasAtivasResposta> adicionarMoedaAtiva(@PathVariable("usuario_id") Integer usuarioId, @RequestBody AdicionarMoedaAtivaRequisicao requisicao) {
        AdicionarMoedasAtivasResposta resposta = moedasAtivasServico.adicionarMoedaAtiva(usuarioId, requisicao);
        return new ResponseEntity<>(resposta, HttpStatus.CREATED);
    }

    // Remover moeda por ID
    @DeleteMapping("/{usuario_id}/{moeda_id}")
    public ResponseEntity<MensagemResposta> removerMoedaAtivaPorId(@PathVariable("usuario_id") Integer usuarioId, @PathVariable("moeda_id") Integer moedaId) {
        MensagemResposta resposta = moedasAtivasServico.removerMoedaAtivaPorId(usuarioId, moedaId);
        return new ResponseEntity<>(resposta, HttpStatus.OK);
    }

    // Remover moeda por símbolo
    @DeleteMapping("/{usuario_id}/simbolo/{simbolo}")
    public ResponseEntity<MensagemResposta> removerMoedaAtivaPorSimbolo(@PathVariable("usuario_id") Integer usuarioId, @PathVariable("simbolo") String simbolo) {
        MensagemResposta resposta = moedasAtivasServico.removerMoedaAtivaPorSimbolo(usuarioId, simbolo);
        return new ResponseEntity<>(resposta, HttpStatus.OK);
    }

    // Quantidade mínima para trading
    @GetMapping("/binance/min_quantity/{simbolo}")
    public ResponseEntity<MinQuantityResposta> obterQuantidadeMinimaParaTrading(@PathVariable("simbolo") String simbolo) {
        MinQuantityResposta resposta = moedasAtivasServico.obterQuantidadeMinimaParaTrading(simbolo);
        return new ResponseEntity<>(resposta, HttpStatus.OK);
    }
}