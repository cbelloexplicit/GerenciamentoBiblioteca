package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProgramaLeitura {
    private long id;
    private String titulo;      // Ex: "Projeto Ficção 2025"
    private Turma turma;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private int trimestre;
    private int ano;
    private List<AtribuicaoLeitura> atribuicoes;

    public ProgramaLeitura(long id, String titulo, Turma turma, LocalDate dataInicio, LocalDate dataFim, int trimestre, int ano) {
        this.id = id;
        this.titulo = titulo;
        this.turma = turma;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.trimestre = trimestre;
        this.ano = ano;
        this.atribuicoes = new ArrayList<>();
    }

    // Construtor sem ID
    public ProgramaLeitura(String titulo, Turma turma, LocalDate dataInicio, LocalDate dataFim, int trimestre, int ano) {
        this.titulo = titulo;
        this.turma = turma;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.trimestre = trimestre;
        this.ano = ano;
        this.atribuicoes = new ArrayList<>();
    }

    // --- Métodos de Gestão da Lista ---

    public void adicionarAtribuicao(AtribuicaoLeitura atribuicao) {
        this.atribuicoes.add(atribuicao);
    }

    public List<AtribuicaoLeitura> getAtribuicoes() {
        return atribuicoes;
    }

    public void setAtribuicoes(List<AtribuicaoLeitura> atribuicoes) {
        this.atribuicoes = atribuicoes;
    }

    // --- Getters e Setters (Padrão) ---


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public int getTrimestre() {
        return trimestre;
    }

    public void setTrimestre(int trimestre) {
        this.trimestre = trimestre;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    @Override
    public String toString() {
        return "ProgramaLeitura{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", turma=" + turma +
                ", dataInicio=" + dataInicio +
                ", dataFim=" + dataFim +
                ", trimestre=" + trimestre +
                ", ano=" + ano +
                ", atribuicoes=" + atribuicoes +
                '}';
    }
}

