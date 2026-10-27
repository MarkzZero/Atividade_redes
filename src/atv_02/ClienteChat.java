package atv_02;

import java.io.*;
import java.net.*;

public class ClienteChat {
    public static void main(String[] args) throws IOException {
        String host = args.length > 0 ? args[0] : "localhost";
        try (Socket socket = new Socket(host, 9002);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
             BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in))) {

            Thread receptor = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) System.out.println(msg);
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
                out.println(linha);
                if (linha.equalsIgnoreCase("sair")) break;
            }
        }
    }
}