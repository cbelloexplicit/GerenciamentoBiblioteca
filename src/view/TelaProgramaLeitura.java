package view;

import Exception.ValidacaoException;
import model.*;
import Service.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TelaProgramaLeitura extends JFrame {

    private JTextField txtTitulo;
    private JComboBox<Turma> cmbTurma;
    private JComboBox<Genero> cmbGenero;
    private JSpinner spnTrimestre;
    private JFormattedTextField txtDataInicio;
    private JFormattedTextField txtDataFim;
    private JButton btnGerarSugestao;

    private JTable tabelaDistribuicao;
    private DefaultTableModel modeloTabela;

    private JButton btnTrocarLivro;

    private JButton btnSalvar;
    private JButton btnCancelar;

    private ProgramaLeituraService programaService;
    private TurmaService turmaService;
    private GeneroService generoService;
    private List<AtribuicaoLeitura> atribuicoesAtuais;

    public TelaProgramaLeitura() {
        this.programaService = new ProgramaLeituraService();
        this.turmaService = new TurmaService();
        this.generoService = new GeneroService();
        this.atribuicoesAtuais = new ArrayList<>();

        configurarJanela();
        inicializarComponentes();
        carregarCombos();
    }

    private void configurarJanela() {
        setTitle("Planejar Programa de Leitura");
        setSize(900, 650); // Um pouco maior
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        //formulario
        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBorder(BorderFactory.createTitledBorder("Configurações do Projeto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; painelForm.add(new JLabel("Título:"), gbc);
        txtTitulo = new JTextField("Leitura Trimestral", 20);
        gbc.gridx = 1; painelForm.add(txtTitulo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; painelForm.add(new JLabel("Turma:"), gbc);
        cmbTurma = new JComboBox<>();
        gbc.gridx = 1; painelForm.add(cmbTurma, gbc);

        gbc.gridx = 0; gbc.gridy = 2; painelForm.add(new JLabel("Gênero Base:"), gbc);
        cmbGenero = new JComboBox<>();
        gbc.gridx = 1; painelForm.add(cmbGenero, gbc);

        try {
            MaskFormatter mask = new MaskFormatter("##/##/####"); mask.setPlaceholderCharacter('_');
            txtDataInicio = new JFormattedTextField(mask); txtDataFim = new JFormattedTextField(mask);
        } catch (Exception e){}
        spnTrimestre = new JSpinner();
        gbc.gridx = 2; gbc.gridy = 1; painelForm.add(new JLabel("Início:"), gbc); gbc.gridx = 3; painelForm.add(txtDataInicio, gbc);
        gbc.gridx = 2; gbc.gridy = 2; painelForm.add(new JLabel("Fim:"), gbc); gbc.gridx = 3; painelForm.add(txtDataFim, gbc);

        btnGerarSugestao = new JButton("1. Gerar Sugestão Automática");
        btnGerarSugestao.setBackground(new Color(100, 149, 237));
        btnGerarSugestao.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4; gbc.anchor = GridBagConstraints.CENTER;
        painelForm.add(btnGerarSugestao, gbc);

        add(painelForm, BorderLayout.NORTH);

        JPanel painelCentral = new JPanel(new BorderLayout(5, 5));
        painelCentral.setBorder(BorderFactory.createTitledBorder("Distribuição de Livros"));

        String[] colunas = {"Aluno", "ID Livro", "Título do Livro"};

        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // NINGUÉM EDITA NA MÃO MAIS!
            }
        };

        tabelaDistribuicao = new JTable(modeloTabela);
        tabelaDistribuicao.setRowHeight(25);
        tabelaDistribuicao.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        painelCentral.add(new JScrollPane(tabelaDistribuicao), BorderLayout.CENTER);

        // Botão de Trocar Livro (Fica logo abaixo da tabela)
        btnTrocarLivro = new JButton("Trocar Livro do Aluno Selecionado (Pesquisar)");
        btnTrocarLivro.setIcon(UIManager.getIcon("FileView.directoryIcon")); // Ícone de pasta/busca se tiver
        painelCentral.add(btnTrocarLivro, BorderLayout.SOUTH);

        add(painelCentral, BorderLayout.CENTER);

        // --- 3. PAINEL SUL (Salvar) ---
        JPanel painelBotoes = new JPanel();
        btnSalvar = new JButton("2. Salvar Programa");
        btnSalvar.setBackground(new Color(34, 139, 34));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFont(new Font("Arial", Font.BOLD, 14));

        btnCancelar = new JButton("Cancelar");

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);
        add(painelBotoes, BorderLayout.SOUTH);

        // --- EVENTOS ---
        btnGerarSugestao.addActionListener(e -> gerarSugestao());
        btnSalvar.addActionListener(e -> salvarPrograma());
        btnCancelar.addActionListener(e -> dispose());

        // Evento Novo: Trocar Livro
        btnTrocarLivro.addActionListener(e -> abrirDialogoTroca());
    }

    private void abrirDialogoTroca() {
        int linha = tabelaDistribuicao.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um aluno na tabela para trocar o livro.");
            return;
        }

        // 1. Abre a janela de busca que criamos
        DialogoSelecionarLivro dialogo = new DialogoSelecionarLivro(this);
        dialogo.setVisible(true); // O código para aqui até ele fechar a janela

        // 2. Quando fechar, verifica se ele escolheu algo
        Livro novoLivro = dialogo.getLivroSelecionado();

        if (novoLivro != null) {
            // 3. Atualiza a Lista Lógica (atribuicoesAtuais)
            // Precisamos achar a atribuição correta. Assumindo que a ordem da tabela = ordem da lista
            AtribuicaoLeitura atribuicao = atribuicoesAtuais.get(linha);
            atribuicao.setLivro(novoLivro);

            // 4. Atualiza a Tabela Visual
            modeloTabela.setValueAt(novoLivro.getId(), linha, 1);
            modeloTabela.setValueAt(novoLivro.getTitulo(), linha, 2);

            JOptionPane.showMessageDialog(this, "Livro atualizado para: " + novoLivro.getTitulo());
        }
    }

    // ... (Métodos carregarCombos e gerarSugestao mantidos iguais ao anterior) ...
    private void carregarCombos() {
        List<Turma> turmas = turmaService.listarTodas();
        for (Turma t : turmas) cmbTurma.addItem(t);
        List<Genero> generos = generoService.listarTodos();
        for (Genero g : generos) cmbGenero.addItem(g);
    }

    private void gerarSugestao() {
        try {
            Turma turma = (Turma) cmbTurma.getSelectedItem();
            Genero genero = (Genero) cmbGenero.getSelectedItem();
            if (turma == null || genero == null) throw new ValidacaoException("Selecione Turma e Gênero.");

            turmaService.carregarAlunos(turma); // Carrega alunos

            // Lógica fake de idade média ou real se tiver implementado
            atribuicoesAtuais = programaService.gerarSugestaoDistribuicao(turma, genero, 10);

            atualizarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        for (AtribuicaoLeitura item : atribuicoesAtuais) {
            String livroId = (item.getLivro() != null) ? String.valueOf(item.getLivro().getId()) : "";
            String livroTitulo = (item.getLivro() != null) ? item.getLivro().getTitulo() : "SEM LIVRO";

            modeloTabela.addRow(new Object[]{
                    item.getAluno().getNome(),
                    livroId,
                    livroTitulo
            });
        }
    }

    private void salvarPrograma() {
        // A lógica de salvar fica MAIS SIMPLES agora, pois a lista 'atribuicoesAtuais'
        // já foi atualizada diretamente pelo método abrirDialogoTroca().
        // Não precisamos mais reconstruir lendo a tabela.

        try {
            String titulo = txtTitulo.getText();
            Turma turma = (Turma) cmbTurma.getSelectedItem();
            int trimestre = 1; // Pegue do spinner
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate inicio = LocalDate.parse(txtDataInicio.getText(), fmt);
            LocalDate fim = LocalDate.parse(txtDataFim.getText(), fmt);

            ProgramaLeitura prog = new ProgramaLeitura(titulo, turma, inicio, fim, trimestre, 2025);
            prog.setAtribuicoes(atribuicoesAtuais); // Lista já atualizada!

            programaService.salvarPrograma(prog);
            JOptionPane.showMessageDialog(this, "Salvo com sucesso!");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }
}