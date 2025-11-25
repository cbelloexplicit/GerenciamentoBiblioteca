package view;

import Exception.ValidacaoException;
import model.Aluno;
import model.Emprestimo;
import model.Exemplar;
import model.Usuario;
import Service.EmprestimoService;
import Service.ProgramaLeituraService; // Novo Import
import Service.UsuarioService;
import persistence.ExemplarDAO;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class TelaRealizarEmprestimo extends JFrame {

    private JTextField txtMatriculaAluno;
    private JLabel lblNomeAluno;
    private JButton btnBuscarAluno;
    private JTextArea txtInfoAluno;

    private JTextField txtIdExemplar;
    private JLabel lblTituloLivro;
    private JLabel lblStatusExemplar;
    private JButton btnBuscarExemplar;

    private JSpinner spnDiasPrazo;
    private JButton btnConfirmar;
    private JButton btnCancelar;

    private Aluno alunoSelecionado;
    private Exemplar exemplarSelecionado;

    private EmprestimoService emprestimoService;
    private UsuarioService usuarioService;
    private ExemplarDAO exemplarDAO;
    private ProgramaLeituraService programaService; // Novo Service

    public TelaRealizarEmprestimo() {
        this.emprestimoService = new EmprestimoService();
        this.usuarioService = new UsuarioService();
        this.exemplarDAO = new ExemplarDAO();
        this.programaService = new ProgramaLeituraService();

        configurarJanela();
        inicializarComponentes();
    }

    private void configurarJanela() {
        setTitle("Registrar Empréstimo");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        JPanel painelForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // 1. Aluno
        gbc.gridx = 0; gbc.gridy = 0; painelForm.add(new JLabel("Matrícula Aluno:"), gbc);

        JPanel pBuscaAluno = new JPanel(new BorderLayout(5, 0));
        txtMatriculaAluno = new JTextField(15);
        btnBuscarAluno = new JButton("Buscar");
        pBuscaAluno.add(txtMatriculaAluno, BorderLayout.CENTER);
        pBuscaAluno.add(btnBuscarAluno, BorderLayout.EAST);
        gbc.gridx = 1; painelForm.add(pBuscaAluno, gbc);

        gbc.gridx = 0; gbc.gridy = 1; painelForm.add(new JLabel("Nome:"), gbc);
        lblNomeAluno = new JLabel("---");
        lblNomeAluno.setFont(new Font("Arial", Font.BOLD, 14));
        lblNomeAluno.setForeground(Color.BLUE);
        gbc.gridx = 1; painelForm.add(lblNomeAluno, gbc);

        // 2. Exemplar
        gbc.gridx = 0; gbc.gridy = 2; painelForm.add(new JLabel("ID Exemplar:"), gbc);

        JPanel pBuscaEx = new JPanel(new BorderLayout(5, 0));
        txtIdExemplar = new JTextField(15);
        btnBuscarExemplar = new JButton("Buscar");
        pBuscaEx.add(txtIdExemplar, BorderLayout.CENTER);
        pBuscaEx.add(btnBuscarExemplar, BorderLayout.EAST);
        gbc.gridx = 1; painelForm.add(pBuscaEx, gbc);

        gbc.gridx = 0; gbc.gridy = 3; painelForm.add(new JLabel("Título:"), gbc);
        lblTituloLivro = new JLabel("---");
        lblTituloLivro.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 1; painelForm.add(lblTituloLivro, gbc);

        gbc.gridx = 0; gbc.gridy = 4; painelForm.add(new JLabel("Status:"), gbc);
        lblStatusExemplar = new JLabel("---");
        gbc.gridx = 1; painelForm.add(lblStatusExemplar, gbc);

        // 3. Prazo
        gbc.gridx = 0; gbc.gridy = 5; painelForm.add(new JLabel("Prazo (Dias):"), gbc);
        spnDiasPrazo = new JSpinner(new SpinnerNumberModel(7, 1, 60, 1));
        gbc.gridx = 1; painelForm.add(spnDiasPrazo, gbc);

        // Botões
        JPanel pBotoes = new JPanel();
        btnConfirmar = new JButton("CONFIRMAR");
        btnConfirmar.setBackground(new Color(0, 100, 0));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setEnabled(false);
        btnCancelar = new JButton("Cancelar");
        pBotoes.add(btnConfirmar);
        pBotoes.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        painelForm.add(pBotoes, gbc);

        add(painelForm, BorderLayout.CENTER);

        // --- PAINEL LATERAL (Situação do Aluno) ---
        JPanel painelLateral = new JPanel(new BorderLayout());
        painelLateral.setBorder(BorderFactory.createTitledBorder(null, "Situação do Aluno", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 12), Color.DARK_GRAY));
        painelLateral.setPreferredSize(new Dimension(280, 0));
        painelLateral.setBackground(new Color(245, 245, 245));

        txtInfoAluno = new JTextArea();
        txtInfoAluno.setEditable(false);
        txtInfoAluno.setMargin(new Insets(10, 10, 10, 10));
        txtInfoAluno.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtInfoAluno.setText("Busque um aluno para\nver seus empréstimos\ne reservas.");

        painelLateral.add(new JScrollPane(txtInfoAluno), BorderLayout.CENTER);
        add(painelLateral, BorderLayout.EAST);

        // --- EVENTOS ---
        btnBuscarAluno.addActionListener(e -> buscarAluno());
        btnBuscarExemplar.addActionListener(e -> buscarExemplar());
        btnConfirmar.addActionListener(e -> realizarEmprestimo());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void buscarAluno() {
        String matricula = txtMatriculaAluno.getText().trim();
        if (matricula.isEmpty()) return;

        Usuario usuario = usuarioService.buscarPorMatricula(matricula);

        if (usuario != null && usuario instanceof Aluno) {
            alunoSelecionado = (Aluno) usuario;
            lblNomeAluno.setText(alunoSelecionado.getNome());
            atualizarPainelLateral(); // Atualiza a info lateral
        } else {
            alunoSelecionado = null;
            lblNomeAluno.setText("Aluno não encontrado.");
            txtInfoAluno.setText("Aluno não encontrado.");
        }
        verificarHabilitarBotao();
    }

    private void atualizarPainelLateral() {
        StringBuilder sb = new StringBuilder();
        sb.append("ALUNO: ").append(alunoSelecionado.getNome()).append("\n");
        sb.append("---------------------------\n");

        // 1. Buscar Empréstimos Ativos
        List<Emprestimo> ativos = emprestimoService.buscarHistoricoAluno(alunoSelecionado);
        sb.append("EMPRÉSTIMOS ATIVOS:\n");
        boolean temAtivos = false;
        for (Emprestimo e : ativos) {
            if (e.isAberto()) {
                sb.append("[ID ").append(e.getExemplar().getId()).append("] ")
                        .append(e.getExemplar().getLivro().getTitulo())
                        .append("\n   Devolução: ").append(e.getDataDevolucaoPrevista())
                        .append(e.isAtrasado() ? " (ATRASADO!)" : "")
                        .append("\n\n");
                temAtivos = true;
            }
        }
        if (!temAtivos) sb.append(" (Nenhum)\n\n");

        // 2. Buscar Reservas (Programa de Leitura)
        sb.append("---------------------------\n");
        sb.append("RESERVAS (Programa):\n");
        try {
            Exemplar reservado = programaService.buscarReservaParaAluno(alunoSelecionado);
            if (reservado != null) {
                sb.append("★ ").append(reservado.getLivro().getTitulo())
                        .append("\n   EXEMPLAR RESERVADO: #").append(reservado.getId())
                        .append("\n   (Disponível para retirada)");
            } else {
                sb.append(" (Nenhuma reserva ativa)");
            }
        } catch (Exception e) {
            sb.append("Erro ao buscar reservas.");
        }

        txtInfoAluno.setText(sb.toString());
        // Rola para o topo
        txtInfoAluno.setCaretPosition(0);
    }

    private void buscarExemplar() {
        String textoId = txtIdExemplar.getText().trim();
        if (textoId.isEmpty()) return;

        try {
            long id = Long.parseLong(textoId);
            Exemplar ex = exemplarDAO.buscarPorId(id);

            if (ex != null) {
                exemplarSelecionado = ex;
                lblTituloLivro.setText(ex.getLivro().getTitulo());

                if (!ex.isDisponivel()) {
                    lblStatusExemplar.setText("INDISPONÍVEL (Emprestado)");
                    lblStatusExemplar.setForeground(Color.RED);
                } else if (ex.isReservado()) {
                    // Se está reservado, avisa, mas o sistema pode deixar passar
                    // SE for para o aluno correto (validado no Service ou visualmente aqui)
                    lblStatusExemplar.setText("RESERVADO (Verifique Aluno)");
                    lblStatusExemplar.setForeground(Color.ORANGE);
                } else {
                    lblStatusExemplar.setText("Disponível");
                    lblStatusExemplar.setForeground(new Color(0, 100, 0));
                }
            } else {
                exemplarSelecionado = null;
                lblTituloLivro.setText("Não encontrado.");
                lblStatusExemplar.setText("---");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID inválido.");
        }
        verificarHabilitarBotao();
    }

    private void verificarHabilitarBotao() {
        btnConfirmar.setEnabled(alunoSelecionado != null && exemplarSelecionado != null);
    }

    private void realizarEmprestimo() {
        try {
            int dias = (int) spnDiasPrazo.getValue();
            emprestimoService.registrarEmprestimo(alunoSelecionado, exemplarSelecionado, dias);
            JOptionPane.showMessageDialog(this, "Empréstimo realizado!");

            // Limpa e reseta
            txtIdExemplar.setText("");
            lblTituloLivro.setText("---");
            lblStatusExemplar.setText("---");
            exemplarSelecionado = null;

            // Atualiza painel lateral para mostrar o novo empréstimo
            atualizarPainelLateral();

        } catch (ValidacaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.WARNING_MESSAGE);
        }
    }
}