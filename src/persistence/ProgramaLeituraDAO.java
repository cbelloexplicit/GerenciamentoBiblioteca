package persistence;

import model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProgramaLeituraDAO {

    // Banco em memória
    private static List<ProgramaLeitura> bancoProgramas = new ArrayList<>();
    private static long proximoId = 1;

    //Dados de Teste
    static {
        // Tenta criar um programa de teste se houver dados suficientes
        TurmaDAO turmaDAO = new TurmaDAO();
        LivroDAO livroDAO = new LivroDAO();

        // Pega a primeira turma e alguns livros
        Turma turma = turmaDAO.buscarPorId(1); // 3º Ano A
        List<Livro> livros = livroDAO.listarTodos();

        if (turma != null && !livros.isEmpty() && !turma.getAlunos().isEmpty()) {

            // Cria o cabeçalho do programa
            ProgramaLeitura prog = new ProgramaLeitura(
                    "Leitura de Verão 2025",
                    turma,
                    LocalDate.now(),
                    LocalDate.now().plusMonths(3),
                    1, // 1º Trimestre
                    2025
            );

            // Cria atribuições falsas (distribui o primeiro livro para o primeiro aluno)
            Aluno aluno1 = turma.getAlunos().get(0);
            Livro livro1 = livros.get(0);

            AtribuicaoLeitura atribuicao = new AtribuicaoLeitura(aluno1, livro1);
            prog.adicionarAtribuicao(atribuicao);

            salvarFake(prog);
        }
    }

    private static void salvarFake(ProgramaLeitura p) {
        p.setId(proximoId++);
        bancoProgramas.add(p);
    }

    // CRUD

    public void salvar(ProgramaLeitura programa) {
        if (programa.getId() == 0) {
            programa.setId(proximoId++);
        }
        // Se for atualização, remove o antigo da lista para substituir
        remover(programa.getId());
        bancoProgramas.add(programa);

        System.out.println("Programa '" + programa.getTitulo() + "' salvo com " + programa.getAtribuicoes().size() + " atribuições.");
    }

    public List<ProgramaLeitura> listarTodos() {
        return new ArrayList<>(bancoProgramas);
    }

    public ProgramaLeitura buscarPorId(long id) {
        for (ProgramaLeitura p : bancoProgramas) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    public void remover(long id) {
        bancoProgramas.removeIf(p -> p.getId() == id);
    }

    // Buscar programas de uma turma específica
    public List<ProgramaLeitura> buscarPorTurma(long idTurma) {
        List<ProgramaLeitura> resultado = new ArrayList<>();
        for (ProgramaLeitura p : bancoProgramas) {
            if (p.getTurma().getId() == idTurma) {
                resultado.add(p);
            }
        }
        return resultado;
    }
}