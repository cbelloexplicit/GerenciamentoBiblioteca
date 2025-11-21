package model;

public class Livro {
    private long id;
    private String titulo;
    private String autor;
    private Genero genero; // Associação: O livro "tem um" Gênero
    private int idadeMinima;
    private int totalCopias;
    private int copiasDisponiveis;

    public Livro(long id, String titulo, String autor, Genero genero, int idadeMinima, int totalCopias) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.idadeMinima = idadeMinima;
        this.totalCopias = totalCopias;
        this.copiasDisponiveis = totalCopias; // Inicialmente, todas estão disponíveis
    }

    // Construtor sem ID para cadastro
    public Livro(String titulo, String autor, Genero genero, int idadeMinima, int totalCopias) {
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.idadeMinima = idadeMinima;
        this.totalCopias = totalCopias;
        this.copiasDisponiveis = totalCopias;
    }

    // Getters e Setters
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

    public String getAutor() {
        return autor;
    }
    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Genero getGenero() {
        return genero;
    }
    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public void diminuirCopias() {
        if (this.copiasDisponiveis > 0) {
            this.copiasDisponiveis--;
        }
    }

    public void adicionarCopias() {
        if (this.copiasDisponiveis < this.totalCopias) {
            this.copiasDisponiveis++;
        }
    }

    public int getIdadeMinima() {
        return idadeMinima;
    }

    public void setIdadeMinima(int idadeMinima) {
        this.idadeMinima = idadeMinima;
    }

    @Override
    public String toString() {
        return titulo + " (" + (genero != null ? genero.getNome() : "Sem Gênero") + ")";
    }
}