package Service;

import Exception.ValidacaoException;
import model.Aluno;
import model.Emprestimo;
import model.Livro;
import persistence.EmprestimoDAO;
import persistence.LivroDAO;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class EmprestimoService {
    private LogService logService;
    private EmprestimoDAO emprestimoDAO;
    private LivroDAO livroDAO;

    public EmprestimoService() {
        this.emprestimoDAO = new EmprestimoDAO();
        this.livroDAO = new LivroDAO();
        this.logService = new LogService();

    }

    //REGISTRAR EMPRÉSTIMO
    //Verifica estoque, cria o registro e atualiza a quantidade de livros.
    public void registrarEmprestimo(Aluno aluno, Livro livro) throws ValidacaoException {
        //Validações Básicas
        if (aluno == null) {
            throw new ValidacaoException("É obrigatório selecionar um aluno.");
        }
        if (livro == null) {
            throw new ValidacaoException("É obrigatório selecionar um livro.");
        }

        // Validação de Regra de Negócio: DISPONIBILIDADE
        // Recarrega o livro do banco para garantir que o dado de estoque está fresco
        Livro livroAtualizado = livroDAO.buscarPorId(livro.getId());

        if (livroAtualizado.getCopiasDisponiveis() <= 0) {
            throw new ValidacaoException("O livro '" + livroAtualizado.getTitulo() + "' não possui cópias disponíveis no momento.");
        }

        // Verificar se o aluno já tem livros atrasados
        List<Emprestimo> historicoAluno = emprestimoDAO.buscarPorAluno(aluno.getId());
        for (Emprestimo e : historicoAluno) {
            if (e.isAtrasado()) {
                throw new ValidacaoException("O aluno possui empréstimos atrasados. Regularize antes de pegar novos livros.");
            }
        }

        //Preparação
        int diasPrazo = 7; // Regra: 7 dias de prazo padrão
        Emprestimo novoEmprestimo = new Emprestimo(aluno, livroAtualizado, LocalDate.now(), diasPrazo);

        //Salva o empréstimo
        emprestimoDAO.salvar(novoEmprestimo);

        // Atualiza o estoque do livro (Decrementa)
        livroAtualizado.dimCopiasDisponiveis();
        livroDAO.salvar(livroAtualizado);
        logService.registrar("REALIZAR EMPRÉSTIMO: " + livro.getTitulo() + " para " + aluno.getNome());

        System.out.println("Empréstimo realizado: " + aluno.getNome() + " pegou " + livroAtualizado.getTitulo());
    }

    //REGISTRAR DEVOLUÇÃO
    //Finaliza o empréstimo, calcula multa (se houver) e repõe o estoque.
    //return Uma mensagem de status (ex: "Devolvido com sucesso" ou "Devolvido com MULTA de R$ 5,00")
    public String registrarDevolucao(long idEmprestimo) throws ValidacaoException {
        Emprestimo emprestimo = emprestimoDAO.buscarPorId(idEmprestimo);

        if (emprestimo == null) {
            throw new ValidacaoException("Empréstimo não encontrado.");
        }

        if (!emprestimo.isAberto()) {
            throw new ValidacaoException("Este empréstimo já foi finalizado anteriormente.");
        }

        //Marca a data de hoje como devolução
        emprestimo.registrarDevolucao(LocalDate.now());

        //Repõe o estoque do livro
        Livro livro = emprestimo.getLivro();
        // Recarrega o livro para garantir integridade
        Livro livroNoBanco = livroDAO.buscarPorId(livro.getId());
        if (livroNoBanco != null) {
            livroNoBanco.addCopiasDisponiveis();
            livroDAO.salvar(livroNoBanco);
        }

        //Verifica Atraso e Calcula Multa
        String mensagemResultado = "Devolução realizada com sucesso!";

        // A lógica de isAtrasado() usa a dataPrevista vs dataReal
        if (emprestimo.getDataDevolucaoReal().isAfter(emprestimo.getDataDevolucaoPrevista())) {
            long diasAtraso = ChronoUnit.DAYS.between(emprestimo.getDataDevolucaoPrevista(), emprestimo.getDataDevolucaoReal());
            double valorMulta = diasAtraso * 2.0; // R$ 2,00 por dia
            mensagemResultado = String.format("ATENÇÃO: Devolução com %d dias de atraso. Multa gerada: R$ %.2f", diasAtraso, valorMulta);

            System.out.println("Multa gerada para " + emprestimo.getAluno().getNome());
        }

        //Salva o empréstimo fechado
        emprestimoDAO.salvar(emprestimo);
        logService.registrar("RECEBER DEVOLUÇÃO: " + livro.getTitulo() + " (Aluno: " + emprestimo.getAluno().getNome() + ")");
        return mensagemResultado;
    }

    //RENOVAR EMPRÉSTIMO
    //Estende o prazo por mais 7 dias, se não estiver atrasado.
    public void renovarEmprestimo(long idEmprestimo) throws ValidacaoException {
        Emprestimo emprestimo = emprestimoDAO.buscarPorId(idEmprestimo);

        if (emprestimo == null || !emprestimo.isAberto()) {
            throw new ValidacaoException("Empréstimo inválido para renovação.");
        }

        if (emprestimo.isAtrasado()) {
            throw new ValidacaoException("Não é possível renovar um livro atrasado. Faça a devolução primeiro.");
        }

        LocalDate novaData = emprestimo.getDataDevolucaoPrevista().plusDays(7);
        emprestimo.setDataDevolucaoPrevista(novaData);
        System.out.println("Renovação solicitada (Implementar setter no Model para efetivar): Nova data " + novaData);
        Livro livro = emprestimo.getLivro();
        Aluno aluno = emprestimo.getAluno();
        emprestimoDAO.salvar(emprestimo);
        logService.registrar("RENOVAR EMPRÉSTIMO: " + livro.getTitulo() + " para " + aluno.getNome());
    }

    //Consultas

    public List<Emprestimo> listarTodos() {
        return emprestimoDAO.listarTodos();
    }

    public List<Emprestimo> listarPendentes() {
        return emprestimoDAO.buscarTodosEmAberto();
    }

    public List<Emprestimo> buscarHistoricoAluno(Aluno aluno) {
        if (aluno == null) return List.of();
        return emprestimoDAO.buscarPorAluno(aluno.getId());
    }
}
