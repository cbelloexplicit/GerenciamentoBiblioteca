package persistence;

import model.Aluno;
import model.Emprestimo;
import model.Exemplar;
import model.Usuario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmprestimoDAO {

    private static final String ARQUIVO = "dados/emprestimos.csv";
    private static List<Emprestimo> bancoEmprestimos = new ArrayList<>();
    private static long proximoId = 1;

    // --- CARGA INICIAL ---
    static {
        List<String> linhas = CsvUtil.lerArquivo(ARQUIVO);
        if (!linhas.isEmpty()) {
            carregarDoArquivo(linhas);
        } else {
            System.out.println("Arquivo de empréstimos vazio ou inexistente. Criando novo banco.");
        }
    }

    // --- LÓGICA DE CARREGAR (CSV -> OBJETO) ---
    private static void carregarDoArquivo(List<String> linhas) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        ExemplarDAO exemplarDAO = new ExemplarDAO();

        long maiorId = 0;

        for (String linha : linhas) {
            try {
                if (linha.trim().isEmpty()) continue;

                String[] dados = linha.split(";");

                long id = Long.parseLong(dados[0]);
                long idAluno = Long.parseLong(dados[1]);
                long idExemplar = Long.parseLong(dados[2]); // Agora lemos o Exemplar

                LocalDate dataEmp = LocalDate.parse(dados[3]);
                LocalDate dataPrev = LocalDate.parse(dados[4]);

                LocalDate dataReal = null;
                if (dados.length > 5 && !dados[5].equals("null") && !dados[5].isEmpty()) {
                    dataReal = LocalDate.parse(dados[5]);
                }

                // Reconstrói as dependências
                Usuario u = usuarioDAO.buscarPorId(idAluno);
                Exemplar exemplar = exemplarDAO.buscarPorId(idExemplar);

                // Só cria se o aluno e o exemplar existirem
                if (u instanceof Aluno && exemplar != null) {
                    Emprestimo e = new Emprestimo(id, (Aluno) u, exemplar, dataEmp, dataPrev, dataReal);
                    bancoEmprestimos.add(e);

                    if (id > maiorId) maiorId = id;
                } else {
                    System.err.println("Aviso: Empréstimo ID " + id + " ignorado. Aluno ou Exemplar não encontrados.");
                }

            } catch (Exception e) {
                System.err.println("Erro ao ler linha de empréstimo: " + linha + " | Erro: " + e.getMessage());
            }
        }
        proximoId = maiorId + 1;
    }

    // --- LÓGICA DE SALVAR (OBJETO -> CSV) ---
    private static void salvarEmArquivo() {
        List<String> linhas = new ArrayList<>();

        for (Emprestimo e : bancoEmprestimos) {
            StringBuilder sb = new StringBuilder();

            sb.append(e.getId()).append(";")
                    .append(e.getAluno().getId()).append(";")
                    .append(e.getExemplar().getId()).append(";") // Salva ID do Exemplar
                    .append(e.getDataEmprestimo()).append(";")
                    .append(e.getDataDevolucaoPrevista()).append(";");

            // Trata data nula
            if (e.getDataDevolucaoReal() != null) {
                sb.append(e.getDataDevolucaoReal());
            } else {
                sb.append("null");
            }

            linhas.add(sb.toString());
        }

        CsvUtil.escreverArquivo(ARQUIVO, linhas, false);
    }

    // --- CRUD ---

    public void salvar(Emprestimo emprestimo) {
        // Se for edição, remove o antigo da lista
        bancoEmprestimos.removeIf(e -> e.getId() == emprestimo.getId());

        if (emprestimo.getId() == 0) {
            emprestimo.setId(proximoId++);
        }

        bancoEmprestimos.add(emprestimo);
        salvarEmArquivo(); // Grava no disco

        System.out.println("Empréstimo ID " + emprestimo.getId() + " gravado.");
    }

    public void remover(long id) {
        bancoEmprestimos.removeIf(e -> e.getId() == id);
        salvarEmArquivo();
    }

    public Emprestimo buscarPorId(long id) {
        for (Emprestimo e : bancoEmprestimos) {
            if (e.getId() == id) return e;
        }
        return null;
    }

    public List<Emprestimo> listarTodos() {
        return new ArrayList<>(bancoEmprestimos);
    }

    // --- CONSULTAS ESPECÍFICAS ---

    public List<Emprestimo> buscarTodosEmAberto() {
        List<Emprestimo> abertos = new ArrayList<>();
        for (Emprestimo e : bancoEmprestimos) {
            if (e.isAberto()) {
                abertos.add(e);
            }
        }
        return abertos;
    }

    public List<Emprestimo> buscarPorAluno(long idAluno) {
        List<Emprestimo> doAluno = new ArrayList<>();
        for (Emprestimo e : bancoEmprestimos) {
            if (e.getAluno().getId() == idAluno) {
                doAluno.add(e);
            }
        }
        return doAluno;
    }

    //Busca empréstimos vinculados a um Livro genérico.
    public List<Emprestimo> buscarPorLivro(long idLivro) {
        List<Emprestimo> doLivro = new ArrayList<>();
        for (Emprestimo e : bancoEmprestimos) {
            // Navega: Emprestimo -> Exemplar -> Livro -> ID
            if (e.getExemplar().getLivro().getId() == idLivro) {
                doLivro.add(e);
            }
        }
        return doLivro;
    }

    //Busca empréstimos de um Exemplar físico específico (código de barras).

    public List<Emprestimo> buscarPorExemplar(long idExemplar) {
        List<Emprestimo> doExemplar = new ArrayList<>();
        for (Emprestimo e : bancoEmprestimos) {
            if (e.getExemplar().getId() == idExemplar) {
                doExemplar.add(e);
            }
        }
        return doExemplar;
    }
}
