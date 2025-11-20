package model;

public abstract class Usuario {

    protected String id_usuario;
    protected String nome;
    protected int matricula;
    protected String senha_hash;
    protected String tipo_usuario;

    public Usuario(String id_usuario, String nome, int matricula, String senha_hash, String tipo_usuario){
        this.id_usuario = id_usuario;
        this.matricula = matricula;
        this.senha_hash = senha_hash;
        this.tipo_usuario = tipo_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getId_usuario() {
        return id_usuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getMatricula() {
        return matricula;
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }

    public String getSenha_hash() {
        return senha_hash;
    }

    public void setSenha_hash(String senha_hash) {
        this.senha_hash = senha_hash;
    }

    public String getTipo_usuario() {
        return tipo_usuario;
    }

    public void setTipo_usuario(String tipo_usuario) {
        this.tipo_usuario = tipo_usuario;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id_usuario='" + id_usuario + '\'' +
                ", nome='" + nome + '\'' +
                ", matricula=" + matricula +
                ", senha_hash='" + senha_hash + '\'' +
                ", tipo_usuario='" + tipo_usuario + '\'' +
                '}';
    }
}
