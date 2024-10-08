import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class LogFile {

    private FileWriter escritor;

    private static LogFile logFile;

    private StringBuilder sb;

    private LogFile() {
        try {
            File quantumFile = new File("programas/quantum.txt");
            Scanner scanner = new Scanner(quantumFile);
            int quantum = scanner.nextInt();
            File dir = new File("logs");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File log = new File(String.format("logs/log%02d.txt", quantum));
            if (!log.exists()) {
                log.createNewFile();
            }
            this.escritor = new FileWriter(log);
            this.sb = new StringBuilder();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Funcao que verifica a existencia do arquivo (se nao existir, cria um novo)
    public static LogFile getInstance() {
        if (logFile == null) {
            logFile = new LogFile();
        }
        return logFile;
    }

    //Funcao para adicionar a mensagem no arquivo
    public void appendMessage(String message) {
        System.out.print(message);
        logFile.sb.append(message);
    }
    //Função para salvar e fechar o arquivo
    public void save() {
        try {
            logFile.escritor.write(sb.toString());
            logFile.escritor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
