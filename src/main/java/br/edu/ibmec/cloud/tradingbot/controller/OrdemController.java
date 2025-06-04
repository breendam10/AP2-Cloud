package br.edu.ibmec.cloud.tradingbot.controller;

import br.edu.ibmec.cloud.tradingbot.dto.OrdemDTOs.*;
import br.edu.ibmec.cloud.tradingbot.servico.OrdemServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ordem")
public class OrdemController {

    @Autowired
    private OrdemServico ordemServico;

    // Criar ordem
    @PostMapping("/{usuario_id}")
    public ResponseEntity<OrdemCriadaResposta> criarOrdem(@PathVariable("usuario_id") Integer usuarioId, @RequestBody OrdemRequisicao requisicao) {
        OrdemCriadaResposta resposta = ordemServico.criarOrdem(usuarioId, requisicao);
        return new ResponseEntity<>(resposta, HttpStatus.CREATED);
    }

    // Listar ordens
    @GetMapping("/{usuario_id}")
    public ResponseEntity<ListaOrdensResposta> listarOrdens(@PathVariable("usuario_id") Integer usuarioId) {
        ListaOrdensResposta resposta = ordemServico.listarOrdens(usuarioId);
        return new ResponseEntity<>(resposta, HttpStatus.OK);
    }

    // Ordem específica
    @GetMapping("/{usuario_id}/{ordem_id}")
    public ResponseEntity<OrdemDetalheResposta> obterOrdemEspecifica(@PathVariable("usuario_id") Integer usuarioId, @PathVariable("ordem_id") String ordemId) {
        OrdemDetalheResposta resposta = ordemServico.obterOrdemEspecifica(usuarioId, ordemId);
        return new ResponseEntity<>(resposta, HttpStatus.OK);
    }

    // Listar prdens abertas (em carteira)
    @GetMapping("/{usuario_id}/abertos")
    public ResponseEntity<ListaOrdensAbertasResposta> listarOrdensAbertas(@PathVariable("usuario_id") Integer usuarioId) {
        ListaOrdensAbertasResposta resposta = ordemServico.listarOrdensAbertas(usuarioId);
        return new ResponseEntity<>(resposta, HttpStatus.OK);
    }
}