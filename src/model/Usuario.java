package model;

public abstract class Usuario {
    protected long  id;
    protected String nome;
    protected String matricula;
    protected String senha;
    protected boolean ativo;

    public Usuario(){
    }

    public Usuario(long id, String nome, String matricula, String senha, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.matricula = matricula;
        this.senha = senha;
        this.ativo = ativo;
    }

    public Usuario(String nome, String matricula, String senha) {
        this.nome = nome;
        this.matricula = matricula;
        this.senha = senha;
        this.ativo = true;
    }

    //metdo abstrato para cada filho responder seu tipo
    public abstract String getTipo();

    //getters e setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void ativarUsuario(){
        this.ativo = true;
    }

    public void inativarUsuario(){
        this.ativo = false;
    }

    //toString básico p/debug
    @Override
    public String toString(){
        String status;
        if(ativo){
            status = "Usuário ativo";
        } else {
            status = "Usuario inativado";
        }
        return "ID: " + id + " | Nome: " + nome + " | Matrícula: " + matricula + " | Tipo: " + getTipo() + " | Status: " + status;

    }

}
