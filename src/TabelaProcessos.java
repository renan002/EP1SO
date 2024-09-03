
import java.util.HashMap;
import java.util.List;

public class TabelaProcessos {

    private static TabelaProcessos tabelaProcessos;

    HashMap<String, BCP> processosRodando;

    private TabelaProcessos(List<BCP> processos) {
        this.processosRodando = new HashMap<>();

        for (BCP processo : processos) {
            this.processosRodando.put(processo.getPID(), processo);
        }
    }

    public BCP getProcesso(String PID) {
        return this.processosRodando.get(PID);
    }

    public static TabelaProcessos iniciar(List<BCP> processos) {
        if (tabelaProcessos == null) {
            tabelaProcessos = new TabelaProcessos(processos);
        }

        return tabelaProcessos;
    }

    public static void removerProcesso(String PID) {
        tabelaProcessos.processosRodando.remove(PID);
    }
}
