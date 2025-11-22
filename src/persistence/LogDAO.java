package persistence;

import model.LogAcesso;
import model.Usuario;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LogDAO {

    private static List<LogAcesso> bancoLogs = new ArrayList<>();
    private static long proximoId = 1;

    // --- SEED INICIAL ---
    static {
        // simular que o Admin logou ontem, só para ter algo na lista
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario admin = usuarioDAO.buscarPorId(1); // Admin Principal

        if (admin != null) {
            LogAcesso logAntigo = new LogAcesso(admin, "LOGIN");
            logAntigo.setDataHora(LocalDateTime.now().minusDays(1)); // Ontem
            salvarFake(logAntigo);

            LogAcesso logLogout = new LogAcesso(admin, "LOGOUT");
            logLogout.setDataHora(LocalDateTime.now().minusDays(1).plusHours(1)); // Saiu 1h depois
            salvarFake(logLogout);
        }
    }

    private static void salvarFake(LogAcesso l) {
        l.setId(proximoId++);
        bancoLogs.add(l);
    }

    public void salvar(LogAcesso log) {
        log.setId(proximoId++);
        bancoLogs.add(log);
    }

    public List<LogAcesso> listarTodos() {
        return new ArrayList<>(bancoLogs);
    }

    // Filtro por usuário (útil para auditoria específica)
    public List<LogAcesso> buscarPorUsuario(long idUsuario) {
        List<LogAcesso> resultado = new ArrayList<>();
        for (LogAcesso l : bancoLogs) {
            if (l.getUsuario().getId() == idUsuario) {
                resultado.add(l);
            }
        }
        return resultado;
    }
}