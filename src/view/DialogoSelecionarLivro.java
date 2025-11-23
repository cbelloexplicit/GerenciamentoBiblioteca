package view;

import model.Exemplar;
import model.Livro;
import Service.LivroService;
import persistence.ExemplarDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class DialogoSelecionarLivro extends JDialog {

    private JTextField txtBusca;
    private JTable tabela;
    private DefaultTableModel modelo;

    private LivroService livroService;
    private ExemplarDAO exemplarDAO; // NOVO: Para consultar estoque

    // O livro que foi escolhido
    private Livro livroSelecionado = null;

    public DialogoSelecionarLivro(Dialog owner) { // Alterado para Dialog para funcionar sobre modais
        super(owner, "Selecionar Título", true);
        init();
    }

    public DialogoSelecionarLivro(Frame owner) {
        super(owner, "Selecionar Título", true);
        init();
    }

    private void init() {
        this.livroService = new LivroService();
        this.exemplarDAO = new ExemplarDAO();

        setSize(600, 450);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // --- BUSCA ---
        JPanel pnlNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtBusca = new JTextField(25);
        JButton btnBuscar = new JButton("Buscar");

        pnlNorte.add(new JLabel("Título/Autor:"));
        pnlNorte.add(txtBusca);
        pnlNorte.add(btnBuscar);
        add(pnlNorte, BorderLayout.NORTH);

        // --- TABELA ---
        String[] colunas = {"ID", "Título", "Autor", "Disponibilidade"};
        modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(modelo);
        tabela.setRowHeight(25);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(200); // Título maior

        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // --- RODAPÉ ---
        JPanel pnlSul = new JPanel();
        JButton btnConfirmar = new JButton("Confirmar Seleção");
        btnConfirmar.setBackground(new Color(100, 200, 100));
        JButton btnCancelar = new JButton("Cancelar");

        pnlSul.add(btnConfirmar);
        pnlSul.add(btnCancelar);
        add(pnlSul, BorderLayout.SOUTH);

        // --- EVENTOS ---
        btnBuscar.addActionListener(e -> pesquisar());
        txtBusca.addActionListener(e -> pesquisar()); // Enter funciona

        tabela.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) confirmar();
            }
        });

        btnConfirmar.addActionListener(e -> confirmar());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void pesquisar() {
        String termo = txtBusca.getText();
        List<Livro> lista = livroService.buscarPorTitulo(termo);

        List<Livro> porAutor = livroService.buscarPorAutor(termo);
        for (Livro l : porAutor) {
            if (lista.stream().noneMatch(existente -> existente.getId() == l.getId())) {
                lista.add(l);
            }
        }

        modelo.setRowCount(0);
        for (Livro l : lista) {
            // LÓGICA NOVA: Consulta o ExemplarDAO
            List<Exemplar> disponiveis = exemplarDAO.buscarDisponiveisPorLivro(l.getId());
            List<Exemplar> total = exemplarDAO.buscarPorLivro(l.getId());

            String status = disponiveis.size() + " / " + total.size();

            modelo.addRow(new Object[]{
                    l.getId(),
                    l.getTitulo(),
                    l.getAutor(),
                    status
            });
        }
    }

    private void confirmar() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um livro na tabela.");
            return;
        }

        long id = (long) tabela.getValueAt(linha, 0);

        List<Exemplar> disponiveis = exemplarDAO.buscarDisponiveisPorLivro(id);
        if (disponiveis.isEmpty()) {
            int resp = JOptionPane.showConfirmDialog(this,
                    "Este livro não possui exemplares disponíveis no momento.\nDeseja selecioná-lo mesmo assim?",
                    "Estoque Esgotado", JOptionPane.YES_NO_OPTION);

            if (resp != JOptionPane.YES_OPTION) return;
        }

        this.livroSelecionado = livroService.buscarPorId(id);
        dispose();
    }

    public Livro getLivroSelecionado() {
        return livroSelecionado;
    }
}