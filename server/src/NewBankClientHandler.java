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
    private SocketAddress address;

    static final String SUCCESS = "SUCCESS";
    static final String FAIL = "FAIL";

    /*
     * Constructor, is given a specific socket, creates a new bank object, opens
     * a reader and a writer for the socket
     */
    public NewBankClientHandler(Socket s) throws IOException {
        bank = NewBank.getBank();
        in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new PrintWriter(s.getOutputStream(), true);
        address = s.getRemoteSocketAddress();
    }

    @Override
    public void run() {

        try {
            /*
             * login section
             */
            printTrace(address, "NEW HANDLER STARTED ");

            out.println("Enter Username");
            String userName = in.readLine();
            out.println("Enter Password");
            String password = in.readLine();
            out.println("Checking Details...");
            // authenticate user and get customer ID token from bank for use in
            // subsequent requests
            Customer customer = bank.checkLogInDetails(userName, password);
            // if the user is authenticated then get requests from the user and
            // process them
            if (customer != null) {
                /*
                 * login successful
                 */


                out.println(NewBankClientHandler.SUCCESS);
                out.println( "Log In Successful (type OPTIONS for help)");

                printTrace(address,
                        "SUCCESSFUL LOGIN FROM " + customer.getUserName());
                      

                /*
                 * service loop
                 */
                while (true) {
                    String request = in.readLine();

                    // Added to account for EOF
                    if (Objects.isNull(request)) {
                        throw (new EOFException());
                    }

                    printTrace(address,
                            "REQUEST FROM " + customer.getUserName());
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
                out.println(NewBankClientHandler.FAIL);
                out.println("\n Log In Failed");
                printTrace(address, "HANDLER TERMINATED - INCORRECT LOGIN");
            }
        } catch (EOFException e) {
            // Connection interrupted
            printTrace(address, "HANDLER EOF DETECTED");
            // e.printStackTrace();

        } catch (IOException e) {
            // Other I/O exceptions
            printTrace(address, "CONNECTION RESET");
            // e.printStackTrace();

        } finally {
            try {
                // Close all well
                in.close();
                out.close();
                printTrace(address, "HANDLER CLOSED");

            } catch (IOException e) {
                // Could not close
                printTrace(address, "HANDLER COULD NOT BE CLOSED");
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    /*
     * Auxiliary trace method to print in the console to correct errors
     */
    private void printTrace(SocketAddress addr, String message) {

        System.err.println(this.getClass().getName() + ": " + addr.toString()
                + " " + message);
    }
}

