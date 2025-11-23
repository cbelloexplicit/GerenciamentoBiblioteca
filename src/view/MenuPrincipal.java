package view;

import model.Usuario;
import Service.LoginService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPrincipal extends JFrame {

    private Usuario usuarioLogado;

    // Componentes do Menu
    private JMenuBar barraMenu;

    // Menus Principais (JMenu)
    private JMenu menuArquivo;
    private JMenu menuCadastros;
    private JMenu menuCirculacao;
    private JMenu menuAcademico;
    private JMenu menuAjuda;

    // Itens de Menu
    private JMenuItem itemSair;
    private JMenuItem itemLogout;

    // Cadastros
    private JMenuItem itemCadUsuario;
    private JMenuItem itemCadLivro;
    private JMenuItem itemCadGenero;
    private JMenuItem itemCadTurma;

    // Circulação
    private JMenuItem itemEmprestimo;
    private JMenuItem itemDevolucao;

    // Acadêmico
    private JMenuItem itemProgramaLeitura;
    private JMenuItem itemMinhasTurmas;

    public MenuPrincipal(Usuario usuario) {
        this.usuarioLogado = usuario;
        initComponents();      // Inicia o visual
        configurarPermissoes(); // Aplica a lógica de quem pode ver o quê
    }

    private void initComponents() {
        // Configuração da Janela
        setTitle("SGBE - Biblioteca Escolar | Usuário: " + usuarioLogado.getNome());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Cria a Barra de Menu
        barraMenu = new JMenuBar();

        // --- 1. Menu Arquivo ---
        menuArquivo = new JMenu("Arquivo");
        itemLogout = new JMenuItem("Fazer Logout");
        itemSair = new JMenuItem("Sair do Sistema");
        menuArquivo.add(itemLogout);
        menuArquivo.addSeparator();
        menuArquivo.add(itemSair);

        // --- 2. Menu Cadastros (Admin/Bibliotecário) ---
        menuCadastros = new JMenu("Cadastros"); //
        itemCadUsuario = new JMenuItem("Usuários");
        itemCadLivro = new JMenuItem("Livros");
        itemCadGenero = new JMenuItem("Gêneros Literários");
        itemCadTurma = new JMenuItem("Turmas");

        menuCadastros.add(itemCadUsuario);
        menuCadastros.add(itemCadLivro);
        menuCadastros.add(itemCadGenero);
        menuCadastros.add(itemCadTurma);

        // --- 3. Menu Circulação (Biblioteca) ---
        menuCirculacao = new JMenu("Circulação");
        itemEmprestimo = new JMenuItem("Realizar Empréstimo");
        itemDevolucao = new JMenuItem("Realizar Devolução");
        menuCirculacao.add(itemEmprestimo);
        menuCirculacao.add(itemDevolucao);

        // --- 4. Menu Acadêmico (Professores) ---
        menuAcademico = new JMenu("Acadêmico");
        itemProgramaLeitura = new JMenuItem("Programa de Leitura");
        itemMinhasTurmas = new JMenuItem("Minhas Turmas");
        menuAcademico.add(itemProgramaLeitura);
        menuAcademico.add(itemMinhasTurmas);

        // Adiciona os menus na barra
        barraMenu.add(menuArquivo);
        barraMenu.add(menuCadastros);
        barraMenu.add(menuCirculacao);
        barraMenu.add(menuAcademico);

        // Define a barra de menu deste JFrame
        setJMenuBar(barraMenu);

        // Adiciona um painel de fundo com uma mensagem de boas-vindas
        JPanel painelFundo = new JPanel(new GridBagLayout());
        JLabel labelBemVindo = new JLabel("Bem-vindo ao Sistema SGBE");
        labelBemVindo.setFont(new Font("Arial", Font.BOLD, 24));
        painelFundo.add(labelBemVindo);
        add(painelFundo);

        // --- CONFIGURAÇÃO DOS EVENTOS ---
        configurarAcoes();
    }

    private void configurarPermissoes() {
        String tipo = usuarioLogado.getTipo();

        if (tipo.equals("ALUNO")) {
            // Aluno vê quase nada, apenas consultas
            menuCadastros.setVisible(false);
            menuCirculacao.setVisible(false);
            menuAcademico.setVisible(false);
        } else if (tipo.equals("PROFESSOR")) {
            // Professor vê Acadêmico, mas não cadastra usuários/livros
            menuCadastros.setVisible(false);
            menuCirculacao.setVisible(false);
            menuAcademico.setVisible(true);
        } else if (tipo.equals("BIBLIOTECARIO")) {
            // Bibliotecário vê Cadastros de Livros e Circulação, mas não Turmas/Usuários
            itemCadUsuario.setEnabled(false);
            itemCadTurma.setEnabled(false);
            menuAcademico.setVisible(false);
        }
        // ADMINISTRADOR vê tudo (nenhuma restrição aplicada)
    }

    private void configurarAcoes() {
        // Ação de Sair
        itemSair.addActionListener(e -> System.exit(0));

        // Ação de Logout
        itemLogout.addActionListener(e -> {
            LoginService service = new LoginService();
            service.realizarLogout(usuarioLogado);
            this.dispose(); // Fecha menu
            new TelaLogin().setVisible(true); // Reabre login
        });

        // Ação Cadastro de Livro
        itemCadLivro.addActionListener(e -> {
            TelaCadastroLivro telaL = new TelaCadastroLivro();
            telaL.setVisible(true);
        });

        //itemCadUsuario
        itemCadUsuario.addActionListener(e -> {
            new TelaGerenciarUsuarios().setVisible(true);
        });
        //itemCadGenero;
        itemCadGenero.addActionListener(e -> {
            new TelaCadastroGenero().setVisible(true);
        });
        //itemCadTurma;
        itemCadTurma.addActionListener(e -> {
            TelaCadastroTurma telaT = new TelaCadastroTurma();
            telaT.setVisible(true);
        });
        // Circulação
        //itemEmprestimo;
        itemEmprestimo.addActionListener(e -> {
            TelaRealizarEmprestimo telaE = new TelaRealizarEmprestimo();
            telaE.setVisible(true);
        });
        //itemDevolucao;
        itemDevolucao.addActionListener(e -> {
            TelaDevolucao telaD = new TelaDevolucao();
            telaD.setVisible(true);
        });
        // Acadêmico
        //itemProgramaLeitura;
        itemProgramaLeitura.addActionListener(e -> {
            TelaProgramaLeitura telaP = new TelaProgramaLeitura();
            telaP.setVisible(true);
        });
        //itemMinhasTurmas;
        itemEmprestimo.addActionListener(e -> {
            TelaRealizarEmprestimo telaE = new TelaRealizarEmprestimo();
            telaE.setVisible(true);
        });
    }
}