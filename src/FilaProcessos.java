import java.util.*;

public class FilaProcessos {

    private static BCP processoExecutando;

    private final Queue<BCP> processosProntosPriority;
    private final Queue<BCP> processosProntosQueue;
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

    public void bloquearProcesso(BCP processo) {
        processo.setEstado(BCP.Estados.BLOQUEADO);
        this.processosBloqueados.add(new ProcessosBloqueados(processo, 2));
    }

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


    private static class ProcessosBloqueados {
        BCP processo;
        int tempoRestante;

        public ProcessosBloqueados(BCP processo, int tempoRestante) {
            this.processo = processo;
            this.tempoRestante = tempoRestante;
        }
    }
}
