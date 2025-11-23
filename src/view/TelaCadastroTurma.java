package view;

import Exception.ValidacaoException;
import model.Professor;
import model.Turma;
import model.Usuario;
import Service.TurmaService;
import Service.UsuarioService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class TelaCadastroTurma extends JFrame {

    private JTextField txtNome;
    private JSpinner spnAno;
    private JComboBox<Professor> cmbProfessor; // Lista suspensa de professores
    private JButton btnSalvar;
    private JButton btnCancelar;

    private TurmaService turmaService;
    private UsuarioService usuarioService;

    public TelaCadastroTurma() {
        this.turmaService = new TurmaService();
        this.usuarioService = new UsuarioService();

        configurarJanela();
        inicializarComponentes();
        carregarProfessores(); // Popula o ComboBox
    }

    private void configurarJanela() {
        setTitle("Cadastro de Turma");
        setSize(450, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
    }

    private void inicializarComponentes() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // --- 1. Nome da Turma ---
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Nome da Turma (ex: 3º A):"), gbc);

        txtNome = new JTextField(20);
        gbc.gridx = 1;
        add(txtNome, gbc);

        // --- 2. Ano Letivo ---
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Ano Letivo:"), gbc);

        // Spinner configurado para o ano atual
        int anoAtual = LocalDate.now().getYear();
        spnAno = new JSpinner(new SpinnerNumberModel(anoAtual, 2000, 2100, 1));
        // Remove a formatação de milhar (ex: 2.025 vira 2025)
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spnAno, "#");
        spnAno.setEditor(editor);

        gbc.gridx = 1;
        add(spnAno, gbc);

        // --- 3. Professor Responsável ---
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Professor Responsável:"), gbc);

        cmbProfessor = new JComboBox<>();
        gbc.gridx = 1;
        add(cmbProfessor, gbc);

        // --- Botões ---
        JPanel painelBotoes = new JPanel();
        btnSalvar = new JButton("Salvar Turma");
        btnSalvar.setBackground(new Color(100, 200, 100));

        btnCancelar = new JButton("Cancelar");

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(painelBotoes, gbc);

        // --- Eventos ---
        btnSalvar.addActionListener(e -> salvarTurma());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void carregarProfessores() {
        List<Usuario> todosUsuarios = usuarioService.listarTodos();

        for (Usuario u : todosUsuarios) {
            if (u instanceof Professor) {
                cmbProfessor.addItem((Professor) u);
            }
        }

        if (cmbProfessor.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Atenção: Nenhum professor cadastrado no sistema!");
        }
    }

    private void salvarTurma() {
        try {
            String nome = txtNome.getText();
            int ano = (int) spnAno.getValue();
            Professor profSelecionado = (Professor) cmbProfessor.getSelectedItem();

            if (profSelecionado == null) {
                throw new ValidacaoException("Selecione um professor.");
            }

            // Cria o objeto Turma (ID 0 pois é novo)
            Turma novaTurma = new Turma(0, nome, ano, profSelecionado);

            // Service valida e salva
            turmaService.salvar(novaTurma);

            JOptionPane.showMessageDialog(this, "Turma '" + nome + "' cadastrada com sucesso!");
            dispose();

        } catch (ValidacaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Validação", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }
}