package Service;
import model.Genero;
import model.Livro;
import persistence.GeneroDAO;
import persistence.LivroDAO;
import Exception.ValidacaoException;
import java.util.List;

public class GeneroService {

    private GeneroDAO generoDAO;
    private LogService logService;
    private LivroDAO livroDAO;

    public GeneroService() {
        this.generoDAO = new GeneroDAO();
        this.livroDAO = new LivroDAO();
        this.logService = new LogService();

    }

    //SALVAR GÊNERO
    public void salvar(Genero genero) throws ValidacaoException {

        // 1. Validação simples
        if (genero.getNome() == null || genero.getNome().trim().isEmpty()) {
            throw new ValidacaoException("O nome do gênero literário é obrigatório.");
        }

        // 2. Validação de Duplicidade (Não ter dois "Romance")
        // Se for um cadastro novo (ID=0), verifica se já existe o nome
        if (genero.getID() == 0 && generoDAO.existePorNome(genero.getNome())) {
            throw new ValidacaoException("Já existe um gênero cadastrado com este nome.");
        }
        boolean novo = (genero.getID() == 0);
        generoDAO.salvar(genero);

        // --- LOG AQUI ---
        String acao = novo ? "CADASTRAR GENERO" : "EDITAR GENERO";
        logService.registrar(acao + ": " + genero.getNome());
    }

    //REMOVER GÊNERO
    public void remover(long id) throws ValidacaoException {
        Genero genero = generoDAO.buscarPorId(id);

        if (genero == null) {
            throw new ValidacaoException("Gênero não encontrado.");
        }

        //TRAVA DE SEGURANÇA
        // Verifica se existem livros usando este gênero
        List<Livro> livrosDoGenero = livroDAO.buscarPorGenero(genero);

        if (!livrosDoGenero.isEmpty()) {
            throw new ValidacaoException(
                    "Não é possível excluir o gênero '" + genero.getNome() +
                            "' pois existem " + livrosDoGenero.size() + " livros associados a ele no acervo."
            );
        }
        Genero l = generoDAO.buscarPorId(id); // Pega o nome antes de apagar
        logService.registrar("EXCLUIR GENERO: " + (l != null ? l.getNome() : id));
        // Se ninguém usa, pode apagar
        generoDAO.remover(id);
    }

    //consulta

    public List<Genero> listarTodos() {
        return generoDAO.listarTodos();
    }

    public Genero buscarPorId(long id) {
        return generoDAO.buscarPorId(id);
    }
}