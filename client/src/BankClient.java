
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
// A Java program for a Client
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BankClient {

    // initialize socket and input output streams
    private static boolean loggedIn = false;
    private static Socket socket = null;
    private static PrintWriter bankOut = null;
    private static BufferedReader bankIn = null;

    // To properly use the bankIn stream
    private static Predicate<String> dataInBuffer = line -> {
        try {
            return bankIn.ready();
        } catch (IOException i) {
            return false;
        }
    };

    // To match the success string
    private static Predicate<String> isSuccess = line -> line
            .matches("^SUCCESS");

    // To match the fail string
    private static Predicate<String> isFail = line -> line.matches("^FAIL");

    // Response not complete
    private static Predicate<String> responseNotComplete = line -> line
            .matches("^(?!SUCCESS|FAIL).+");

    // constructor not allowed
    private BankClient() {
    };

    // To connect
    public static void connect(String address, int port) {
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

    // To login
    public static boolean bankLogin(String username, String password) {

        bankOut.println(username);
        bankOut.println(password);

        String line = "";
        try {
            while ((line = bankIn.readLine()).matches("^(?!SUCCESS|FAIL).+"))
                ;
        } catch (IOException e) {
            printTrace("Exception in method bankLogin");
        }

        BankClient.loggedIn = isSuccess.test(line);

        printTrace("Authenticated = " + BankClient.loggedIn);
        return BankClient.loggedIn;
    }

    // To get accounts
    public static List<AccountModel> getAccounts() {

        List<AccountModel> retrieved = new ArrayList<>();

        bankOut.println("SHOWMYACCOUNTS");
        Pattern p = Pattern.compile(
                "^\\<([a-zA-Z0-9]+)#([a-zA-Z0-9]+)#(-?\\d*.\\d*)#\\[(.*)\\]\\>$");

        String line = "";
        try {
            while ((line = bankIn.readLine()).matches("^(?!SUCCESS|FAIL).+")) {

                System.err.println(line);
                Matcher m = p.matcher(line);
                if (m.find()) {

                    // print the group out for verification
                    System.out.println(
                            "Retrieved: " + m.group(1) + "\t" + m.group(2)
                                    + "\t" + m.group(3) + "\t" + m.group(4));
                    retrieved.add(new AccountModel(m.group(1), m.group(2),
                            m.group(3), m.group(4)));

                } else {
                    System.err.println("Discarded");
                }
            }

        } catch (IOException e) {
            printTrace("Exception in method getAccounts");
        }
        return retrieved;
    }

    // To create a new account
    public static boolean newAccount(String accountname) {

        bankOut.println("ADDACCOUNT " + accountname);

        String line = "";
        try {
            while ((line = bankIn.readLine()).matches("^(?!SUCCESS|FAIL).+")) {
            }
        } catch (IOException e) {
            printTrace("Exception in method createAccount");
        }
        return line.matches("^SUCCESS");
    }

    // To deposit money
    public static boolean deposit(String accountname, String amount) {

        bankOut.println("DEPOSIT " + accountname + " " + amount);

        String line = "";
        try {
            while ((line = bankIn.readLine()).matches("^(?!SUCCESS|FAIL).+")) {
            }
        } catch (IOException e) {
            printTrace("Exception in method deposit");
        }
        return line.matches("^SUCCESS");
    }

    // To withdraw money
    public static boolean withdraw(String accountname, String amount) {

        bankOut.println("WITHDRAW " + accountname + " " + amount);

        String line = "";
        try {
            while ((line = bankIn.readLine()).matches("^(?!SUCCESS|FAIL).+")) {
            }
        } catch (IOException e) {
            printTrace("Exception in method withdraw");
        }
        return line.matches("^SUCCESS");
    }

    // To move money from account to account
    public static boolean move(String amount, String accountname,
            String toAccount) {

        bankOut.println("MOVE " + amount + " " + accountname + " " + toAccount);

        String line = "";
        try {
            while ((line = bankIn.readLine()).matches("^(?!SUCCESS|FAIL).+")) {
            }
        } catch (IOException e) {
            printTrace("Exception in method withdraw");
        }
        return line.matches("^SUCCESS");
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
    private static void printTrace(String message) {

        System.err.println(BankClient.class.getName() + ": " + message);
    }
}