package atv_01;
import java.io.*;
import java.net.*;
 
public class ClienteTexto {
    public static void main(String[] args) throws IOException {
        String host = args.length > 0 ? args[0] : "localhost";
        try (Socket socket = new Socket(host, 9001);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
             BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in))) {
 
            System.out.println("Conectado ao servidor. Digite mensagens (\"sair\" encerra).");
 
            Thread receptor = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        System.out.println("Servidor: " + msg);
                    }
                    System.out.println("Servidor encerrou a conexão.");
                    System.exit(0);
                } catch (IOException e) {
                    System.out.println("Conexão encerrada.");
                }
            });
            receptor.setDaemon(true);
            receptor.start();
 
            String linha;
            while ((linha = teclado.readLine()) != null) {
                if (linha.equalsIgnoreCase("sair")) break;
                out.println(linha);
            }
        }
    }
}
