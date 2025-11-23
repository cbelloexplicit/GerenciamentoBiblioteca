package view;

import model.*;
import persistence.ProgramaLeituraDAO;
import Service.EmprestimoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TelaConsultaAluno extends JFrame {

    private Aluno alunoLogado;

    // Componentes
    private JTabbedPane abas;
    private JTable tabelaAtivos;
    private JTable tabelaHistorico;
    private JLabel lblLivroPrograma;
    private JLabel lblPrazoPrograma;
    private JLabel lblTituloPrograma;

    // Services
    private EmprestimoService emprestimoService;
    // Vamos usar o DAO direto aqui apenas para consultar o programa vigente
    private ProgramaLeituraDAO programaDAO;

    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public TelaConsultaAluno(Usuario usuario) {
        // Garante que é um aluno
        if (usuario instanceof Aluno) {
            this.alunoLogado = (Aluno) usuario;
        } else {
            JOptionPane.showMessageDialog(this, "Erro: Apenas alunos podem acessar esta tela.");
            dispose();
            return;
        }

        this.emprestimoService = new EmprestimoService();
        this.programaDAO = new ProgramaLeituraDAO();

        configurarJanela();
        inicializarComponentes();
        carregarDados();
    }

    private void configurarJanela() {
        setTitle("Meu Painel - " + alunoLogado.getNome() + " (Turma: " + alunoLogado.getTurma() + ")");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void inicializarComponentes() {
        abas = new JTabbedPane();

        // --- ABA 1: MEUS EMPRÉSTIMOS (ATIVOS) ---
        JPanel painelAtivos = new JPanel(new BorderLayout());

        String[] colunas = {"Livro", "Data Retirada", "Devolução Prevista", "Status"};
        DefaultTableModel modeloAtivos = new DefaultTableModel(colunas, 0);
        tabelaAtivos = new JTable(modeloAtivos);
        tabelaAtivos.setRowHeight(25);

        painelAtivos.add(new JScrollPane(tabelaAtivos), BorderLayout.CENTER);

        // Legenda
        JLabel lblAviso = new JLabel(" * Fique atento às datas em vermelho!");
        lblAviso.setForeground(Color.RED);
        painelAtivos.add(lblAviso, BorderLayout.SOUTH);

        abas.addTab("Empréstimos Ativos", new ImageIcon(), painelAtivos, "Livros que estão comigo agora");

        // --- ABA 2: PROGRAMA DE LEITURA (O QUE DEVO LER?) ---
        JPanel painelPrograma = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel lblTitulo = new JLabel("Livro Atribuído para este Trimestre");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0;
        painelPrograma.add(lblTitulo, gbc);

        // Caixa de destaque para o livro
        JPanel boxLivro = new JPanel(new GridLayout(3, 1));
        boxLivro.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
        boxLivro.setBackground(new Color(230, 240, 255));
        boxLivro.setPreferredSize(new Dimension(400, 150));

        lblTituloPrograma = new JLabel("Carregando...", SwingConstants.CENTER);
        lblTituloPrograma.setFont(new Font("Arial", Font.BOLD, 14));

        lblLivroPrograma = new JLabel("---", SwingConstants.CENTER);
        lblLivroPrograma.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 22));
        lblLivroPrograma.setForeground(new Color(0, 0, 100));

        lblPrazoPrograma = new JLabel("---", SwingConstants.CENTER);

        boxLivro.add(lblTituloPrograma);
        boxLivro.add(lblLivroPrograma);
        boxLivro.add(lblPrazoPrograma);

        gbc.gridy = 1;
        painelPrograma.add(boxLivro, gbc);

        abas.addTab("Programa de Leitura", null, painelPrograma, "Livro escolar obrigatório");

        // --- ABA 3: HISTÓRICO ---
        JPanel painelHistorico = new JPanel(new BorderLayout());
        String[] colHist = {"Livro", "Data Retirada", "Data Devolução", "Observação"};
        DefaultTableModel modeloHist = new DefaultTableModel(colHist, 0);
        tabelaHistorico = new JTable(modeloHist);
        painelHistorico.add(new JScrollPane(tabelaHistorico), BorderLayout.CENTER);

        abas.addTab("Histórico Completo", null, painelHistorico, "Tudo que já li");

        add(abas, BorderLayout.CENTER);

        // Botão Fechar
        JButton btnFechar = new JButton("Voltar ao Menu");
        btnFechar.addActionListener(e -> dispose());
        add(btnFechar, BorderLayout.SOUTH);
    }

    private void carregarDados() {
        // 1. Carregar Empréstimos (Ativos e Histórico)
        List<Emprestimo> historico = emprestimoService.buscarHistoricoAluno(alunoLogado);

        DefaultTableModel modelAtivos = (DefaultTableModel) tabelaAtivos.getModel();
        DefaultTableModel modelHist = (DefaultTableModel) tabelaHistorico.getModel();
        modelAtivos.setRowCount(0);
        modelHist.setRowCount(0);

        for (Emprestimo e : historico) {
            if (e.isAberto()) {
                // Lógica de Status Visual
                String status = "No Prazo";
                if (e.isAtrasado()) {
                    long dias = ChronoUnit.DAYS.between(e.getDataDevolucaoPrevista(), LocalDate.now());
                    status = "ATRASADO (" + dias + " dias)";
                }

                modelAtivos.addRow(new Object[]{
                        e.getLivro().getTitulo(),
                        e.getDataEmprestimo().format(fmt),
                        e.getDataDevolucaoPrevista().format(fmt),
                        status
                });
            } else {
                // Histórico
                String obs = "Devolvido";
                if (e.getDataDevolucaoReal() != null && e.getDataDevolucaoReal().isAfter(e.getDataDevolucaoPrevista())) {
                    obs = "Devolvido com Atraso";
                }

                modelHist.addRow(new Object[]{
                        e.getLivro().getTitulo(),
                        e.getDataEmprestimo().format(fmt),
                        e.getDataDevolucaoReal() != null ? e.getDataDevolucaoReal().format(fmt) : "-",
                        obs
                });
            }
        }

        // 2. Carregar Programa de Leitura
        // Precisamos achar um programa que tenha ESTE aluno na lista
        List<ProgramaLeitura> programas = programaDAO.listarTodos();
        boolean encontrou = false;

        for (ProgramaLeitura prog : programas) {
            // Verifica se o programa está ativo (datas)
            if (prog.isAtivo()) {
                // Varre as atribuições desse programa
                for (AtribuicaoLeitura at : prog.getAtribuicoes()) {
                    if (at.getAluno().getId() == alunoLogado.getId()) {
                        lblTituloPrograma.setText(prog.getTitulo());

                        if (at.getLivro() != null) {
                            lblLivroPrograma.setText(at.getLivro().getTitulo());
                        } else {
                            lblLivroPrograma.setText("(Nenhum livro atribuído)");
                        }

                        lblPrazoPrograma.setText("Ler até: " + prog.getDataFim().format(fmt));
                        encontrou = true;
                        break; // Sai do loop interno
                    }
                }
            }
            if (encontrou) break; // Sai do loop externo se achou
        }

        if (!encontrou) {
            lblTituloPrograma.setText("Sem programa ativo");
            lblLivroPrograma.setText("---");
            lblPrazoPrograma.setText("Aguarde indicações do professor");
        }
    }
}