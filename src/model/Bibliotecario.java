package model;

public class Bibliotecario extends Usuario{
    public Bibliotecario(String id_usuario, String nome, int matricula, String senha_hash, String tipo_usuario) {
        super(id_usuario, nome, matricula, senha_hash, tipo_usuario);
    }
}
