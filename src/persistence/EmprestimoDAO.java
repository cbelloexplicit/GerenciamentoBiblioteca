package persistence;

import model.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EmprestimoDAO {

    private static List<Emprestimo> bancoEmprestimos = new ArrayList<>();
    private static long proximoId = 1;

    // --- SEED INICIAL (Dados de Teste) ---
    static {
        // Precisamos de alunos e livros reais para criar emprestimos
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        LivrosDAO livroDAO = new LivrosDAO();

        // Recupera o aluno "João Aluno" (ID 4 conforme o UsuarioDAO anterior)
        Usuario u = usuarioDAO.buscarPorId(4);
        Aluno aluno = (u instanceof Aluno) ? (Aluno) u : null;

        // Recupera livros
        Livro livro1 = livroDAO.buscarPorId(1); // Duna
        Livro livro2 = livroDAO.buscarPorId(2); // Dom Casmurro
        Livro livro3 = livroDAO.buscarPorId(3); // Matemática

        if (aluno != null && livro1 != null && livro2 != null && livro3 != null) {

            // Cenario 1: Empréstimo NORMAL (Em aberto, feito hoje, prazo de 7 dias)
            salvarFake(new Emprestimo(aluno, livro1, LocalDate.now(), 7));

            // Cenario 2: Empréstimo ATRASADO (Feito há 20 dias, prazo era 7 dias)
            // LocalDate.now().minusDays(20) cria uma data no passado
            Emprestimo atrasado = new Emprestimo(aluno, livro2, LocalDate.now().minusDays(20), 7);
            salvarFake(atrasado);

            // Cenario 3: Empréstimo FINALIZADO (Já devolvido)
            Emprestimo devolvido = new Emprestimo(aluno, livro3, LocalDate.now().minusDays(10), 7);
            devolvido.registrarDevolucao(LocalDate.now()); // Devolveu hoje
            salvarFake(devolvido);
        }
    }

    private static void salvarFake(Emprestimo e) {
        e.setId(proximoId++);
        bancoEmprestimos.add(e);
    }

    // --- MÉTODOS CRUD BÁSICOS ---

    public void salvar(Emprestimo emprestimo) {
        if (emprestimo.getId() == 0) {
            emprestimo.setId(proximoId++);
        }
        // Se for edição, remove o antigo para substituir (simplificação para List)
        remover(emprestimo.getId());
        bancoEmprestimos.add(emprestimo);
        System.out.println("Empréstimo salvo/atualizado: ID " + emprestimo.getId());
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

    public void remover(long id) {
        bancoEmprestimos.removeIf(e -> e.getId() == id);
    }

    // --- CONSULTAS ESPECÍFICAS (Regras de Negócio) ---

    // 1. Buscar apenas o que está pendente (para o Bibliotecário cobrar)
    public List<Emprestimo> buscarTodosEmAberto() {
        List<Emprestimo> abertos = new ArrayList<>();
        for (Emprestimo e : bancoEmprestimos) {
            if (e.isAberto()) {
                abertos.add(e);
            }
        }
        return abertos;
    }

    // 2. Buscar histórico de um aluno específico (Painel do Aluno)
    public List<Emprestimo> buscarPorAluno(long idAluno) {
        List<Emprestimo> doAluno = new ArrayList<>();
        for (Emprestimo e : bancoEmprestimos) {
            if (e.getAluno().getId() == idAluno) {
                doAluno.add(e);
            }
        }
        return doAluno;
    }

    // 3. Buscar histórico de um livro (saber quem já leu)
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