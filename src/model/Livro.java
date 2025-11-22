package model;

public class Livro {
    private long id;
    private String titulo;
    private String autor;
    private Genero genero;
    private int idadeMinima;
    private int totalCopias;
    private int copiasDisponiveis;

    public Livro(long id, String titulo, String autor, Genero genero, int idadeMinima, int totalCopias, int copiasDisponiveis) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.idadeMinima = idadeMinima;
        this.totalCopias = totalCopias;
        this.copiasDisponiveis = copiasDisponiveis;
    }

    public Livro(String titulo, String autor, Genero genero, int idadeMinima, int totalCopias) {
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.idadeMinima = idadeMinima;
        this.totalCopias = totalCopias;
        this.copiasDisponiveis = totalCopias;
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

    public int getIdadeMinima() {
        return idadeMinima;
    }

    public void setIdadeMinima(int idadeMinima) {
        this.idadeMinima = idadeMinima;
    }

    public int getTotalCopias() {
        return totalCopias;
    }

    public void setTotalCopias(int totalCopias) {
        this.totalCopias = totalCopias;
    }

    public int getCopiasDisponiveis() {
        return copiasDisponiveis;
    }

    public boolean setCopiasDisponiveis(int altCopiasDisponiveis) {
        if(altCopiasDisponiveis <= totalCopias && altCopiasDisponiveis >= 0) {
            this.copiasDisponiveis = copiasDisponiveis;
            return true;
        }else{
            return false;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    //add copias e dim copias
    public boolean addCopiasDisponiveis() {
        if (copiasDisponiveis < totalCopias) {
            this.copiasDisponiveis = copiasDisponiveis++;
            return true;
        } else {
            return false;
        }
    }

    public boolean dimCopiasDisponiveis() {
        if (copiasDisponiveis >= 0) {
            this.copiasDisponiveis = copiasDisponiveis--;
            return true;
        } else {
            return false;
        }

    }

    @Override
    public String toString() {
        return "Livro" + titulo +
                "id=" + id +
                ", autor='" + autor +
                ", genero=" + genero +
                ", idadeMinima=" + idadeMinima +
                ", totalCopias=" + totalCopias +
                ", copiasDisponiveis=" + copiasDisponiveis +
                '}';
    }
}