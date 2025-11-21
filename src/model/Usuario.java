package model;

public abstract class Usuario {

    protected long id;
    protected String nome;
    protected String matricula;
    protected String senha;
    protected boolean ativo;

    public Usuario() {
    }

    // Construtor completo
    public Usuario(long id, String nome, String matricula, String senha, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.matricula = matricula;
        this.senha = senha;
        this.ativo = ativo;
    }

    // Construtor sem ID (usado antes de salvar no arquivo)
    public Usuario(String nome, String matricula, String senha) {
        this.nome = nome;
        this.matricula = matricula;
        this.senha = senha;
        this.ativo = true; // Por padrão, nasce ativo
    }

    // --- MÉTODOS ABSTRATOS ---
    // Cada filho será obrigado a responder qual é o seu tipo.
    public abstract String getTipo();

    // --- GETTERS E SETTERS ---

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

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Nome: " + nome + " | Matrícula: " + matricula + " | Tipo: " + getTipo();
    }
}