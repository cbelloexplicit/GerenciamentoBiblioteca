package view;

import Exception.ValidacaoException;
import model.Aluno;
import model.Livro;
import model.Usuario;
import Service.EmprestimoService;
import Service.LivroService;
import Service.UsuarioService;

import javax.swing.*;
import java.awt.*;

public class TelaRealizarEmprestimo extends JFrame {

    // Componentes Visuais
    private JTextField txtMatriculaAluno;
    private JLabel lblNomeAluno;
    private JButton btnBuscarAluno;

    private JTextField txtIdLivro;
    private JLabel lblTituloLivro;
    private JLabel lblStatusLivro;
    private JButton btnBuscarLivro;

    private JButton btnConfirmar;
    private JButton btnCancelar;

    // Controle de Estado (Armazena quem foi encontrado na busca)
    private Aluno alunoSelecionado;
    private Livro livroSelecionado;

    // Services
    private EmprestimoService emprestimoService;
    private UsuarioService usuarioService;
    private LivroService livroService;

    public TelaRealizarEmprestimo() {
        // Inicializa os serviços
        this.emprestimoService = new EmprestimoService();
        this.usuarioService = new UsuarioService();
        this.livroService = new LivroService();

        configurarJanela();
        inicializarComponentes();
    }

    private void configurarJanela() {
        setTitle("Registrar Novo Empréstimo");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
    }

    private void inicializarComponentes() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // --- SEÇÃO ALUNO ---

        // 1. Label e Campo Matrícula
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Matrícula do Aluno:"), gbc);

        JPanel painelBuscaAluno = new JPanel(new BorderLayout(5, 0));
        txtMatriculaAluno = new JTextField(15);
        btnBuscarAluno = new JButton("Buscar");
        painelBuscaAluno.add(txtMatriculaAluno, BorderLayout.CENTER);
        painelBuscaAluno.add(btnBuscarAluno, BorderLayout.EAST);

        gbc.gridx = 1;
        add(painelBuscaAluno, gbc);

        // 2. Feedback do Aluno (Nome encontrado)
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Aluno Selecionado:"), gbc);

        lblNomeAluno = new JLabel("[Nenhum aluno selecionado]");
        lblNomeAluno.setFont(new Font("Arial", Font.BOLD, 14));
        lblNomeAluno.setForeground(Color.DARK_GRAY);
        gbc.gridx = 1;
        add(lblNomeAluno, gbc);

        // Separador visual
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(new JSeparator(), gbc);
        gbc.gridwidth = 1; // Reset

        // --- SEÇÃO LIVRO ---

        // 3. Label e Campo ID Livro
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("ID do Livro:"), gbc);

        JPanel painelBuscaLivro = new JPanel(new BorderLayout(5, 0));
        txtIdLivro = new JTextField(15);
        btnBuscarLivro = new JButton("Buscar");
        painelBuscaLivro.add(txtIdLivro, BorderLayout.CENTER);
        painelBuscaLivro.add(btnBuscarLivro, BorderLayout.EAST);

        gbc.gridx = 1;
        add(painelBuscaLivro, gbc);

        // 4. Feedback do Livro (Título encontrado)
        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Livro Selecionado:"), gbc);

        lblTituloLivro = new JLabel("[Nenhum livro selecionado]");
        lblTituloLivro.setFont(new Font("Arial", Font.BOLD, 14));
        lblTituloLivro.setForeground(Color.DARK_GRAY);
        gbc.gridx = 1;
        add(lblTituloLivro, gbc);

        // 5. Status do Estoque
        lblStatusLivro = new JLabel("");
        gbc.gridx = 1; gbc.gridy = 5;
        add(lblStatusLivro, gbc);

        // --- BOTÕES DE AÇÃO ---
        JPanel painelBotoes = new JPanel();
        btnConfirmar = new JButton("CONFIRMAR EMPRÉSTIMO");
        btnConfirmar.setBackground(new Color(100, 200, 100));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFont(new Font("Arial", Font.BOLD, 12));
        btnConfirmar.setEnabled(false); // Só habilita se buscar aluno e livro

        btnCancelar = new JButton("Cancelar");

        painelBotoes.add(btnConfirmar);
        painelBotoes.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(painelBotoes, gbc);

        // --- EVENTOS ---
        configurarEventos();
    }

    private void configurarEventos() {

        // Botão Buscar Aluno
        btnBuscarAluno.addActionListener(e -> buscarAluno());

        // Botão Buscar Livro
        btnBuscarLivro.addActionListener(e -> buscarLivro());

        // Botão Confirmar
        btnConfirmar.addActionListener(e -> realizarEmprestimo());

        // Botão Cancelar
        btnCancelar.addActionListener(e -> dispose());
    }

    private void buscarAluno() {
        String matricula = txtMatriculaAluno.getText().trim();
        if (matricula.isEmpty()) return;

        Usuario usuario = usuarioService.buscarPorMatricula(matricula);

        if (usuario != null && usuario instanceof Aluno) {
            alunoSelecionado = (Aluno) usuario;
            lblNomeAluno.setText(alunoSelecionado.getNome());
            lblNomeAluno.setForeground(new Color(0, 100, 0)); // Verde
            verificarHabilitarBotao();
        } else {
            alunoSelecionado = null;
            lblNomeAluno.setText("Aluno não encontrado ou não é aluno.");
            lblNomeAluno.setForeground(Color.RED);
            btnConfirmar.setEnabled(false);
        }
    }

    private void buscarLivro() {
        String idTexto = txtIdLivro.getText().trim();
        if (idTexto.isEmpty()) return;

        try {
            long id = Long.parseLong(idTexto);
            Livro livro = livroService.buscarPorId(id);

            if (livro != null) {
                livroSelecionado = livro;
                lblTituloLivro.setText(livro.getTitulo());
                lblTituloLivro.setForeground(new Color(0, 100, 0)); // Verde

                // Mostra status do estoque
                int disponiveis = livro.getCopiasDisponiveis();
                if (disponiveis > 0) {
                    lblStatusLivro.setText("Disponível: " + disponiveis + " cópias");
                    lblStatusLivro.setForeground(new Color(0, 100, 0));
                } else {
                    lblStatusLivro.setText("INDISPONÍVEL (0 cópias)");
                    lblStatusLivro.setForeground(Color.RED);
                    livroSelecionado = null; // Impede selecionar livro sem estoque
                }

                verificarHabilitarBotao();

            } else {
                livroSelecionado = null;
                lblTituloLivro.setText("Livro não encontrado (ID inválido).");
                lblTituloLivro.setForeground(Color.RED);
                lblStatusLivro.setText("");
                btnConfirmar.setEnabled(false);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "O ID do livro deve ser um número.");
        }
    }

    private void verificarHabilitarBotao() {
        // Só libera o botão confirmar se ambos estiverem carregados
        btnConfirmar.setEnabled(alunoSelecionado != null && livroSelecionado != null);
    }

    private void realizarEmprestimo() {
        try {
            // Chama o Service que baixa estoque, salva histórico
            emprestimoService.registrarEmprestimo(alunoSelecionado, livroSelecionado);

            JOptionPane.showMessageDialog(this,
                    "Empréstimo realizado com sucesso!\n" +
                            "Aluno: " + alunoSelecionado.getNome() + "\n" +
                            "Livro: " + livroSelecionado.getTitulo() + "\n" +
                            "Devolução prevista em 7 dias.");

            // Limpa a tela para o próximo
            limparTela();

        } catch (ValidacaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro ao Emprestar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparTela() {
        txtMatriculaAluno.setText("");
        txtIdLivro.setText("");
        lblNomeAluno.setText("[Nenhum aluno selecionado]");
        lblNomeAluno.setForeground(Color.DARK_GRAY);
        lblTituloLivro.setText("[Nenhum livro selecionado]");
        lblTituloLivro.setForeground(Color.DARK_GRAY);
        lblStatusLivro.setText("");

        alunoSelecionado = null;
        livroSelecionado = null;
        btnConfirmar.setEnabled(false);
    }
}