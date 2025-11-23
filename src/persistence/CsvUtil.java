package persistence;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

//Classe utilitária genérica para lidar com a leitura e escrita de arquivos (evita repetir código try-catch e BufferedReader).
public class CsvUtil {
    private static final String separador = ";";

    //Lê um arquivo CSV e retorna uma lista com as linhas.
    public static List<String> lerArquivo(String nomeArquivo) {
        List<String> linhas = new ArrayList<>();
        File arquivo = new File(nomeArquivo);
        // Se o arquivo não existe, retorna lista vazia para não quebrar o programa
        if (!arquivo.exists()) {
            return linhas;
        }
        // fecha o arquivo automaticamente ao terminar
        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                if (!linha.trim().isEmpty()) { // Ignora linhas em branco
                    linhas.add(linha);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo " + nomeArquivo + ": " + e.getMessage());
        }

        return linhas;
    }
    //ESCREVE uma lista de Strings num arquivo.
    public static void escreverArquivo(String nomeArquivo, List<String> conteudo, boolean append) {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(nomeArquivo, append))) {

            for (String linha : conteudo) {
                escritor.write(linha);
                escritor.newLine();
            }

        } catch (IOException e) {
            System.err.println("Erro ao gravar no arquivo " + nomeArquivo + ": " + e.getMessage());
        }
    }

    // auxiliar caso precise quebrar a linha nos DAOs
    public static String[] separarLinha(String linha) {
        // O -1 garante que campos vazios no final não sejam ignorados
        return linha.split(separador, -1);
    }
}
