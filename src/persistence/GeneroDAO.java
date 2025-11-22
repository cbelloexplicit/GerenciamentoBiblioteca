package persistence;

import model.Genero;

import java.util.ArrayList;
import java.util.List;

public class GeneroDAO {
    private static List<Genero> bancoGeneros = new ArrayList<>();
    private static long proximoId = 1;

    // --- SEED INICIAL ---
    static {
        salvarFake(new Genero("Ficção Científica"));
        salvarFake(new Genero("Romance"));
        salvarFake(new Genero("Terror"));
        salvarFake(new Genero("Didático"));
        salvarFake(new Genero("História"));
        salvarFake(new Genero("Fantasia"));
    }

    private static void salvarFake(Genero g) {
        g.setID(proximoId++);
        bancoGeneros.add(g);
    }

    // --- MÉTODOS CRUD ---

    public void salvar(Genero genero) {
        if (genero.getID() == 0) {
            genero.setID(proximoId++);
        }
        bancoGeneros.add(genero);
    }

    public List<Genero> listarTodos() {
        return new ArrayList<>(bancoGeneros);
    }

    public Genero buscarPorId(long id) {
        for (Genero g : bancoGeneros) {
            if (g.getID() == id) {
                return g;
            }
        }
        return null;
    }
    public Genero buscarPorNome(String nome) {
        for (Genero g : bancoGeneros) {
            if (g.getNome().toUpperCase().contains(nome.toUpperCase())) {
                return g;
            }
        }
        return null;
    }

    public void remover(long id) {
        bancoGeneros.removeIf(g -> g.getID() == id);
    }

    public boolean existePorNome(String nome) {
        for (Genero g : bancoGeneros) {
            if (g.getNome().equalsIgnoreCase(nome))
                return true;
        }
        return false;
    }
}

