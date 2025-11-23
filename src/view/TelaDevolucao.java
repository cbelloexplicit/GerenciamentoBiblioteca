package view;

import Exception.ValidacaoException;
import model.Emprestimo;
import Service.EmprestimoService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class TelaDevolucao extends JFrame {

    // Componentes
    private JTextField txtBuscaId; // Renomeado para ser genérico
    private JButton btnBuscar;

    // Painel de Detalhes
    private JLabel lblAluno;
    private JLabel lblTituloLivro;
    private JLabel lblIdExemplar;
    private JLabel lblDataEmprestimo;
    private JLabel lblDataPrevista;
    private JLabel lblStatusPrazo;

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
        setSize(550, 480);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
    }

    private void inicializarComponentes() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // --- 1. BUSCA INTELIGENTE ---
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("ID (Exemplar ou Livro):"), gbc);

        JPanel painelBusca = new JPanel(new BorderLayout(5, 0));
        txtBuscaId = new JTextField(15);
        txtBuscaId.setToolTipText("Digite o Código de Barras ou o ID do Livro");
        btnBuscar = new JButton("Buscar");

        painelBusca.add(txtBuscaId, BorderLayout.CENTER);
        painelBusca.add(btnBuscar, BorderLayout.EAST);

        gbc.gridx = 1;
        add(painelBusca, gbc);

        // --- 2. DETALHES ---
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        add(new JSeparator(), gbc);
        gbc.gridwidth = 1;

        // Título
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Livro Identificado:"), gbc);
        lblTituloLivro = new JLabel("---");
        lblTituloLivro.setFont(new Font("Arial", Font.BOLD, 13));
        lblTituloLivro.setForeground(new Color(0, 50, 100));
        gbc.gridx = 1;
        add(lblTituloLivro, gbc);

        // Exemplar
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Exemplar (Físico):"), gbc);
        lblIdExemplar = new JLabel("---");
        gbc.gridx = 1;
        add(lblIdExemplar, gbc);

        // Aluno
        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Está com o Aluno:"), gbc);
        lblAluno = new JLabel("---");
        gbc.gridx = 1;
        add(lblAluno, gbc);

        // Datas
        gbc.gridx = 0; gbc.gridy = 5;
        add(new JLabel("Data Retirada:"), gbc);
        lblDataEmprestimo = new JLabel("---");
        gbc.gridx = 1;
        add(lblDataEmprestimo, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        add(new JLabel("Data Prevista:"), gbc);
        lblDataPrevista = new JLabel("---");
        gbc.gridx = 1;
        add(lblDataPrevista, gbc);

        // Status
        gbc.gridx = 0; gbc.gridy = 7;
        add(new JLabel("Situação:"), gbc);
        lblStatusPrazo = new JLabel("---");
        lblStatusPrazo.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 1;
        add(lblStatusPrazo, gbc);

        // --- 3. BOTÕES ---
        JPanel painelBotoes = new JPanel();
        btnConfirmarDevolucao = new JButton("Confirmar Baixa");
        btnConfirmarDevolucao.setBackground(new Color(100, 200, 100));
        btnConfirmarDevolucao.setEnabled(false);

        btnCancelar = new JButton("Cancelar");

        painelBotoes.add(btnConfirmarDevolucao);
        painelBotoes.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(painelBotoes, gbc);

        // --- EVENTOS ---
        btnBuscar.addActionListener(e -> buscarInteligente());
        txtBuscaId.addActionListener(e -> buscarInteligente());
        btnConfirmarDevolucao.addActionListener(e -> realizarDevolucao());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void buscarInteligente() {
        String texto = txtBuscaId.getText().trim();
        if (texto.isEmpty()) return;

        try {
            long idBusca = Long.parseLong(texto);
            List<Emprestimo> pendentes = emprestimoService.listarPendentes();

            // 1. Tenta match exato pelo ID do EXEMPLAR (Prioridade)
            for (Emprestimo e : pendentes) {
                if (e.getExemplar().getId() == idBusca) {
                    preencherTela(e);
                    return;
                }
            }

            // 2. Se não achou, tenta match pelo ID do LIVRO
            List<Emprestimo> candidatos = new ArrayList<>();
            for (Emprestimo e : pendentes) {
                if (e.getExemplar().getLivro().getId() == idBusca) {
                    candidatos.add(e);
                }
            }

            if (candidatos.isEmpty()) {
                limparTela();
                JOptionPane.showMessageDialog(this, "Nenhum empréstimo encontrado com ID " + idBusca + " (nem Exemplar, nem Livro).");
            }
            else if (candidatos.size() == 1) {
                // Sorte! Só tem uma cópia desse livro emprestada.
                preencherTela(candidatos.get(0));
            }
            else {
                // Conflito: Vários alunos têm esse livro. Quem está devolvendo?
                resolverConflito(candidatos);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Digite apenas números.");
        }
    }

    private void resolverConflito(List<Emprestimo> lista) {
        // Cria uma lista de opções para o bibliotecário escolher
        String[] opcoes = new String[lista.size()];
        for (int i = 0; i < lista.size(); i++) {
            Emprestimo e = lista.get(i);
            opcoes[i] = "Aluno: " + e.getAluno().getNome() + " | Exemplar #" + e.getExemplar().getId();
        }

        String escolha = (String) JOptionPane.showInputDialog(this,
                "Existem " + lista.size() + " cópias de '" + lista.get(0).getExemplar().getLivro().getTitulo() + "' emprestadas.\nQual aluno está devolvendo?",
                "Múltiplos Empréstimos Encontrados",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoes,
                opcoes[0]);

        if (escolha != null) {
            // Acha qual foi escolhido
            for (int i = 0; i < opcoes.length; i++) {
                if (opcoes[i].equals(escolha)) {
                    preencherTela(lista.get(i));
                    return;
                }
            }
        }
    }

    private void preencherTela(Emprestimo e) {
        emprestimoEncontrado = e;

        lblTituloLivro.setText(e.getExemplar().getLivro().getTitulo());
        lblIdExemplar.setText("#" + e.getExemplar().getId());
        lblAluno.setText(e.getAluno().getNome());
        lblDataEmprestimo.setText(e.getDataEmprestimo().format(fmt));
        lblDataPrevista.setText(e.getDataDevolucaoPrevista().format(fmt));

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

    private void limparTela() {
        emprestimoEncontrado = null;
        lblTituloLivro.setText("---");
        lblIdExemplar.setText("---");
        lblAluno.setText("---");
        lblDataEmprestimo.setText("---");
        lblDataPrevista.setText("---");
        lblStatusPrazo.setText("---");
        btnConfirmarDevolucao.setEnabled(false);
    }

    private void realizarDevolucao() {
        if (emprestimoEncontrado == null) return;
        try {
            String msg = emprestimoService.registrarDevolucao(emprestimoEncontrado.getId());
            JOptionPane.showMessageDialog(this, msg);
            limparTela();
            txtBuscaId.setText("");
            txtBuscaId.requestFocus();
        } catch (ValidacaoException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }
}