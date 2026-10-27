package atv_01;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorTexto {
    public static void main(String[] args) throws IOException {
        try (ServerSocket servidor = new ServerSocket(9001)) {
            System.out.println("Servidor aguardando cliente na porta 9001...");
            try (Socket socket = servidor.accept();
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                 PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
                 BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in))) {
 
                System.out.println("Cliente conectado: " + socket.getInetAddress());
 
                Thread receptor = new Thread(() -> {
                    try {
                        String msg;
                        while ((msg = in.readLine()) != null) {
                            System.out.println("Cliente: " + msg);
                        }
                        System.out.println("Cliente desconectou.");
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

}
