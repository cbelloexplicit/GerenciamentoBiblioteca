package view;

import Exception.ValidacaoException;
import Service.TurmaService;
import model.*;
import Service.UsuarioService;
import java.util.List;
import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TelaCadastroUsuario extends JFrame {

    // Componentes visuais
    private JTextField txtNome;
    private JTextField txtMatricula;
    private JPasswordField txtSenha;
    private JComboBox<String> cmbTipoUsuario;

    // Campos dinâmicos
    private JLabel lblCampoExtra;
    private JTextField txtCampoExtra; // Para Departamento (Professor)
    private JComboBox<Turma> cmbTurma; // NOVO: Para Turma (Aluno)

    private JLabel lblDataNasc;
    private JFormattedTextField txtDataNasc;

    private JButton btnSalvar;
    private JButton btnCancelar;

    // Services
    private UsuarioService usuarioService;
    private TurmaService turmaService; // NOVO

    public TelaCadastroUsuario() {
        this.usuarioService = new UsuarioService();
        this.turmaService = new TurmaService(); // Inicializa

        configurarJanela();
        inicializarComponentes();
        configurarEventos();

        // Estado inicial
        atualizarCamposExtras("ALUNO");
    }

    private void configurarJanela() {
        setTitle("Cadastro de Usuário");
        setSize(550, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
    }

    private void inicializarComponentes() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Espaçamento
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- 1. Nome ---
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Nome Completo:"), gbc);
        txtNome = new JTextField(25);
        gbc.gridx = 1;
        add(txtNome, gbc);

        // --- 2. Matrícula ---
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Matrícula/Login:"), gbc);
        txtMatricula = new JTextField(15);
        gbc.gridx = 1;
        add(txtMatricula, gbc);

        // --- 3. Senha ---
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Senha:"), gbc);
        txtSenha = new JPasswordField(15);
        gbc.gridx = 1;
        add(txtSenha, gbc);

        // --- 4. Tipo de Usuário ---
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Tipo de Usuário:"), gbc);
        String[] tipos = {"ALUNO", "PROFESSOR", "BIBLIOTECARIO", "ADMINISTRADOR"};
        cmbTipoUsuario = new JComboBox<>(tipos);
        gbc.gridx = 1;
        add(cmbTipoUsuario, gbc);

        // --- 5. Campo Extra (Híbrido: Texto ou Combo) ---
        gbc.gridx = 0; gbc.gridy = 4;
        lblCampoExtra = new JLabel("Turma:");
        add(lblCampoExtra, gbc);

        // Campo Texto (Para Departamento)
        txtCampoExtra = new JTextField(15);
        txtCampoExtra.setVisible(false); // Começa escondido
        gbc.gridx = 1;
        add(txtCampoExtra, gbc);

        // Campo Combo (Para Turma - NOVO)
        cmbTurma = new JComboBox<>();
        carregarTurmasNoCombo(); // Preenche a lista
        gbc.gridx = 1;
        add(cmbTurma, gbc); // Adiciona na MESMA posição do txtCampoExtra

        // --- 6. Data Nascimento ---
        gbc.gridx = 0; gbc.gridy = 5;
        lblDataNasc = new JLabel("Data Nasc. (dd/mm/aaaa):");
        add(lblDataNasc, gbc);

        try {
            MaskFormatter mascaraData = new MaskFormatter("##/##/####");
            mascaraData.setPlaceholderCharacter('_');
            txtDataNasc = new JFormattedTextField(mascaraData);
        } catch (Exception e) {
            txtDataNasc = new JFormattedTextField();
        }
        gbc.gridx = 1;
        add(txtDataNasc, gbc);

        // --- 7. Botões ---
        JPanel painelBotoes = new JPanel();
        btnSalvar = new JButton("Salvar Usuário");
        btnSalvar.setBackground(new Color(100, 200, 100));
        btnCancelar = new JButton("Cancelar");

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(painelBotoes, gbc);
    }

    private void carregarTurmasNoCombo() {
        cmbTurma.removeAllItems();
        List<Turma> turmas = turmaService.listarTodas();
        for (Turma t : turmas) {
            cmbTurma.addItem(t); // O Java usa o toString() da Turma para exibir o nome
        }
    }

    private void configurarEventos() {
        btnCancelar.addActionListener(e -> dispose());
        btnSalvar.addActionListener(e -> salvarUsuario());

        // Evento ao mudar o tipo de usuário
        cmbTipoUsuario.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                atualizarCamposExtras((String) e.getItem());
            }
        });
    }

    // Lógica para alternar entre Digitar Departamento e Selecionar Turma
    private void atualizarCamposExtras(String tipo) {
        if (tipo.equals("ALUNO")) {
            // Configuração para Aluno
            lblCampoExtra.setText("Selecione a Turma:");
            lblCampoExtra.setVisible(true);

            txtCampoExtra.setVisible(false); // Esconde texto
            cmbTurma.setVisible(true);       // Mostra combo

            lblDataNasc.setVisible(true);
            txtDataNasc.setVisible(true);

        } else if (tipo.equals("PROFESSOR")) {
            // Configuração para Professor
            lblCampoExtra.setText("Departamento:");
            lblCampoExtra.setVisible(true);

            txtCampoExtra.setVisible(true);  // Mostra texto
            cmbTurma.setVisible(false);      // Esconde combo

            lblDataNasc.setVisible(false);
            txtDataNasc.setVisible(false);

        } else {
            // Configuração para Admin/Bibliotecário
            lblCampoExtra.setVisible(false);
            txtCampoExtra.setVisible(false);
            cmbTurma.setVisible(false);
            lblDataNasc.setVisible(false);
            txtDataNasc.setVisible(false);
        }
    }

    private void salvarUsuario() {
        try {
            String nome = txtNome.getText();
            String matricula = txtMatricula.getText();
            String senha = new String(txtSenha.getPassword());
            String tipo = (String) cmbTipoUsuario.getSelectedItem();

            Usuario novoUsuario = null;

            switch (tipo) {
                case "ALUNO":
                    Turma turmaSelecionada = (Turma) cmbTurma.getSelectedItem();
                    if (turmaSelecionada == null) throw new ValidacaoException("Selecione uma turma.");

                    // Como seu Aluno Model usa String para turma, pegamos o nome dela
                    String nomeTurma = turmaSelecionada.getNome();

                    // Tratamento da Data
                    String dataTexto = txtDataNasc.getText();
                    LocalDate dataNasc = null;
                    if (dataTexto != null && !dataTexto.equals("__/__/____")) {
                        try {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                            dataNasc = LocalDate.parse(dataTexto, formatter);
                        } catch (DateTimeParseException e) {
                            throw new ValidacaoException("Data de nascimento inválida.");
                        }
                    }

                    novoUsuario = new Aluno(nome, matricula, senha, nomeTurma, dataNasc);
                    break;

                case "PROFESSOR":
                    novoUsuario = new Professor(nome, matricula, senha);
                    break;

                case "BIBLIOTECARIO":
                case "BIBLIOTECÁRIO":
                    novoUsuario = new Bibliotecario(nome, matricula, senha);
                    break;

                case "ADMINISTRADOR":
                    novoUsuario = new Administrador(nome, matricula, senha);
                    break;
            }

            if (novoUsuario != null) {
                usuarioService.salvar(novoUsuario);
                JOptionPane.showMessageDialog(this, tipo + " cadastrado com sucesso!");
                dispose();
            }

        } catch (ValidacaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Validação", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro crítico: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}