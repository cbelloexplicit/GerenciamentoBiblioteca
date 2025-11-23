package view;

import Exception.ValidacaoException;
import model.Genero;
import model.Livro;
import Service.GeneroService;
import Service.LivroService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TelaCadastroLivro extends JFrame {

    // Componentes
    private JTextField txtTitulo;
    private JTextField txtAutor;
    private JComboBox<Genero> cmbGenero; // Caixa de seleção
    private JSpinner spnIdade; // Campo numérico com setinhas
    private JSpinner spnCopias;
    private JButton btnSalvar;
    private JButton btnCancelar;

    // services
    private LivroService livroService;
    private GeneroService generoService;

    public TelaCadastroLivro() {
        this.livroService = new LivroService();
        this.generoService = new GeneroService();

        configurarJanela();
        inicializarComponentes();
        carregarGeneros();
    }

    private void configurarJanela() {
        setTitle("Cadastro de Livro");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fecha só essa janela
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
    }

    private void inicializarComponentes() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Margens
        gbc.anchor = GridBagConstraints.WEST;

        // --- Título ---
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Título do Livro:"), gbc);

        txtTitulo = new JTextField(25);
        gbc.gridx = 1; gbc.gridy = 0;
        add(txtTitulo, gbc);

        // --- Autor ---
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Autor:"), gbc);

        txtAutor = new JTextField(25);
        gbc.gridx = 1; gbc.gridy = 1;
        add(txtAutor, gbc);

        // --- Gênero ---
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Gênero:"), gbc);

        cmbGenero = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(cmbGenero, gbc);
        gbc.fill = GridBagConstraints.NONE;

        // --- Idade Mínima ---
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Idade Mínima:"), gbc);

        spnIdade = new JSpinner(new SpinnerNumberModel(10, 0, 18, 1));
        gbc.gridx = 1; gbc.gridy = 3;
        add(spnIdade, gbc);

        // --- Quantidade de Cópias ---
        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Total de Cópias:"), gbc);

        spnCopias = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        gbc.gridx = 1; gbc.gridy = 4;
        add(spnCopias, gbc);

        // --- Botões ---
        JPanel painelBotoes = new JPanel();
        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");

        // Cor nos botões para ficar bonito
        btnSalvar.setBackground(new Color(100, 200, 100));

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(painelBotoes, gbc);

        // --- AÇÕES ---
        btnCancelar.addActionListener(e -> dispose()); // Fecha a janela

        btnSalvar.addActionListener(e -> salvarLivro());
    }

    private void carregarGeneros() {
        // Busca do DAO e joga no ComboBox
        List<Genero> lista = generoService.listarTodos();
        for (Genero g : lista) {
            cmbGenero.addItem(g);
        }
    }

    private void salvarLivro() {
        try {
            //Coleta dados da tela
            String titulo = txtTitulo.getText();
            String autor = txtAutor.getText();
            Genero genero = (Genero) cmbGenero.getSelectedItem();
            int idade = (int) spnIdade.getValue();
            int copias = (int) spnCopias.getValue();

            //Cria o objeto
            Livro novoLivro = new Livro(0, titulo, autor, genero, idade, copias);

            //Chama o Service
            livroService.salvar(novoLivro);

            //Feedback
            JOptionPane.showMessageDialog(this, "Livro cadastrado com sucesso!");
            dispose(); // Fecha a tela

        } catch (ValidacaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}