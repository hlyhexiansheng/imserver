import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by neojobs on 16/1/1.
 */
public class BIOSocketClient {

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("localhost",9503);

        InputStream inputStream = socket.getInputStream();

        OutputStream outputStream = socket.getOutputStream();

        new WriteThread(outputStream).start();
        new ReadThread(inputStream).start();

    }
}

class WriteThread extends Thread{

    private OutputStream outputStream;

    public WriteThread(OutputStream outputStream){
        this.outputStream = outputStream;
        this.setName("writeThread");
    }

    @Override
    public void run() {
        try {
            Scanner scanner = new Scanner(System.in);
            while (true){
                String next = scanner.next();
                this.outputStream.write(next.getBytes());
            }
        }catch (Exception e){
            System.out.println("[WriteThread] lost connection....");
            System.exit(0);
        }
    }
}

class ReadThread extends Thread{

    private InputStream inputStream;

    public ReadThread(InputStream inputStream){
        this.inputStream = inputStream;
        this.setName("readThread");
    }

    @Override
    public void run() {
        try {
            while (true){
                byte[] bytes = new byte[1024];
                int length = inputStream.read(bytes);
                if(length < 0){
                    break;
                }
                System.out.println("rev.." + new String(Arrays.copyOf(bytes, length)));
            }
        }catch (Exception ex){
            System.out.println("[ReadThread]lost connection....");
            System.exit(0);
        }

    }
}