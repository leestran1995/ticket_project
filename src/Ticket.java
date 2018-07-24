import java.util.Arrays;

public class Ticket {
    byte[] hash;    // The hash value of the ticket
    boolean used;   // Whether the ticket has been used yet

    public Ticket(byte[] hash_input) {
        hash = hash_input;
        used = false;
    }

    public boolean validateTicket(byte[] hash_input) {
        if (Arrays.equals(hash_input, hash)) {
            used = true;
            return true;
        }
        return false;
    }
}
