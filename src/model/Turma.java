package model;

import java.util.ArrayList;
import java.util.List;

public class Turma {
    private long id;
    private String nome;       // Ex: "3º Ano A"
    private int anoLetivo;     // Ex: 2025
    private Professor professorResponsavel; // Associação

    // Esta lista não vai direto para o CSV da Turma,
    // ela é preenchida pelo DAO buscando no alunos.csv quem tem essa turma.
    private List<Aluno> alunos;

    public Turma(long id, String nome, int anoLetivo, Professor professorResponsavel) {
        this.id = id;
        this.nome = nome;
        this.anoLetivo = anoLetivo;
        this.professorResponsavel = professorResponsavel;
        this.alunos = new ArrayList<>();
    }

    // Métodos para facilitar o gerenciamento da lista
    public void adicionarAluno(Aluno aluno) {
        this.alunos.add(aluno);
    }

    public List<Aluno> getAlunos() {
        return alunos;
    }

    // Getters e Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getAnoLetivo() { return anoLetivo; }
    public void setAnoLetivo(int anoLetivo) { this.anoLetivo = anoLetivo; }

    public Professor getProfessorResponsavel() { return professorResponsavel; }
    public void setProfessorResponsavel(Professor professorResponsavel) { this.professorResponsavel = professorResponsavel; }

    @Override
    public String toString() {
        return nome + " (" + anoLetivo + ") - Prof. " + (professorResponsavel != null ? professorResponsavel.getNome() : "Sem atribuição");
    }
}