package view;

import Exception.ValidacaoException;
import model.Emprestimo;
import Service.EmprestimoService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TelaDevolucao extends JFrame {

    // Componentes
    private JTextField txtIdLivro;
    private JButton btnBuscar;

    // Painel de Detalhes (só aparece se achar o empréstimo)
    private JLabel lblAluno;
    private JLabel lblTituloLivro;
    private JLabel lblDataEmprestimo;
    private JLabel lblDataPrevista;
    private JLabel lblStatusPrazo; // "No prazo" ou "ATRASADO"

    private JButton btnConfirmarDevolucao;
    private JButton btnCancelar;

    // Controle
    private Emprestimo emprestimoEncontrado;
    private EmprestimoService emprestimoService;
    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public TelaDevolucao() {
        this.emprestimoService = new EmprestimoService();
        configurarJanela();
        inicializarComponentes();
    }

    private void configurarJanela() {
        setTitle("Devolução de Livros");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
    }

    private void inicializarComponentes() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // --- 1. BUSCA (ID DO LIVRO) ---
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("ID do Livro Devolvido:"), gbc);

        JPanel painelBusca = new JPanel(new BorderLayout(5, 0));
        txtIdLivro = new JTextField(15);
        btnBuscar = new JButton("Buscar Empréstimo");
        painelBusca.add(txtIdLivro, BorderLayout.CENTER);
        painelBusca.add(btnBuscar, BorderLayout.EAST);

        gbc.gridx = 1;
        add(painelBusca, gbc);

        // --- 2. DETALHES DO EMPRÉSTIMO ---
        // Título do Livro
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Livro:"), gbc);
        lblTituloLivro = new JLabel("---");
        lblTituloLivro.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridx = 1;
        add(lblTituloLivro, gbc);

        // Nome do Aluno
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Emprestado para:"), gbc);
        lblAluno = new JLabel("---");
        gbc.gridx = 1;
        add(lblAluno, gbc);

        // Datas
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Data Retirada:"), gbc);
        lblDataEmprestimo = new JLabel("---");
        gbc.gridx = 1;
        add(lblDataEmprestimo, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Data Prevista:"), gbc);
        lblDataPrevista = new JLabel("---");
        gbc.gridx = 1;
        add(lblDataPrevista, gbc);

        // Status (Multa)
        gbc.gridx = 0; gbc.gridy = 5;
        add(new JLabel("Situação:"), gbc);
        lblStatusPrazo = new JLabel("---");
        lblStatusPrazo.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 1;
        add(lblStatusPrazo, gbc);

        // --- 3. BOTÕES DE AÇÃO ---
        JPanel painelBotoes = new JPanel();
        btnConfirmarDevolucao = new JButton("Confirmar Devolução");
        btnConfirmarDevolucao.setBackground(new Color(100, 200, 100)); // Verde
        btnConfirmarDevolucao.setEnabled(false); // Só habilita se achar empréstimo

        btnCancelar = new JButton("Cancelar");

        painelBotoes.add(btnConfirmarDevolucao);
        painelBotoes.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(painelBotoes, gbc);

        // --- EVENTOS ---
        btnBuscar.addActionListener(e -> buscarEmprestimoAtivo());
        btnConfirmarDevolucao.addActionListener(e -> realizarDevolucao());
        btnCancelar.addActionListener(e -> dispose());
    }

    /**
     * Lógica: Procura nos empréstimos PENDENTES se existe algum com esse ID de Livro.
     */
    private void buscarEmprestimoAtivo() {
        String textoId = txtIdLivro.getText().trim();
        if (textoId.isEmpty()) return;

        try {
            long idLivro = Long.parseLong(textoId);
            List<Emprestimo> pendentes = emprestimoService.listarPendentes();

            emprestimoEncontrado = null;

            // Varre a lista procurando o livro
            for (Emprestimo e : pendentes) {
                if (e.getLivro().getId() == idLivro) {
                    emprestimoEncontrado = e;
                    break;
                }
            }

            if (emprestimoEncontrado != null) {
                exibirDetalhes(emprestimoEncontrado);
            } else {
                limparDetalhes();
                JOptionPane.showMessageDialog(this, "Nenhum empréstimo ativo encontrado para o Livro ID " + idLivro);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID do livro inválido.");
        }
    }

    private void exibirDetalhes(Emprestimo e) {
        lblTituloLivro.setText(e.getLivro().getTitulo());
        lblAluno.setText(e.getAluno().getNome() + " (Mat: " + e.getAluno().getMatricula() + ")");
        lblDataEmprestimo.setText(e.getDataEmprestimo().format(fmt));
        lblDataPrevista.setText(e.getDataDevolucaoPrevista().format(fmt));

        // Verifica atraso visualmente
        if (LocalDate.now().isAfter(e.getDataDevolucaoPrevista())) {
            long dias = ChronoUnit.DAYS.between(e.getDataDevolucaoPrevista(), LocalDate.now());
            lblStatusPrazo.setText("ATRASADO (" + dias + " dias)");
            lblStatusPrazo.setForeground(Color.RED);
        } else {
            lblStatusPrazo.setText("No Prazo");
            lblStatusPrazo.setForeground(new Color(0, 100, 0));
        }

        btnConfirmarDevolucao.setEnabled(true);
    }

    private void limparDetalhes() {
        lblTituloLivro.setText("---");
        lblAluno.setText("---");
        lblDataEmprestimo.setText("---");
        lblDataPrevista.setText("---");
        lblStatusPrazo.setText("---");
        lblStatusPrazo.setForeground(Color.BLACK);
        btnConfirmarDevolucao.setEnabled(false);
        emprestimoEncontrado = null;
    }

    private void realizarDevolucao() {
        if (emprestimoEncontrado == null) return;

        try {
            // Chama o serviço para efetivar (ele calcula a multa internamente e retorna mensagem)
            String resultado = emprestimoService.registrarDevolucao(emprestimoEncontrado.getId());

            JOptionPane.showMessageDialog(this, resultado);

            // Limpa tudo para o próximo
            txtIdLivro.setText("");
            limparDetalhes();

        } catch (ValidacaoException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }
}