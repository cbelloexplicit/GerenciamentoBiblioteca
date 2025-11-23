package view;

import Exception.ValidacaoException;
import model.Genero;
import Service.GeneroService;

import javax.swing.*;
import java.awt.*;

public class TelaCadastroGenero extends JFrame {

    private JTextField txtNome;
    private JButton btnSalvar;
    private JButton btnCancelar;

    private GeneroService generoService;

    public TelaCadastroGenero() {
        this.generoService = new GeneroService();
        configurarJanela();
        inicializarComponentes();
    }

    private void configurarJanela() {
        setTitle("Cadastro de Gênero Literário");
        setSize(400, 200); // Janela menor pois tem poucos campos
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
    }

    private void inicializarComponentes() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Campo Nome ---
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Nome do Gênero:"), gbc);

        txtNome = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0;
        add(txtNome, gbc);

        // --- Botões ---
        JPanel painelBotoes = new JPanel();
        btnSalvar = new JButton("Salvar");
        btnSalvar.setBackground(new Color(100, 200, 100));

        btnCancelar = new JButton("Cancelar");

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(painelBotoes, gbc);

        // --- Ações ---
        btnSalvar.addActionListener(e -> salvarGenero());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void salvarGenero() {
        try {
            String nome = txtNome.getText();

            // Cria o objeto (ID 0 pois é novo)
            Genero novoGenero = new Genero(nome);

            // O Service valida se o nome está vazio ou se já existe duplicado
            generoService.salvar(novoGenero);

            JOptionPane.showMessageDialog(this, "Gênero '" + nome + "' cadastrado com sucesso!");
            dispose();

        } catch (ValidacaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage());
        }
    }
}