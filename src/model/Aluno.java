package model;

public class Aluno extends Usuario {
        private String turma;
        private String dataNascimento;

    public Aluno(long id, String nome, String matricula, String senha, boolean ativo, String turma, String dataNascimento) {
        super(id, nome, matricula, senha, ativo);
        this.turma = turma;
        this.dataNascimento = dataNascimento;
    }

    // Construtor para novo cadastro
    public Aluno(String nome, String matricula, String senha, String turma, String dataNascimento) {
        super(nome, matricula, senha);
        this.turma = turma;
        this.dataNascimento = dataNascimento;
    }

    public String getTurma() {
        return turma;
    }
    public void setTurma(String turma) {
        this.turma = turma;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
    @Override
    public String getTipo() {
        return "ALUNO";
    }
}
