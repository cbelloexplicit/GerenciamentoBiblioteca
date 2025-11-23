package persistence;

import model.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private static final String ARQUIVO = "dados/usuarios.csv";
    private static List<Usuario> bancoUsuarios = new ArrayList<>();
    private static long proximoId = 1;

    static {
        List<String> linhas = CsvUtil.lerArquivo(ARQUIVO);

        if (linhas.isEmpty()) {
            // SEED DE SEGURANÇA: Se o arquivo não existe, cria o Admin padrão
            // Isso evita que você fique trancado fora do sistema na primeira execução
            System.out.println("Arquivo de usuários vazio. Criando Admin padrão...");
            Usuario admin = new Administrador("Admin Principal", "admin", "123");
            salvarInterno(admin);
            salvarEmArquivo(); // Cria o arquivo físico
        } else {
            carregarDoArquivo(linhas);
        }
    }

    // --- LÓGICA DE LEITURA (CSV -> OBJETO) ---
    private static void carregarDoArquivo(List<String> linhas) {
        long maiorId = 0;

        System.out.println("--- INICIANDO CARGA DE USUÁRIOS ---");

        for (String linha : linhas) {
            try {
                if (linha.trim().isEmpty()) continue;

                String[] dados = linha.split(";", -1);

                System.out.println("Lendo: " + linha); // DEBUG

                if (dados.length < 6) {
                    System.out.println("ERRO: Linha com colunas insuficientes: " + dados.length);
                    continue;
                }

                long id = Long.parseLong(dados[0]);

                String tipo = dados[1].trim().toUpperCase();

                String nome = dados[2];
                String matricula = dados[3];
                String senha = dados[4];
                boolean ativo = Boolean.parseBoolean(dados[5]);

                Usuario u = null;

                switch (tipo) {
                    case "ADMINISTRADOR":
                    case "ADMIN": // Garantia extra
                        u = new Administrador(id, nome, matricula, senha, ativo);
                        break;

                    case "BIBLIOTECARIO":
                    case "BIBLIOTECÁRIO":
                        u = new Bibliotecario(id, nome, matricula, senha, ativo);
                        break;

                    case "PROFESSOR":
                        u = new Professor(id, nome, matricula, senha, ativo);
                        break;

                    case "ALUNO":
                        String turma = (dados.length > 6) ? dados[6] : "";
                        LocalDate dataNasc = null;
                        // Tratamento robusto de data
                        if (dados.length > 7 && dados[7] != null && !dados[7].equals("null") && !dados[7].trim().isEmpty()) {
                            try {
                                dataNasc = LocalDate.parse(dados[7].trim());
                            } catch (Exception e) {
                                System.out.println("Aviso: Data inválida para aluno " + nome);
                            }
                        }
                        u = new Aluno(id, nome, matricula, senha, ativo, turma, dataNasc);
                        break;

                    default:
                        System.out.println("ALERTA: Tipo desconhecido encontrado: [" + tipo + "]");
                        break;
                }

                if (u != null) {
                    bancoUsuarios.add(u);
                    System.out.println("SUCCESS: Carregado " + u.getNome() + " (" + u.getTipo() + ")");
                    if (id > maiorId) maiorId = id;
                }

            } catch (Exception e) {
                System.err.println("ERRO CRÍTICO ao processar linha: " + linha);
                e.printStackTrace();
            }
        }
        proximoId = maiorId + 1;
        System.out.println("--- CARGA FINALIZADA: " + bancoUsuarios.size() + " usuários ---");
    }

    // --- LÓGICA DE GRAVAÇÃO (OBJETO -> CSV) ---
    private static void salvarEmArquivo() {
        List<String> linhas = new ArrayList<>();

        for (Usuario u : bancoUsuarios) {
            StringBuilder sb = new StringBuilder();

            // 1. Garante que o tipo não é nulo
            String tipo = u.getTipo();
            if (tipo == null) tipo = "DESCONHECIDO";

            // 2. Monta os dados comuns
            // Layout: id;tipo;nome;matricula;senha;ativo
            sb.append(u.getId()).append(";")
                    .append(tipo.toUpperCase()).append(";") // Força maiúsculo
                    .append(u.getNome()).append(";")
                    .append(u.getMatricula()).append(";")
                    .append(u.getSenha()).append(";")
                    .append(u.isAtivo());

            // 3. Monta os dados extras (Polimorfismo)
            if (u instanceof Aluno) {
                Aluno a = (Aluno) u;
                sb.append(";").append(a.getTurma() == null ? "" : a.getTurma());
                sb.append(";").append(a.getDataNascimento() == null ? "null" : a.getDataNascimento().toString());
            }
            // IMPORTANTE: O 'else' abaixo garante que Admin/Prof/Biblio
            // tenham as colunas extras vazias para não quebrar o CSV
            else {
                sb.append("; ;null");
            }

            linhas.add(sb.toString());
        }

        // false = Sobrescreve o arquivo inteiro com a lista atualizada
        CsvUtil.escreverArquivo(ARQUIVO, linhas, false);
    }

    // --- MÉTODOS CRUD ---

    private static void salvarInterno(Usuario u) {
        if (u.getId() == 0) u.setId(proximoId++);
        bancoUsuarios.add(u);
    }

    public void salvar(Usuario usuario) {
        // Se for edição, remove a versão antiga da lista
        bancoUsuarios.removeIf(u -> u.getId() == usuario.getId());

        // Gera ID se for novo
        if (usuario.getId() == 0) {
            usuario.setId(proximoId++);
        }

        // Adiciona na lista
        bancoUsuarios.add(usuario);

        // Persiste no arquivo
        salvarEmArquivo();
        System.out.println("Usuário salvo: " + usuario.getNome() + " (" + usuario.getTipo() + ")");
    }

    public void remover(long id) {
        boolean removeu = bancoUsuarios.removeIf(u -> u.getId() == id);
        if (removeu) {
            salvarEmArquivo();
        }
    }

    // --- MÉTODOS DE CONSULTA (Leitura em Memória - Rápido) ---

    public List<Usuario> listarTodos() {
        return new ArrayList<>(bancoUsuarios);
    }

    public List<Usuario> listarApenasAlunos() {
        List<Usuario> alunos = new ArrayList<>();
        for (Usuario u : bancoUsuarios) {
            if (u instanceof Aluno) {
                alunos.add(u);
            }
        }
        return alunos;
    }

    public List<Usuario> listarApenasAdmin() {
        List<Usuario> admin = new ArrayList<>();
        for (Usuario u : bancoUsuarios) {
            if (u instanceof Administrador) {
                admin.add(u);
            }
        }
        return admin;
    }

    public List<Usuario> listarApenasBibliotec() {
        List<Usuario> bibliotec = new ArrayList<>();
        for (Usuario u : bancoUsuarios) {
            if (u instanceof Bibliotecario) {
                bibliotec.add(u);
            }
        }
        return bibliotec;
    }

    public List<Usuario> listarApenasProf() {
        List<Usuario> prof = new ArrayList<>();
        for (Usuario u : bancoUsuarios) {
            if (u instanceof Professor) {
                prof.add(u);
            }
        }
        return prof;
    }

    public Usuario buscarPorMatricula(String matricula) {
        for (Usuario u : bancoUsuarios) {
            if (u.getMatricula().equalsIgnoreCase(matricula)) {
                return u;
            }
        }
        return null;
    }

    public Usuario buscarPorId(long id) {
        for (Usuario u : bancoUsuarios) {
            if (u.getId() == id) {
                return u;
            }
        }
        return null;
    }
}