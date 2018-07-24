import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Frontend
 *
 * This class includes the functionality that will be on the frontend of the client, primarily
 * validating tickets through QR code readings.
 *
 * Eventually, this should be written so that the Frontend client reads the QR code and sends the ticket
 * hash to the backend for validation.
 */

public class Frontend {
    private Socket clientSocket;

    public void start(String ip, int port) throws IOException {
        System.out.println("Connecting to server");
        clientSocket = new Socket(ip, port);
        System.out.println("Connected to server");
        DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());
        byte[] hash = {10, 3, 4, 5};
        System.out.println("Hash length is: " + hash.length);

        // Send the length of the data first
        dataOut.writeInt(hash.length);
        dataOut.flush();

        dataOut.write(hash, 0, hash.length);
        dataOut.flush();
    }

    public static void main(String[] args) {
        Frontend client = new Frontend();
        try {
            client.start("10.0.0.233", 6666);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
