package persistence;

import model.Genero;
import model.Livro;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LivrosDAO {

    private static List<Livro> bancoLivros = new ArrayList<>();
    private static long proximoId = 1;

    // --- SEED INICIAL (Livros de Teste) ---
    static {
        // Precisamos buscar os Gêneros para associar aos livros
        GenerosDAO generoDAO = new GenerosDAO();

        // Recuperando gêneros pelo ID (assumindo que 1=Ficção, 2=Romance, etc. do passo anterior)
        Genero ficcao = generoDAO.buscarPorId(1);
        Genero romance = generoDAO.buscarPorId(2);
        Genero didatico = generoDAO.buscarPorId(4);

        // Criando livros apenas se os gêneros existirem (segurança)
        if (ficcao != null) {
            salvarFake(new Livro("Duna", "Frank Herbert", ficcao, 14, 5));
        }
        if (romance != null) {
            salvarFake(new Livro("Dom Casmurro", "Machado de Assis", romance, 12, 3));
        }
        if (didatico != null) {
            salvarFake(new Livro("Matemática 1", "Giovanni Jr", didatico, 10, 10));
        }
    }

    private static void salvarFake(Livro l) {
        l.setId(proximoId++);
        bancoLivros.add(l);
    }

    // --- MÉTODOS CRUD ---

    public void salvar(Livro livro) {
        if (livro.getId() == 0) {
            livro.setId(proximoId++);
        }
        bancoLivros.add(livro);
        System.out.println("Livro salvo: " + livro.getTitulo());
    }

    public List<Livro> listarTodos() {
        return new ArrayList<>(bancoLivros);
    }

    public Livro buscarPorId(long id) {
        for (Livro l : bancoLivros) {
            if (l.getId() == id) return l;
        }
        return null;
    }

    public void remover(long id) {
        bancoLivros.removeIf(l -> l.getId() == id);
    }

    // --- MÉTODOS DE BUSCA ESPECÍFICOS (Requisitos do Trabalho) ---

    // Busca aproximada (ex: pesquisar "Harry" acha "Harry Potter")
    public List<Livro> buscarPorTitulo(String termo) {
        List<Livro> resultado = new ArrayList<>();
        for (Livro l : bancoLivros) {
            if (l.getTitulo().toLowerCase().contains(termo.toLowerCase())) {
                resultado.add(l);
            }
        }
        return resultado;
    }

    public List<Livro> buscarPorAutor(String autor) {
        List<Livro> resultado = new ArrayList<>();
        for (Livro l : bancoLivros) {
            if (l.getAutor().toLowerCase().contains(autor.toLowerCase())) {
                resultado.add(l);
            }
        }
        return resultado;
    }

    // Filtro por Gênero (ex: listar todos de "Terror")
    public List<Livro> buscarPorGenero(Genero genero) {
        List<Livro> resultado = new ArrayList<>();
        for (Livro l : bancoLivros) {
            if (l.getGenero().getId() == genero.getId()) {
                resultado.add(l);
            }
        }
        return resultado;
    }
}