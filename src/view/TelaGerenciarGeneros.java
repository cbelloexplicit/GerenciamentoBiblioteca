package view;

import Exception.ValidacaoException;
import model.Genero;
import Service.GeneroService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TelaGerenciarGeneros extends JFrame {

    // Componentes
    private JTextField txtId;
    private JTextField txtNome;
    private JTable tabelaGeneros;
    private DefaultTableModel modeloTabela;

    private JButton btnSalvar;
    private JButton btnExcluir;
    private JButton btnLimpar;

    private GeneroService generoService;

    public TelaGerenciarGeneros() {
        this.generoService = new GeneroService();
        configurarJanela();
        inicializarComponentes();
        atualizarTabela();
    }

    private void configurarJanela() {
        setTitle("Gerenciamento de Gêneros Literários");
        setSize(600, 450); // Janela maior
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        // --- 1. PAINEL DE EDIÇÃO (NORTE) ---
        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBorder(BorderFactory.createTitledBorder("Dados do Gênero"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        painelForm.add(new JLabel("ID:"), gbc);

        txtId = new JTextField(5);
        txtId.setEditable(false); // Não pode digitar ID
        txtId.setText("0"); // 0 indica Novo Cadastro
        gbc.gridx = 1;
        painelForm.add(txtId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        painelForm.add(new JLabel("Nome do Gênero:"), gbc);

        txtNome = new JTextField();
        txtNome.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Ocupa todo espaço sobrando
        painelForm.add(txtNome, gbc);

        add(painelForm, BorderLayout.NORTH);

        // --- 2. LISTAGEM (CENTRO) ---
        String[] colunas = {"ID", "Nome do Gênero"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tabelaGeneros = new JTable(modeloTabela);
        tabelaGeneros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaGeneros.setRowHeight(22);

        // Evento: Clicar na tabela preenche o formulário
        tabelaGeneros.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                preencherFormularioPelaTabela();
            }
        });

        add(new JScrollPane(tabelaGeneros), BorderLayout.CENTER);

        // --- 3. BOTÕES (SUL) ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnLimpar = new JButton("Novo / Limpar");
        btnLimpar.setToolTipText("Limpa os campos para cadastrar um novo");

        btnExcluir = new JButton("Excluir");
        btnExcluir.setBackground(new Color(200, 100, 100));
        btnExcluir.setForeground(Color.WHITE);

        btnSalvar = new JButton("Salvar");
        btnSalvar.setBackground(new Color(100, 200, 100));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setPreferredSize(new Dimension(100, 30));

        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnSalvar);

        add(painelBotoes, BorderLayout.SOUTH);

        // --- EVENTOS DOS BOTÕES ---
        btnSalvar.addActionListener(e -> salvarGenero());
        btnExcluir.addActionListener(e -> excluirGenero());
        btnLimpar.addActionListener(e -> limparCampos());
    }

    // --- LÓGICA ---

    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        List<Genero> lista = generoService.listarTodos();
        for (Genero g : lista) {
            modeloTabela.addRow(new Object[]{g.getID(), g.getNome()});
        }
    }

    private void preencherFormularioPelaTabela() {
        int linha = tabelaGeneros.getSelectedRow();
        if (linha != -1) {
            // Pega dados da tabela
            long id = (long) tabelaGeneros.getValueAt(linha, 0);
            String nome = (String) tabelaGeneros.getValueAt(linha, 1);

            // Joga nos campos
            txtId.setText(String.valueOf(id));
            txtNome.setText(nome);
        }
    }

    private void limparCampos() {
        txtId.setText("0");
        txtNome.setText("");
        tabelaGeneros.clearSelection();
        txtNome.requestFocus();
    }

    private void salvarGenero() {
        try {
            long id = Long.parseLong(txtId.getText());
            String nome = txtNome.getText();

            // Cria objeto (se ID=0 é novo, se ID>0 o DAO/Service deve tratar atualização)
            Genero genero = new Genero(id, nome);

            // Salva
            generoService.salvar(genero);

            JOptionPane.showMessageDialog(this, "Gênero salvo com sucesso!");
            limparCampos();
            atualizarTabela(); // Recarrega a lista para mostrar o novo item

        } catch (ValidacaoException ex) {
            JOptionPane.showMessageDialog(this, "Aviso: " + ex.getMessage(), "Validação", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }

    private void excluirGenero() {
        String idStr = txtId.getText();
        if (idStr.equals("0")) {
            JOptionPane.showMessageDialog(this, "Selecione um gênero na tabela para excluir.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir este gênero?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                long id = Long.parseLong(idStr);
                generoService.remover(id);

                JOptionPane.showMessageDialog(this, "Gênero excluído.");
                limparCampos();
                atualizarTabela();

            } catch (ValidacaoException ex) {
                // Aqui o sistema avisa se tiver livros usando o gênero!
                JOptionPane.showMessageDialog(this, "Não foi possível excluir: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}