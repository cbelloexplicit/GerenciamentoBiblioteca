package view;

import Exception.ValidacaoException;
import model.Livro;
import Service.LivroService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class TelaGerenciarAcervo extends JFrame {

    // Componentes
    private JTextField txtBusca;
    private JButton btnBuscar;
    private JTable tabelaLivros;
    private DefaultTableModel modeloTabela; // Controla os dados da tabela

    private JButton btnNovo;
    private JButton btnEditar;
    private JButton btnExcluir;
    private JButton btnAtualizar; // Para recarregar a lista
    private LivroService livroService;

    public TelaGerenciarAcervo() {
        this.livroService = new LivroService();
        configurarJanela();
        inicializarComponentes();
        atualizarTabela(); // Carrega os dados assim que abre
    }

    private void configurarJanela() {
        setTitle("Gerenciamento de Acervo");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout()); // Layout: Norte (Busca), Centro (Tabela), Sul (Botões)
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
        // Define as colunas
        String[] colunas = {"ID", "Título", "Autor", "Gênero", "Idade Mín.", "Disponível"};

        // Cria o modelo impedindo edição direta nas células
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaLivros = new JTable(modeloTabela);
        tabelaLivros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Só seleciona 1 por vez

        // Coloca a tabela num ScrollPane (barra de rolagem)
        JScrollPane scrollPane = new JScrollPane(tabelaLivros);
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. PAINEL SUL (BOTÕES DE AÇÃO) ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnNovo = new JButton("Novo Livro");
        btnNovo.setBackground(new Color(100, 200, 100)); // Verde

        btnEditar = new JButton("Editar Selecionado");

        btnExcluir = new JButton("Excluir Selecionado");
        btnExcluir.setBackground(new Color(200, 100, 100)); // Vermelho

        painelBotoes.add(btnNovo);
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

        // Recarregar (Limpa busca)
        btnAtualizar.addActionListener(e -> {
            txtBusca.setText("");
            atualizarTabela();
        });

        // Novo Livro
        btnNovo.addActionListener(e -> {
            TelaCadastroLivro telaCadastro = new TelaCadastroLivro();
            telaCadastro.setVisible(true);

            // Adiciona um "ouvinte" para quando a tela de cadastro fechar, atualizar a tabela aqui
            telaCadastro.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    atualizarTabela();
                }
            });
        });

        // Excluir
        btnExcluir.addActionListener(e -> excluirLivroSelecionado());

        // Editar (Lógica básica de capturar ID)
        btnEditar.addActionListener(e -> {
            int linhaSelecionada = tabelaLivros.getSelectedRow();
            if (linhaSelecionada == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um livro para editar.");
                return;
            }
            // Pega o ID da coluna 0
            long id = (long) tabelaLivros.getValueAt(linhaSelecionada, 0);
            JOptionPane.showMessageDialog(this, "Funcionalidade de Edição: ID " + id + " selecionado.\n(Requer adaptar TelaCadastroLivro para receber ID).");
        });
    }

    private void atualizarTabela() {
        List<Livro> todos = livroService.listarTodos();
        preencherTabela(todos);
    }

    private void preencherTabela(List<Livro> lista) {
        // Limpa a tabela atual
        modeloTabela.setRowCount(0);

        for (Livro l : lista) {
            // Cria a linha visual com os dados do objeto
            Object[] linha = {
                    l.getId(),
                    l.getTitulo(),
                    l.getAutor(),
                    l.getGenero().getNome(),
                    l.getIdadeMinima() + " anos",
                    l.getCopiasDisponiveis() + " / " + l.getTotalCopias()
            };
            modeloTabela.addRow(linha);
        }
    }

    private void excluirLivroSelecionado() {
        int linha = tabelaLivros.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um livro na tabela para excluir.");
            return;
        }

        // Pega o ID que está na coluna 0 da linha selecionada
        long idLivro = (long) tabelaLivros.getValueAt(linha, 0);
        String titulo = (String) tabelaLivros.getValueAt(linha, 1);

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir '" + titulo + "'?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                livroService.remover(idLivro);
                atualizarTabela(); // Remove da tela
                JOptionPane.showMessageDialog(this, "Livro excluído com sucesso.");
            } catch (ValidacaoException ex) {
                // Se o livro estiver emprestado, o Service lança erro e mostramos aqui
                JOptionPane.showMessageDialog(this, "Não foi possível excluir: " + ex.getMessage(), "Erro", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}