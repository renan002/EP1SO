import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Main {
    public static int quantum = 0;
    private static PriorityQueue<Processo> processosProntos = new PriorityQueue<>();
    public static void main(String[] args) {

        quantum = getQuantum();
        getPrioridades();

        while (!processosProntos.isEmpty()) {
            Processo processo = processosProntos.poll();
            System.out.println("Carregando "+processo);
        }

    }

    public static int getQuantum() {
        int i = -1;
        try {
            File file = new File("programas/quantum.txt");
            Scanner scanner = new Scanner(file);
            i = scanner.nextInt();
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return i;
    }

    public static void getPrioridades() {
        int i = 1;
        try{
            File file = new File("programas/prioridades.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String fileName = i<10 ? "0" + i : i + "";
                int line = scanner.nextInt();
                File processo = new File("programas/" + fileName + ".txt");
                Scanner scanner2 = new Scanner(processo);
                List<String> instrucoes = new ArrayList<>();
                String nome = scanner2.nextLine();
                while (scanner2.hasNext()) {
                    instrucoes.add(scanner2.nextLine());
                }
                scanner2.close();
                processosProntos.add(new Processo(line, "Pronto", nome, instrucoes));
                i++;
            }
            scanner.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

}

class Processo implements Comparable<Processo>{
    int prioridade;
    String estado;
    String nome;
    List<String> instrucoes;

    Processo(int prioridade, String estado, String nome, List<String> instrucoes) {
        this.prioridade = prioridade;
        this.estado = estado;
        this.nome = nome;
        this.instrucoes = instrucoes;
    }

    @Override
    public int compareTo(Processo o) {
        return Integer.compare(o.prioridade, this.prioridade);
    }

    @Override
    public String toString() {
        return nome;
    }
}