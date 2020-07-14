
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Objects;

/*
 * This class is the handler for connections that are established with the
 * socket
 * 
 * It extends the class Thread which implements the Runnable interface, so it
 * must implement the "run" method
 * 
 */
public class NewBankClientHandler extends Thread {

    private NewBank bank;
    private BufferedReader in;
    private PrintWriter out;
    private SocketAddress adress;

    /*
     * Constructor, is given a specific socket, creates a new bank object, opens
     * a reader and a writer for the socket
     */
    public NewBankClientHandler(Socket s) throws IOException {
        bank = NewBank.getBank();
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new PrintWriter(s.getOutputStream(), true);
        adress = s.getRemoteSocketAddress();
    }

    @Override
    public void run() {

        try {
            /*
             * login section
             */
            System.err.println(this.getClass().getName() + ": "
                    + adress.toString() + " NEW HANDLER STARTED ");

            out.println("Enter Username");
            String userName = in.readLine();
            out.println("Enter Password");
            String password = in.readLine();
            out.println("Checking Details...");
            // authenticate user and get customer ID token from bank for use in
            // subsequent requests
            CustomerID customer = bank.checkLogInDetails(userName, password);
            // if the user is authenticated then get requests from the user and
            // process them
            if (customer != null) {
                /*
                 * login successful
                 */
                out.println("Log In Successful. What do you want to do?");
                System.err.println(this.getClass().getName() + ": "
                        + adress.toString() + " SUCCESSFUL LOGIN FROM "
                        + customer.getKey());
                /*
                 * service loop
                 */
                while (true) {
                    String request = in.readLine();

                    // Added to account for EOF
                    if (Objects.isNull(request)) {
                        throw (new EOFException());
                    }

                    System.err.println(
                            this.getClass().getName() + ": " + adress.toString()
                                    + " REQUEST FROM " + customer.getKey());
                    /*
                     * This is where the specific requests are processed
                     */
                    String response = bank.processRequest(customer, request);
                    out.println(response);
                }
            } else {

                /*
                 * customer rejection
                 */
                out.println("Log In Failed");
                System.err.println(
                        this.getClass().getName() + ": " + adress.toString()
                                + " HANDLER TERMINATED - INCORRECT LOGIN");
            }
        } catch (EOFException e) {
            // Connection interrupted
            System.err.println(this.getClass().getName() + ": "
                    + adress.toString() + " HANDLER EOF DETECTED");
            // e.printStackTrace();

        } catch (IOException e) {
            // Other I/O exceptions
            System.err.println(this.getClass().getName() + ": "
                    + adress.toString() + " HANDLER COULD NOT START");
            e.printStackTrace();

        } finally {
            try {
                // Close all well
                in.close();
                out.close();
                System.err.println(this.getClass().getName() + ": "
                        + adress.toString() + " HANDLER CLOSED");

            } catch (IOException e) {
                // Could not close
                System.err.println(this.getClass().getName() + ": "
                        + adress.toString() + " HANDLER COULD NOT BE CLOSED");
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

}
