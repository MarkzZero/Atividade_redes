package atv_02;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
 
/** Exercício 2 - Servidor que intermedia conversa entre vários clientes (porta 9002). */
public class ServidorChat {
    private static final List<Atendente> clientes = new CopyOnWriteArrayList<>();
 
    public static void main(String[] args) throws IOException {
        try (ServerSocket servidor = new ServerSocket(9002)) {
            System.out.println("Servidor de chat na porta 9002...");
            while (true) {
                Socket socket = servidor.accept();
                Atendente a = new Atendente(socket);
                clientes.add(a);
                new Thread(a).start();
            }
        }
    }
 
    /** Envia a mensagem para todos, exceto o remetente. */
    static void broadcast(String msg, Atendente remetente) {
        for (Atendente c : clientes) {
            if (c != remetente) c.enviar(msg);
        }
    }
 
    static class Atendente implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private String nome = "?";
 
        Atendente(Socket socket) { this.socket = socket; }
 
        void enviar(String msg) { out.println(msg); }
 
        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"))) {
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
                out.println("Qual é o seu nome?");
                nome = in.readLine();
                if (nome == null || nome.trim().isEmpty()) nome = "Anônimo" + socket.getPort();
                System.out.println(nome + " entrou.");
                broadcast("*** " + nome + " entrou no chat ***", this);
 
                String msg;
                while ((msg = in.readLine()) != null) {
                    if (msg.equalsIgnoreCase("sair")) break;
                    System.out.println(nome + ": " + msg);
                    broadcast(nome + ": " + msg, this);
                }
            } catch (IOException e) {
                // cliente caiu
            } finally {
                clientes.remove(this);
                broadcast("*** " + nome + " saiu do chat ***", this);
                System.out.println(nome + " saiu.");
                try { socket.close(); } catch (IOException ignored) {}
            }
        }
    }
}
 