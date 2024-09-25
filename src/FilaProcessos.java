import java.util.*;

public class FilaProcessos {

    //O processo atualmente sendo executado
    private static BCP processoExecutando;

    /*Estas duas Filas representam os processos prontos
      As duas estruturas armazenam os mesmos dados espelhados
      processosProntosPriority é uma PriorityQueue e armazena os processos prontos por ordem de créditos
      processosProntosQueue é uma Queue e armazena os processos prontos em ordem de FIFO
    */
    private final Queue<BCP> processosProntosPriority;
    private final Queue<BCP> processosProntosQueue;

    //Estrutura que armazena os processos bloqueados
    List<ProcessosBloqueados> processosBloqueados;

    public FilaProcessos() {
        this.processosProntosPriority = new PriorityQueue<>();
        this.processosProntosQueue = new LinkedList<>();
        this.processosBloqueados = new ArrayList<>();
    }

    private void addProcesso(BCP processo) {
        if (!this.processosProntosPriority.contains(processo))
            this.processosProntosPriority.add(processo);
        if (!this.processosProntosQueue.contains(processo))
            this.processosProntosQueue.add(processo);
    }

    public void inserirProcessos(List<BCP> processos) {
        for (BCP processo : processos) {
            this.inserirProcesso(processo);
        }
    }

    /*Este método busca um novo processo para ser executado priorizando a PriorityQueue
        Caso o processo retornado pela priorityQueue esteja com os creditos zerados, significa que ainda temos processos a serem executados e/ou bloqueados
        Então devemos buscar um novo processo por round robin, utiizando a processosProntosQueue
    * */
    private BCP pollProcesso() {
        BCP processo = this.processosProntosPriority.peek();
        if (processo.creditos > 0) {
            processo = this.processosProntosPriority.poll();
            this.processosProntosQueue.remove(processo);
            return processo;
        } else {
            processo = this.processosProntosQueue.poll();
            this.processosProntosPriority.remove(processo);
            return processo;
        }

    }

    public void inserirProcesso(BCP processo) {
        if (processo.getEstado().equals(BCP.Estados.PRONTO)) {
            this.addProcesso(processo);
        }
    }

    public Boolean temProcessosProntos() {
        return !this.processosProntosPriority.isEmpty();
    }

    //Método chamado pela classe do Escalonador que retorna o próximo processo pronto a ser executado, seguindo a ordem de prioridade
    public BCP iniciarNovoProcesso() {
        if (this.temProcessosProntos()) {
            BCP processo = pollProcesso();
            processo.setEstado(BCP.Estados.EXECUTANDO);
                if (processo != processoExecutando) {
                    processo.creditos -= 1;
                    processoExecutando = processo;
                    String mensagem = ("Executando "+processoExecutando +"\n");
                    LogFile.getInstance().appendMessage(mensagem);
                }
                return processo;

        }

        return null;
    }

    //Método chamado pela classe do Escalonador quando se é necessário bloquear um processo por E/S
    public void bloquearProcesso(BCP processo) {
        processo.setEstado(BCP.Estados.BLOQUEADO);
        this.processosBloqueados.add(new ProcessosBloqueados(processo, 2));
    }

    //Método chamado pela classe do Escalonador quando se passa um quantum para remover um tempo de bloqueio dos processos bloqueados
    public void diminuirBloqueados() {
        List<ProcessosBloqueados> bloqueados = this.processosBloqueados;
        for (int i = 0; i < bloqueados.size(); i++) {
            ProcessosBloqueados p = bloqueados.get(i);
            p.tempoRestante -= 1;

            if (p.tempoRestante == 0) {
                p.processo.setEstado(BCP.Estados.PRONTO);
                addProcesso(p.processo);
                this.processosBloqueados.remove(p);
            }
        }
    }

    //Estrutura que representa um processo bloqueado em memória
    private static class ProcessosBloqueados {
        BCP processo;
        int tempoRestante;

        public ProcessosBloqueados(BCP processo, int tempoRestante) {
            this.processo = processo;
            this.tempoRestante = tempoRestante;
        }
    }
}
