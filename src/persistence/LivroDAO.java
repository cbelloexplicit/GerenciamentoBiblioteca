package persistence;

import model.Genero;
import model.Livro;
import java.util.ArrayList;
import java.util.List;

public class LivroDAO {

    private static final String ARQUIVO = "dados/livros.csv";
    private static List<Livro> bancoLivros = new ArrayList<>();
    private static long proximoId = 1;

    static {
        List<String> linhas = CsvUtil.lerArquivo(ARQUIVO);
        if (linhas.isEmpty()) {
        } else {
            carregarDoArquivo(linhas);
        }
    }

    private static void carregarDoArquivo(List<String> linhas) {
        GeneroDAO generoDAO = new GeneroDAO();
        long maiorId = 0;

        for (String linha : linhas) {
            try {
                String[] dados = linha.split(";");
                // id;titulo;autor;id_genero;idade_min;total;disponivel

                long id = Long.parseLong(dados[0]);
                String titulo = dados[1];
                String autor = dados[2];
                long idGenero = Long.parseLong(dados[3]);
                int idade = Integer.parseInt(dados[4]);
                int total = Integer.parseInt(dados[5]);
                int disp = Integer.parseInt(dados[6]);

                // Busca o objeto Gênero real pelo ID salvo
                Genero g = generoDAO.buscarPorId(idGenero);
                if (g == null) g = new Genero(idGenero, "Gênero Desconhecido");

                Livro l = new Livro(id, titulo, autor, g, idade, total);

                while (l.getCopiasDisponiveis() > disp) {
                    l.dimCopiasDisponiveis();
                }

                bancoLivros.add(l);
                if (id > maiorId) maiorId = id;

            } catch (Exception e) {
                System.err.println("Erro linha livro: " + linha);
            }
        }
        proximoId = maiorId + 1;
    }

    private static void salvarEmArquivo() {
        List<String> linhas = new ArrayList<>();
        for (Livro l : bancoLivros) {
            StringBuilder sb = new StringBuilder();
            sb.append(l.getId()).append(";")
                    .append(l.getTitulo()).append(";")
                    .append(l.getAutor()).append(";")
                    .append(l.getGenero().getID()).append(";") // Salva só o ID do gênero
                    .append(l.getIdadeMinima()).append(";")
                    .append(l.getTotalCopias()).append(";")
                    .append(l.getCopiasDisponiveis());

            linhas.add(sb.toString());
        }
        CsvUtil.escreverArquivo(ARQUIVO, linhas, false);
    }

    public void salvar(Livro livro) {
        bancoLivros.removeIf(l -> l.getId() == livro.getId());
        if (livro.getId() == 0) livro.setId(proximoId++);
        bancoLivros.add(livro);
        salvarEmArquivo();
    }

    public void remover(long id) {
        bancoLivros.removeIf(l -> l.getId() == id);
        salvarEmArquivo();
    }

    // ... (métodos de busca mantidos iguais) ...
    public List<Livro> listarTodos() { return new ArrayList<>(bancoLivros); }
    public Livro buscarPorId(long id) { for(Livro l : bancoLivros) if(l.getId()==id) return l; return null; }
    public List<Livro> buscarPorTitulo(String t) {
        List<Livro> r = new ArrayList<>();
        for(Livro l: bancoLivros) if(l.getTitulo().toLowerCase().contains(t.toLowerCase())) r.add(l);
        return r;
    }
    public List<Livro> buscarPorAutor(String a) {
        List<Livro> r = new ArrayList<>();
        for(Livro l: bancoLivros) if(l.getAutor().toLowerCase().contains(a.toLowerCase())) r.add(l);
        return r;
    }
    public List<Livro> buscarPorGenero(Genero g) {
        List<Livro> r = new ArrayList<>();
        for(Livro l: bancoLivros) if(l.getGenero().getID() == g.getID()) r.add(l);
        return r;
    }
}