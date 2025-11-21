package model;

public class Genero {
    private long id;
    private String nome;

    public Genero(long id, String nome) {
        this.id = id;
        this.nome = nome;
    }
    // Construtor para novos cadastros
    public Genero(String nome) {
        this.nome = nome;
    }

    // Getters e Setters
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return nome;
    }
}

