import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
    Implementação do EP
    Código no Github para consulta
    https://github.com/renan002/EP1SO
    @JDK 17
 */
public class Escalonador {

    public static FilaProcessos filaProcessos;
    public static TabelaProcessos tabelaProcessos;

    //Valor do quantum a ser lido do arquivo
    public static int quantum = 0;
    //Media de trocas de processos (Total de trocas/total de processos)
    static Double MediaTP = 0.0;
    //Media de instrucoes por quantum (Total de instruçoes/Quantidade de quantums realizados)
    static Double MediaIQ = 0.0;
    //Quantidade de quantums realizados
    static int QuantQ = 0;



    public static void main(String[] args) {
        try {
            List<BCP> processos = lerProcessos();
            //Estrutura de dados para armazenar os processos
            filaProcessos = new FilaProcessos();
            filaProcessos.inserirProcessos(processos);
            tabelaProcessos = TabelaProcessos.iniciar(processos);

            quantum = lerQuantum();

            //Loop que executa todos os processos, só para quando nao tivermos mais nenhum processo na memória
            //ou seja, todos os processos foram executados
            while (TabelaProcessos.temProcessosExecutando()) {
                executarProcessos();
            }
            //Insercao das informacoes necessarias para o arquivo
            LogFile.getInstance().appendMessage(String.format("Quantum: %d%n",quantum));
            LogFile.getInstance().appendMessage(String.format("MediaDeTrocaDeProcessos: %.2f%n", (MediaTP/10)));
            LogFile.getInstance().appendMessage(String.format("MediaDeInstruçõesPorQuantum: %.2f%n", MediaIQ/QuantQ));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LogFile.getInstance().save();
        }
    }

    /*
        Método principal da solução.
        Este método é executado até que todos os processos sejam finalizados
        Ele recebe um novo processo da fila e executa as instrucoes até que seu quantum acabe ou o processo seja bloqueado ou quando o processo se encerre
     */
    private static void executarProcessos() {
        BCP processoExecutando = filaProcessos.iniciarNovoProcesso();

        filaProcessos.diminuirBloqueados();

        if (processoExecutando != null) {
            for (int i = 0; i < quantum; i++) {
                String instrucao = processoExecutando.proximaInstrucao();
                if (instrucao.equals("COM")) {
                    processoExecutando.incrementarPC();
                } else if (instrucao.contains("X=")) {
                    String[] instrucaoSplit = instrucao.split("=");
                    processoExecutando.setX(instrucaoSplit[1]);
                    processoExecutando.incrementarPC();
                } else if (instrucao.contains("Y=")) {
                    String[] instrucaoSplit = instrucao.split("=");
                    processoExecutando.setY(instrucaoSplit[1]);
                    processoExecutando.incrementarPC();
                } else if (instrucao.equals("E/S")) {
                    MediaTP++;
                    MediaIQ += (i+1);
                    QuantQ++;
                    String mensagem = "E/S iniciada em " + processoExecutando + "\n" +
                            "Interrompendo " + processoExecutando + " após " + (i + 1) + " instruções\n";
                    LogFile.getInstance().appendMessage(mensagem);
                    processoExecutando.incrementarPC();
                    filaProcessos.bloquearProcesso(processoExecutando);
                    break;
                } else if (instrucao.equals("SAIDA")) {
                    MediaTP++;
                    MediaIQ += (i+1);
                    QuantQ++;
                    String mensagem = "Interrompendo " + processoExecutando + " após " + (i + 1) + " instruções\n";
                    LogFile.getInstance().appendMessage(mensagem);
                    processoExecutando.processoFinalizado();
                    break;
                }
            }

            if (processoExecutando.getEstado().equals(BCP.Estados.EXECUTANDO)) {
                MediaTP++;
                MediaIQ += quantum;
                QuantQ++;
                String mensagem = "Interrompendo " + processoExecutando + " após " + quantum + " instruções\n";

                LogFile.getInstance().appendMessage(mensagem);
                processoExecutando.setEstado(BCP.Estados.PRONTO);
                filaProcessos.inserirProcesso(processoExecutando);
            }
        } else {
            String mensagem = "Nenhum processo executando\n";

            LogFile.getInstance().appendMessage(mensagem);
        }
    }

    /*
        Método para leitura dos processos contidos na pasta "programas"
        Este método cria um objeto "BCP" para cada arquivo de processo e armazena sua prioridade
     */
    private static List<BCP> lerProcessos() {
        ArrayList<BCP> processos = new ArrayList<>();
        try{
            int i = 1;
            File prioridades = new File("programas/prioridades.txt");
            Scanner scanner = new Scanner(prioridades);
            while (scanner.hasNextInt()) {
                File processo = new File(String.format("programas/%02d.txt",i));
                Scanner scanner2 = new Scanner(processo);
                String nome = scanner2.nextLine();
                ArrayList<String> instrucoes = new ArrayList<>();
                while (scanner2.hasNext()) {
                    instrucoes.add(scanner2.nextLine());
                }
                processos.add(new BCP(BCP.Estados.PRONTO, scanner.nextInt(), nome, instrucoes));
                scanner2.close();
                i++;
            }
            scanner.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        processos.sort(BCP::compareTo);

        processos.forEach(p -> LogFile.getInstance().appendMessage(String.format("Carregando %s%n", p)));

        return processos;
    }

    private static int lerQuantum() {
        try {
            File quantumFile = new File("programas/quantum.txt");
            Scanner scanner = new Scanner(quantumFile);
            return scanner.nextInt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

}

