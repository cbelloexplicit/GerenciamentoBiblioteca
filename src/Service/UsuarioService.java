package Service;

import Exception.ValidacaoException;
import model.Administrador;
import model.Aluno;
import model.Usuario;
import persistence.UsuarioDAO;

import java.time.LocalDate;
import java.util.List;

public class UsuarioService {

    private UsuarioDAO usuarioDAO;
    private LogService logService;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
        this.logService = new LogService();
    }

    // SALVAR OU ATUALIZAR USUÁRIO
    public void salvar(Usuario usuario) throws ValidacaoException {
        // 1. Validação de Campos Obrigatórios
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new ValidacaoException("O nome do usuário é obrigatório.");
        }
        if (usuario.getMatricula() == null || usuario.getMatricula().trim().isEmpty()) {
            throw new ValidacaoException("A matrícula é obrigatória.");
        }
        if (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {
            throw new ValidacaoException("A senha é obrigatória.");
        }

        // 2. Validação de Duplicidade de Matrícula
        Usuario existente = usuarioDAO.buscarPorMatricula(usuario.getMatricula());

        // Se achou alguém E esse alguém NÃO sou eu mesmo (comparando IDs)
        if (existente != null && existente.getId() != usuario.getId()) {
            throw new ValidacaoException("Já existe um usuário cadastrado com a matrícula: " + usuario.getMatricula());
        }

        // 3. Validação Específica de Aluno (SE for aluno, valida data)
        if (usuario instanceof Aluno) {
            Aluno aluno = (Aluno) usuario;

            if (aluno.getDataNascimento() == null) {
                throw new ValidacaoException("Para alunos, a Data de Nascimento é obrigatória.");
            }

            if (aluno.getDataNascimento().isAfter(LocalDate.now())) {
                throw new ValidacaoException("A data de nascimento não pode ser no futuro.");
            }
        }

        boolean novo = (usuario.getId() == 0);
        usuarioDAO.salvar(usuario);

        String acao = novo ? "CADASTRAR USUARIO" : "EDITAR USUARIO";
        logService.registrar(acao + ": " + usuario.getNome() + " | " + usuario.getTipo());
    }

    //EXCLUIR USUÁRIO
    public void excluir(long id) throws ValidacaoException {
        Usuario usuarioAlvo = usuarioDAO.buscarPorId(id);

        if (usuarioAlvo == null) {
            throw new ValidacaoException("Usuário não encontrado para exclusão.");
        }

        if (usuarioAlvo instanceof Administrador) {
            long totalAdmins = contarAdministradores();
            if (totalAdmins <= 1) {
                throw new ValidacaoException("Não é possível excluir o único Administrador do sistema.");
            }
        }
        Usuario l = usuarioDAO.buscarPorId(id); // Pega o nome antes de apagar
        usuarioDAO.remover(id);

        // --- LOG AQUI ---
        logService.registrar("EXCLUIR USUARIO: " + (l != null ? l.getNome() : id));
    }

    //DESATIVAR USUÁRIO
    public void desativarUsuario(long id) throws ValidacaoException {
        Usuario u = usuarioDAO.buscarPorId(id);
        if (u != null) {
            if (u instanceof Administrador && contarAdministradores() <= 1 && u.isAtivo()) {
                throw new ValidacaoException("Não é possível desativar o único Administrador.");
            }
            u.inativarUsuario();
            usuarioDAO.salvar(u); // Salva o status alterado
        }
    }

    // Métodos de Consulta e Auxiliares
    public List<Usuario> listarTodos() { return usuarioDAO.listarTodos(); }
    public List<Usuario> listarApenasAlunos() { return usuarioDAO.listarApenasAlunos(); }
    public List<Usuario> listarApenasAdmin() { return usuarioDAO.listarApenasAdmin(); }
    public List<Usuario> listarApenasProf() { return usuarioDAO.listarApenasProf(); }
    public List<Usuario> listarApenasBibliotec() { return usuarioDAO.listarApenasBibliotec(); }

    public Usuario buscarPorId(long id) { return usuarioDAO.buscarPorId(id); }
    public Usuario buscarPorMatricula(String matricula) { return usuarioDAO.buscarPorMatricula(matricula); }

    private long contarAdministradores() {
        List<Usuario> todos = usuarioDAO.listarTodos();
        long count = 0;
        for (Usuario u : todos) {
            if (u instanceof Administrador && u.isAtivo()) {
                count++;
            }
        }
        return count;
    }
}