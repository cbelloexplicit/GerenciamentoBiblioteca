package model;

public class Exemplar {
    private long id;
    private Livro livro; // Referência ao livro
    private boolean disponivel; // Está na biblioteca
    private boolean reservado;  // Está separado para um programa de leitura

    // Construtor
    public Exemplar(long id, Livro livro, boolean disponivel, boolean reservado) {
        this.id = id;
        this.livro = livro;
        this.disponivel = disponivel;
        this.reservado = reservado;
    }

    // Novo cadastro
    public Exemplar(Livro livro) {
        this.livro = livro;
        this.disponivel = true;
        this.reservado = false;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Livro getLivro() { return livro; }

    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }

    public boolean isReservado() { return reservado; }
    public void setReservado(boolean reservado) { this.reservado = reservado; }

    @Override
    public String toString() {
        return "Exemplar #" + id + " - " + livro.getTitulo();
    }
}