# 📚 SGBE - Sistema de Gerenciamento de Biblioteca Escolar

> Projeto acadêmico desenvolvido em Java para gerenciamento de acervo, empréstimos e programas de leitura escolar.

Este sistema simula o ambiente de uma biblioteca escolar, permitindo o controle de fluxo de livros, gestão de turmas e a automação de programas de leitura trimestrais. O projeto utiliza persistência de dados via arquivos **CSV**, dispensando o uso de SGBDs complexos para fins didáticos.

## 🚀 Tecnologias e Conceitos Aplicados

* **Linguagem:** Java (JDK 17+)
* **Interface Gráfica:** Java Swing (JFrame, JTable, Layout Managers)
* **Persistência:** Arquivos de Texto (.csv)
* **Arquitetura:** MVC (Model - View - Controller/Service)
* **Conceitos de POO:**
    * ✅ **Herança:** (`Usuario` -> `Aluno`, `Professor`, `Bibliotecario`, `Administrador`)
    * ✅ **Polimorfismo:** (Tratamento genérico de usuários nos DAOs e Menus)
    * ✅ **Tratamento de Exceções:** (Exceptions personalizadas para regras de negócio)
    * ✅ **Coleções:** (`List`, `ArrayList`, `Map` para manipulação de dados)

---

## 🔐 Credenciais de Teste (Acesso Rápido)

O sistema já vem populado com uma base de dados na pasta `dados/`. Utilize os usuários abaixo para testar os diferentes perfis de acesso:

| Perfil | Usuário (Matrícula) | Senha | Permissões Principais |
| :--- | :--- | :--- | :--- |
| **Administrador** | `admin` | `123` | Gerenciar usuários, ver logs de auditoria. |
| **Bibliotecário** | `biblio` | `123` | Cadastrar livros/gêneros, realizar empréstimos e devoluções. |
| **Professor** | `prof1` | `123` | Consultar turmas, criar programas de leitura automatizados. |
| **Aluno** | `aluno` | `123` | Consultar acervo, ver histórico pessoal e livros atribuídos. |

> **Nota:** A senha padrão para todos os usuários de teste é `123`.

---

## 🧪 Cenários de Teste Sugeridos

Para validar o funcionamento do sistema, siga estes roteiros:

### Cenário 1: Fluxo da Biblioteca (Bibliotecário)
1. Faça login como **Bibliotecário** (`biblio`).
2. Vá em **"Novo Empréstimo"**.
3. Pesquise o Aluno pela matrícula `202415` (Sophia Barbosa) e o Livro pelo ID `116` (Harry Potter).
4. Confirme o empréstimo.
5. Vá em **"Devolução"**, digite o ID do livro (`116`) e confirme a devolução (o sistema calculará multa se a data estiver atrasada).

### Cenário 2: Automação Acadêmica (Professor)
1. Faça login como **Professor** (`prof1`).
2. Vá em **"Planejar Leitura"**.
3. Selecione a Turma **"3º Ano A"** e o Gênero **"Romance Brasileiro"**.
4. Clique em **"Gerar Sugestão Automática"**.
5. O sistema distribuirá livros (como *Dom Casmurro* e *Vidas Secas*) aleatoriamente entre os alunos, respeitando o estoque.
6. Edite manualmente um livro na tabela (clique em "Trocar Livro") e salve o programa.

### Cenário 3: Visão do Aluno
1. Faça login como **Aluno** (`aluno` ou matrícula `202401` - Miguel).
2. Clique em **"Meu Painel"**.
3. Verifique na aba **"Programa de Leitura"** qual livro foi atribuído pelo professor.
4. Verifique na aba **"Histórico"** os livros que você já leu.

### Cenário 4: Auditoria (Administrador)
1. Faça login como **Administrador** (`admin`).
2. Clique em **"Logs de Acesso"**.
3. Visualize o histórico de quem entrou, saiu e modificou dados no sistema.

---

## 📂 Estrutura do Projeto

O código está organizado nos seguintes pacotes:

* `model`: Classes que representam os dados (Usuario, Livro, Emprestimo...).
* `view`: Telas do sistema (Swing).
* `service`: Regras de negócio e validações.
* `persistence`: DAOs responsáveis por ler e escrever nos arquivos `.csv`.
* `exception`: Exceções personalizadas (`ValidacaoException`, `AutenticacaoException`).
* `util`: Utilitários globais (Sessão do usuário logado).

---

## 🛠️ Como Rodar o Projeto

1.  Certifique-se de ter o **Java JDK** instalado (versão 17 ou superior recomendada).
2.  Clone este repositório.
3.  Abra o projeto na sua IDE favorita (IntelliJ, Eclipse, NetBeans).
4.  **Importante:** Verifique se a pasta `db/` existe na raiz do projeto com os arquivos `.csv`.
5.  Execute a classe principal:
    ```java
    src/main/Main.java
    ```

---

## 📄 Estrutura dos Arquivos CSV

Os dados são salvos na pasta `db/` seguindo este padrão:

* **usuarios.csv**: `ID;TIPO;NOME;MATRICULA;SENHA;ATIVO;CAMPO_EXTRA;DATA_NASC`
* **livros.csv**: `ID;TITULO;AUTOR;ID_GENERO;IDADE_MIN;TOTAL;DISPONIVEL`
* **emprestimos.csv**: `ID;ID_ALUNO;ID_LIVRO;DATA_EMP;DATA_PREV;DATA_REAL`

---

**Desenvolvido para a disciplina de Linguagem de Programação Orientada a Objetos.**
