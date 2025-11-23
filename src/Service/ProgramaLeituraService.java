package Service;

import Exception.ValidacaoException;
import model.*;
import persistence.LivroDAO;
import persistence.ProgramaLeituraDAO;
import persistence.TurmaDAO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProgramaLeituraService {

    private LivroDAO livroDAO;
    private TurmaDAO turmaDAO; // garantir que a turma existe e tem alunos
    private ProgramaLeituraDAO programaDAO;

    public ProgramaLeituraService() {
        this.livroDAO = new LivroDAO();
        this.turmaDAO = new TurmaDAO();
    }

    //GERAR SUGESTÃO AUTOMÁTICA DE DISTRIBUIÇÃO
    //Pega todos os livros do gênero, filtra os impróprios para a idade, cria um "pool" de cópias disponíveis e sorteia entre os alunos.
    // turma A turma que vai ler
    // genero O gênero literário escolhido pelo professor.
    // idadeMediaTurma A idade base para filtrar conteúdo impróprio.
    // return Uma lista de pares (Aluno -> Livro Sugerido) para o professor revisar.

    public List<AtribuicaoLeitura> gerarSugestaoDistribuicao(Turma turma, Genero genero, int idadeMediaTurma) throws ValidacaoException {

        // Validações Iniciais
        if (turma == null || turma.getAlunos().isEmpty()) {
            throw new ValidacaoException("A turma selecionada não possui alunos matriculados.");
        }
        if (genero == null) {
            throw new ValidacaoException("Selecione um gênero literário.");
        }

        // Busca livros do gênero e filtra por IDADE e ESTOQUE
        List<Livro> livrosDoGenero = livroDAO.buscarPorGenero(genero);
        List<Livro> poolDeCopias = new ArrayList<>();

        for (Livro l : livrosDoGenero) {
            // Idade apropriada
            boolean idadeOk = l.getIdadeMinima() <= idadeMediaTurma;

            //Tem cópia na estante?
            boolean temEstoque = l.getCopiasDisponiveis() > 0;

            if (idadeOk && temEstoque) {
                // Adiciona ao "pool" o número exato de cópias disponíveis
                // Se tem 3 cópias de "Duna", adiciona 3 vezes na lista.
                for (int i = 0; i < l.getCopiasDisponiveis(); i++) {
                    poolDeCopias.add(l);
                }
            }
        }

        if (poolDeCopias.isEmpty()) {
            throw new ValidacaoException("Não há livros disponíveis deste gênero para a idade informada.");
        }

        //O Sorteio (Embaralhamento)
        Collections.shuffle(poolDeCopias);

        //A Distribuição
        List<AtribuicaoLeitura> sugestao = new ArrayList<>();
        int indexLivro = 0;

        for (Aluno aluno : turma.getAlunos()) {
            Livro livroSorteado = null;

            // Se ainda tem livro no pool, entrega um para o aluno
            if (indexLivro < poolDeCopias.size()) {
                livroSorteado = poolDeCopias.get(indexLivro);
                indexLivro++;
            } else {
                // Acabaram os livros! O aluno fica com NULL (professor resolve manualmente na tela)
                System.out.println("Aviso: Faltou livro para o aluno " + aluno.getNome());
            }

            sugestao.add(new AtribuicaoLeitura(aluno, livroSorteado));
        }

        return sugestao;
    }

    //EFETIVAR (SALVAR) O PROGRAMA
    //Recebe o objeto completo já editado pelo professor e grava.
    public void salvarPrograma(ProgramaLeitura programa) throws ValidacaoException {

        //Validações do Cabeçalho
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

        // Processar as Atribuições (Baixar Estoque)
        for (AtribuicaoLeitura item : programa.getAtribuicoes()) {
            Livro livro = item.getLivro();
            Aluno aluno = item.getAluno();

            if (livro != null) {
                // Recarrega do banco para ver estoque atual (segurança contra concorrencia)
                Livro livroAtual = livroDAO.buscarPorId(livro.getId());

                if (livroAtual.getCopiasDisponiveis() <= 0) {
                    throw new ValidacaoException("O livro '" + livro.getTitulo() + "' atribuído ao aluno " + aluno.getNome() + " não tem mais cópias disponíveis.");
                }

                // Decrementa estoque (Reserva para o projeto)
                livroAtual.dimCopiasDisponiveis();
                livroDAO.salvar(livroAtual);
            }
        }

        //Salvar o Programa
        programaDAO.salvar(programa);
        System.out.println("SUCESSO: Programa '" + programa.getTitulo() + "' salvo com " + programa.getAtribuicoes().size() + " atribuições.");

        //Salvar linha no programas.csv
        //Salvar N linhas no programa_detalhes.csv
    }
}