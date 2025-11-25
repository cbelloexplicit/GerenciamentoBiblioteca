package persistence;

import model.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProgramaLeituraDAO {

    private static final String ARQUIVO_HEADER = "dados/programas.csv";
    private static final String ARQUIVO_DETALHES = "dados/programa_detalhes.csv";

    private static List<ProgramaLeitura> bancoProgramas = new ArrayList<>();
    private static long proximoId = 1;

    // --- CARGA INICIAL ---
    static {
        List<String> headers = CsvUtil.lerArquivo(ARQUIVO_HEADER);
        List<String> detalhes = CsvUtil.lerArquivo(ARQUIVO_DETALHES);

        if (!headers.isEmpty()) {
            carregarDoArquivo(headers, detalhes);
        }
    }

    // --- LEITURA ---
    private static void carregarDoArquivo(List<String> headers, List<String> detalhes) {
        TurmaDAO turmaDAO = new TurmaDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        ExemplarDAO exemplarDAO = new ExemplarDAO();

        long maiorId = 0;

        // 1. Carregar Headers
        for (String linha : headers) {
            try {
                String[] dados = linha.split(";");
                // Layout: id;titulo;id_turma;data_inicio;data_fim;trimestre;ano

                long id = Long.parseLong(dados[0]);
                String titulo = dados[1];
                long idTurma = Long.parseLong(dados[2]);
                LocalDate inicio = LocalDate.parse(dados[3]);
                LocalDate fim = LocalDate.parse(dados[4]);
                int trimestre = Integer.parseInt(dados[5]);
                int ano = Integer.parseInt(dados[6]);

                Turma turma = turmaDAO.buscarPorId(idTurma);

                if (turma != null) {
                    ProgramaLeitura prog = new ProgramaLeitura(id, titulo, turma, inicio, fim, trimestre, ano);
                    bancoProgramas.add(prog);
                    if (id > maiorId) maiorId = id;
                }
            } catch (Exception e) {
                System.err.println("Erro header programa: " + linha);
            }
        }
        proximoId = maiorId + 1;

        // 2. Carregar Detalhes
        for (String linha : detalhes) {
            try {
                String[] dados = linha.split(";");
                // Layout id_programa;id_aluno;ID_EXEMPLAR

                long idPrograma = Long.parseLong(dados[0]);
                long idAluno = Long.parseLong(dados[1]);
                String idExemplarStr = dados[2];

                ProgramaLeitura programaPai = buscarNaListaPorId(idPrograma);

                if (programaPai != null) {
                    Usuario alunoUser = usuarioDAO.buscarPorId(idAluno);

                    if (alunoUser instanceof Aluno) {
                        Aluno aluno = (Aluno) alunoUser;
                        Exemplar exemplar = null;

                        if (!idExemplarStr.equals("null") && !idExemplarStr.isEmpty()) {
                            long idEx = Long.parseLong(idExemplarStr);
                            exemplar = exemplarDAO.buscarPorId(idEx);
                        }

                        // Cria a atribuição atualizada
                        AtribuicaoLeitura atribuicao = new AtribuicaoLeitura(aluno, exemplar);
                        programaPai.adicionarAtribuicao(atribuicao);
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro detalhe programa: " + linha);
            }
        }
    }

    // --- GRAVAÇÃO ---
    private static void salvarEmArquivo() {
        List<String> linhasHeader = new ArrayList<>();
        List<String> linhasDetalhes = new ArrayList<>();

        for (ProgramaLeitura p : bancoProgramas) {
            // 1. Header
            StringBuilder sb = new StringBuilder();
            sb.append(p.getId()).append(";")
                    .append(p.getTitulo()).append(";")
                    .append(p.getTurma().getId()).append(";")
                    .append(p.getDataInicio()).append(";")
                    .append(p.getDataFim()).append(";")
                    .append(p.getTrimestre()).append(";")
                    .append(p.getAno());

            linhasHeader.add(sb.toString());

            // 2. Detalhes
            for (AtribuicaoLeitura at : p.getAtribuicoes()) {
                // Layout: id_programa;id_aluno;ID_EXEMPLAR
                StringBuilder sbDet = new StringBuilder();
                sbDet.append(p.getId()).append(";")
                        .append(at.getAluno().getId()).append(";");

                if (at.getExemplar() != null) {
                    sbDet.append(at.getExemplar().getId());
                } else {
                    sbDet.append("null");
                }

                linhasDetalhes.add(sbDet.toString());
            }
        }

        CsvUtil.escreverArquivo(ARQUIVO_HEADER, linhasHeader, false);
        CsvUtil.escreverArquivo(ARQUIVO_DETALHES, linhasDetalhes, false);
    }

    // --- CRUD BÁSICO ---
    public void salvar(ProgramaLeitura programa) {
        bancoProgramas.removeIf(p -> p.getId() == programa.getId());
        if (programa.getId() == 0) programa.setId(proximoId++);
        bancoProgramas.add(programa);
        salvarEmArquivo();
    }

    public void remover(long id) {
        bancoProgramas.removeIf(p -> p.getId() == id);
        salvarEmArquivo();
    }

    public List<ProgramaLeitura> listarTodos() { return new ArrayList<>(bancoProgramas); }

    public ProgramaLeitura buscarPorId(long id) { return buscarNaListaPorId(id); }

    public List<ProgramaLeitura> buscarPorTurma(long idTurma) {
        List<ProgramaLeitura> resultado = new ArrayList<>();
        for (ProgramaLeitura p : bancoProgramas) {
            if (p.getTurma().getId() == idTurma) resultado.add(p);
        }
        return resultado;
    }

    private static ProgramaLeitura buscarNaListaPorId(long id) {
        for (ProgramaLeitura p : bancoProgramas) if (p.getId() == id) return p;
        return null;
    }
}