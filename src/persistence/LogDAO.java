package persistence;

import model.LogAcesso;
import model.Usuario;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LogDAO {

    private static final String ARQUIVO = "dados/logs.csv";
    private static List<LogAcesso> bancoLogs = new ArrayList<>();
    private static long proximoId = 1;

    // --- CARGA INICIAL ---
    static {
        List<String> linhas = CsvUtil.lerArquivo(ARQUIVO);
        if (!linhas.isEmpty()) {
            carregarDoArquivo(linhas);
        }
    }

    // --- CARREGAR (CSV -> OBJETO) ---
    private static void carregarDoArquivo(List<String> linhas) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        long maiorId = 0;

        for (String linha : linhas) {
            try {
                String[] dados = linha.split(";");
                // Layout: id;id_usuario;data_iso;acao

                long id = Long.parseLong(dados[0]);
                long idUsuario = Long.parseLong(dados[1]);
                LocalDateTime dataHora = LocalDateTime.parse(dados[2]);
                String acao = dados[3];

                Usuario u = usuarioDAO.buscarPorId(idUsuario);

                // Só carrega o log se o usuário ainda existir
                if (u != null) {
                    LogAcesso log = new LogAcesso(id, u, dataHora, acao);
                    bancoLogs.add(log);

                    if (id > maiorId) maiorId = id;
                }

            } catch (Exception e) {
                System.err.println("Erro ao ler linha de log: " + linha);
            }
        }
        proximoId = maiorId + 1;
    }

    // --- SALVAR (OBJETO -> CSV) ---
    public void salvar(LogAcesso log) {
        // 1. Atualiza memória e ID
        log.setId(proximoId++);
        bancoLogs.add(log);

        // 2. Prepara a linha
        // Layout: id;id_usuario;data_iso;acao
        String linha = log.getId() + ";" +
                log.getUsuario().getId() + ";" +
                log.getDataHora().toString() + ";" +
                log.getAcao();

        // 3. Grava no disco
        // Criamos uma lista com apenas 1 item para mandar pro CsvUtil
        List<String> novaLinhaLista = new ArrayList<>();
        novaLinhaLista.add(linha);

        CsvUtil.escreverArquivo(ARQUIVO, novaLinhaLista, true);

        System.out.println("Log gravado: " + log.getAcao());
    }

    // --- MÉTODOS DE CONSULTA ---

    public List<LogAcesso> listarTodos() {
        return new ArrayList<>(bancoLogs);
    }

    public List<LogAcesso> buscarPorUsuario(long idUsuario) {
        List<LogAcesso> resultado = new ArrayList<>();
        for (LogAcesso l : bancoLogs) {
            if (l.getUsuario().getId() == idUsuario) {
                resultado.add(l);
            }
        }
        return resultado;
    }
}