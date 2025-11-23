package view;

import Exception.ValidacaoException;
import model.*;
import Service.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class TelaProgramaLeitura extends JFrame {

    // --- Componentes do Formulário ---
    private JTextField txtTitulo;
    private JComboBox<Turma> cmbTurma;
    private JComboBox<Genero> cmbGenero;
    private JSpinner spnTrimestre;
    private JFormattedTextField txtDataInicio;
    private JFormattedTextField txtDataFim;
    private JButton btnGerarSugestao;

    // --- Tabela de Distribuição ---
    private JTable tabelaDistribuicao;
    private DefaultTableModel modeloTabela;

    // --- Botões de Rodapé ---
    private JButton btnSalvar;
    private JButton btnCancelar;

    // --- Services e Controle ---
    private ProgramaLeituraService programaService;
    private TurmaService turmaService;
    private GeneroService generoService;
    private LivroService livroService; // Para validar edições manuais

    // Lista temporária que guarda os dados da tabela
    private List<AtribuicaoLeitura> atribuicoesAtuais;

    public TelaProgramaLeitura() {
        // Inicializa Services
        this.programaService = new ProgramaLeituraService();
        this.turmaService = new TurmaService();
        this.generoService = new GeneroService();
        this.livroService = new LivroService();
        this.atribuicoesAtuais = new ArrayList<>();

        configurarJanela();
        inicializarComponentes();
        carregarCombos();
    }

    private void configurarJanela() {
        setTitle("Planejar Programa de Leitura");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10)); // Espaçamento
    }

    private void inicializarComponentes() {
        // --- 1. PAINEL NORTE (Formulário de Configuração) ---
        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBorder(BorderFactory.createTitledBorder("Configurações do Projeto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Título do Projeto
        gbc.gridx = 0; gbc.gridy = 0;
        painelForm.add(new JLabel("Título do Projeto:"), gbc);
        txtTitulo = new JTextField("Leitura Trimestral", 20);
        gbc.gridx = 1; gbc.gridy = 0;
        painelForm.add(txtTitulo, gbc);

        // Turma
        gbc.gridx = 0; gbc.gridy = 1;
        painelForm.add(new JLabel("Selecione a Turma:"), gbc);
        cmbTurma = new JComboBox<>(); // Será preenchido depois
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        painelForm.add(cmbTurma, gbc);

        // Gênero Literário
        gbc.gridx = 0; gbc.gridy = 2;
        painelForm.add(new JLabel("Gênero Literário:"), gbc);
        cmbGenero = new JComboBox<>(); // Será preenchido depois
        gbc.gridx = 1; gbc.gridy = 2;
        painelForm.add(cmbGenero, gbc);

        // Datas e Trimestre
        gbc.gridx = 2; gbc.gridy = 0;
        painelForm.add(new JLabel("Trimestre (1-4):"), gbc);
        spnTrimestre = new JSpinner(new SpinnerNumberModel(1, 1, 4, 1));
        gbc.gridx = 3; gbc.gridy = 0;
        painelForm.add(spnTrimestre, gbc);

        try {
            MaskFormatter mascaraData = new MaskFormatter("##/##/####");
            mascaraData.setPlaceholderCharacter('_');

            gbc.gridx = 2; gbc.gridy = 1;
            painelForm.add(new JLabel("Data Início:"), gbc);
            txtDataInicio = new JFormattedTextField(mascaraData);
            txtDataInicio.setColumns(10);
            gbc.gridx = 3; gbc.gridy = 1;
            painelForm.add(txtDataInicio, gbc);

            gbc.gridx = 2; gbc.gridy = 2;
            painelForm.add(new JLabel("Data Fim:"), gbc);
            txtDataFim = new JFormattedTextField(mascaraData);
            txtDataFim.setColumns(10);
            gbc.gridx = 3; gbc.gridy = 2;
            painelForm.add(txtDataFim, gbc);

        } catch (Exception e) { e.printStackTrace(); }

        // Botão Gerar
        btnGerarSugestao = new JButton("Gerar Sugestão Automática");
        btnGerarSugestao.setBackground(new Color(100, 149, 237)); // Azul Cornflower
        btnGerarSugestao.setForeground(Color.WHITE);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        painelForm.add(btnGerarSugestao, gbc);

        add(painelForm, BorderLayout.NORTH);

        // --- 2. PAINEL CENTRAL (Tabela Editável) ---
        // Colunas: Aluno (Fixo), ID Livro (Editável), Título Livro (Visual)
        String[] colunas = {"Aluno", "ID Livro (Editável)", "Título do Livro"};

        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Só permite editar a coluna do ID do Livro (coluna 1)
                return column == 1;
            }
        };

        tabelaDistribuicao = new JTable(modeloTabela);
        tabelaDistribuicao.setRowHeight(25);

        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.setBorder(BorderFactory.createTitledBorder("Distribuição de Livros (Revise e Edite se necessário)"));
        painelTabela.add(new JScrollPane(tabelaDistribuicao), BorderLayout.CENTER);

        add(painelTabela, BorderLayout.CENTER);

        // --- 3. PAINEL SUL (Ações Finais) ---
        JPanel painelBotoes = new JPanel();
        btnSalvar = new JButton("Salvar Programa");
        btnSalvar.setBackground(new Color(34, 139, 34)); // Verde Floresta
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFont(new Font("Arial", Font.BOLD, 14));

        btnCancelar = new JButton("Cancelar");

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);

        add(painelBotoes, BorderLayout.SOUTH);

        // --- Eventos ---
        btnGerarSugestao.addActionListener(e -> gerarSugestao());
        btnSalvar.addActionListener(e -> salvarPrograma());
        btnCancelar.addActionListener(e -> dispose());
    }

    private void carregarCombos() {
        // Carrega Turmas
        List<Turma> turmas = turmaService.listarTodas();
        for (Turma t : turmas) cmbTurma.addItem(t);

        // Carrega Gêneros
        List<Genero> generos = generoService.listarTodos();
        for (Genero g : generos) cmbGenero.addItem(g);
    }

    //Chama o service para distribuir livros e preenche a tabela.
    private void gerarSugestao() {
        try {
            Turma turmaSelecionada = (Turma) cmbTurma.getSelectedItem();
            Genero genero = (Genero) cmbGenero.getSelectedItem();

            if (turmaSelecionada == null || genero == null) {
                throw new ValidacaoException("Selecione Turma e Gênero.");
            }
            turmaService.carregarAlunos(turmaSelecionada);
            if (turmaSelecionada.getAlunos().isEmpty()) {
                throw new ValidacaoException("A turma '" + turmaSelecionada.getNome() + "' não possui alunos cadastrados ou os nomes das turmas não coincidem.");
            }
            int idadeMedia = calcularIdadeMedia(turmaSelecionada);

            JOptionPane.showMessageDialog(this,
                    "Calculando sugestões para " + turmaSelecionada.getAlunos().size() + " alunos (Média: " + idadeMedia + " anos).",
                    "Processando",
                    JOptionPane.INFORMATION_MESSAGE);
            atribuicoesAtuais = programaService.gerarSugestaoDistribuicao(turmaSelecionada, genero, idadeMedia);

            atualizarTabela();
            JOptionPane.showMessageDialog(this, "Sugestão gerada! Verifique a tabela abaixo.");

        } catch (ValidacaoException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar: " + ex.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage());
        }
    }
    //aux p/ calc idade media turma
    private int calcularIdadeMedia(Turma turma) {
        List<Aluno> alunos = turma.getAlunos();

        if (alunos == null || alunos.isEmpty()) {
            return 10; // Valor padrão se a turma estiver vazia
        }

        int somaIdades = 0;
        int alunosComData = 0;
        LocalDate hoje = LocalDate.now();

        for (Aluno a : alunos) {
            // Verifica se a data existe
            if (a.getDataNascimento() != null) {
                // Period.between calcula a diferença exata em anos, meses e dias
                int idade = Period.between(a.getDataNascimento(), hoje).getYears();
                somaIdades += idade;
                alunosComData++;
            }
        }

        // Evita divisão por zero
        if (alunosComData == 0) {
            return 10; // Valor padrão
        }

        return somaIdades / alunosComData; // Retorna a média inteira (ex: 15)
    }

    private void atualizarTabela() {
        modeloTabela.setRowCount(0); // Limpa

        for (AtribuicaoLeitura item : atribuicoesAtuais) {
            String nomeAluno = item.getAluno().getNome();
            String idLivro = "";
            String tituloLivro = "--- SEM LIVRO ---";

            if (item.getLivro() != null) {
                idLivro = String.valueOf(item.getLivro().getId());
                tituloLivro = item.getLivro().getTitulo();
            }

            modeloTabela.addRow(new Object[]{nomeAluno, idLivro, tituloLivro});
        }
    }

    private void salvarPrograma() {
        try {
            // 1. Valida Dados do Form
            String titulo = txtTitulo.getText();
            Turma turma = (Turma) cmbTurma.getSelectedItem();
            int trimestre = (int) spnTrimestre.getValue();

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate inicio = LocalDate.parse(txtDataInicio.getText(), fmt);
            LocalDate fim = LocalDate.parse(txtDataFim.getText(), fmt);

            // 2. Reconstrói a lista de atribuições baseada na Tabela (caso o prof tenha editado)
            // Isso permite que a edição manual do ID funcione
            List<AtribuicaoLeitura> listaFinal = reconstruirListaDaTabela(turma);

            // 3. Cria o Objeto Programa
            ProgramaLeitura programa = new ProgramaLeitura(titulo, turma, inicio, fim, trimestre, LocalDate.now().getYear());
            programa.setAtribuicoes(listaFinal);

            // 4. Salva
            programaService.salvarPrograma(programa);

            JOptionPane.showMessageDialog(this, "Programa de Leitura salvo com sucesso!");
            dispose();

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use dd/mm/aaaa.");
        } catch (ValidacaoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro de Validação", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro crítico: " + e.getMessage());
        }
    }

    //auxiliar que lê a JTable linha por linha para capturar edições manuais.
    private List<AtribuicaoLeitura> reconstruirListaDaTabela(Turma turma) throws ValidacaoException {
        List<AtribuicaoLeitura> lista = new ArrayList<>();
        List<Aluno> alunos = turma.getAlunos(); // A ordem da tabela é a mesma da lista de alunos (esperado)
        // Como a tabela pode ter sido reordenada visualmente, o ideal seria ter o ID do aluno na tabela.

        for (int i = 0; i < modeloTabela.getRowCount(); i++) {
            // Recupera o objeto original da lista (para pegar o Aluno correto)
            AtribuicaoLeitura itemOriginal = atribuicoesAtuais.get(i);
            Aluno aluno = itemOriginal.getAluno();

            // Pega o ID que está na célula (que pode ter sido editado)
            String idLivroTexto = (String) modeloTabela.getValueAt(i, 1);
            Livro livroFinal = null;

            if (idLivroTexto != null && !idLivroTexto.trim().isEmpty()) {
                try {
                    long id = Long.parseLong(idLivroTexto);
                    // Valida se o livro existe
                    livroFinal = livroService.buscarPorId(id);
                    if (livroFinal == null) {
                        throw new ValidacaoException("O ID de livro " + id + " informado na linha " + (i+1) + " não existe.");
                    }
                } catch (NumberFormatException e) {
                    throw new ValidacaoException("ID de livro inválido na linha " + (i+1));
                }
            }

            lista.add(new AtribuicaoLeitura(aluno, livroFinal));
        }
        return lista;
    }
}