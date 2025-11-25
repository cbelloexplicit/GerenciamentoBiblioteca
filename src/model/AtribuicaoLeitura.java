package model;

public class AtribuicaoLeitura {
    private Aluno aluno;
    private Exemplar exemplar;

    public AtribuicaoLeitura(Aluno aluno, Exemplar exemplar) {
        this.aluno = aluno;
        this.exemplar = exemplar;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public Exemplar getExemplar() {
        return exemplar;
    }

    public void setExemplar(Exemplar exemplar) {
        this.exemplar = exemplar;
    }

    @Override
    public String toString() {
        if (exemplar != null) {
            return aluno.getNome() + " -> " + exemplar.getLivro().getTitulo() + " (Exemplar #" + exemplar.getId() + ")";
        } else {
            return aluno.getNome() + " -> SEM LIVRO";
        }
    }
}