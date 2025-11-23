package persistence;

import model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    private static List<Usuario> bancoUsuarios = new ArrayList<>();
    // Simulador de Auto-Increment do banco
    private static long proximoId = 1;

    // Assim que rodar o programa, esse usuário já existe.
    static {
        criarUsuarioFake(new Administrador("Admin Principal", "admin", "123"));
        criarUsuarioFake(new Bibliotecario("Ana Biblio", "biblio", "123"));
        criarUsuarioFake(new Professor("Prof. Carlos", "prof", "123"));
        criarUsuarioFake(new Aluno("João Aluno", "aluno", "123", "3A", LocalDate.of(2010, 5, 20)));
    }
    private static void criarUsuarioFake(Usuario u) {
        u.setId(proximoId++);
        bancoUsuarios.add(u);
    }
    // --- CRUD (Create, Read, Update, Delete) ---
    public void salvar(Usuario usuario) {
        // Simula a gerar ID do banco
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
        return null; // Não encontrou
    }

    public Usuario buscarPorId(long id) {
        for (Usuario u : bancoUsuarios) {
            if (u.getId() == id) {
                return u;
            }
        }
        return null;
    }
    public Usuario buscarPorNome(String nome) {
        for (Usuario u : bancoUsuarios) {
            if (u.getNome().toUpperCase().contains(nome.toUpperCase())) {
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

    //Listar apenas
    public List<Usuario> listarApenasAlunos() {
        List<Usuario> alunos = new ArrayList<>();
        for (Usuario u : bancoUsuarios) {
            if (u instanceof Aluno) { // Verifica o tipo
                alunos.add(u);
            }
        }
        return alunos;
    }
    public List<Usuario> listarApenasProfessores() {
        List<Usuario> professores = new ArrayList<>();
        for (Usuario u : bancoUsuarios) {
            if (u instanceof Professor) { // Verifica o tipo
                professores.add(u);
            }
        }
        return professores;
    }
    public List<Usuario> listarApenasAdmin() {
        List<Usuario> admins = new ArrayList<>();
        for (Usuario u : bancoUsuarios) {
            if (u instanceof Administrador) { // Verifica o tipo
                admins.add(u);
            }
        }
        return admins;
    }
    public List<Usuario> listarApenasBibliotec() {
        List<Usuario> bibliotecarios = new ArrayList<>();
        for (Usuario u : bancoUsuarios) {
            if (u instanceof Bibliotecario) { // Verifica o tipo
                bibliotecarios.add(u);
            }
        }
        return bibliotecarios;
    }
}
