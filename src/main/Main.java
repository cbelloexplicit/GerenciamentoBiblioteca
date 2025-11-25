package main;

import view.TelaLogin;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Erro ao configurar tema visual: " + e.getMessage());
        }

        System.out.println("Sistema iniciado com dados de teste (Mock).");
        System.out.println("");
        System.out.println("--- CREDENCIAIS PARA TESTE ---");
        System.out.println("1. ADMINISTRADOR:");
        System.out.println("   Matrícula: admin");
        System.out.println("   Senha:     123");
        System.out.println("");
        System.out.println("2. PROFESSOR:");
        System.out.println("   Matrícula: prof");
        System.out.println("   Senha:     123");
        System.out.println("");
        System.out.println("3. ALUNO:");
        System.out.println("   Matrícula: isabella");
        System.out.println("   Senha:     senha123");
        System.out.println("");
        System.out.println("4. BIBLIOTECARIO:");
        System.out.println("   Matrícula: biblio");
        System.out.println("   Senha:     123");
        System.out.println("=================================================");

        SwingUtilities.invokeLater(() -> {
            TelaLogin login = new TelaLogin();
            login.setVisible(true);
        });
    }
}