import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static FilaProcessos filaProcessos;
    public static TabelaProcessos tabelaProcessos;

    static int quantum = 0;

    public static void main(String[] args) {
        List<BCP> processos = lerProcessos();
        filaProcessos = new FilaProcessos();
        filaProcessos.inserirProcessos(processos);
        tabelaProcessos = TabelaProcessos.iniciar(processos);

        quantum = lerQuantum();

        while (TabelaProcessos.temProcessosExecutando()) {
            executarProcessos();
        }

    }


    private static void executarProcessos() {
        BCP processoExecutando = filaProcessos.iniciarNovoProcesso();

        if (processoExecutando.creditos < 0) {
            System.out.println("puta que me pariu");
        }
        filaProcessos.diminuirBloqueados();

        if (processoExecutando != null){

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
                    System.out.println("E/S iniciada em " + processoExecutando);
                    System.out.println("Interrompendo " + processoExecutando + " após " + (i + 1) + " instruções");
                    processoExecutando.incrementarPC();
                    filaProcessos.bloquearProcesso(processoExecutando);
                    break;
                } else if (instrucao.equals("SAIDA")) {
                    System.out.println("Interrompendo " + processoExecutando + " após " + (i + 1) + " instruções");
                    processoExecutando.processoFinalizado();
                    break;
                }

            }

            if (processoExecutando.getEstado().equals(BCP.Estados.EXECUTANDO)) {
                System.out.println("Interrompendo " + processoExecutando + " após " + quantum + " instruções");
                processoExecutando.setEstado(BCP.Estados.PRONTO);
                filaProcessos.inserirProcesso(processoExecutando);
            }
        }else {
            System.out.println("Nenhum processo executando");
        }

    }


    private static List<BCP> lerProcessos() {
        ArrayList<BCP> processos = new ArrayList<>();
        try{
            int i = 1;
            File prioridades = new File("programas/prioridades.txt");
            Scanner scanner = new Scanner(prioridades);
            while (scanner.hasNextInt()) {
                File processo = new File("programas/" + (i < 10 ? "0" + i : i) + ".txt");
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

        }catch (Exception e) {
            e.printStackTrace();
        }

        processos.sort(BCP::compareTo);

        processos.forEach(p -> System.out.println("Carregando " + p));

        return processos;
    }

    private static int lerQuantum() {
        try{
            File quantum = new File("programas/quantum.txt");
            Scanner scanner = new Scanner(quantum);
            return scanner.nextInt();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

}