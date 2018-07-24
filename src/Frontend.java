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

    /**
     * Start the client process. At the moment this is just a testing placeholder. In the future, this
     * will handle the client's functionality. It should read a QR code, decode the hash from that
     * QR code, and send that hash to the server for validation. It should then display a message
     * depending on whether the ticket was valid or not.
     * @param ip
     * @param port
     * @throws IOException
     */
    public void start(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        DataOutputStream dataOut = new DataOutputStream(clientSocket.getOutputStream());
        byte[] hash = {10, 3, 4, 5};

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
