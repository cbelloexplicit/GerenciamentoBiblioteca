package model;

public class Administrador extends Usuario {

    // Construtor completo
    public Administrador(long id, String nome, String matricula, String senha, boolean ativo) {
        super(id, nome, matricula, senha, ativo);
    }

    // Construtor para novos cadastros
    public Administrador(String nome, String matricula, String senha) {
        super(nome, matricula, senha);
    }

    @Override
    public String getTipo() {
        return "ADMINISTRADOR";
    }
}