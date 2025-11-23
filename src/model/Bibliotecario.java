package model;

public class Bibliotecario extends Usuario{
    //construtor completo
    public Bibliotecario(long id, String nome, String matricula, String senha, boolean ativo) {
        super(id, nome, matricula, senha, ativo);
    }

    // Construtor para novo cadastro
    public Bibliotecario(String nome, String matricula, String senha) {
        super(nome, matricula, senha);
    }
    @Override
    public String getTipo(){
        return "BIBLIOTECÁRIO";
    }
}
