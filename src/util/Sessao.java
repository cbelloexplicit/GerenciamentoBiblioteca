package util;

import model.Usuario;

public class Sessao {
    // Variável estática: existe uma só para a aplicação inteira
    private static Usuario usuarioLogado;

    public static Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public static void setUsuarioLogado(Usuario usuario) {
        Sessao.usuarioLogado = usuario;
    }

    public static boolean isLogado() {
        return usuarioLogado != null;
    }
}