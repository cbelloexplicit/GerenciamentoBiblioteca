package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogAcesso {

    private long id;
    private Usuario usuario;        // Quem fez a ação
    private LocalDateTime dataHora; // Quando fez
    private String acao;            // O que fez (Ex: "LOGIN", "LOGOUT", "SENHA_ALTERADA")

    // Construtor completo
    public LogAcesso(long id, Usuario usuario, LocalDateTime dataHora, String acao) {
        this.id = id;
        this.usuario = usuario;
        this.dataHora = dataHora;
        this.acao = acao;
    }

    // Construtor para novos logs (dataHora automática)
    public LogAcesso(Usuario usuario, String acao) {
        this.usuario = usuario;
        this.acao = acao;
        this.dataHora = LocalDateTime.now();
    }

    // --- Getters e Setters ---

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getAcao() {
        return acao;
    }
    public void setAcao(String acao) {
        this.acao = acao;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return String.format("[%s] %s - %s (%s)",
                dataHora.format(formatter),
                acao,
                usuario.getNome(),
                usuario.getMatricula()
        );
    }
}