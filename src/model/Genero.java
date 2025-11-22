package model;

public class Genero {
    private long id;
    private String nome;
    //construtor completo
    public Genero(long id, String nome){
        this.id = id;
        this.nome = nome;
    }
    //construtor cadastro
    public Genero(String nome){
        this.nome = nome;
    }

    //getter setter
    public long getID(){
        return id;
    }
    public String getNome(){
        return nome;
    }
    public void setID(long ID){
        this.id = ID;
    }
    public void setNome(String nome){
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "ID: "+ id + " | Gênero: " + nome;
    }
}
