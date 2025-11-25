package view;

import Exception.ValidacaoException;
import model.Emprestimo;
import Service.EmprestimoService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TelaGerenciarEmprestimos extends JFrame {

    private JTable tabela;
    private DefaultTableModel modelo;
    private JCheckBox chkApenasPendentes;
    private JButton btnRenovar;
    private JButton btnEditarPrazo;
    private JButton btnDarBaixa;
    private JButton btnFechar;

    private EmprestimoService service;
    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public TelaGerenciarEmprestimos() {
        this.service = new EmprestimoService();
        configurarJanela();
        inicializarComponentes();
        carregarTabela();
    }

    private void configurarJanela() {
        setTitle("Gerenciamento de Empréstimos (Visão do Bibliotecário)");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        // --- FILTROS (NORTE) ---
        JPanel painelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        chkApenasPendentes = new JCheckBox("Mostrar apenas Pendentes/Atrasados");
        chkApenasPendentes.setSelected(true);

        JButton btnAtualizar = new JButton("Atualizar Lista");

        painelNorte.add(chkApenasPendentes);
        painelNorte.add(btnAtualizar);
        add(painelNorte, BorderLayout.NORTH);

        // --- TABELA (CENTRO) ---
        String[] colunas = {"ID", "Aluno", "Livro (Exemplar)", "Data Saída", "Prevista", "Devolução", "Status"};

        modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tabela = new JTable(modelo);
        tabela.setRowHeight(25);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Renderizador para colorir linhas atrasadas
        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                String status = (String) table.getModel().getValueAt(row, 6); // Coluna Status

                if (status.contains("ATRASADO")) {
                    c.setForeground(Color.RED);
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else if (status.equals("Concluído")) {
                    c.setForeground(Color.GRAY);
                } else {
                    c.setForeground(Color.BLACK); // Pendente normal
                }

                // Mantém a seleção visível (fundo azul padrão)
                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                    c.setForeground(table.getSelectionForeground());
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        });

        // Ajuste de colunas
        tabela.getColumnModel().getColumn(1).setPreferredWidth(150); // Aluno
        tabela.getColumnModel().getColumn(2).setPreferredWidth(200); // Livro

        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // --- BOTÕES (SUL) ---
        JPanel painelSul = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnRenovar = new JButton("Renovar (+7 dias)");
        btnEditarPrazo = new JButton("Editar Prazo (Manual)");
        btnDarBaixa = new JButton("Registrar Devolução");
        btnFechar = new JButton("Fechar");

        // Cores
        btnRenovar.setBackground(new Color(173, 216, 230)); // Azul claro
        btnDarBaixa.setBackground(new Color(144, 238, 144)); // Verde claro

        painelSul.add(btnRenovar);
        painelSul.add(btnEditarPrazo);
        painelSul.add(btnDarBaixa);
        painelSul.add(btnFechar);

        add(painelSul, BorderLayout.SOUTH);

        // --- EVENTOS ---
        chkApenasPendentes.addActionListener(e -> carregarTabela());
        btnAtualizar.addActionListener(e -> carregarTabela());
        btnFechar.addActionListener(e -> dispose());

        btnRenovar.addActionListener(e -> acaoRenovar());
        btnDarBaixa.addActionListener(e -> acaoDevolver());
        btnEditarPrazo.addActionListener(e -> acaoEditarPrazo());
    }

    private void carregarTabela() {
        modelo.setRowCount(0);
        List<Emprestimo> lista;

        if (chkApenasPendentes.isSelected()) {
            lista = service.listarPendentes();
        } else {
            lista = service.listarTodos();
        }

        for (Emprestimo e : lista) {
            String status;
            if (e.isAberto()) {
                if (e.isAtrasado()) {
                    long dias = ChronoUnit.DAYS.between(e.getDataDevolucaoPrevista(), LocalDate.now());
                    status = "ATRASADO (" + dias + " dias)";
                } else {
                    status = "Em Andamento";
                }
            } else {
                status = "Concluído";
            }

            // Formata Exemplar
            String livroInfo = e.getExemplar().getLivro().getTitulo() + " (#" + e.getExemplar().getId() + ")";

            modelo.addRow(new Object[]{
                    e.getId(),
                    e.getAluno().getNome(),
                    livroInfo,
                    e.getDataEmprestimo().format(fmt),
                    e.getDataDevolucaoPrevista().format(fmt),
                    (e.getDataDevolucaoReal() != null ? e.getDataDevolucaoReal().format(fmt) : "-"),
                    status
            });
        }
    }

    private void acaoRenovar() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) { JOptionPane.showMessageDialog(this, "Selecione um empréstimo."); return; }

        long id = (long) tabela.getValueAt(linha, 0);
        try {
            service.renovarEmprestimo(id);
            JOptionPane.showMessageDialog(this, "Renovado com sucesso!");
            carregarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }

    private void acaoDevolver() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) { JOptionPane.showMessageDialog(this, "Selecione um empréstimo."); return; }

        long id = (long) tabela.getValueAt(linha, 0);
        try {
            String msg = service.registrarDevolucao(id);
            JOptionPane.showMessageDialog(this, msg);
            carregarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }

    private void acaoEditarPrazo() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) { JOptionPane.showMessageDialog(this, "Selecione um empréstimo."); return; }

        long id = (long) tabela.getValueAt(linha, 0);
        String dataAtualStr = (String) tabela.getValueAt(linha, 4); // Coluna Prevista

        String novaDataStr = JOptionPane.showInputDialog(this,
                "Digite a nova data de devolução (dd/MM/yyyy):", dataAtualStr);

        if (novaDataStr != null) {
            try {
                LocalDate novaData = LocalDate.parse(novaDataStr, fmt);
                service.atualizarEmprestimo(id, novaData);
                JOptionPane.showMessageDialog(this, "Data atualizada!");
                carregarTabela();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Data inválida ou erro ao salvar: " + ex.getMessage());
            }
        }
    }
}