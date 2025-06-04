package br.edu.ibmec.cloud.tradingbot.servico;

import br.edu.ibmec.cloud.tradingbot.dto.BinanceApiDTOs.TickerResposta;
import br.edu.ibmec.cloud.tradingbot.dto.MoedasDTOs.*;
import br.edu.ibmec.cloud.tradingbot.modelo.MoedaUsuario;
import br.edu.ibmec.cloud.tradingbot.modelo.Usuario;
import br.edu.ibmec.cloud.tradingbot.repositorio.UsuarioRepositorio;
import br.edu.ibmec.cloud.tradingbot.resposta.MensagemResposta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MoedasServico {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private BinanceServico binanceServico;

    public TradingPairsResposta listarParesDeTradingDisponiveis() {
        List<String> tradingPairs = binanceServico.obterParesDeTradingDisponiveis();
        return new TradingPairsResposta(true, tradingPairs, tradingPairs.size());
    }

    public ListaMoedasAtivasResposta listarMoedasAtivasDoUsuario(Integer usuarioId) {
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (usuario.getChaveApiBinance() == null || usuario.getChaveSecretaBinance() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chaves da Binance não configuradas para o usuário.");
        }

        binanceServico.setApiKey(usuario.getChaveApiBinance());
        binanceServico.setSecretKey(usuario.getChaveSecretaBinance());

        List<String> simbolosAtivos = usuario.getTickers().stream()
                .map(MoedaUsuario::getSimbolo)
                .collect(Collectors.toList());

        List<MoedaAtivaDetalheResposta> moedasDetalhes = new ArrayList<>();
        if (!simbolosAtivos.isEmpty()) {
            List<TickerResposta> tickersBinance = binanceServico.obterTickers(simbolosAtivos);

            for (MoedaUsuario tickerUsuario : usuario.getTickers()) {
                Optional<TickerResposta> binanceTicker = tickersBinance.stream()
                        .filter(t -> t.getSimbolo().equals(tickerUsuario.getSimbolo()))
                        .findFirst();

                moedasDetalhes.add(new MoedaAtivaDetalheResposta(
                    tickerUsuario.getIdentificador(),
                    tickerUsuario.getSimbolo(),
                    binanceTicker.map(TickerResposta::getUltimoPreco).orElse(0.0) // 0.0 se não encontrar o preço
                ));
            }
        }

        return new ListaMoedasAtivasResposta(moedasDetalhes.size(), moedasDetalhes);
    }

    @Transactional
    public AdicionarMoedasAtivasResposta adicionarMoedaAtiva(Integer usuarioId, AdicionarMoedaAtivaRequisicao requisicao) {
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        List<String> simbolosParaAdicionar = new ArrayList<>();
        if (requisicao.getSimbolo() != null) {
            simbolosParaAdicionar.add(requisicao.getSimbolo());
        } else if (requisicao.getSimbolos() != null) {
            simbolosParaAdicionar.addAll(requisicao.getSimbolos());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nenhum símbolo fornecido para adicionar.");
        }

        List<String> adicionadas = new ArrayList<>();
        List<AdicionarMoedaIgnorada> ignoradas = new ArrayList<>();

        List<String> simbolosJaCadastrados = usuario.getTickers().stream()
                .map(MoedaUsuario::getSimbolo)
                .collect(Collectors.toList());

        for (String simbolo : simbolosParaAdicionar) {
            if (simbolosJaCadastrados.contains(simbolo)) {
                ignoradas.add(new AdicionarMoedaIgnorada(simbolo, "Moeda já cadastrada para este usuário"));
            } else {
                MoedaUsuario novoTicker = new MoedaUsuario();
                novoTicker.setSimbolo(simbolo);
                usuario.getTickers().add(novoTicker);
                adicionadas.add(simbolo);
            }
        }
        usuarioRepositorio.save(usuario);

        String message = String.format("%d moeda(s) adicionada(s) com sucesso", adicionadas.size());
        return new AdicionarMoedasAtivasResposta(message, adicionadas, ignoradas);
    }

    @Transactional
    public MensagemResposta removerMoedaAtivaPorId(Integer usuarioId, Integer moedaId) {
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        Optional<MoedaUsuario> tickerParaRemover = usuario.getTickers().stream()
                .filter(t -> t.getIdentificador().equals(moedaId))
                .findFirst();

        if (tickerParaRemover.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Moeda ativa não encontrada com o ID: " + moedaId);
        }

        usuario.getTickers().remove(tickerParaRemover.get());
        usuarioRepositorio.save(usuario);

        return new MensagemResposta("Moeda ativa removida com sucesso");
    }

    @Transactional
    public MensagemResposta removerMoedaAtivaPorSimbolo(Integer usuarioId, String simbolo) {
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        Optional<MoedaUsuario> tickerParaRemover = usuario.getTickers().stream()
                .filter(t -> t.getSimbolo().equalsIgnoreCase(simbolo))
                .findFirst();

        if (tickerParaRemover.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Moeda ativa não encontrada com o símbolo: " + simbolo);
        }

        usuario.getTickers().remove(tickerParaRemover.get());
        usuarioRepositorio.save(usuario); 

        return new MensagemResposta("Moeda " + simbolo + " removida com sucesso");
    }

    public MinQuantityResposta obterQuantidadeMinimaParaTrading(String simbolo) {
        double minQuantity = binanceServico.obterQuantidadeMinima(simbolo);
        return new MinQuantityResposta(true, minQuantity);
    }
}