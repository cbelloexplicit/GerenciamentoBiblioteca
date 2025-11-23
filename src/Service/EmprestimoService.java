package Service;

import Exception.ValidacaoException;
import model.Aluno;
import model.Emprestimo;
import model.Exemplar;
import model.Livro;
import persistence.EmprestimoDAO;
import persistence.LivroDAO;
import persistence.ExemplarDAO;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class EmprestimoService {
    private LogService logService;
    private EmprestimoDAO emprestimoDAO;
    private ExemplarDAO exemplarDAO;

    public EmprestimoService() {
        this.emprestimoDAO = new EmprestimoDAO();
        this.exemplarDAO = new ExemplarDAO();
        this.logService = new LogService();
    }

    public void registrarEmprestimo(Aluno aluno, Exemplar exemplar, int diasPrazo) throws ValidacaoException {
        // Validações Básicas
        if (aluno == null) throw new ValidacaoException("Aluno obrigatório.");
        if (exemplar == null) throw new ValidacaoException("Exemplar obrigatório.");

        // 1. Verificar disponibilidade física
        if (!exemplar.isDisponivel()) {
            throw new ValidacaoException("Este exemplar (ID " + exemplar.getId() + ") já está emprestado.");
        }

        // 2. Verificar se está reservado (Regra do Programa de Leitura)
        if (exemplar.isReservado()) {
            System.out.println("AVISO: Emprestando exemplar reservado.");
        }

        // 3. Verificar atrasos do aluno
        List<Emprestimo> historico = emprestimoDAO.buscarPorAluno(aluno.getId());
        for (Emprestimo e : historico) {
            if (e.getDataDevolucaoReal() == null && LocalDate.now().isAfter(e.getDataDevolucaoPrevista())) {
                throw new ValidacaoException("Aluno com pendências. Regularize antes.");
            }
        }

        // Validação do Prazo
        if (diasPrazo <= 0) throw new ValidacaoException("O prazo deve ser maior que zero.");

        // Criação do Empréstimo
        Emprestimo novo = new Emprestimo(aluno, exemplar, LocalDate.now(), diasPrazo);

        // Atualiza status do Exemplar
        exemplar.setDisponivel(false);
        // Se ele estava reservado e o aluno pegou, podemos tirar a reserva ou manter até devolver?
        // Geralmente, ao pegar, a reserva "sai" e vira empréstimo.
        exemplar.setReservado(false);

        // Salva tudo
        emprestimoDAO.salvar(novo);
        // exemplarDAO.salvar(exemplar); // Atualiza o CSV de exemplares

        logService.registrar("EMPRÉSTIMO: Exemplar " + exemplar.getId() + " (" + exemplar.getLivro().getTitulo() + ") para " + aluno.getNome());
    }

    public String registrarDevolucao(long idEmprestimo) throws ValidacaoException {
        Emprestimo emprestimo = emprestimoDAO.buscarPorId(idEmprestimo);
        if (emprestimo == null) throw new ValidacaoException("Empréstimo não encontrado.");

        // Baixa no empréstimo
        emprestimo.registrarDevolucao(LocalDate.now());

        // Devolve o exemplar ao estoque
        Exemplar ex = emprestimo.getExemplar();
        ex.setDisponivel(true);
        // ex.setReservado(false); // Já garantimos isso no empréstimo

        // exemplarDAO.salvar(ex);
        emprestimoDAO.salvar(emprestimo);

        // Lógica de Multa (Manteve igual)
        String msg = "Devolvido com sucesso.";
        if (emprestimo.getDataDevolucaoReal().isAfter(emprestimo.getDataDevolucaoPrevista())) {
            long dias = ChronoUnit.DAYS.between(emprestimo.getDataDevolucaoPrevista(), emprestimo.getDataDevolucaoReal());
            msg = "ATRASADO! Multa de R$ " + (dias * 2.0);
        }

        logService.registrar("DEVOLUÇÃO: " + ex.getId());
        return msg;
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
        Exemplar exemplar = emprestimo.getExemplar();
        Aluno aluno = emprestimo.getAluno();
        emprestimoDAO.salvar(emprestimo);
        logService.registrar("RENOVAR EMPRÉSTIMO: " + exemplar.getLivro().getTitulo() + " para " + aluno.getNome());
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
    public void atualizarEmprestimo(long idEmprestimo, LocalDate novaDataPrevista) throws ValidacaoException {
        Emprestimo e = emprestimoDAO.buscarPorId(idEmprestimo);
        if (e == null) throw new ValidacaoException("Empréstimo não encontrado.");

        if (novaDataPrevista.isBefore(e.getDataEmprestimo())) {
            throw new ValidacaoException("A data prevista não pode ser anterior à data do empréstimo.");
        }

        e.setDataDevolucaoPrevista(novaDataPrevista);
        emprestimoDAO.salvar(e);
        logService.registrar("EDITAR EMPRÉSTIMO (ID " + idEmprestimo + "): Nova data prevista " + novaDataPrevista);
    }
}
