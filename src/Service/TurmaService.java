package Service;

import Exception.ValidacaoException;
import model.Aluno;
import model.Professor;
import model.Turma;
import model.Usuario;
import persistence.TurmaDAO;
import persistence.UsuarioDAO;

import java.time.Year;
import java.util.List;

public class TurmaService {

    private TurmaDAO turmaDAO;
    private UsuarioDAO usuarioDAO;
    private LogService logService;


    public TurmaService() {
        this.turmaDAO = new TurmaDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.logService = new LogService();

    }

    //SALVAR OU ATUALIZAR TURMA
    //Validações: Nome, Ano e Professor Responsável.
    public void salvar(Turma turma) throws ValidacaoException {

        // Validações de Campos
        if (turma.getNome() == null || turma.getNome().trim().isEmpty()) {
            throw new ValidacaoException("O nome da turma é obrigatório (Ex: '3º Ano A').");
        }

        if (turma.getAnoLetivo() < 2000 || turma.getAnoLetivo() > Year.now().getValue() + 1) {
            throw new ValidacaoException("O ano letivo informado é inválido.");
        }

        if (turma.getProfessorResponsavel() == null) {
            throw new ValidacaoException("É necessário atribuir um professor responsável à turma.");
        }

        // O usuário atribuído é mesmo um professor
        if (!(turma.getProfessorResponsavel() instanceof Professor)) {
            throw new ValidacaoException("O usuário selecionado para a turma não é um Professor.");
        }

        // Validação de Duplicidade (Nome + Ano)
        Turma existente = turmaDAO.buscarPorNome(turma.getNome());
        if (existente != null && existente.getId() != turma.getId() && existente.getAnoLetivo() == turma.getAnoLetivo()) {
            throw new ValidacaoException("Já existe uma turma com este nome neste ano letivo.");
        }
        boolean novo = (turma.getId() == 0);
        turmaDAO.salvar(turma);

        // --- LOG AQUI ---
        String acao = novo ? "CADASTRAR TURMA" : "EDITAR TURMA";
        logService.registrar(acao + ": " + turma.getNome());
    }

    //remover
    public void remover(long id) throws ValidacaoException {
        Turma turma = turmaDAO.buscarPorId(id);
        if (turma == null) {
            throw new ValidacaoException("Turma não encontrada.");
        }

        // Regra de Integridade: Não apagar turma se houver alunos vinculados a ela
        List<Usuario> alunos = usuarioDAO.listarApenasAlunos();
        for (Usuario u : alunos) {
            Aluno aluno = (Aluno) u;
            if (aluno.getTurma() != null && aluno.getTurma().equalsIgnoreCase(turma.getNome())) {
                throw new ValidacaoException("Não é possível remover a turma pois existem alunos matriculados nela.");
            }
        }
        Turma l = turmaDAO.buscarPorId(id); // Pega o nome antes de apagar
        turmaDAO.remover(id);

        logService.registrar("EXCLUIR TURMA: " + (l != null ? l.getNome() : id));
    }

    //CARREGAR ALUNOS DA TURMA
    public Turma carregarAlunos(Turma turma) {
        if (turma == null) return null;

        turma.getAlunos().clear();

        // Busca todos os alunos do sistema
        List<Usuario> todosAlunos = usuarioDAO.listarApenasAlunos();

        for (Usuario u : todosAlunos) {
            Aluno aluno = (Aluno) u;
            if (aluno.getTurma() != null && aluno.getTurma().equalsIgnoreCase(turma.getNome())) {
                turma.adicionarAluno(aluno);
            }
        }

        return turma;
    }

    //Consultas

    public List<Turma> listarTodas() {
        return turmaDAO.listarTodas();
    }

    public Turma buscarPorId(long id) {
        Turma t = turmaDAO.buscarPorId(id);
        return carregarAlunos(t);
    }

    public Turma buscarPorNome(String nome) {
        Turma t = turmaDAO.buscarPorNome(nome);
        return carregarAlunos(t);
    }

    public List<Turma> buscarPorProfessor(Professor professor) {
        if (professor == null) return List.of();

        List<Turma> turmas = turmaDAO.buscarPorProfessor(professor.getId());

        for (Turma t : turmas) {
            carregarAlunos(t);
        }

        return turmas;
    }
}