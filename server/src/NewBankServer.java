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
     * Constructor, creates a server socket that listens for inbound connections
     */
    public NewBankServer(int port) throws IOException {
        server = new ServerSocket(port);
    }

    /*
     * The key method that includes the code to be run inside the thread
     */
    @Override
    public void run() {

        printTrace("NEW SERVER LISTENING ON " + server.getLocalPort());
        try {
            while (true) {
                /*
                 * Blocks waiting for the server socket for a new connection
                 * request, it gives it to a handler to take care of the
                 * connection (socket)
                 * 
                 * This means that as many sockets are made as clients are
                 * actually connecting to the server
                 * 
                 * Loops back once gives the connection to the handler and
                 * awaits the next connection request into the server socket
                 */
                Socket socket = server.accept();
                printTrace("NEW CONNECTION CREATED, SENDING TO HANDLER");
                NewBankClientHandler handler = new NewBankClientHandler(socket);
                handler.start();

            }
        } catch (IOException e) {

            printTrace("COULD NOT MAKE MORE CONNECTIONS");
            e.printStackTrace();

        } finally {

            try {
                // Close and good bye
                printTrace("CLOSING THE SERVER");
                server.close();

            } catch (IOException e) {
                // Couldn't even close!
                printTrace("COULD NOT CLOSE THE SERVER");
                Thread.currentThread().interrupt();
            }
        }
    }

    /*
     * If you run locally and not in a container (clicking run after this
     * comment in vscode) you can interact with a terminal with the command:
     * 
     * socat - TCP4:localhost:80
     * 
     * or when executed from azure
     * 
     * socat - TCP4:swe2-2020-8.southeastasia.azurecontainer.io:80
     * 
     * with the container logs to be observed with
     * 
     * az container attach --name bankserver
     * 
     * Note that this will have to change if we instrument the port or change it
     * in the future
     * 
     * Important, the environment variable DB_MASTER_KEY must supply the key to
     * access the database
     *
     */
    public static void main(String[] args) throws IOException {

        /*
         * Connects to the Azure database and loads users
         */
        BankCosmosDb.loadBankCustomers();
        BankCosmosDb.loadBankAccounts();
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

    /*
     * Auxiliary trace method to print in the console to correct errors
     */
    private void printTrace(String message) {

        System.err.println(this.getClass().getName() + ": " + message);
    }
}
