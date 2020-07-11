import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * This class is the main server that listens to port 80 and creates a handler
 * for each connection and calls it
 * 
 * It extends the class Thread which implements the Runnable interface, so it
 * must implement the "run" method
 * 
 */
public class NewBankServer extends Thread {

    /*
     * Server waits for requests to come from the network
     */
    private ServerSocket server;

    /*
     * Constructor, listen to the selected port
     */
    public NewBankServer(int port) throws IOException {
        server = new ServerSocket(port);
    }

    /*
     * The key method that includes the code to be run inside the thread
     */
    @Override
    public void run() {

        System.err.println(this.getClass().getName()
                + ": NEW SERVER LISTENING ON " + server.getLocalPort());
        try {
            while (true) {
                /*
                 * Blocks, listens to the server socket and when there's a new
                 * connection, it gives it to a handler
                 * 
                 * This means that as many sockets are made as clients
                 * connecting to the server
                 * 
                 * Loops back once gives the connection to the handler and
                 * awaits the next one
                 */
                Socket s = server.accept();
                System.err.println(this.getClass().getName()
                        + ": NEW CONNECTION CREATED, SENDING TO HANDLER");
                /*
                 * After the new connection a new client handler is created
                 */
                NewBankClientHandler clientHandler = new NewBankClientHandler(
                        s);
                clientHandler.start();
            }
        } catch (IOException e) {

            System.err.println(this.getClass().getName()
                    + ": COULD NOT MAKE MORE CONNECTIONS");
            e.printStackTrace();

        } finally {
            try {
                // Close and good bye
                System.err.println(
                        this.getClass().getName() + ": CLOSING THE SERVER");
                server.close();

            } catch (IOException e) {
                // Couldn't even close!
                System.err.println(this.getClass().getName()
                        + ": COULD NOT CLOSE THE SERVER");
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        /*
         * starts a new NewBankServer thread on a specified port number
         * 
         * The port specified is 80 to make it easy to check with browsers but
         * it can be changed. Ideally we would have this selected by the
         * configuration, not in the code.
         * 
         * Because NewBankServer is a subclass of Thread which implements the
         * "run" method, "start" will create a new thread and, within the
         * thread, will run the code in "run"
         */
        new NewBankServer(80).start();
    }
}
