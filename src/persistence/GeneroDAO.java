package persistence;

import model.Genero;
import java.util.ArrayList;
import java.util.List;

public class GeneroDAO {
    private static final String ARQUIVO = "dados/generos.csv";

    private static List<Genero> bancoGeneros = new ArrayList<>();
    private static long proximoId = 1;

    // --- CARGA INICIAL (Estático) ---
    static {
        List<String> linhas = CsvUtil.lerArquivo(ARQUIVO);
        if (!linhas.isEmpty()) {
            carregarDoArquivo(linhas);
        } else {
            System.out.println("Arquivo de gêneros vazio ou inexistente.");
        }
    }

    // --- LÓGICA DE CARREGAR (CSV -> OBJETO) ---
    private static void carregarDoArquivo(List<String> linhas) {
        long maiorId = 0;

        for (String linha : linhas) {
            try {
                // Layout: id;nome
                String[] dados = linha.split(";");

                long id = Long.parseLong(dados[0]);
                String nome = dados[1];

                Genero g = new Genero(id, nome);
                bancoGeneros.add(g);

                if (id > maiorId) maiorId = id;

            } catch (Exception e) {
                System.err.println("Erro ao ler linha de gênero: " + linha);
            }
        }
        proximoId = maiorId + 1;
    }

    // --- LÓGICA DE SALVAR (OBJETO -> CSV) ---
    private static void salvarEmArquivo() {
        List<String> linhas = new ArrayList<>();

        for (Genero g : bancoGeneros) {
            // Layout: id;nome
            String linha = g.getID() + ";" + g.getNome();
            linhas.add(linha);
        }

        // false = sobrescreve o arquivo todo com a lista atualizada
        CsvUtil.escreverArquivo(ARQUIVO, linhas, false);
    }

    // --- MÉTODOS CRUD ---

    public void salvar(Genero genero) {
        // Se for edição, remove o antigo da lista
        bancoGeneros.removeIf(g -> g.getID() == genero.getID());

        if (genero.getID() == 0) {
            genero.setID(proximoId++);
        }

        bancoGeneros.add(genero);
        salvarEmArquivo(); // Grava no disco

        System.out.println("Gênero '" + genero.getNome() + "' gravado no CSV.");
    }

    public void remover(long id) {
        bancoGeneros.removeIf(g -> g.getID() == id);
        salvarEmArquivo(); // Atualiza o disco removendo a linha
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

    public boolean existePorNome(String nome) {
        for (Genero g : bancoGeneros) {
            if (g.getNome().equalsIgnoreCase(nome)) return true;
        }
        return false;
    }
}