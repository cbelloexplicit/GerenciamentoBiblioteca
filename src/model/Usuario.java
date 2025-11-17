package model;

public class Usuario {

    private String id_usuario;
    private String nome;
    private int matricula;
    private String senha_hash;
	private String tipo_usuario;

    public Usuario(String id_usuario, String nome, int matricula, String senha_hash, String tipo_usuario){
        this.id_usuario = id_usuario;
        this.matricula = matricula;
        this.senha_hash = senha_hash;
        this.tipo_usuario = tipo_usuario;
    }
}
