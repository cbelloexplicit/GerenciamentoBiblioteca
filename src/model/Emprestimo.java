package model;

import java.time.LocalDate;

public class Emprestimo {
    private long id;
    private Aluno aluno;  // quem pegou
    private Exemplar exemplar;  // o que pegou
    private LocalDate dataEmprestimo;
    private LocalDate dataDevolucaoPrevista;
    private LocalDate dataDevolucaoReal; // NULL se ainda não foi devolvido

    public Emprestimo(Aluno aluno, Exemplar exemplar, LocalDate dataEmprestimo, int diasParaDevolucao) {
        this.aluno = aluno;
        this.exemplar = exemplar;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucaoPrevista = dataEmprestimo.plusDays(diasParaDevolucao);
        this.dataDevolucaoReal = null; // Está em aberto
    }

    // Construtor completo
    public Emprestimo(long id, Aluno aluno, Exemplar exemplar, LocalDate dataEmp, LocalDate dataPrev, LocalDate dataReal) {
        this.id = id;
        this.aluno = aluno;
        this.exemplar = exemplar;
        this.dataEmprestimo = dataEmp;
        this.dataDevolucaoPrevista = dataPrev;
        this.dataDevolucaoReal = dataReal;
    }

    public boolean isAberto() {
        return dataDevolucaoReal == null;
    }

    public boolean isAtrasado() {
        // Se está aberto E hoje já passou da data prevista
        return isAberto() && LocalDate.now().isAfter(dataDevolucaoPrevista);
    }

    public void registrarDevolucao(LocalDate dataDevolucao) {
        this.dataDevolucaoReal = dataDevolucao;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public Exemplar getExemplar() { return exemplar; }
    public Livro getLivroInfo() { return exemplar.getLivro(); }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public void setDataEmprestimo(LocalDate dataEmprestimo) {
        this.dataEmprestimo = dataEmprestimo;
    }

    public LocalDate getDataDevolucaoPrevista() {
        return dataDevolucaoPrevista;
    }

    public void setDataDevolucaoPrevista(LocalDate dataDevolucaoPrevista) {
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
    }

    public LocalDate getDataDevolucaoReal() {
        return dataDevolucaoReal;
    }

    public void setDataDevolucaoReal(LocalDate dataDevolucaoReal) {
        this.dataDevolucaoReal = dataDevolucaoReal;
    }

    @Override
    public String toString() {
        String status = "";
        String s = "Emprestimo{" +
                "id=" + id +
                ", aluno=" + aluno.getNome() +
                ", livro=" + exemplar.getLivro().getTitulo() +
                ", dataEmprestimo=" + dataEmprestimo +
                ", dataDevolucaoPrevista=" + dataDevolucaoPrevista +
                ", situação= " + status;
        if (isAtrasado()) {
            status = "ATRASADO";
            return s;
        } else if (isAberto()) {
            status = "EM ANDAMENTO";
            return s;
        } else {
            return "Emprestimo{" +
                    "id=" + id +
                    ", aluno=" + aluno.getNome() +
                    ", livro=" + exemplar.getLivro().getTitulo() +
                    ", dataEmprestimo=" + dataEmprestimo +
                    ", dataDevolucaoPrevista=" + dataDevolucaoPrevista +
                    ", dataDevolucaoReal=" + dataDevolucaoReal +
                    ", situação= CONCLUIDO }";
        }

    }
}