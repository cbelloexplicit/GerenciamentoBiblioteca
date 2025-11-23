package persistence;

import model.Aluno;
import model.Emprestimo;
import model.Livro;
import model.Usuario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmprestimoDAO {

    private static final String ARQUIVO = "dados/emprestimos.csv";
    private static List<Emprestimo> bancoEmprestimos = new ArrayList<>();
    private static long proximoId = 1;

    // --- CARGA INICIAL (Estático) ---
    static {
        List<String> linhas = CsvUtil.lerArquivo(ARQUIVO);
        if (!linhas.isEmpty()) {
            carregarDoArquivo(linhas);
        } else {
            System.out.println("Arquivo de empréstimos vazio ou inexistente.");
        }
    }

    // --- LÓGICA DE CARREGAR (CSV -> OBJETO) ---
    private static void carregarDoArquivo(List<String> linhas) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        LivroDAO livroDAO = new LivroDAO();

        long maiorId = 0;

        for (String linha : linhas) {
            try {
                String[] dados = linha.split(";");
                // Layout: id;id_aluno;id_livro;data_emp;data_prev;data_real

                long id = Long.parseLong(dados[0]);
                long idAluno = Long.parseLong(dados[1]);
                long idLivro = Long.parseLong(dados[2]);
                LocalDate dataEmp = LocalDate.parse(dados[3]);
                LocalDate dataPrev = LocalDate.parse(dados[4]);

                LocalDate dataReal = null;
                if (!dados[5].equals("null")) {
                    dataReal = LocalDate.parse(dados[5]);
                }

                // Reconstrói as dependências
                Usuario u = usuarioDAO.buscarPorId(idAluno);
                Livro l = livroDAO.buscarPorId(idLivro);

                // Só cria se o aluno e o livro existirem (Integridade Referencial)
                if (u instanceof Aluno && l != null) {
                    Emprestimo e = new Emprestimo(id, (Aluno) u, l, dataEmp, dataPrev, dataReal);
                    bancoEmprestimos.add(e);

                    if (id > maiorId) maiorId = id;
                }

            } catch (Exception e) {
                System.err.println("Erro ao ler linha de empréstimo: " + linha);
            }
        }
        proximoId = maiorId + 1;
    }

    // --- LÓGICA DE SALVAR (OBJETO -> CSV) ---
    private static void salvarEmArquivo() {
        List<String> linhas = new ArrayList<>();

        for (Emprestimo e : bancoEmprestimos) {
            StringBuilder sb = new StringBuilder();

            // Layout: id;id_aluno;id_livro;data_emp;data_prev;data_real
            sb.append(e.getId()).append(";")
                    .append(e.getAluno().getId()).append(";")
                    .append(e.getLivro().getId()).append(";")
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

    public List<Emprestimo> buscarPorLivro(long idLivro) {
        List<Emprestimo> doLivro = new ArrayList<>();
        for (Emprestimo e : bancoEmprestimos) {
            if (e.getLivro().getId() == idLivro) {
                doLivro.add(e);
            }
        }
        return doLivro;
    }
}

