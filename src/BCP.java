import java.util.List;

/*
    Classe que representa um processo na memória
    Cada processo tem um nome, uma prioridade e uma lista de instruções
    A instrucao sendo executada é o indice PC na lista de instrucoes

 */
public class BCP implements Comparable<BCP>{
    private Estados estado;
    private int PC;
    Processo processo;
    int creditos;

    BCP(Estados estado, int prioridade, String nome, List<String> instrucoes) {
        this.estado = estado;
        this.PC = 0;
        this.processo = new Processo(prioridade, nome, instrucoes);
        this.creditos = prioridade;

    }

    public String getPID() {
        return processo.nome;
    }

    public Estados getEstado() {
        return this.estado;
    }

    public void setEstado(Estados estado) {
        this.estado = estado;
    }

    public void incrementarPC() {
        this.PC++;
    }

    public String proximaInstrucao() {
        return this.processo.instrucoes.get(this.PC);
    }

    public int getPrioridade() {
        return processo.prioridade;
    }

    public void setX(String X) {
        this.processo.X = X;
    }

    public void setY(String Y) {
        this.processo.Y = Y;
    }

    public String getX() {
        return this.processo.X;
    }

    public String getY() {
        return this.processo.Y;
    }

    public void processoFinalizado() {
        this.setEstado(Estados.FINALIZADO);
        String mensagem = String.format("%s terminado. X=%s. Y=%s%n", this.getPID(), this.getX(), this.getY());
        LogFile.getInstance().appendMessage(mensagem);
        TabelaProcessos.removerProcesso(this.getPID());
    }

    //Método utilizado para ordenar os processos utilizando a fila de prioridade do Java
    @Override
    public int compareTo(BCP o) {
        return Integer.compare(o.processo.prioridade, this.processo.prioridade);
    }

    @Override
    public String toString() {

        return processo.nome;
    }

    private static class Processo{
        int prioridade;
        String nome;
        List<String> instrucoes;
        String X;
        String Y;
        Processo(int prioridade, String nome, List<String> instrucoes) {
            this.prioridade = prioridade;
            this.nome = nome;
            this.instrucoes = instrucoes;
            this.X = "0";
            this.Y = "0";
        }
    }

    public enum Estados {
        PRONTO, EXECUTANDO, BLOQUEADO, FINALIZADO;
    }
}