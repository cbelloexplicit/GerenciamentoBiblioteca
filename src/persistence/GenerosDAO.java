package persistence;

import model.Genero;
import java.util.ArrayList;
import java.util.List;

public class GenerosDAO {

    private static List<Genero> bancoGeneros = new ArrayList<>();
    private static long proximoId = 1;

    // --- SEED INICIAL (Pré-instalação) ---
    static {
        salvarSample(new Genero("Ficção Científica"));
        salvarSample(new Genero("Romance"));
        salvarSample(new Genero("Terror"));
        salvarSample(new Genero("Didático"));
        salvarSample(new Genero("História"));
        salvarSample(new Genero("Fábula"));
        salvarSample(new Genero("Conto"));
        salvarSample(new Genero("Fantasia"));
    }

    private static void salvarSample(Genero g) {
        g.setId(proximoId++);
        bancoGeneros.add(g);
    }

    // --- MÉTODOS CRUD ---

    public void salvar(Genero genero) {
        if (genero.getId() == 0) {
            genero.setId(proximoId++);
        }
        bancoGeneros.add(genero);
    }

    public List<Genero> listarTodos() {
        return new ArrayList<>(bancoGeneros);
    }

    public Genero buscarPorId(long id) {
        for (Genero g : bancoGeneros) {
            if (g.getId() == id) {
                return g;
            }
        }
        return null;
    }

    public void remover(long id) {
        bancoGeneros.removeIf(g -> g.getId() == id);
    }

    public boolean existePorNome(String nome) {
        for (Genero g : bancoGeneros) {
            if (g.getNome().equalsIgnoreCase(nome)) return true;
        }
        return false;
    }
}