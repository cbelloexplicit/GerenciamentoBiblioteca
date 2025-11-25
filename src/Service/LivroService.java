package Service;

import Exception.ValidacaoException;
import model.Exemplar;
import model.Genero;
import model.Livro;
import persistence.ExemplarDAO;
import persistence.LivroDAO;

import java.util.List;

public class LivroService {

    private LivroDAO livroDAO;
    private ExemplarDAO exemplarDAO; // Necessário para validar exclusão
    private LogService logService;

    public LivroService() {
        this.livroDAO = new LivroDAO();
        this.exemplarDAO = new ExemplarDAO();
        this.logService = new LogService();
    }

    // SALVAR (Apenas Metadados)
    public void salvar(Livro livro) throws ValidacaoException {

        // Validações de Campos Obrigatórios
        if (livro.getTitulo() == null || livro.getTitulo().trim().isEmpty()) {
            throw new ValidacaoException("O título do livro é obrigatório.");
        }

        if (livro.getAutor() == null || livro.getAutor().trim().isEmpty()) {
            throw new ValidacaoException("O nome do autor é obrigatório.");
        }

        if (livro.getGenero() == null) {
            throw new ValidacaoException("É necessário selecionar um gênero literário.");
        }

        if (livro.getIdadeMinima() < 0) {
            throw new ValidacaoException("A idade mínima não pode ser negativa.");
        }

        boolean novo = (livro.getId() == 0);
        livroDAO.salvar(livro);

        String acao = novo ? "CADASTRAR LIVRO" : "EDITAR LIVRO";
        logService.registrar(acao + ": " + livro.getTitulo());
    }

    // REMOVER
    public void remover(long id) throws ValidacaoException {
        Livro livro = livroDAO.buscarPorId(id);

        if (livro == null) {
            throw new ValidacaoException("Livro não encontrado.");
        }

        // Validação de Integridade:
        // Não podemos apagar o "Titulo" se existem exemplares físicos dele cadastrados.
        List<Exemplar> exemplaresFisicos = exemplarDAO.buscarPorLivro(id);

        if (!exemplaresFisicos.isEmpty()) {
            throw new ValidacaoException(
                    "Não é possível remover o livro '" + livro.getTitulo() +
                            "' pois existem " + exemplaresFisicos.size() + " exemplares físicos cadastrados.\n" +
                            "Remova os exemplares primeiro."
            );
        }

        Livro l = livroDAO.buscarPorId(id);
        livroDAO.remover(id);

        logService.registrar("EXCLUIR LIVRO: " + (l != null ? l.getTitulo() : id));
    }

    // CONSULTAS

    public List<Livro> listarTodos() {
        return livroDAO.listarTodos();
    }

    public Livro buscarPorId(long id) {
        return livroDAO.buscarPorId(id);
    }

    public List<Livro> buscarPorTitulo(String termo) {
        if (termo == null || termo.isEmpty()) return listarTodos();
        return livroDAO.buscarPorTitulo(termo);
    }

    public List<Livro> buscarPorAutor(String autor) {
        if (autor == null || autor.isEmpty()) return listarTodos();
        return livroDAO.buscarPorAutor(autor);
    }

    public List<Livro> buscarPorGenero(Genero genero) {
        if (genero == null) return listarTodos();
        return livroDAO.buscarPorGenero(genero);
    }
}