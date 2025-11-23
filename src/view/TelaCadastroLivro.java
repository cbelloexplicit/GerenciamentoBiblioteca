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

    // O campo de cópias foi REMOVIDO daqui.
    // Agora as cópias são adicionadas na tela "Gerenciar Acervo" -> "Gerenciar Exemplares".

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
        setTitle("Cadastro de Novo Título");
        setSize(500, 350); // Altura ajustada (menos campos)
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
    }

    private void inicializarComponentes() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Margens
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Título ---
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0;
        add(new JLabel("Título do Livro:"), gbc);

        txtTitulo = new JTextField(25);
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0;
        add(txtTitulo, gbc);

        // --- Autor ---
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        add(new JLabel("Autor:"), gbc);

        txtAutor = new JTextField(25);
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0;
        add(txtAutor, gbc);

        // --- Gênero ---
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0;
        add(new JLabel("Gênero:"), gbc);

        cmbGenero = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.weightx = 1.0;
        add(cmbGenero, gbc);

        // --- Idade Mínima ---
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 0;
        add(new JLabel("Idade Mínima (anos):"), gbc);

        spnIdade = new JSpinner(new SpinnerNumberModel(10, 0, 18, 1));
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.weightx = 1.0;
        add(spnIdade, gbc);

        // --- Aviso (Feedback Visual) ---
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        JLabel lblAviso = new JLabel("<html><center><font color='gray' size='3'>Após salvar o título, vá em 'Gerenciar Acervo'<br>para adicionar os exemplares físicos.</font></center></html>");
        lblAviso.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblAviso, gbc);
        gbc.gridwidth = 1; // Reset

        // --- Botões ---
        JPanel painelBotoes = new JPanel();
        btnSalvar = new JButton("Salvar Título");
        btnCancelar = new JButton("Cancelar");

        btnSalvar.setBackground(new Color(100, 200, 100)); // Verde
        btnSalvar.setForeground(Color.WHITE);

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(painelBotoes, gbc);

        // --- AÇÕES ---
        btnCancelar.addActionListener(e -> dispose());

        btnSalvar.addActionListener(e -> salvarLivro());
    }

    private void carregarGeneros() {
        cmbGenero.removeAllItems();
        List<Genero> lista = generoService.listarTodos();
        for (Genero g : lista) {
            cmbGenero.addItem(g);
        }
    }

    private void salvarLivro() {
        try {
            // Coleta dados da tela
            String titulo = txtTitulo.getText();
            String autor = txtAutor.getText();
            Genero genero = (Genero) cmbGenero.getSelectedItem();
            int idade = (int) spnIdade.getValue();

            // ID = 0 (Novo)
            Livro novoLivro = new Livro(0, titulo, autor, genero, idade);

            // Chama o Service
            livroService.salvar(novoLivro);

            // Feedback
            JOptionPane.showMessageDialog(this, "Título cadastrado com sucesso!\nAgora adicione os exemplares no Gerenciador de Acervo.");
            dispose();

        } catch (ValidacaoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Validação", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}