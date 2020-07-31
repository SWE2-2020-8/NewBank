
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

import javafx.util.Pair;

public class BankClient {

    // initialize socket and input output streams
    private static boolean loggedIn = false;
    private static Socket socket = null;
    private static PrintWriter bankOut = null;
    private static BufferedReader bankIn = null;

    // To match the success string
    private static Predicate<String> isSuccess = line -> line
            .matches("^SUCCESS");

    // Response not complete check
    private static Predicate<String> responseNotComplete = line -> line
            .matches("^(?!SUCCESS|FAIL).+");

    // constructor not allowed outside
    private BankClient() {
    }

    // To establish a connection
    public static void connect(String address, int port) {
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
        printTrace("Connected to server");
    }

    // Parses the server output until it gets outcome
    private static boolean parseServerOutcome() {

        String line = "";
        try {
            while (responseNotComplete.test(line = bankIn.readLine()))
                ;
        } catch (IOException e) {
            printTrace("Exception trying to parse server output");
        }
        return isSuccess.test(line);
    }

    // Parses the server output for a list of accounts
    private static List<AccountModel> parseAccounts() {

        List<AccountModel> retrieved = new ArrayList<>();
        Pattern p = Pattern.compile(
                "^\\<([a-zA-Z0-9]+)#([a-zA-Z0-9]+)#(-?\\d*.\\d*)#\\[(.*)\\]\\>$");
        String line = "";
        try {
            while ((line = bankIn.readLine()).matches("^(?!SUCCESS|FAIL).+")) {

                System.err.println(line);
                Matcher m = p.matcher(line);
                if (m.find())
                    retrieved.add(new AccountModel(m.group(1), m.group(2),
                            m.group(3), m.group(4)));
                else
                    System.err.println("Account element discarded");
            }
        } catch (IOException e) {
            printTrace("Exception trying to parse accounts");
        }
        return retrieved;
    }

    // To parse the server output for a list of users
    private static List<Pair<String, String>> parseUsers() {

        // TBD

        return null;
    }

    // To login
    public static boolean bankLogin(String username, String password) {

        bankOut.println(username);
        bankOut.println(password);

        BankClient.loggedIn = parseServerOutcome();
        printTrace("Authenticated = " + BankClient.loggedIn);
        return BankClient.loggedIn;
    }

    // To get accounts
    public static List<AccountModel> getAccounts() {

        bankOut.println("SHOWMYACCOUNTS");
        return parseAccounts();
    }

    // To create a new account
    public static boolean newAccount(String accountname) {

        bankOut.println("ADDACCOUNT " + accountname);
        return parseServerOutcome();
    }

    // To deposit money
    public static boolean deposit(String accountname, String amount) {

        bankOut.println("DEPOSIT " + accountname + " " + amount);
        return parseServerOutcome();
    }

    // To withdraw money
    public static boolean withdraw(String accountname, String amount) {

        bankOut.println("WITHDRAW " + accountname + " " + amount);
        return parseServerOutcome();
    }

    // To move money from account to account
    public static boolean move(String amount, String accountname,
            String toAccount) {

        bankOut.println("MOVE " + amount + " " + accountname + " " + toAccount);
        return parseServerOutcome();
    }

    // To change password
    public static boolean changePassword(String oldPass, String newPass) {

        bankOut.println("CHANGEPASSWORD " + oldPass + " " + newPass);
        return parseServerOutcome();
    }

    // To add user
    public static boolean addUser(String username, String password) {

        bankOut.println("ADDUSER " + username + " " + password);
        return parseServerOutcome();
    }

    // To list users
    public static List<Pair<String, String>> listUsers() {

        bankOut.println("LISTUSERS");
        return parseUsers();
    }

    // To list accounts
    public static List<AccountModel> listUsers() {

        bankOut.println("LISTACCOUNTS");
        return parseAccounts();
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