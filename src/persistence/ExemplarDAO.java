package persistence;

import model.Exemplar;
import model.Livro;

import java.util.ArrayList;
import java.util.List;

public class ExemplarDAO {

    private static final String ARQUIVO = "dados/exemplares.csv";
    private static List<Exemplar> bancoExemplares = new ArrayList<>();
    private static long proximoId = 1;

    // --- CARGA INICIAL ---
    static {
        List<String> linhas = CsvUtil.lerArquivo(ARQUIVO);
        if (!linhas.isEmpty()) {
            carregarDoArquivo(linhas);
        } else {
            System.out.println("Arquivo de exemplares vazio ou inexistente. Criando novo banco.");
        }
    }

    // --- LÓGICA DE CARREGAR (CSV -> OBJETO) ---
    private static void carregarDoArquivo(List<String> linhas) {
        LivroDAO livroDAO = new LivroDAO();
        long maiorId = 0;

        for (String linha : linhas) {
            try {
                if (linha.trim().isEmpty()) continue;

                String[] dados = linha.split(";");
                // Layout esperado: id;id_livro;disponivel;reservado

                long id = Long.parseLong(dados[0]);
                long idLivro = Long.parseLong(dados[1]);
                boolean disponivel = Boolean.parseBoolean(dados[2]);
                boolean reservado = Boolean.parseBoolean(dados[3]);

                // Busca o Livro
                Livro livro = livroDAO.buscarPorId(idLivro);

                if (livro != null) {
                    Exemplar exemplar = new Exemplar(id, livro, disponivel, reservado);
                    bancoExemplares.add(exemplar);

                    if (id > maiorId) maiorId = id;
                } else {
                    System.err.println("Aviso: Exemplar ID " + id + " ignorado pois o Livro ID " + idLivro + " não existe.");
                }

            } catch (Exception e) {
                System.err.println("Erro ao ler linha de exemplar: " + linha + " | " + e.getMessage());
            }
        }
        proximoId = maiorId + 1;
    }

    // --- LÓGICA DE SALVAR (OBJETO -> CSV) ---
    private static void salvarEmArquivo() {
        List<String> linhas = new ArrayList<>();

        for (Exemplar e : bancoExemplares) {
            StringBuilder sb = new StringBuilder();
            // Layout: id;id_livro;disponivel;reservado
            sb.append(e.getId()).append(";")
                    .append(e.getLivro().getId()).append(";")
                    .append(e.isDisponivel()).append(";")
                    .append(e.isReservado());

            linhas.add(sb.toString());
        }

        CsvUtil.escreverArquivo(ARQUIVO, linhas, false);
    }

    // --- MÉTODOS CRUD ---

    public void salvar(Exemplar exemplar) {
        // Se for edição, remove o antigo da lista
        bancoExemplares.removeIf(e -> e.getId() == exemplar.getId());

        if (exemplar.getId() == 0) {
            exemplar.setId(proximoId++);
        }

        bancoExemplares.add(exemplar);
        salvarEmArquivo(); // Grava no disco

        // Feedback no console
        System.out.println("Exemplar ID " + exemplar.getId() + " gravado.");
    }

    public void remover(long id) {
        bancoExemplares.removeIf(e -> e.getId() == id);
        salvarEmArquivo();
    }

    public Exemplar buscarPorId(long id) {
        for (Exemplar e : bancoExemplares) {
            if (e.getId() == id) return e;
        }
        return null;
    }

    public List<Exemplar> listarTodos() {
        return new ArrayList<>(bancoExemplares);
    }

    // --- CONSULTAS ESPECÍFICAS ---

    //Busca todos os exemplares físicos de um determinado Título (Livro).

    public List<Exemplar> buscarPorLivro(long idLivro) {
        List<Exemplar> resultado = new ArrayList<>();
        for (Exemplar e : bancoExemplares) {
            if (e.getLivro().getId() == idLivro) {
                resultado.add(e);
            }
        }
        return resultado;
    }

    //Busca apenas os exemplares que estão disponíveis para empréstimo imediato. (Não emprestados e não reservados)

    public List<Exemplar> buscarDisponiveisPorLivro(long idLivro) {
        List<Exemplar> resultado = new ArrayList<>();
        for (Exemplar e : bancoExemplares) {
            if (e.getLivro().getId() == idLivro && e.isDisponivel() && !e.isReservado()) {
                resultado.add(e);
            }
        }
        return resultado;
    }
}