package view;

import model.Exemplar;
import model.Livro;
import Service.LivroService;
import persistence.ExemplarDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaPesquisaLivro extends JFrame {

    private JTextField txtBusca;
    private JButton btnBuscar;
    private JButton btnLimpar;
    private JTable tabelaLivros;
    private DefaultTableModel modeloTabela;

    private LivroService livroService;
    private ExemplarDAO exemplarDAO;

    public TelaPesquisaLivro() {
        this.livroService = new LivroService();
        this.exemplarDAO = new ExemplarDAO();

        configurarJanela();
        inicializarComponentes();
        carregarTabela();
    }

    private void configurarJanela() {
        setTitle("Consultar Acervo da Biblioteca");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        JPanel painelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelNorte.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        painelNorte.add(new JLabel("Pesquisar por Título ou Autor:"));

        txtBusca = new JTextField(25);
        painelNorte.add(txtBusca);

        btnBuscar = new JButton("Pesquisar");
        btnBuscar.setBackground(new Color(70, 130, 180));
        btnBuscar.setForeground(Color.WHITE);

        btnLimpar = new JButton("Limpar / Ver Todos");

        painelNorte.add(btnBuscar);
        painelNorte.add(btnLimpar);

        add(painelNorte, BorderLayout.NORTH);

        String[] colunas = {"Título", "Autor", "Gênero", "Status (Disp. / Total)"};

        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override // Aluno não pode editar nada
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabelaLivros = new JTable(modeloTabela);
        tabelaLivros.setRowHeight(25);
        tabelaLivros.setFillsViewportHeight(true);

        tabelaLivros.getColumnModel().getColumn(0).setPreferredWidth(250);
        tabelaLivros.getColumnModel().getColumn(3).setPreferredWidth(120);

        add(new JScrollPane(tabelaLivros), BorderLayout.CENTER);

        JLabel lblDica = new JLabel(" * Para pegar um livro emprestado, anote o Título e peça ao bibliotecário.");
        lblDica.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        lblDica.setFont(new Font("Arial", Font.ITALIC, 12));
        add(lblDica, BorderLayout.SOUTH);

        btnBuscar.addActionListener(e -> pesquisar());
        btnLimpar.addActionListener(e -> {
            txtBusca.setText("");
            carregarTabela();
        });

        txtBusca.addActionListener(e -> pesquisar());
    }

    private void carregarTabela() {
        List<Livro> todos = livroService.listarTodos();
        preencherTabela(todos);
    }

    private void pesquisar() {
        String termo = txtBusca.getText().trim();

        if (termo.isEmpty()) {
            carregarTabela();
            return;
        }

        List<Livro> resultados = livroService.buscarPorTitulo(termo);

        List<Livro> porAutor = livroService.buscarPorAutor(termo);

        for (Livro l : porAutor) {
            boolean jaExiste = resultados.stream().anyMatch(r -> r.getId() == l.getId());
            if (!jaExiste) {
                resultados.add(l);
            }
        }

        preencherTabela(resultados);

        if (resultados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum livro encontrado com o termo: " + termo);
        }
    }

    private void preencherTabela(List<Livro> lista) {
        modeloTabela.setRowCount(0);

        for (Livro l : lista) {
            // Busca quantos estão disponíveis (não emprestados, não reservados)
            List<Exemplar> disponiveis = exemplarDAO.buscarDisponiveisPorLivro(l.getId());
            // Busca o total físico que a biblioteca tem
            List<Exemplar> totalFisico = exemplarDAO.buscarPorLivro(l.getId());

            String status;
            int qtdDisp = disponiveis.size();
            int qtdTotal = totalFisico.size();

            if (qtdDisp > 0) {
                status = "Disponível (" + qtdDisp + " / " + qtdTotal + ")";
            } else if (qtdTotal == 0) {
                status = "SEM ACERVO"; // Livro cadastrado mas sem exemplares criados
            } else {
                status = "INDISPONÍVEL"; // Tem exemplares, mas todos emprestados/reservados
            }

            modeloTabela.addRow(new Object[]{
                    l.getTitulo(),
                    l.getAutor(),
                    l.getGenero().getNome(),
                    status
            });
        }
    }
}