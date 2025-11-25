package view;

import Exception.ValidacaoException;
import model.Exemplar;
import model.Livro;
import Service.LivroService;
import persistence.ExemplarDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class TelaGerenciarAcervo extends JFrame {

    //
    private JTextField txtBusca;
    private JButton btnBuscar;
    private JTable tabelaLivros;
    private DefaultTableModel modeloTabela;

    private JButton btnNovo;
    private JButton btnEditar;
    private JButton btnExcluir;
    private JButton btnExemplares;
    private JButton btnAtualizar;

    private LivroService livroService;
    private ExemplarDAO exemplarDAO;

    public TelaGerenciarAcervo() {
        this.livroService = new LivroService();
        this.exemplarDAO = new ExemplarDAO();

        configurarJanela();
        inicializarComponentes();
        atualizarTabela();
    }

    private void configurarJanela() {
        setTitle("Gerenciamento de Acervo (Livros e Exemplares)");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void inicializarComponentes() {

        // --- 1. PAINEL NORTE (BUSCA) ---
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBusca.add(new JLabel("Buscar por Título:"));

        txtBusca = new JTextField(30);
        painelBusca.add(txtBusca);

        btnBuscar = new JButton("Pesquisar");
        painelBusca.add(btnBuscar);

        btnAtualizar = new JButton("Recarregar Lista");
        painelBusca.add(btnAtualizar);

        add(painelBusca, BorderLayout.NORTH);

        // --- 2. PAINEL CENTRAL (TABELA) ---
        String[] colunas = {"ID", "Título", "Autor", "Gênero", "Idade Mín.", "Estoque (Disp / Total)"};

        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaLivros = new JTable(modeloTabela);
        tabelaLivros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaLivros.setRowHeight(25);

        tabelaLivros.getColumnModel().getColumn(1).setPreferredWidth(200); // Título
        tabelaLivros.getColumnModel().getColumn(5).setPreferredWidth(120); // Estoque

        add(new JScrollPane(tabelaLivros), BorderLayout.CENTER);

        // --- 3. PAINEL SUL (BOTÕES DE AÇÃO) ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnNovo = new JButton("Novo Título");
        btnNovo.setBackground(new Color(100, 200, 100)); // Verde

        btnExemplares = new JButton("Gerenciar Exemplares");
        btnExemplares.setToolTipText("Adicionar ou remover cópias físicas deste livro");
        btnExemplares.setBackground(new Color(100, 149, 237)); // Azul

        btnEditar = new JButton("Editar Dados");

        btnExcluir = new JButton("Excluir Título");
        btnExcluir.setBackground(new Color(200, 100, 100)); // Vermelho

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnExemplares);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);

        add(painelBotoes, BorderLayout.SOUTH);

        // --- CONFIGURAÇÃO DOS EVENTOS ---
        configurarAcoes();
    }

    private void configurarAcoes() {

        // Buscar
        btnBuscar.addActionListener(e -> {
            String termo = txtBusca.getText();
            List<Livro> resultados = livroService.buscarPorTitulo(termo);
            preencherTabela(resultados);
        });

        // Recarregar
        btnAtualizar.addActionListener(e -> {
            txtBusca.setText("");
            atualizarTabela();
        });

        // Novo Livro (Abre tela de cadastro)
        btnNovo.addActionListener(e -> {
            TelaCadastroLivro telaCadastro = new TelaCadastroLivro();
            telaCadastro.setVisible(true);
            telaCadastro.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    atualizarTabela();
                }
            });
        });

        // Gerenciar Exemplares (Adicionar cópias)
        btnExemplares.addActionListener(e -> gerenciarExemplares());

        // Editar
        btnEditar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Para editar título/autor, implemente passando o ID para a TelaCadastroLivro.");
        });

        // Excluir
        btnExcluir.addActionListener(e -> excluirLivroSelecionado());
    }

    private void atualizarTabela() {
        List<Livro> todos = livroService.listarTodos();
        preencherTabela(todos);
    }

    private void preencherTabela(List<Livro> lista) {
        modeloTabela.setRowCount(0);

        for (Livro l : lista) {
            // LÓGICA: Consulta o DAO de exemplares para saber as quantidades
            List<Exemplar> totalExemplares = exemplarDAO.buscarPorLivro(l.getId());
            List<Exemplar> disponiveis = exemplarDAO.buscarDisponiveisPorLivro(l.getId());

            String statusEstoque = disponiveis.size() + " / " + totalExemplares.size();

            Object[] linha = {
                    l.getId(),
                    l.getTitulo(),
                    l.getAutor(),
                    l.getGenero().getNome(),
                    l.getIdadeMinima() + " anos",
                    statusEstoque // Exibe "5 / 10"
            };
            modeloTabela.addRow(linha);
        }
    }
    private void gerenciarExemplares() {
        int linha = tabelaLivros.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um livro para gerenciar seus exemplares.");
            return;
        }

        long idLivro = (long) tabelaLivros.getValueAt(linha, 0);
        String titulo = (String) tabelaLivros.getValueAt(linha, 1);
        Livro livro = livroService.buscarPorId(idLivro);

        String input = JOptionPane.showInputDialog(this,
                "Livro: " + titulo + "\n\nQuantas NOVAS cópias deseja adicionar ao acervo?",
                "Adicionar Exemplares", JOptionPane.QUESTION_MESSAGE);

        if (input != null && !input.isEmpty()) {
            try {
                int qtd = Integer.parseInt(input);
                if (qtd <= 0) throw new NumberFormatException();

                // Cria os exemplares
                for (int i = 0; i < qtd; i++) {
                    Exemplar novo = new Exemplar(livro);
                    exemplarDAO.salvar(novo);
                }

                JOptionPane.showMessageDialog(this, qtd + " exemplares adicionados com sucesso!");
                atualizarTabela();

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Quantidade inválida.");
            }
        }
    }

    private void excluirLivroSelecionado() {
        int linha = tabelaLivros.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um livro para excluir.");
            return;
        }

        long idLivro = (long) tabelaLivros.getValueAt(linha, 0);
        String titulo = (String) tabelaLivros.getValueAt(linha, 1);

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o cadastro de '" + titulo + "'?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                livroService.remover(idLivro);
                atualizarTabela();
                JOptionPane.showMessageDialog(this, "Livro excluído.");

            } catch (ValidacaoException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Não permitido", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}