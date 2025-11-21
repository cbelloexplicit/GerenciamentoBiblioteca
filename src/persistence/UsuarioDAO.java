package persistence;
import model.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private static List<Usuario> bancoUsuarios = new ArrayList<>();
    private static long proximoId = 1;

    // Bloco estático para iniciar com dados
    static {
        criarUsuarioFake(new Administrador("Admin Principal", "admin", "123"));
        criarUsuarioFake(new Bibliotecario("Ana Biblio", "biblio", "123"));
        criarUsuarioFake(new Professor("Prof. Carlos", "prof", "123"));
        criarUsuarioFake(new Aluno("João Aluno", "aluno", "123", "3A"));
    }

    // auxiliar
    private static void criarUsuarioFake(Usuario u) {
        u.setId(proximoId++);
        bancoUsuarios.add(u);
    }

    // --- MÉTODOS CRUD ---

    public void salvar(Usuario usuario) {
        // Simula a geração de ID do banco
        if (usuario.getId() == 0) {
            usuario.setId(proximoId++);
        }
        bancoUsuarios.add(usuario);
        System.out.println("DAO: Usuário salvo: " + usuario.getNome());
    }

    public Usuario buscarPorMatricula(String matricula) {
        for (Usuario u : bancoUsuarios) {
            if (u.getMatricula().equals(matricula)) {
                return u;
            }
        }
        return null;
    }

    public Usuario buscarPorId(long id) {
        for (Usuario u : bancoUsuarios) {
            if (u.getId() == id) {
                return u;
            }
        }
        return null;
    }

    public List<Usuario> listarTodos() {
        // Retorna uma cópia para proteger a lista original
        return new ArrayList<>(bancoUsuarios);
    }

    public void remover(long id) {
        bancoUsuarios.removeIf(u -> u.getId() == id);
    }

    public List<Usuario> listarApenasAlunos() {
        List<Usuario> alunos = new ArrayList<>();
        for (Usuario u : bancoUsuarios) {
            if (u instanceof Aluno) { // Verifica o tipo
                alunos.add(u);
            }
        }
        return alunos;
    }
}