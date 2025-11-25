package view;

import Exception.AutenticacaoException;
import model.Usuario;
import Service.LoginService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TelaLogin extends JFrame {

    private JTextField campoMatricula;
    private JPasswordField campoSenha;
    private JButton botaoEntrar;
    private JButton botaoSair;
    private JButton btnRelatorioLogs;

    private LoginService loginService;

    public TelaLogin() {
        this.loginService = new LoginService();
        configurarJanela();
        inicializarComponentes();
    }

    private void configurarJanela() {
        setTitle("SGBE - Acesso");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza na tela
        setResizable(false);

        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(33, 32, 32));
    }

    private void inicializarComponentes() {
        // --- 1. PAINEL PRINCIPAL ---
        JPanel painelCaixa = new JPanel();
        painelCaixa.setLayout(new BorderLayout(10, 10));
        painelCaixa.setBackground(new Color(78, 77, 77));
        painelCaixa.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // --- 2. TÍTULO (Topo) ---
        JLabel labelTitulo = new JLabel("Login do Sistema", SwingConstants.CENTER);
        labelTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        labelTitulo.setForeground(Color.WHITE);
        labelTitulo.setBorder(new EmptyBorder(0, 0, 15, 0));
        painelCaixa.add(labelTitulo, BorderLayout.NORTH);

        // --- 3. FORMULÁRIO (Centro - Labels e Campos) ---
        JPanel painelForm = new JPanel(new GridLayout(2, 2, 5, 10));
        painelForm.setBackground(new Color(78, 77, 77));

        JLabel lblMatricula = new JLabel("Matrícula:");
        lblMatricula.setFont(new Font("Arial", Font.PLAIN, 14));

        campoMatricula = new JTextField();
        campoMatricula.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setFont(new Font("Arial", Font.PLAIN, 14));

        campoSenha = new JPasswordField();
        campoSenha.setFont(new Font("Arial", Font.PLAIN, 14));

        // Adiciona na ordem de leitura: Label -> Campo -> Label -> Campo
        painelForm.add(lblMatricula);
        painelForm.add(campoMatricula);
        painelForm.add(lblSenha);
        painelForm.add(campoSenha);

        painelCaixa.add(painelForm, BorderLayout.CENTER);

        // --- 4. BOTÕES (Rodapé) ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotoes.setBackground(new Color(78, 77, 77));
        painelBotoes.setBorder(new EmptyBorder(15, 0, 0, 0));

        botaoEntrar = new JButton("Entrar");
        botaoEntrar.setBackground(new Color(70, 130, 180)); // Azul
        botaoEntrar.setForeground(Color.WHITE);
        botaoEntrar.setFont(new Font("Arial", Font.BOLD, 12));
        botaoEntrar.setPreferredSize(new Dimension(100, 30));

        botaoSair = new JButton("Sair");
        botaoSair.setBackground(new Color(200, 80, 80)); // Vermelho
        botaoSair.setForeground(Color.WHITE);
        botaoSair.setPreferredSize(new Dimension(80, 30));

        painelBotoes.add(botaoEntrar);
        painelBotoes.add(botaoSair);

        painelCaixa.add(painelBotoes, BorderLayout.SOUTH);

        add(painelCaixa);

        // --- EVENTOS ---
        configurarEventos();
    }

    private void configurarEventos() {
        botaoSair.addActionListener(e -> System.exit(0));

        botaoEntrar.addActionListener(e -> tentarLogin());

        // Permitir apertar ENTER na senha
        campoSenha.addActionListener(e -> tentarLogin());
    }

    private void tentarLogin() {
        String matricula = campoMatricula.getText();
        String senha = new String(campoSenha.getPassword());

        try {
            Usuario usuarioLogado = loginService.logar(matricula, senha);

            // Abre o Menu Principal
            MenuPrincipal menu = new MenuPrincipal(usuarioLogado);
            menu.setVisible(true);
            this.dispose();

        } catch (AutenticacaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Acesso", JOptionPane.ERROR_MESSAGE);
        }
    }
}