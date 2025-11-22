package model;

    public class AtribuicaoLeitura {
        private Aluno aluno;
        private Livro livro;

        public AtribuicaoLeitura(Aluno aluno, Livro livro) {
            this.aluno = aluno;
            this.livro = livro;
        }

        public Aluno getAluno() {
            return aluno;
        }

        public void setAluno(Aluno aluno) {
            this.aluno = aluno;
        }

        public Livro getLivro() {
            return livro;
        }

        public void setLivro(Livro livro) {
            this.livro = livro;
        }

        @Override
        public String toString() {
            return aluno.getNome() + " -> " + (livro != null ? livro.getTitulo() : "SEM LIVRO DISPONÍVEL");
        }
    }
