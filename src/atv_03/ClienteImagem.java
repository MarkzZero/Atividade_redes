package atv_03;

import java.io.*;
import java.net.*;

public class ClienteImagem {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Uso: java ClienteImagem <arquivo> [host]");
            return;
        }
        File arquivo = new File(args[0]);
        String host = args.length > 1 ? args[1] : "localhost";

        try (Socket socket = new Socket(host, 9003);
             DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
             DataInputStream in = new DataInputStream(socket.getInputStream());
             FileInputStream fis = new FileInputStream(arquivo)) {

            out.writeUTF(arquivo.getName());
            out.writeLong(arquivo.length());

            byte[] buffer = new byte[4096];
            int lidos;
            while ((lidos = fis.read(buffer)) != -1) {
                out.write(buffer, 0, lidos);
            }
            out.flush();

            System.out.println("Resposta do servidor: " + in.readUTF());
        }
    }
}