package model;

public class Livro {
    private long id;
    private String titulo;
    private String autor;
    private Genero genero;
    private int idadeMinima;

    public Livro(long id, String titulo, String autor, Genero genero, int idadeMinima) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.idadeMinima = idadeMinima;
    }

    // Getters e Setters
    public long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public Genero getGenero() { return genero; }
    public int getIdadeMinima() { return idadeMinima; }

    public void setId(long id) {
        this.id = id;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public void setIdadeMinima(int idadeMinima) {
        this.idadeMinima = idadeMinima;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }


    @Override
    public String toString() {
        return titulo + " (" + autor + ")";
    }
}