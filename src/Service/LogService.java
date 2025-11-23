package Service;

import persistence.LogDAO;
import model.Usuario;
import model.LogAcesso;
import util.Sessao;

import java.util.List;

public class LogService {

    private LogDAO logDAO;

    public LogService() {
        this.logDAO = new LogDAO();
    }

    //REGISTRAR LOG
    public void registrarAtividade(Usuario usuario, String acao) {
        if (usuario != null && acao != null && !acao.isEmpty()) {
            LogAcesso novoLog = new LogAcesso(usuario, acao);
            logDAO.salvar(novoLog);
            // Feedback no console para debug
            System.out.println("LOG REGISTRADO: [" + acao + "] Usuário: " + usuario.getMatricula());
        }
    }

    //listar todos
    public List<LogAcesso> obterRelatorioCompleto() {
        return logDAO.listarTodos();
    }

    //filtrar por usuario
    public List<LogAcesso> obterRelatorioPorUsuario(Usuario usuario) {
        if (usuario == null) {
            return obterRelatorioCompleto();
        }
        return logDAO.buscarPorUsuario(usuario.getId());

    }
    public void registrar(String acao) {
        if (Sessao.isLogado()) {
            registrarAtividade(Sessao.getUsuarioLogado(), acao);
        } else {
            System.out.println("Tentativa de log sem usuário logado: " + acao);
        }


        }
    }