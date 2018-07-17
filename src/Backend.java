import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
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
    private static boolean verifyTicket(byte[] ticket, byte[][] ticketDatabase) {
        for(byte[] tic : ticketDatabase) {
            if (Arrays.equals(ticket, tic)) {
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
    public static byte[] createHashFromPassword(String password){
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
    public static byte[][] createTicketHashes(String password, int numTickets){
        byte[][] tickets = new byte[numTickets][32];

        for(int i = 0; i < numTickets; i++){
            String iteratedPassword = password + i;
            byte[] hash = createHashFromPassword(iteratedPassword);
            tickets[i] = hash;
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


}
