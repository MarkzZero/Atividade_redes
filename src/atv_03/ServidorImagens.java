package atv_03;

import java.io.*;
import java.net.*;
 
public class ServidorImagens {
    public static void main(String[] args) throws IOException {
        File pasta = new File("imagens_recebidas");
        pasta.mkdirs();
 
        try (ServerSocket servidor = new ServerSocket(9003)) {
            System.out.println("Servidor de imagens na porta 9003. Pasta: " + pasta.getAbsolutePath());
            while (true) {
                Socket socket = servidor.accept();
                new Thread(() -> receber(socket, pasta)).start();
            }
        }
    }
 
    private static void receber(Socket socket, File pasta) {
        try (Socket s = socket; DataInputStream in = new DataInputStream(new BufferedInputStream(s.getInputStream()))) {
            String nome = new File(in.readUTF()).getName(); // getName evita "../" no caminho
            long tamanho = in.readLong();
            File destino = new File(pasta, System.currentTimeMillis() + "_" + nome);
 
            try (FileOutputStream fos = new FileOutputStream(destino)) {
                byte[] buffer = new byte[4096];
                long restante = tamanho;
                while (restante > 0) {
                    int lidos = in.read(buffer, 0, (int) Math.min(buffer.length, restante));
                    if (lidos == -1) throw new EOFException("Conexão interrompida");
                    fos.write(buffer, 0, lidos);
                    restante -= lidos;
                }
            }
            System.out.println("Imagem salva: " + destino.getName() + " (" + tamanho + " bytes)");
 
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            out.writeUTF("OK: " + destino.getName());
            out.flush();
        } catch (IOException e) {
            System.out.println("Erro ao receber imagem: " + e.getMessage());
        }
    }
}
