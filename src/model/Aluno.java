package model;

public class Aluno extends Usuario {
    private String turma;

    // Construtor completo
    public Aluno(long id, String nome, String matricula, String senha, boolean ativo, String turma) {
        super(id, nome, matricula, senha, ativo);
        this.turma = turma;
    }

    // Construtor para novos cadastros
    public Aluno(String nome, String matricula, String senha, String turma) {
        super(nome, matricula, senha);
        this.turma = turma;

    }
    public String getTurma() {
        return turma;
    }
    public void setTurma(String turma) {
        this.turma = turma;
    }

    @Override
    public String getTipo() {
        return "ALUNO";
    }

    @Override
    public String toString() {
        return "Aluno{" +
                "turma='" + turma + '\'' +
                ", id=" + id +
                ", nome='" + nome + '\'' +
                ", matricula='" + matricula + '\'' +
                ", senha='" + senha + '\'' +
                ", ativo=" + ativo +
                '}';
    }
}
