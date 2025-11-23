package Service;

import Exception.ValidacaoException;
import model.*;
import persistence.ExemplarDAO;
import persistence.LivroDAO;
import persistence.ProgramaLeituraDAO;
import persistence.TurmaDAO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProgramaLeituraService {

    private ExemplarDAO exemplarDAO; // Nova dependência essencial
    private LivroDAO livroDAO;
    private TurmaDAO turmaDAO;
    private ProgramaLeituraDAO programaDAO;
    private LogService logService;

    public ProgramaLeituraService() {
        this.exemplarDAO = new ExemplarDAO();
        this.livroDAO = new LivroDAO();
        this.turmaDAO = new TurmaDAO();
        this.programaDAO = new ProgramaLeituraDAO();
        this.logService = new LogService();
    }

    /**
     * GERAR SUGESTÃO AUTOMÁTICA COM EXEMPLARES ÚNICOS
     * Busca exemplares físicos que atendem aos critérios (Gênero + Idade)
     * e que não estejam nem emprestados nem reservados.
     */
    public List<AtribuicaoLeitura> gerarSugestaoDistribuicao(Turma turma, Genero genero, int idadeMediaTurma) throws ValidacaoException {

        // 1. Validações Básicas
        if (turma == null || turma.getAlunos().isEmpty()) {
            throw new ValidacaoException("A turma selecionada não possui alunos matriculados.");
        }
        if (genero == null) {
            throw new ValidacaoException("Selecione um gênero literário.");
        }

        // 2. Buscar o 'Pool' de Exemplares Disponíveis
        // Estratégia: Pegar todos os exemplares e filtrar em memória (ou usar método específico no DAO se houver)
        List<Exemplar> todosExemplares = exemplarDAO.listarTodos();
        List<Exemplar> poolDisponivel = new ArrayList<>();

        for (Exemplar ex : todosExemplares) {
            Livro livro = ex.getLivro();

            // Filtros de Regra de Negócio:
            boolean generoBate = livro.getGenero().getID() == genero.getID();
            boolean idadeOk = livro.getIdadeMinima() <= idadeMediaTurma;
            boolean estaNaEstante = ex.isDisponivel(); // Não está emprestado
            boolean naoEstaReservado = !ex.isReservado(); // Não está separado para outro projeto

            if (generoBate && idadeOk && estaNaEstante && naoEstaReservado) {
                poolDisponivel.add(ex);
            }
        }

        if (poolDisponivel.isEmpty()) {
            throw new ValidacaoException("Não há exemplares físicos disponíveis deste gênero para a idade informada.");
        }

        // 3. O Sorteio
        Collections.shuffle(poolDisponivel);

        // 4. A Distribuição (Aluno <-> Exemplar Único)
        List<AtribuicaoLeitura> sugestao = new ArrayList<>();
        int indexExemplar = 0;

        for (Aluno aluno : turma.getAlunos()) {
            Exemplar exemplarSorteado = null;

            if (indexExemplar < poolDisponivel.size()) {
                exemplarSorteado = poolDisponivel.get(indexExemplar);
                indexExemplar++;
            } else {
                System.out.println("Aviso: Faltou livro para o aluno " + aluno.getNome());
                // O professor terá que resolver manualmente na tela
            }

            // Nota: Certifique-se que sua classe AtribuicaoLeitura foi atualizada para aceitar Exemplar no construtor
            sugestao.add(new AtribuicaoLeitura(aluno, exemplarSorteado));
        }

        return sugestao;
    }

    /**
     * SALVAR O PROGRAMA E APLICAR RESERVAS
     * Se a data de início for hoje (ou passada), os exemplares ficam "travados" (reservados).
     */
    public void salvarPrograma(ProgramaLeitura programa) throws ValidacaoException {

        // 1. Validações do Cabeçalho
        if (programa.getTitulo() == null || programa.getTitulo().isEmpty()) {
            throw new ValidacaoException("O título do programa é obrigatório.");
        }
        if (programa.getDataInicio() == null || programa.getDataFim() == null) {
            throw new ValidacaoException("As datas de início e fim são obrigatórias.");
        }
        if (programa.getDataFim().isBefore(programa.getDataInicio())) {
            throw new ValidacaoException("A data final não pode ser anterior à data inicial.");
        }
        if (programa.getAtribuicoes().isEmpty()) {
            throw new ValidacaoException("A lista de distribuição de livros está vazia.");
        }

        // 2. Verifica se devemos ativar a RESERVA agora
        // Se DataInicio <= Hoje, o programa está valendo e os livros devem ser bloqueados para empréstimo comum.
        boolean programaAtivo = !programa.getDataInicio().isAfter(LocalDate.now());

        // 3. Processar Exemplares
        for (AtribuicaoLeitura item : programa.getAtribuicoes()) {
            Exemplar exemplar = item.getExemplar(); // Atualize o getter em AtribuicaoLeitura
            Aluno aluno = item.getAluno();

            if (exemplar != null) {
                // Recarrega do banco para garantir status atual (evita conflito de concorrência)
                Exemplar exemplarAtual = exemplarDAO.buscarPorId(exemplar.getId());

                if (exemplarAtual == null) {
                    throw new ValidacaoException("O exemplar ID " + exemplar.getId() + " não existe mais no acervo.");
                }

                // Se vamos reservar, temos que garantir que ele AINDA está disponível
                if (programaAtivo) {
                    if (!exemplarAtual.isDisponivel()) {
                        throw new ValidacaoException("Conflito: O exemplar '" + exemplarAtual.getLivro().getTitulo() +
                                "' (ID: " + exemplarAtual.getId() + ") acabou de ser emprestado para outra pessoa.");
                    }

                    // Aplica a Reserva
                    exemplarAtual.setReservado(true);
                    exemplarDAO.salvar(exemplarAtual); // Atualiza status no CSV de exemplares
                }
            }
        }

        // 4. Salvar o Programa (Cabeçalho e Lista de Pares)
        boolean novo = (programa.getId() == 0);
        programaDAO.salvar(programa);

        // 5. Log
        String statusReserva = programaAtivo ? " [RESERVAS APLICADAS]" : " [AGENDADO]";
        logService.registrar("PROGRAMA SALVO: " + programa.getTitulo() + statusReserva + " | Turma: " + programa.getTurma().getNome());

        System.out.println("SUCESSO: Programa salvo. " + programa.getAtribuicoes().size() + " exemplares processados.");
    }
    public Exemplar buscarReservaParaAluno(Aluno aluno) {
        // Busca programas da turma do aluno
        List<ProgramaLeitura> programas = programaDAO.buscarPorTurma(turmaDAO.buscarPorNome(aluno.getTurma()).getId());

        for (ProgramaLeitura p : programas) {
            if (p.isAtivo()) {
                for (AtribuicaoLeitura at : p.getAtribuicoes()) {
                    if (at.getAluno().getId() == aluno.getId()) {
                        return at.getExemplar(); // Retorna o exemplar reservado
                    }
                }
            }
        }
        return null;
    }
}