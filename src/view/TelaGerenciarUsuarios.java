package view;

import Exception.ValidacaoException;
import model.Usuario;
import Service.UsuarioService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class TelaGerenciarUsuarios extends JFrame {

    private JTable tabelaUsuarios;
    private DefaultTableModel modeloTabela;
    private JTextField txtBusca;

    private JButton btnNovo;
    private JButton btnExcluir;
    private JButton btnToggleAtivo; // Botão para Ativar/Desativar

    private UsuarioService usuarioService;

    public TelaGerenciarUsuarios() {
        this.usuarioService = new UsuarioService();
        configurarJanela();
        inicializarComponentes();
        carregarTabela();
    }

    private void configurarJanela() {
        setTitle("Gerenciamento de Usuários");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        // --- NORTE: Busca ---
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBusca.add(new JLabel("Buscar por Matrícula:"));
        txtBusca = new JTextField(20);
        JButton btnBuscar = new JButton("Buscar");
        JButton btnRecarregar = new JButton("Recarregar");

        painelBusca.add(txtBusca);
        painelBusca.add(btnBuscar);
        painelBusca.add(btnRecarregar);

        add(painelBusca, BorderLayout.NORTH);

        // --- CENTRO: Tabela ---
        String[] colunas = {"ID", "Nome", "Matrícula", "Tipo", "Status"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabelaUsuarios = new JTable(modeloTabela);
        tabelaUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabelaUsuarios), BorderLayout.CENTER);

        // --- SUL: Botões ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnNovo = new JButton("Novo Usuário");
        btnNovo.setBackground(new Color(100, 200, 100)); // Verde

        btnToggleAtivo = new JButton("Ativar/Desativar");
        btnToggleAtivo.setToolTipText("Muda o status do usuário sem excluir do histórico");

        btnExcluir = new JButton("Excluir Permanentemente");
        btnExcluir.setBackground(new Color(200, 100, 100)); // Vermelho

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnToggleAtivo);
        painelBotoes.add(btnExcluir);

        add(painelBotoes, BorderLayout.SOUTH);

        // --- EVENTOS ---

        btnNovo.addActionListener(e -> {
            TelaCadastroUsuario telaCad = new TelaCadastroUsuario();
            telaCad.setVisible(true);
            telaCad.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    carregarTabela();
                }
            });
        });

        btnExcluir.addActionListener(e -> excluirUsuario());

        btnToggleAtivo.addActionListener(e -> alternarStatus());

        btnRecarregar.addActionListener(e -> carregarTabela());
        btnBuscar.addActionListener(e -> buscarUsuario());
    }

    private void carregarTabela() {
        modeloTabela.setRowCount(0);
        List<Usuario> lista = usuarioService.listarTodos();

        for (Usuario u : lista) {
            modeloTabela.addRow(new Object[]{
                    u.getId(),
                    u.getNome(),
                    u.getMatricula(),
                    u.getTipo(),
                    u.isAtivo() ? "ATIVO" : "INATIVO"
            });
        }
    }

    private void buscarUsuario() {
        String mat = txtBusca.getText().trim();
        if (mat.isEmpty()) { carregarTabela(); return; }

        Usuario u = usuarioService.buscarPorMatricula(mat);
        modeloTabela.setRowCount(0);
        if (u != null) {
            modeloTabela.addRow(new Object[]{u.getId(), u.getNome(), u.getMatricula(), u.getTipo(), u.isAtivo() ? "ATIVO" : "INATIVO"});
        } else {
            JOptionPane.showMessageDialog(this, "Usuário não encontrado.");
        }
    }

    private void excluirUsuario() {
        int linha = tabelaUsuarios.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para excluir.");
            return;
        }

        long id = (long) tabelaUsuarios.getValueAt(linha, 0);
        String nome = (String) tabelaUsuarios.getValueAt(linha, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja EXCLUIR PERMANENTEMENTE o usuário " + nome + "?\nIsso pode quebrar o histórico de empréstimos se ele já tiver usado a biblioteca.",
                "Cuidado", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                usuarioService.excluir(id);
                carregarTabela();
                JOptionPane.showMessageDialog(this, "Usuário excluído.");
            } catch (ValidacaoException ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        }
    }

    private void alternarStatus() {
        int linha = tabelaUsuarios.getSelectedRow();
        if (linha == -1) return;

        long id = (long) tabelaUsuarios.getValueAt(linha, 0);
        String statusAtual = (String) tabelaUsuarios.getValueAt(linha, 4);

        try {
            if (statusAtual.equals("ATIVO")) {
                usuarioService.desativarUsuario(id);
                JOptionPane.showMessageDialog(this, "Usuário desativado.");
            } else {
                JOptionPane.showMessageDialog(this, "Para reativar, implemente o método ativar() no Service.");
            }
            carregarTabela();
        } catch (ValidacaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}