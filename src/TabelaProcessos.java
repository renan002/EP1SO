
import java.util.HashMap;
import java.util.List;

public class TabelaProcessos {

    private static TabelaProcessos tabelaProcessos;

    HashMap<String, BCP> processosEmMemoria;

    private TabelaProcessos(List<BCP> processos) {
        this.processosEmMemoria = new HashMap<>();

        for (BCP processo : processos) {
            this.processosEmMemoria.put(processo.getPID(), processo);
        }
    }

    public static TabelaProcessos iniciar(List<BCP> processos) {
        if (tabelaProcessos == null) {
            tabelaProcessos = new TabelaProcessos(processos);
        }

        return tabelaProcessos;
    }

    public static void removerProcesso(String PID) {
        tabelaProcessos.processosEmMemoria.remove(PID);
    }

    public static Boolean temProcessosExecutando() {
        boolean redistribuirCreditos = true;
        for (BCP processo : tabelaProcessos.processosEmMemoria.values()) {
            if (processo.creditos > 0) {
                redistribuirCreditos = false;
                break;
            }
        }
        if (redistribuirCreditos)
            tabelaProcessos.redistribuirCreditos();
        return !tabelaProcessos.processosEmMemoria.isEmpty();
    }

    private void redistribuirCreditos() {
        for (BCP processo : tabelaProcessos.processosEmMemoria.values()) {
            processo.creditos = processo.getPrioridade();
        }
    }

}
