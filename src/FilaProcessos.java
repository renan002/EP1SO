import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FilaProcessos {

    private static BCP processoExecutando;

    Queue<BCP> processosProntos;
    List<ProcessosBloqueados> processosBloqueados;

    public FilaProcessos() {
        this.processosProntos = new LinkedList<>();
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

    public Boolean temProcessosProntos() {
        return !this.processosProntos.isEmpty();
    }

    public BCP iniciarNovoProcesso() {
        if (this.temProcessosProntos()) {
            BCP processo = this.processosProntos.poll();
            processo.setEstado(BCP.Estados.EXECUTANDO);

            if (processo != processoExecutando) {
                processo.creditos -= 1;
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
        List<ProcessosBloqueados> bloqueados = this.processosBloqueados;
        for (int i = 0; i < bloqueados.size(); i++) {
            ProcessosBloqueados p = bloqueados.get(i);
            p.tempoRestante -= 1;

            if (p.tempoRestante == 0) {
                p.processo.setEstado(BCP.Estados.PRONTO);
                this.processosProntos.add(p.processo);
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
