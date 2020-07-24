package newbank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
// A Java program for a Client
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class BankClient {

    // initialize socket and input output streams
    private boolean loggedIn = false;
    private Socket socket = null;
    private PrintWriter bankOut = null;
    private BufferedReader bankIn = null;

    // To properly use the bankIn stream
    Predicate<String> dataInBuffer = line -> {
        try {
            return bankIn.ready();
        } catch (IOException i) {
            return false;
        }
    };

    // To match the success string
    Predicate<String> isSuccess = line -> line.matches("^SUCCESS");

    // To match the fail string
    Predicate<String> isFail = line -> line.matches("^FAIL");

    // Response not complete
    Predicate<String> responseNotComplete = line -> line
            .matches("^(?!SUCCESS|FAIL).+");

    // constructor to put ip address and port
    public BankClient(String address, int port) {
        // establish a connection
        try {
            socket = new Socket(address, port);

            // sends output to the socket
            bankOut = new PrintWriter(socket.getOutputStream(), true);

            // Gets input from the socket
            bankIn = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

        } catch (UnknownHostException u) {
            System.out.println(u);
        } catch (IOException i) {
            System.out.println(i);
        }
        printTrace("Connected");
    }

    public boolean bankLogin(String username, String password) {

        bankOut.println(username);
        bankOut.println(password);

        String line = "";
        try {
            while ((line = bankIn.readLine()).matches("^(?!SUCCESS|FAIL).+"))
                ;
        } catch (IOException e) {
            printTrace("Exception in method bankLogin");
        }

        this.loggedIn = isSuccess.test(line);

        printTrace("Authenticated = " + this.loggedIn);
        return this.loggedIn;
    }

    public boolean getAccounts() {

        bankOut.println("SHOWMYACCOUNTS");

        String line = "";
        try {
            while ((line = bankIn.readLine()).matches("^(?!SUCCESS|FAIL).+"))
                System.err.println(line);
        } catch (IOException e) {
            printTrace("Exception in method getAccounts");
        }
        return isSuccess.test(line);
    }

    @Override
    @Deprecated
    protected void finalize() throws Throwable {
        // close the connection
        try {
            bankOut.close();
            socket.close();
        } catch (IOException i) {
            System.out.println(i);
        }
    }

    /*
     * Auxiliary trace method to print in the console to correct errors
     */
    private void printTrace(String message) {

        System.err.println(this.getClass().getName() + ": " + message);
    }
}