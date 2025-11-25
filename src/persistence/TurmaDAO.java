package persistence;

import model.Professor;
import model.Turma;
import model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class TurmaDAO {

    private static final String ARQUIVO = "dados/turmas.csv";
    private static List<Turma> bancoTurmas =new ArrayList<>();
    private static long proximoId = 1;

    // --- CARGA INICIAL ---
    static {
        List<String> linhas = CsvUtil.lerArquivo(ARQUIVO);
        if (!linhas.isEmpty()) {
            carregarDoArquivo(linhas);
        } else {
            System.out.println("Arquivo de turmas vazio ou inexistente.");
        }
    }

    // --- LÓGICA DE CARREGAR (CSV -> OBJETO) ---
    private static void carregarDoArquivo(List<String> linhas) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        long maiorId = 0;

        for (String linha : linhas) {
            try {
                String[] dados = linha.split(";");
                // Layout: id;nome;ano;id_professor

                long id = Long.parseLong(dados[0]);
                String nome = dados[1];
                int ano = Integer.parseInt(dados[2]);
                long idProfessor = Long.parseLong(dados[3]);

                // Busca o professor pelo ID salvo
                Usuario u = usuarioDAO.buscarPorId(idProfessor);

                // Valida se o usuário existe e se é mesmo um Professor
                if (u instanceof Professor) {
                    Turma t = new Turma(id, nome, ano, (Professor) u);
                    bancoTurmas.add(t);

                    if (id > maiorId) maiorId = id;
                } else {
                    System.err.println("Aviso: Turma ID " + id + " ignorada pois o professor ID " + idProfessor + " não foi encontrado.");
                }

            } catch (Exception e) {
                System.err.println("Erro ao ler linha de turma: " + linha);
            }
        }
        proximoId = maiorId + 1;
    }

    // --- LÓGICA DE SALVAR (OBJETO -> CSV) ---
    private static void salvarEmArquivo() {
        List<String> linhas = new ArrayList<>();

        for (Turma t : bancoTurmas) {
            // Layout: id;nome;ano;id_professor
            StringBuilder sb = new StringBuilder();
            sb.append(t.getId()).append(";")
                    .append(t.getNome()).append(";")
                    .append(t.getAnoLetivo()).append(";")
                    .append(t.getProfessorResponsavel().getId());

            linhas.add(sb.toString());
        }

        CsvUtil.escreverArquivo(ARQUIVO, linhas, false);
    }

    // --- CRUD ---

    public void salvar(Turma turma) {
        // Se for edição, remove a antiga
        bancoTurmas.removeIf(t -> t.getId() == turma.getId());

        if (turma.getId() == 0) {
            turma.setId(proximoId++);
        }

        bancoTurmas.add(turma);
        salvarEmArquivo();

        System.out.println("Turma '" + turma.getNome() + "' gravada no CSV.");
    }

    public void remover(long id) {
        bancoTurmas.removeIf(t -> t.getId() == id);
        salvarEmArquivo();
    }

    public List<Turma> listarTodas() {
        return new ArrayList<>(bancoTurmas);
    }

    public Turma buscarPorId(long id) {
        for (Turma t : bancoTurmas) {
            if (t.getId() == id) return t;
        }
        return null;
    }

    public Turma buscarPorNome(String nome) {
        for (Turma t : bancoTurmas) {
            if (t.getNome().equalsIgnoreCase(nome)) return t;
        }
        return null;
    }

    public List<Turma> buscarPorProfessor(long idProfessor) {
        List<Turma> resultado = new ArrayList<>();
        for (Turma t : bancoTurmas) {
            if (t.getProfessorResponsavel().getId() == idProfessor) {
                resultado.add(t);
            }
        }
        return resultado;
    }
}
