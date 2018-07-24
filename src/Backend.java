import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.*;
import java.net.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * This class is a placeholder for the backend manager of the ticketing system. It will handle the following
 * functionalities:
 *
 * Creating tickets
 * Storing tickets
 * Retrieving stored tickets
 * Verifying tickets
 * Marking tickets as used
 */

public class Backend {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    /*
     * verifyTicket
     *
     * If the ticket is in the database, return true. Otherwise return false
     */
    private static boolean verifyTicket(byte[] ticket, Ticket[] ticketDatabase) {
        for(Ticket tic : ticketDatabase) {
            if(tic.validateTicket(ticket)) {
                return true;
            }
        }
        return false;
    }


    /*
     * createHashFromPassword
     *
     * Take a password and create an array that contains the hash using
     * the SHA-256 algorithm
     *
     */
    private static byte[] createHashFromPassword(String password){
        try {
            byte[] data1 = password.getBytes("UTF-8");

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] digest = messageDigest.digest(data1);
            return digest;
        }
        catch (IOException e){
            System.out.println("Unknown encoding");
        }
        catch (NoSuchAlgorithmException n){
            System.out.println("Unknown algorithm");
        }
        return null;
    }

    /*
     * createTicketHashes
     *
     * For some password and a number of tickets to be created, create that
     * number of hashes and store in a byte[][] matrix that is to be returned.
     */
    public static Ticket[] createTicketHashes(String password, int numTickets){
        Ticket[] tickets = new Ticket[numTickets];

        for(int i = 0; i < numTickets; i++){
            String iteratedPassword = password + i;
            byte[] hash = createHashFromPassword(iteratedPassword);
            tickets[i] = new Ticket(hash);
        }
        return tickets;
    }


    // Generate a QR Code Image from 'text' and store ti 'filePath'
    private static void generateQRCodeImage(String text, int width, int height, String filePath)
            throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }

    public static boolean saveTicketArrayToFile(String outputPath, Ticket[] ticketArray) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(outputPath));
            outputStream.writeObject(ticketArray);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.toString());
            return false;
        }
        return true;
    }

    public static Ticket[] readTicketFile(String filePath) {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(filePath));
            return (Ticket[])inputStream.readObject();
        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.toString());
        } catch (ClassNotFoundException c) {
            System.out.println("Error: " + c.toString());
        }
        return null;
    }

    /**
     * The server's process. This handles the receiving of requests from clients.
     * @param port
     * @throws IOException
     */
    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Waiting for client to connect");
        clientSocket = serverSocket.accept();
        System.out.println("Client connected");
        DataInputStream dataInput = new DataInputStream(clientSocket.getInputStream());

        boolean done = false;
        while(!done) {
            int length = dataInput.readInt();   // First find out how many bytes we should read
            
            byte[] hash = new byte[length];

            // There is probably a better way to do this than a for loop. readAllBytes perhaps?
            for(int i = 0; i < length; i++) {
                hash[i] = dataInput.readByte();
            }
            System.out.println(Arrays.toString(hash));
            done = true;
        }
    }

    public static void main(String[] args) {
        Backend server = new Backend();
        try {
            server.start(6666);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
