package model;

public class Professor extends Usuario{
    //construtor completo
    public Professor(long id, String nome, String matricula, String senha, boolean ativo) {
        super(id, nome, matricula, senha, ativo);
    }

    // Construtor para novo cadastro
    public Professor(String nome, String matricula, String senha) {
        super(nome, matricula, senha);
    }
    public String getTipo(){
        return "PROFESSOR";
    }
}
