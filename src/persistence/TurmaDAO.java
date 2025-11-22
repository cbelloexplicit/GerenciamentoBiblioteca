package persistence;
import model.*;
import java.util.ArrayList;
import java.util.List;

    public class TurmaDAO {

        private static List<Turma> bancoTurmas = new ArrayList<>();
        private static long proximoId = 1;

        // dados teste
        static {
            // Precisa de um professor para criar a turma.
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario u = usuarioDAO.buscarPorId(3);

            // Verifica se achou e se é mesmo um professor
            Professor profResponsavel = (u instanceof Professor) ? (Professor) u : null;

            if (profResponsavel != null) {
                salvarFake(new Turma(0, "3º Ano A", 2025,profResponsavel));
                salvarFake(new Turma(0, "1º Ano B", 2025,profResponsavel));
            } else {
                // Fallback caso o professor não exista no teste
                salvarFake(new Turma(0, "Turma Teste Sem Prof", 2025, null));
            }
        }

        private static void salvarFake(Turma t) {
            t.setId(proximoId++);
            bancoTurmas.add(t);
        }

        // --- MÉTODOS CRUD ---

        public void salvar(Turma turma) {
            if (turma.getId() == 0) {
                turma.setId(proximoId++);
            }
            // Se for atualização, remove a antiga
            remover(turma.getId());
            bancoTurmas.add(turma);
            System.out.println("Turma salva: " + turma.getNome());
        }

        public List<Turma> listarTodas() {
            return new ArrayList<>(bancoTurmas);
        }

        public Turma buscarPorId(long id) {
            for (Turma t : bancoTurmas) {
                if (t.getId() == id) return t;
            }
            return null;
        }

        public Turma buscarPorNome(String nome) {
            for (Turma t : bancoTurmas) {
                if (t.getNome().equalsIgnoreCase(nome)) return t;
            }
            return null;
        }

        public void remover(long id) {
            bancoTurmas.removeIf(t -> t.getId() == id);
        }

        // listar turmas de um professor específico
        public List<Turma> buscarPorProfessor(long idProfessor) {
            List<Turma> resultado = new ArrayList<>();
            for (Turma t : bancoTurmas) {
                if (t.getProfessorResponsavel() != null && t.getProfessorResponsavel().getId() == idProfessor) {
                    resultado.add(t);
                }
            }
            return resultado;
        }

}
