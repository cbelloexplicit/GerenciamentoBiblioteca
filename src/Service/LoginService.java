package Service;

import persistence.UsuarioDAO;
import model.Usuario;
import Exception.AutenticacaoException;

public class LoginService {
    private UsuarioDAO usuarioDAO;
    private LogService LogService;

    public LoginService() {
        this.usuarioDAO = new UsuarioDAO();
        this.LogService = new LogService();
    }

    //login
    public Usuario logar(String matricula, String senha) throws AutenticacaoException {
        //validacao de campo vazio
        if (matricula == null || matricula.trim().isEmpty()) {
            throw new AutenticacaoException("Por favor, informe a matrícula.");
        }
        if (senha == null || senha.trim().isEmpty()) {
            throw new AutenticacaoException("Por favor, informe a senha.");
        }

        // 2. Busca o usuário no banco de dados (DAO)
        Usuario usuario = usuarioDAO.buscarPorMatricula(matricula);

        // 3. Verifica credenciais (Se usuário existe E se a senha bate)
        if (usuario == null || !usuario.getSenha().equals(senha)) {
            throw new AutenticacaoException("Usuário ou senha incorretos.");
        }

        // 4. Verifica status do cadastro
        if (!usuario.isAtivo()) {
            throw new AutenticacaoException("Acesso negado. Usuário desativado.");
        }
        //5. sucessso -> registra log
        try {
            LogService.registrarAtividade(usuario, "LOGIN");
        } catch (Exception e) {
            System.out.println("Erro ao gravar log: " + e.getMessage());
        }
        return usuario;
    }
    //logout
    public void realizarLogout(Usuario usuario) {
        if (usuario != null) {
            LogService.registrarAtividade(usuario, "LOGOUT");
            System.out.println("Logout realizado para: " + usuario.getNome());
        }
    }
}

