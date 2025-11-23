package view;

import model.LogAcesso;
import Service.LogService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TelaRelatorioLogs extends JFrame {

    private JTable tabelaLogs;
    private DefaultTableModel modeloTabela;
    private LogService logService;

    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public TelaRelatorioLogs() {
        this.logService = new LogService();
        configurarJanela();
        inicializarComponentes();
        carregarLogs();
    }

    private void configurarJanela() {
        setTitle("Relatório de Acessos e Auditoria");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        // --- TÍTULO ---
        JLabel lblTitulo = new JLabel("Histórico de Acessos do Sistema", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // --- TABELA ---
        String[] colunas = {"ID", "Data/Hora", "Usuário", "Matrícula", "Ação Realizada"};

        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override // Impede edição
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabelaLogs = new JTable(modeloTabela);
        tabelaLogs.setRowHeight(25);

        tabelaLogs.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tabelaLogs.getColumnModel().getColumn(1).setPreferredWidth(150); // Data
        tabelaLogs.getColumnModel().getColumn(2).setPreferredWidth(200); // Nome
        tabelaLogs.getColumnModel().getColumn(4).setPreferredWidth(150); // Ação

        add(new JScrollPane(tabelaLogs), BorderLayout.CENTER);

        // --- BOTÕES (Rodapé) ---
        JPanel painelSul = new JPanel();
        JButton btnAtualizar = new JButton("Atualizar Lista");
        JButton btnFechar = new JButton("Fechar");

        painelSul.add(btnAtualizar);
        painelSul.add(btnFechar);

        add(painelSul, BorderLayout.SOUTH);

        // --- AÇÕES ---
        btnAtualizar.addActionListener(e -> carregarLogs());
        btnFechar.addActionListener(e -> dispose());
    }

    private void carregarLogs() {
        modeloTabela.setRowCount(0);

        List<LogAcesso> logs = logService.obterRelatorioCompleto();

        for (LogAcesso log : logs) {
            modeloTabela.addRow(new Object[]{
                    log.getId(),
                    log.getDataHora().format(fmt),
                    log.getUsuario().getNome(),
                    log.getUsuario().getMatricula(),
                    log.getAcao() // ex: LOGIN, LOGOUT
            });
        }

        if (logs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum registro de log encontrado.");
        }
    }
}