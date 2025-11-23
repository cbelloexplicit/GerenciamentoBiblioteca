package view;

import model.Livro;
import Service.LivroService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

// Extends JDialog significa que é uma janela "filha" que bloqueia a "mãe" até fechar
public class DialogoSelecionarLivro extends JDialog {

    private JTextField txtBusca;
    private JTable tabela;
    private DefaultTableModel modelo;
    private LivroService livroService;

    // O livro que foi escolhido (começa nulo)
    private Livro livroSelecionado = null;

    public DialogoSelecionarLivro(Frame owner) {
        super(owner, "Selecionar Livro para o Aluno", true); // true = Modal (bloqueia a tela de trás)
        this.livroService = new LivroService();

        setSize(600, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        inicializar();
    }

    private void inicializar() {
        // --- BUSCA ---
        JPanel pnlNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtBusca = new JTextField(25);
        JButton btnBuscar = new JButton("Buscar por Título");

        pnlNorte.add(new JLabel("Pesquisar:"));
        pnlNorte.add(txtBusca);
        pnlNorte.add(btnBuscar);
        add(pnlNorte, BorderLayout.NORTH);

        // --- TABELA ---
        String[] colunas = {"ID", "Título", "Autor", "Estoque"};
        modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(modelo);
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

        // Atalho: Duplo clique na tabela já seleciona
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
        if (lista.isEmpty()) lista = livroService.buscarPorAutor(termo); // Tenta autor também

        modelo.setRowCount(0);
        for (Livro l : lista) {
            modelo.addRow(new Object[]{
                    l.getId(), l.getTitulo(), l.getAutor(), l.getCopiasDisponiveis()
            });
        }
    }

    private void confirmar() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) return;

        long id = (long) tabela.getValueAt(linha, 0);
        this.livroSelecionado = livroService.buscarPorId(id);

        dispose(); // Fecha a janela e volta para a tela anterior
    }

    // Método para a tela mãe pegar o resultado
    public Livro getLivroSelecionado() {
        return livroSelecionado;
    }
}