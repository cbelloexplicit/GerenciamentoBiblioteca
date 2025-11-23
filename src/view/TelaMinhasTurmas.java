package view;

import model.*;
import persistence.ProgramaLeituraDAO;
import Service.TurmaService;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TelaMinhasTurmas extends JFrame {

    private Professor professorLogado;

    // Componentes
    private JList<Turma> listaTurmas;
    private DefaultListModel<Turma> modeloListaTurmas;
    private JTable tabelaAlunos;
    private DefaultTableModel modeloTabelaAlunos;
    private JLabel lblDetalhesTurma;
    private JLabel lblProgramaAtivo; // Mostra o nome do projeto atual

    // Services e DAOs
    private TurmaService turmaService;
    private ProgramaLeituraDAO programaDAO;

    public TelaMinhasTurmas(Usuario usuario) {
        if (usuario instanceof Professor) {
            this.professorLogado = (Professor) usuario;
        } else {
            JOptionPane.showMessageDialog(this, "Acesso negado.");
            dispose();
            return;
        }

        this.turmaService = new TurmaService();
        this.programaDAO = new ProgramaLeituraDAO(); // Inicializa o DAO

        configurarJanela();
        inicializarComponentes();
        carregarTurmas();
    }

    private void configurarJanela() {
        setTitle("Minhas Turmas e Leituras - Prof. " + professorLogado.getNome());
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        // --- PAINEL ESQUERDO (Lista) ---
        modeloListaTurmas = new DefaultListModel<>();
        listaTurmas = new JList<>(modeloListaTurmas);
        listaTurmas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaTurmas.setBorder(BorderFactory.createTitledBorder("Suas Turmas"));
        listaTurmas.addListSelectionListener(this::aoSelecionarTurma);

        JScrollPane scrollLista = new JScrollPane(listaTurmas);
        scrollLista.setPreferredSize(new Dimension(220, 0));
        add(scrollLista, BorderLayout.WEST);

        // --- PAINEL CENTRO (Tabela) ---
        JPanel painelDireito = new JPanel(new BorderLayout());

        // Cabeçalho com detalhes
        JPanel painelInfo = new JPanel(new GridLayout(2, 1));
        lblDetalhesTurma = new JLabel("Selecione uma turma à esquerda.");
        lblDetalhesTurma.setFont(new Font("Arial", Font.BOLD, 16));

        lblProgramaAtivo = new JLabel(" ");
        lblProgramaAtivo.setForeground(new Color(0, 100, 0)); // Verde escuro

        painelInfo.add(lblDetalhesTurma);
        painelInfo.add(lblProgramaAtivo);
        painelInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        painelDireito.add(painelInfo, BorderLayout.NORTH);

        // Tabela com nova coluna "Leitura Atual"
        String[] colunas = {"Matrícula", "Nome do Aluno", "Idade", "Leitura Atual (Programa)"};

        modeloTabelaAlunos = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabelaAlunos = new JTable(modeloTabelaAlunos);
        tabelaAlunos.setRowHeight(25);
        // Ajusta largura das colunas
        tabelaAlunos.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabelaAlunos.getColumnModel().getColumn(3).setPreferredWidth(250);

        painelDireito.add(new JScrollPane(tabelaAlunos), BorderLayout.CENTER);

        add(painelDireito, BorderLayout.CENTER);

        // --- Botão Fechar ---
        JButton btnFechar = new JButton("Voltar ao Menu");
        btnFechar.addActionListener(e -> dispose());
        add(btnFechar, BorderLayout.SOUTH);
    }

    private void carregarTurmas() {
        List<Turma> turmas = turmaService.buscarPorProfessor(professorLogado);
        modeloListaTurmas.clear();
        for (Turma t : turmas) modeloListaTurmas.addElement(t);
    }

    private void aoSelecionarTurma(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            Turma turma = listaTurmas.getSelectedValue();
            if (turma != null) {
                lblDetalhesTurma.setText("Turma: " + turma.getNome() + " (" + turma.getAnoLetivo() + ")");
                carregarAlunosELeituras(turma);
            }
        }
    }

    private void carregarAlunosELeituras(Turma turma) {
        modeloTabelaAlunos.setRowCount(0);
        List<Aluno> alunos = turma.getAlunos();

        // 1. Busca o Programa de Leitura ATIVO para esta turma
        // (Para saber qual livro cada um tem que ler)
        Map<Long, String> mapaLeituras = buscarLeiturasDaTurma(turma);

        if (alunos.isEmpty()) {
            modeloTabelaAlunos.addRow(new Object[]{"-", "Nenhum aluno matriculado", "-", "-"});
        } else {
            for (Aluno a : alunos) {
                // Cálculo de Idade
                String idadeStr = "-";
                if (a.getDataNascimento() != null) {
                    int idade = Period.between(a.getDataNascimento(), LocalDate.now()).getYears();
                    idadeStr = idade + " anos";
                }

                // Verifica se tem livro no mapa
                String livroAtribuido = mapaLeituras.getOrDefault(a.getId(), "---");

                modeloTabelaAlunos.addRow(new Object[]{
                        a.getMatricula(),
                        a.getNome(),
                        idadeStr,
                        livroAtribuido // Mostra o livro aqui!
                });
            }
        }
    }

    //Varre os programas de leitura, acha o ativo para essa turma
    private Map<Long, String> buscarLeiturasDaTurma(Turma turma) {
        Map<Long, String> mapa = new HashMap<>();
        List<ProgramaLeitura> programas = programaDAO.buscarPorTurma(turma.getId());

        ProgramaLeitura programaAtivo = null;

        // Procura um programa vigente (dentro da data de hoje)
        for (ProgramaLeitura p : programas) {
            if (p.isAtivo()) {
                programaAtivo = p;
                break;
            }
        }

        if (programaAtivo != null) {
            lblProgramaAtivo.setText("Projeto Ativo: " + programaAtivo.getTitulo() +
                    " (Até " + programaAtivo.getDataFim() + ")");

            // Mapeia: Para cada atribuição, guarda "ID Aluno" -> "Título Livro"
            for (AtribuicaoLeitura at : programaAtivo.getAtribuicoes()) {
                if (at.getLivro() != null) {
                    mapa.put(at.getAluno().getId(), at.getLivro().getTitulo());
                } else {
                    mapa.put(at.getAluno().getId(), "(Sem livro atribuído)");
                }
            }
        } else {
            lblProgramaAtivo.setText("Nenhum programa de leitura ativo no momento.");
        }

        return mapa;
    }
}