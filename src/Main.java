import javax.print.attribute.standard.Media;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static FilaProcessos filaProcessos;
    public static TabelaProcessos tabelaProcessos;

    static String caminho = "arquivo.txt";
    static FileWriter escritor;
    static int quantum = 0;
    static Double MediaTP = 0.0;
    static int QuantP = 0;


    public static void main(String[] args) {
        try {
            escritor = new FileWriter(caminho, true);
            escritor.write("Iniciando a execução...\n");
            escritor.flush();

            List<BCP> processos = lerProcessos();
            filaProcessos = new FilaProcessos();
            filaProcessos.inserirProcessos(processos);
            tabelaProcessos = TabelaProcessos.iniciar(processos);

            quantum = lerQuantum();

            while (TabelaProcessos.temProcessosExecutando()) {
                executarProcessos();
            }
            escreveNoArquivo("Quantum: "+ quantum + "\n");
            escreveNoArquivo("MediaDeTrocaDeProcessos: " + (MediaTP/10) + "\n");
            escreveNoArquivo("QuantidadeDeProcessos: " + QuantP);
        } catch (IOException e) {
            System.err.println("Erro ao inicializar o escritor: " + e.getMessage());
        } finally {
            // Fechar o escritor se foi inicializado
            try {
                if (escritor != null) {
                    escritor.close();
                }
            } catch (IOException e) {
                System.err.println("Erro ao fechar o escritor: " + e.getMessage());
            }
        }
    }

    private static void executarProcessos() {
        BCP processoExecutando = filaProcessos.iniciarNovoProcesso();

        filaProcessos.diminuirBloqueados();

        if (processoExecutando != null) {
            for (int i = 0; i < quantum; i++) {
                QuantP++;
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
                    String mensagem = "E/S iniciada em " + processoExecutando + "\n" +
                            "Interrompendo " + processoExecutando + " após " + (i + 1) + " instruções\n";
                    System.out.print(mensagem);
                    escreveNoArquivo(mensagem);
                    processoExecutando.incrementarPC();
                    filaProcessos.bloquearProcesso(processoExecutando);
                    break;
                } else if (instrucao.equals("SAIDA")) {
                    MediaTP++;
                    String mensagem = "Interrompendo " + processoExecutando + " após " + (i + 1) + " instruções\n";
                    System.out.print(mensagem);
                    escreveNoArquivo(mensagem);
                    processoExecutando.processoFinalizado();
                    break;
                }
            }

            if (processoExecutando.getEstado().equals(BCP.Estados.EXECUTANDO)) {
                MediaTP++;
                String mensagem = "Interrompendo " + processoExecutando + " após " + quantum + " instruções\n";
                System.out.print(mensagem);
                escreveNoArquivo(mensagem);
                processoExecutando.setEstado(BCP.Estados.PRONTO);
                filaProcessos.inserirProcesso(processoExecutando);
            }
        } else {
            String mensagem = "Nenhum processo executando\n";
            System.out.print(mensagem);
            escreveNoArquivo(mensagem);
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

        } catch (Exception e) {
            e.printStackTrace();
        }
        processos.sort(BCP::compareTo);

        processos.forEach(p -> {
            String Mensagem ="Carregando " + p + "\n";
            System.out.print(Mensagem);
            escreveNoArquivo(Mensagem);
        });

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

    public static void escreveNoArquivo(String texto) {
        try {
            if (escritor != null) {
                escritor.write(texto);
                escritor.flush();
            }
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo: " + e.getMessage());
        }
    }
}

