package view;

import model.Usuario;
import Service.LoginService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MenuPrincipal extends JFrame {

    private Usuario usuarioLogado;

    // Componentes (Agora são Botões, não MenuItems)
    private JButton btnCadUsuario;
    private JButton btnCadLivro;
    private JButton btnCadGenero;
    private JButton btnCadTurma;

    private JButton btnEmprestimo;
    private JButton btnDevolucao;

    private JButton btnProgramaLeitura;
    private JButton btnMinhasTurmas;
    private JButton btnMeuPainelAluno; // Novo para o aluno
    private JButton btnRelatorioLogs;
    private JButton btnPesquisaPublica;
    private JButton btnLogout;

    // Painel que agrupa os botões
    private JPanel painelGrid;

    public MenuPrincipal(Usuario usuario) {
        this.usuarioLogado = usuario;
        configurarJanela();
        inicializarComponentes();
        aplicarPermissoes();
    }

    private void configurarJanela() {
        setTitle("SGBE - Menu Principal | Olá, " + usuarioLogado.getNome());
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        // Layout principal centralizado
        setLayout(new GridBagLayout());
    }

    private void inicializarComponentes() {
        GridBagConstraints gbc = new GridBagConstraints();

        // --- 1. Cabeçalho (Título) ---
        JLabel lblTitulo = new JLabel("Sistema de Biblioteca Escolar");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(220, 220, 220));

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 30, 0);
        add(lblTitulo, gbc);

        // --- 2. Painel de Botões (Grid) ---
        // GridLayout(0, 3) = Linhas automáticas, 3 Colunas. Gap de 15px.
        painelGrid = new JPanel(new GridLayout(0, 4, 15, 15));
        painelGrid.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Inicializa Botões
        btnCadUsuario = criarBotao("Gerenciar Usuários", new Color(70, 130, 180));
        btnCadLivro = criarBotao("Gerenciar Acervo", new Color(70, 130, 180));
        btnCadGenero = criarBotao("Cadastrar Gênero", new Color(100, 149, 237));
        btnCadTurma = criarBotao("Cadastrar Turma", new Color(100, 149, 237));

        btnEmprestimo = criarBotao("Novo Empréstimo", new Color(60, 179, 113));
        btnDevolucao = criarBotao("Devolução", new Color(60, 179, 113));

        btnProgramaLeitura = criarBotao("Planejar Leitura", new Color(218, 165, 32));
        btnMinhasTurmas = criarBotao("Minhas Turmas", new Color(218, 165, 32));

        btnMeuPainelAluno = criarBotao("Meu Painel", new Color(147, 112, 219));
        btnPesquisaPublica = criarBotao("Pesquisar Livros", new Color(100, 149, 237)); // Azul Cornflower
        btnMeuPainelAluno = criarBotao("Meu Painel", new Color(147, 112, 219));
        btnRelatorioLogs = criarBotao("Logs de Acesso", new Color(112, 128, 144));

        painelGrid.add(btnCadUsuario);
        painelGrid.add(btnCadLivro);
        painelGrid.add(btnCadGenero);
        painelGrid.add(btnCadTurma);
        painelGrid.add(btnEmprestimo);
        painelGrid.add(btnDevolucao);
        painelGrid.add(btnProgramaLeitura);
        painelGrid.add(btnMinhasTurmas);
        painelGrid.add(btnMeuPainelAluno);
        painelGrid.add(btnCadUsuario);
        painelGrid.add(btnRelatorioLogs);
        painelGrid.add(btnPesquisaPublica);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(painelGrid, gbc);

        // --- 3. Botão Sair (Rodapé) ---
        btnLogout = new JButton("Sair do Sistema");
        btnLogout.setBackground(new Color(200, 80, 80));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setPreferredSize(new Dimension(150, 40));

        gbc.gridy = 2;
        gbc.insets = new Insets(40, 0, 0, 0); // Margem acima do botão sair
        add(btnLogout, gbc);

        // --- Configurar Ações ---
        configurarAcoes();
    }

    private JButton criarBotao(String texto, Color corFundo) {
        JButton btn = new JButton(texto);
        btn.setPreferredSize(new Dimension(180, 100)); // Botões grandes
        btn.setBackground(corFundo);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createRaisedBevelBorder());
        return btn;
    }

    private void aplicarPermissoes() {
        if (usuarioLogado == null) return;

        String tipo = usuarioLogado.getTipo().trim().toUpperCase();

        // DEBUG
        System.out.println("--- MENU PRINCIPAL ---");
        System.out.println("Usuário Logado: " + usuarioLogado.getNome());
        System.out.println("Tipo detectado (Processado): [" + tipo + "]");
        System.out.println("----------------------");

        esconderTodosBotoes();

        if (btnPesquisaPublica != null) btnPesquisaPublica.setVisible(true);
        btnLogout.setVisible(true);

        switch (tipo) {
            case "ADMINISTRADOR":
            case "ADMIN":
                btnCadUsuario.setVisible(true);
                btnCadLivro.setVisible(true);
                btnCadGenero.setVisible(true);
                btnCadTurma.setVisible(true);
                btnEmprestimo.setVisible(true);
                btnDevolucao.setVisible(true);
                if(btnRelatorioLogs != null) btnRelatorioLogs.setVisible(true);
                if(btnPesquisaPublica != null) btnPesquisaPublica.setVisible(true);
                break;

            case "BIBLIOTECARIO":
            case "BIBLIOTECÁRIO":
                btnCadLivro.setVisible(true);
                btnCadGenero.setVisible(true);
                btnEmprestimo.setVisible(true);
                btnDevolucao.setVisible(true);
                break;

            case "PROFESSOR":
                btnProgramaLeitura.setVisible(true);
                btnMinhasTurmas.setVisible(true);
                if(btnPesquisaPublica != null) btnPesquisaPublica.setVisible(true);
                break;

            case "ALUNO":
                btnMeuPainelAluno.setVisible(true);
                break;

            default:
                System.out.println("ERRO VISUAL: Tipo de usuário [" + tipo + "] não tem permissões configuradas no switch.");
                JOptionPane.showMessageDialog(this, "Seu perfil de usuário (" + tipo + ") não possui botões configurados.");
                break;
        }
    }

    private void esconderTodosBotoes() {
        btnCadUsuario.setVisible(false);
        btnCadLivro.setVisible(false);
        btnCadGenero.setVisible(false);
        btnCadTurma.setVisible(false);
        btnEmprestimo.setVisible(false);
        btnDevolucao.setVisible(false);
        btnProgramaLeitura.setVisible(false);
        btnMinhasTurmas.setVisible(false);
        btnMeuPainelAluno.setVisible(false);
        btnRelatorioLogs.setVisible(false);
        btnPesquisaPublica.setVisible(false);

    }

    private void configurarAcoes() {
        // ADMIN / CADASTROS
        btnCadUsuario.addActionListener(e -> new TelaGerenciarUsuarios().setVisible(true));
        btnCadLivro.addActionListener(e -> new TelaGerenciarAcervo().setVisible(true));
        btnCadGenero.addActionListener(e -> new TelaGerenciarGeneros().setVisible(true));
        btnCadTurma.addActionListener(e -> new TelaCadastroTurma().setVisible(true));
        btnRelatorioLogs.addActionListener(e -> new TelaRelatorioLogs().setVisible(true));

        // BIBLIOTECA
        btnEmprestimo.addActionListener(e -> new TelaRealizarEmprestimo().setVisible(true));
        btnDevolucao.addActionListener(e -> new TelaDevolucao().setVisible(true));

        // PROFESSOR
        btnProgramaLeitura.addActionListener(e -> new TelaProgramaLeitura().setVisible(true));
        btnMinhasTurmas.addActionListener(e -> new TelaMinhasTurmas(usuarioLogado).setVisible(true));


        // ALUNO
        btnMeuPainelAluno.addActionListener(e -> new TelaConsultaAluno(usuarioLogado).setVisible(true));
        btnPesquisaPublica.addActionListener(e -> new TelaPesquisaLivro().setVisible(true));
        // SAIR
        btnLogout.addActionListener(e -> {
            new LoginService().realizarLogout(usuarioLogado);
            dispose();
            new TelaLogin().setVisible(true);
        });
    }
}