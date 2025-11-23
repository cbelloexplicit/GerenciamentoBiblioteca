package Service;

import Exception.ValidacaoException;
import model.Genero;
import model.Livro;
import persistence.LivroDAO;

import java.util.List;

public class LivroService {

    private LivroDAO livroDAO;

    public LivroService() {
        this.livroDAO = new LivroDAO();
    }

    //salvar ou atualizar
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

        // Validações de Lógica

        if (livro.getIdadeMinima() < 0) {
            throw new ValidacaoException("A idade mínima não pode ser negativa.");
        }

        if (livro.getTotalCopias() < 0) {
            throw new ValidacaoException("O total de cópias não pode ser negativo.");
        }

        // Se for um cadastro novo (ID=0), exige pelo menos 1 cópia
        if (livro.getId() == 0 && livro.getTotalCopias() == 0) {
            throw new ValidacaoException("Para cadastrar um novo livro, informe pelo menos 1 cópia.");
        }

        //Atualização de Estoque
        // Se o usuário editar o total de cópias, ajustar as cópias disponíveis
        if (livro.getId() > 0) {
            Livro antigo = livroDAO.buscarPorId(livro.getId());
            if (antigo != null) {
                int diferenca = livro.getTotalCopias() - antigo.getTotalCopias();
                // Ajusta o disponível somando a diferença (pode ser negativa se reduziu o acervo)
                int novoDisponivel = antigo.getCopiasDisponiveis() + diferenca;

                if (novoDisponivel < 0) {
                    throw new ValidacaoException("Não é possível reduzir o acervo pois há livros emprestados.");
                }
            }
        }

        // Gravação
        livroDAO.salvar(livro);
    }

    //remover
    public void remover(long id) throws ValidacaoException {
        Livro livro = livroDAO.buscarPorId(id);

        if (livro == null) {
            throw new ValidacaoException("Livro não encontrado.");
        }

        // Validação Importante: Não apagar livro se todos os exemplares não estiverem na biblioteca
        if (livro.getCopiasDisponiveis() < livro.getTotalCopias()) {
            throw new ValidacaoException("Não é possível remover o livro pois há exemplares emprestados.");
        }

        livroDAO.remover(id);
    }

    //CONSULTA

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

    // Verifica disponibilidade (usado pelo Bibliotecário)
    public boolean verificarDisponibilidade(long idLivro) {
        Livro l = livroDAO.buscarPorId(idLivro);
        return l != null && l.getCopiasDisponiveis() > 0;
    }
}