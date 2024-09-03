import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class FilaProcessos {

    private static BCP processoExecutando;

    PriorityQueue<BCP> processosProntos;
    List<ProcessosBloqueados> processosBloqueados;

    public FilaProcessos() {
        this.processosProntos = new PriorityQueue<>();
        this.processosBloqueados = new ArrayList<>();
    }

    public void inserirProcessos(List<BCP> processos) {
        for (BCP processo : processos) {
            this.inserirProcesso(processo);
        }
    }

    public void inserirProcesso(BCP processo) {
        if (processo.getEstado().equals(BCP.Estados.PRONTO)) {
            this.processosProntos.add(processo);
        }
    }

    private void redistribuirCreditos() {
        for (BCP processo : this.processosProntos) {
            processo.creditos = processo.getPrioridade();
        }
    }

    private Boolean temProcessosProntos() {
        if (!this.processosProntos.isEmpty()){
            for (BCP processo : this.processosProntos) {
                if (processo.creditos > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public BCP iniciarNovoProcesso() {
        if (this.temProcessosProntos()) {
            BCP processo = this.processosProntos.poll();
            processo.setEstado(BCP.Estados.EXECUTANDO);
            processo.creditos -= 1;
            if (processo != processoExecutando) {
                processoExecutando = processo;
                System.out.println("Executando "+processoExecutando);
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
        this.processosBloqueados.forEach((p) -> {
            p.tempoRestante -= 1;

            if (p.tempoRestante==0) {
                p.processo.setEstado(BCP.Estados.PRONTO);
                this.processosProntos.add(p.processo);
                this.processosBloqueados.remove(p);
            }
        });


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
