package br.edu.ibmec.cloud.tradingbot.servico;

import br.edu.ibmec.cloud.tradingbot.dto.BinanceApiDTOs.BinanceOrdemResposta;
import br.edu.ibmec.cloud.tradingbot.dto.OrdemDTOs.*;
import br.edu.ibmec.cloud.tradingbot.modelo.RelatorioOrdemUsuario;
import br.edu.ibmec.cloud.tradingbot.modelo.Usuario;
import br.edu.ibmec.cloud.tradingbot.repositorio.RelatorioOrdemUsuarioRepositorio;
import br.edu.ibmec.cloud.tradingbot.repositorio.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdemServico {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private RelatorioOrdemUsuarioRepositorio relatorioOrdemUsuarioRepositorio;

    @Autowired
    private BinanceServico binanceServico;

    @Transactional
    public OrdemCriadaResposta criarOrdem(Integer usuarioId, OrdemRequisicao requisicao) {
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (usuario.getChaveApiBinance() == null || usuario.getChaveSecretaBinance() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chaves da Binance não configuradas para o usuário.");
        }

        binanceServico.setApiKey(usuario.getChaveApiBinance());
        binanceServico.setSecretKey(usuario.getChaveSecretaBinance());

        String tipoOperacaoBinance;
        if ("COMPRA".equalsIgnoreCase(requisicao.getTp_operacao())) {
            tipoOperacaoBinance = "BUY";
        } else if ("VENDA".equalsIgnoreCase(requisicao.getTp_operacao())) {
            tipoOperacaoBinance = "SELL";
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de operação inválido: " + requisicao.getTp_operacao());
        }

        // Criar ordem na Binance
        BinanceOrdemResposta binanceResposta = binanceServico.criarOrdemMercado(
                requisicao.getSimbolo(),
                requisicao.getQuantidade(),
                tipoOperacaoBinance
        );

        // Salvar relatório da ordem no banco de dados local
        RelatorioOrdemUsuario relatorio = new RelatorioOrdemUsuario();
        relatorio.setOrdemIdBinance(binanceResposta.getOrdemId());
        relatorio.setSimbolo(binanceResposta.getSimbolo());
        relatorio.setQuantidade(binanceResposta.getQuantidadeExecutada());
        relatorio.setDataOperacao(LocalDateTime.now());
        relatorio.setStatus(binanceResposta.getStatus());

        relatorio.setTpOperacao(requisicao.getTp_operacao());
        relatorio.setTipoOrdem(requisicao.getTipo());

        // Se for uma ordem de compra, registre o preço de compra
        if ("COMPRA".equalsIgnoreCase(requisicao.getTp_operacao())) {
            relatorio.setPrecoCompra(binanceResposta.getPrecoMedioExecucao());
            relatorio.setStatus("EM_CARTEIRA");
        } else { // Se for uma ordem de venda, atualize a ordem de compra anterior
            relatorio.setPrecoVenda(binanceResposta.getPrecoMedioExecucao());

            RelatorioOrdemUsuario ordemCompraExistente = usuario.getRelatoriosOrdens().stream()
                .filter(o -> o.getSimbolo().equalsIgnoreCase(requisicao.getSimbolo()) && "EM_CARTEIRA".equalsIgnoreCase(o.getStatus()))
                .findFirst() // Pega a primeira ordem de compra aberta para este símbolo
                .orElse(null);

            if (ordemCompraExistente != null) {
                ordemCompraExistente.setPrecoVenda(binanceResposta.getPrecoMedioExecucao());
                ordemCompraExistente.setStatus("VENDIDA");
                relatorioOrdemUsuarioRepositorio.save(ordemCompraExistente);
            }
            relatorio.setStatus("VENDIDA"); // Status da ordem de venda
        }

        if (usuario.getRelatoriosOrdens() == null) {
            usuario.setRelatoriosOrdens(new java.util.ArrayList<>());
        }
        usuario.getRelatoriosOrdens().add(relatorio);
        usuarioRepositorio.save(usuario);

        return new OrdemCriadaResposta(
                "Ordem criada com sucesso",
                binanceResposta.getOrdemId(),
                binanceResposta.getSimbolo(),
                requisicao.getTipo(), // Tipo da ordem (MERCADO/LIMITE) da requisição
                requisicao.getTp_operacao(),
                binanceResposta.getQuantidadeExecutada(),
                binanceResposta.getPrecoMedioExecucao(),
                relatorio.getStatus(),
                binanceResposta.getFills()
        );
    }

    public ListaOrdensResposta listarOrdens(Integer usuarioId) {
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        List<OrdemDetalheResposta> ordens = usuario.getRelatoriosOrdens().stream()
                .map(rel -> new OrdemDetalheResposta(
                        rel.getSimbolo(),
                        rel.getOrdemIdBinance(),
                        rel.getQuantidade(),
                        rel.getTipoOrdem(),
                        rel.getTpOperacao(),
                        rel.getPrecoVenda() > 0 ? rel.getPrecoVenda() : rel.getPrecoCompra(),
                        rel.getStatus(),
                        null
                ))
                .collect(Collectors.toList());

        return new ListaOrdensResposta(ordens.size(), ordens);
    }

    public OrdemDetalheResposta obterOrdemEspecifica(Integer usuarioId, String ordemId) {
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        RelatorioOrdemUsuario relatorio = usuario.getRelatoriosOrdens().stream()
                .filter(o -> o.getOrdemIdBinance() != null && o.getOrdemIdBinance().equals(ordemId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordem não encontrada"));

        return new OrdemDetalheResposta(
                relatorio.getSimbolo(),
                relatorio.getOrdemIdBinance(),
                relatorio.getQuantidade(),
                relatorio.getTipoOrdem(),
                relatorio.getTpOperacao(),
                relatorio.getPrecoVenda() > 0 ? relatorio.getPrecoVenda() : relatorio.getPrecoCompra(),
                relatorio.getStatus(),
                null
        );
    }

    public ListaOrdensAbertasResposta listarOrdensAbertas(Integer usuarioId) {
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        List<OrdemAbertaDetalheResposta> ordensAbertas = usuario.getRelatoriosOrdens().stream()
                .filter(rel -> "EM_CARTEIRA".equalsIgnoreCase(rel.getStatus()))
                .map(rel -> new OrdemAbertaDetalheResposta(
                        rel.getIdentificador(),
                        rel.getSimbolo(),
                        rel.getQuantidade(),
                        rel.getPrecoCompra(),
                        rel.getDataOperacao(),
                        rel.getStatus()
                ))
                .collect(Collectors.toList());

        return new ListaOrdensAbertasResposta(ordensAbertas.size(), ordensAbertas);
    }
}