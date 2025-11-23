package view;

import Exception.ValidacaoException;
import model.Professor;
import model.Turma;
import model.Usuario;
import Service.TurmaService;
import Service.UsuarioService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class TelaCadastroTurma extends JFrame {

    // Componentes
    private JTextField txtNome;
    private JSpinner spnAno;
    private JComboBox<Professor> cmbProfessor;

    private JButton btnSalvar;
    private JButton btnCancelar;

    private TurmaService turmaService;
    private UsuarioService usuarioService;

    public TelaCadastroTurma() {
        this.turmaService = new TurmaService();
        this.usuarioService = new UsuarioService();

        configurarJanela();
        inicializarComponentes();
        carregarProfessores(); // Carrega a lista
    }

    private void configurarJanela() {
        setTitle("Cadastro de Turma");
        // Tamanho fixo e confortável para não ficar gigante nem minuscula
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza
        setResizable(false); // Impede que o usuário quebre o layout esticando

        getContentPane().setBackground(new Color(60, 60, 60));
        setLayout(new BorderLayout());
    }

    private void inicializarComponentes() {
        // --- 1. TÍTULO (Topo) ---
        JLabel lblTitulo = new JLabel("Nova Turma", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(new EmptyBorder(15, 0, 15, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // --- 2. FORMULÁRIO (Centro) ---
        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBackground(new Color(78, 77, 77));
        // Cria uma "caixa" com borda ao redor dos campos
        painelForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5); // Espaçamento entre campos
        gbc.anchor = GridBagConstraints.WEST;   // Alinha à esquerda
        gbc.fill = GridBagConstraints.HORIZONTAL; // Estica a caixa de texto

        // -- Campo: Nome --
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0; // Label não estica
        JLabel lblNome = new JLabel("Nome (ex: 3º A):");
        lblNome.setFont(new Font("Arial", Font.BOLD, 12));
        painelForm.add(lblNome, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0; // Campo estica
        txtNome = new JTextField();
        painelForm.add(txtNome, gbc);

        // -- Campo: Ano Letivo --
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel lblAno = new JLabel("Ano Letivo:");
        lblAno.setFont(new Font("Arial", Font.BOLD, 12));
        painelForm.add(lblAno, gbc);

        // Configura Spinner para Ano (sem vírgula)
        int anoAtual = LocalDate.now().getYear();
        spnAno = new JSpinner(new SpinnerNumberModel(anoAtual, 2000, 2100, 1));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spnAno, "#");
        spnAno.setEditor(editor);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0;
        painelForm.add(spnAno, gbc);

        // -- Campo: Professor --
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0;
        JLabel lblProf = new JLabel("Professor Resp.:");
        lblProf.setFont(new Font("Arial", Font.BOLD, 12));
        painelForm.add(lblProf, gbc);

        cmbProfessor = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.weightx = 1.0;
        painelForm.add(cmbProfessor, gbc);

        // Adiciona o painel de formulário no centro da janela, com margens
        JPanel containerCentro = new JPanel(new BorderLayout());
        containerCentro.add(painelForm);
        containerCentro.setBorder(new EmptyBorder(0, 20, 0, 20)); // Margens laterais na janela
        containerCentro.setBackground(new Color(78, 77, 77)); // Cor de fundo igual à janela

        add(containerCentro, BorderLayout.CENTER);

        // --- 3. BOTÕES (Rodapé) ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBotoes.setBackground(new Color(78, 77, 77));
        painelBotoes.setBorder(new EmptyBorder(10, 20, 10, 20));

        btnCancelar = new JButton("Cancelar");

        btnSalvar = new JButton("Salvar Turma");
        btnSalvar.setBackground(new Color(100, 200, 100)); // Verde
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFont(new Font("Arial", Font.BOLD, 12));

        painelBotoes.add(btnCancelar);
        painelBotoes.add(btnSalvar);

        add(painelBotoes, BorderLayout.SOUTH);

        // --- EVENTOS ---
        btnSalvar.addActionListener(e -> salvarTurma());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void carregarProfessores() {
        cmbProfessor.removeAllItems();
        List<Usuario> todosUsuarios = usuarioService.listarTodos();

        boolean temProfessor = false;
        for (Usuario u : todosUsuarios) {
            // Filtra apenas quem é PROFESSOR
            if (u instanceof Professor) {
                cmbProfessor.addItem((Professor) u);
                temProfessor = true;
            }
        }

        if (!temProfessor) {
            // Adiciona um item fake só para não quebrar o layout visualmente
            cmbProfessor.addItem(null);
            JOptionPane.showMessageDialog(this, "Aviso: Nenhum professor cadastrado.\nVocê precisa cadastrar um usuário do tipo 'Professor' antes.");
        }
    }

    private void salvarTurma() {
        try {
            String nome = txtNome.getText();
            int ano = (int) spnAno.getValue();
            Professor profSelecionado = (Professor) cmbProfessor.getSelectedItem();

            if (profSelecionado == null) {
                throw new ValidacaoException("É obrigatório selecionar um Professor.");
            }

            // ID 0 = Nova Turma
            Turma novaTurma = new Turma(0, nome, ano, profSelecionado);

            turmaService.salvar(novaTurma);

            JOptionPane.showMessageDialog(this, "Turma '" + nome + "' salva com sucesso!");
            dispose(); // Fecha a janela

        } catch (ValidacaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Validação", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }
}