import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.*;
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

    public static void main(String[] args) {
        Ticket[] tickets = createTicketHashes("butts", 100);
        for(int i = 0; i < 10; i++) {
            System.out.println(Arrays.toString(tickets[i].hash));
            System.out.println(tickets[i].used);
        }
    }
}
