package view;

import Exception.AutenticacaoException;
import model.Usuario;
import Service.LoginService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaLogin extends JFrame {

    private JTextField campoMatricula;
    private JPasswordField campoSenha;
    private JButton botaoEntrar;
    private JButton botaoSair;

    // Conecta tela e Login
    private LoginService loginService;

    public TelaLogin() {
        this.loginService = new LoginService();
        configurarJanela();
        inicializarComponentes();
    }

    private void configurarJanela() {
        setTitle("SGBE - Sistema de Biblioteca Escolar");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void inicializarComponentes() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // --- Título ---
        JLabel labelTitulo = new JLabel("Acesso ao Sistema");
        labelTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Ocupa 2 colunas
        painel.add(labelTitulo, gbc);

        // --- Label Matrícula ---
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST; // Alinha à direita
        painel.add(new JLabel("Matrícula:"), gbc);

        // --- Campo Matrícula ---
        campoMatricula = new JTextField(15);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST; // Alinha à esquerda
        painel.add(campoMatricula, gbc);

        // --- Label Senha ---
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        painel.add(new JLabel("Senha:"), gbc);

        // --- Campo Senha ---
        campoSenha = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        painel.add(campoSenha, gbc);

        // --- Botões (Painel interno para alinhar) ---
        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.setBackground(new Color(240, 248, 255));

        botaoEntrar = new JButton("Entrar");
        botaoSair = new JButton("Sair");

        painelBotoes.add(botaoEntrar);
        painelBotoes.add(botaoSair);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        painel.add(painelBotoes, gbc);

        add(painel);

        // --- AÇÕES DOS BOTÕES ---

        botaoEntrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tentarLogin();
            }
        });

        botaoSair.addActionListener(e -> System.exit(0));

        campoSenha.addActionListener(e -> tentarLogin());
    }

    private void tentarLogin() {
        String matricula = campoMatricula.getText();
        String senha = new String(campoSenha.getPassword());

        try {
            Usuario usuarioLogado = loginService.logar(matricula, senha);

            JOptionPane.showMessageDialog(this, "Bem-vindo(a), " + usuarioLogado.getNome() + "!");

            // Abre o Menu Principal
            MenuPrincipal menu = new MenuPrincipal(usuarioLogado);
            menu.setVisible(true);

            this.dispose();

        } catch (AutenticacaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Acesso", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- MAIN PARA TESTE RÁPIDO ---
    public static void main(String[] args) {
        // Tenta deixar com o visual do Windows/Mac
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new TelaLogin().setVisible(true);
        });
    }
}
