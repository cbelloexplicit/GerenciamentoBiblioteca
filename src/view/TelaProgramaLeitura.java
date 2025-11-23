package view;

import Exception.ValidacaoException;
import model.*;
import Service.*;
import persistence.ExemplarDAO;

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
    private ExemplarDAO exemplarDAO; // Necessário para verificar estoque na troca manual

    private List<AtribuicaoLeitura> atribuicoesAtuais;

    public TelaProgramaLeitura() {
        this.programaService = new ProgramaLeituraService();
        this.turmaService = new TurmaService();
        this.generoService = new GeneroService();
        this.exemplarDAO = new ExemplarDAO();
        this.atribuicoesAtuais = new ArrayList<>();

        configurarJanela();
        inicializarComponentes();
        carregarCombos();
    }

    private void configurarJanela() {
        setTitle("Planejar Programa de Leitura (Alocação de Exemplares)");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        // --- 1. FORMULÁRIO DE CONFIGURAÇÃO ---
        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBorder(BorderFactory.createTitledBorder("Configurações do Projeto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; painelForm.add(new JLabel("Título do Projeto:"), gbc);
        txtTitulo = new JTextField("Leitura Trimestral", 20);
        gbc.gridx = 1; painelForm.add(txtTitulo, gbc);

        gbc.gridx = 0; gbc.gridy = 1; painelForm.add(new JLabel("Turma:"), gbc);
        cmbTurma = new JComboBox<>();
        gbc.gridx = 1; painelForm.add(cmbTurma, gbc);

        gbc.gridx = 0; gbc.gridy = 2; painelForm.add(new JLabel("Gênero Base:"), gbc);
        cmbGenero = new JComboBox<>();
        gbc.gridx = 1; painelForm.add(cmbGenero, gbc);

        // Datas
        try {
            MaskFormatter mask = new MaskFormatter("##/##/####");
            mask.setPlaceholderCharacter('_');
            txtDataInicio = new JFormattedTextField(mask);
            txtDataFim = new JFormattedTextField(mask);

            // Sugestão de data (hoje)
            txtDataInicio.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            txtDataFim.setText(LocalDate.now().plusMonths(3).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        } catch (Exception e){}

        gbc.gridx = 2; gbc.gridy = 1; painelForm.add(new JLabel("Início:"), gbc);
        gbc.gridx = 3; painelForm.add(txtDataInicio, gbc);

        gbc.gridx = 2; gbc.gridy = 2; painelForm.add(new JLabel("Fim:"), gbc);
        gbc.gridx = 3; painelForm.add(txtDataFim, gbc);

        btnGerarSugestao = new JButton("1. Gerar Sugestão Automática");
        btnGerarSugestao.setBackground(new Color(100, 149, 237)); // Azul Cornflower
        btnGerarSugestao.setForeground(Color.WHITE);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 5, 5, 5);
        painelForm.add(btnGerarSugestao, gbc);

        add(painelForm, BorderLayout.NORTH);

        // --- 2. TABELA DE DISTRIBUIÇÃO ---
        JPanel painelCentral = new JPanel(new BorderLayout(5, 5));
        painelCentral.setBorder(BorderFactory.createTitledBorder("Distribuição de Exemplares"));

        String[] colunas = {"Aluno", "ID Exemplar", "Título do Livro"};

        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabelaDistribuicao = new JTable(modeloTabela);
        tabelaDistribuicao.setRowHeight(25);
        tabelaDistribuicao.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Ajuste colunas
        tabelaDistribuicao.getColumnModel().getColumn(1).setPreferredWidth(80); // ID menor
        tabelaDistribuicao.getColumnModel().getColumn(2).setPreferredWidth(300); // Título maior

        painelCentral.add(new JScrollPane(tabelaDistribuicao), BorderLayout.CENTER);

        // Botão de Trocar
        btnTrocarLivro = new JButton("Trocar Livro do Aluno Selecionado");
        btnTrocarLivro.setToolTipText("Escolha outro título e o sistema buscará um exemplar disponível.");
        painelCentral.add(btnTrocarLivro, BorderLayout.SOUTH);

        add(painelCentral, BorderLayout.CENTER);

        // --- 3. BOTÕES DE AÇÃO (SUL) ---
        JPanel painelBotoes = new JPanel();
        btnSalvar = new JButton("2. Salvar Programa");
        btnSalvar.setBackground(new Color(34, 139, 34)); // Verde Floresta
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFont(new Font("Arial", Font.BOLD, 14));
        btnSalvar.setPreferredSize(new Dimension(200, 40));

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(100, 40));

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);
        add(painelBotoes, BorderLayout.SOUTH);

        // --- EVENTOS ---
        btnGerarSugestao.addActionListener(e -> gerarSugestao());
        btnSalvar.addActionListener(e -> salvarPrograma());
        btnCancelar.addActionListener(e -> dispose());
        btnTrocarLivro.addActionListener(e -> abrirDialogoTroca());
    }

    private void carregarCombos() {
        cmbTurma.removeAllItems();
        cmbGenero.removeAllItems();

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

            // Carrega os alunos da turma (caso não estejam carregados)
            turmaService.carregarAlunos(turma);

            // Chama o serviço que agora busca EXEMPLARES reais
            // Idade média hardcoded em 10, pode melhorar depois
            atribuicoesAtuais = programaService.gerarSugestaoDistribuicao(turma, genero, 10);

            atualizarTabela();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar: " + ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        for (AtribuicaoLeitura item : atribuicoesAtuais) {
            String idExemplar = "";
            String tituloLivro = "SEM LIVRO DISPONÍVEL";

            if (item.getExemplar() != null) {
                idExemplar = String.valueOf(item.getExemplar().getId());
                tituloLivro = item.getExemplar().getLivro().getTitulo();
            }

            modeloTabela.addRow(new Object[]{
                    item.getAluno().getNome(),
                    idExemplar,
                    tituloLivro
            });
        }
    }

    private void abrirDialogoTroca() {
        int linha = tabelaDistribuicao.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um aluno na tabela para trocar o livro.");
            return;
        }

        // 1. Abre a janela para escolher o TÍTULO (Livro)
        DialogoSelecionarLivro dialogo = new DialogoSelecionarLivro(this);
        dialogo.setVisible(true); // Modal trava aqui

        Livro livroEscolhido = dialogo.getLivroSelecionado();

        if (livroEscolhido != null) {
            // 2. O sistema tenta achar um EXEMPLAR físico desse livro
            List<Exemplar> disponiveis = exemplarDAO.buscarDisponiveisPorLivro(livroEscolhido.getId());

            if (disponiveis.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Não há exemplares disponíveis na estante para '" + livroEscolhido.getTitulo() + "'.\n" +
                                "Escolha outro título ou adicione cópias no acervo.",
                        "Estoque Insuficiente", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Pega o primeiro disponível (Logística Simplificada)
            Exemplar novoExemplar = disponiveis.get(0);

            // 3. Atualiza a Lista Lógica e Visual
            AtribuicaoLeitura atribuicao = atribuicoesAtuais.get(linha);
            atribuicao.setExemplar(novoExemplar);

            modeloTabela.setValueAt(novoExemplar.getId(), linha, 1);
            modeloTabela.setValueAt(novoExemplar.getLivro().getTitulo(), linha, 2);

            JOptionPane.showMessageDialog(this, "Livro trocado! Alocado exemplar #" + novoExemplar.getId());
        }
    }

    private void salvarPrograma() {
        try {
            String titulo = txtTitulo.getText();
            Turma turma = (Turma) cmbTurma.getSelectedItem();

            // Tratamento de datas
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate inicio = LocalDate.parse(txtDataInicio.getText(), fmt);
            LocalDate fim = LocalDate.parse(txtDataFim.getText(), fmt);

            // Cria o objeto Programa
            ProgramaLeitura prog = new ProgramaLeitura(titulo, turma, inicio, fim, 1, LocalDate.now().getYear());
            prog.setAtribuicoes(atribuicoesAtuais);

            // O Service vai cuidar de RESERVAR os exemplares se a data for hoje
            programaService.salvarPrograma(prog);

            JOptionPane.showMessageDialog(this, "Programa de Leitura salvo com sucesso!\nOs exemplares foram reservados.");
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
        }
    }
}