
/**
 * 
 * Client for the NewBank Server
 * 
 * Does all the interactions with the NewBank server
 * 
 * 
 */
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

    // Keep track of users and connections
    private static String userName;
    private static Socket socket = null;
    private static PrintWriter bankOut = null;
    private static BufferedReader bankIn = null;

    // Some REGEX
    private static final String NOT_FINISHED = "^(?!SUCCESS|FAIL).+";
    private static final String IS_SUCCESS = "^SUCCESS";
    private static final String IS_COMMENT = "^//.*";
    private static final String REGEX_USER = "^\\<([a-zA-Z0-9]+)#([a-zA-Z0-9]+)\\>$";
    private static final String REGEX_ACCOUNT = "^\\<([a-zA-Z0-9]+)#([a-zA-Z0-9]+)#(-?\\d*.\\d*)#\\[(.*)\\]\\>$";

    // To check server output
    private static Predicate<String> isSuccess = line -> line
            .matches(IS_SUCCESS);
    private static Predicate<String> responseNotComplete = line -> line
            .matches(NOT_FINISHED);

    // No access to constructor
    private BankClient() {
    }

    // Setting up the connection
    public static void connect(final String address, final int port) {

        try {

            // Open the socket
            socket = new Socket(address, port);

            // sends output to the socket
            bankOut = new PrintWriter(socket.getOutputStream(), true);

            // Gets input from the socket
            bankIn = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

        } catch (final UnknownHostException u) {
            System.out.println(u);
        } catch (final IOException i) {
            System.out.println(i);
        }
        printTrace("Connected to server");
    }

    // Logging in
    public static boolean bankLogin(final String username,
            final String password) {

        bankOut.println(username);
        bankOut.println(password);

        if (parseServerOutcome()) {

            BankClient.userName = username;
            printTrace("Authenticated " + BankClient.userName);
            return true;
        } else {
            printTrace("Authentication failed");
            return false;
        }
    }

    // Get if the logged user is Admin
    public static boolean isAdmin() {

        return BankClient.userName.equals("Admin");
    }

    // Get the name of the logged user
    public static String getUsername() {

        return BankClient.userName;
    }

    // Parsing server output until outcome is provided
    private static boolean parseServerOutcome() {

        String line = "";
        try {
            while (responseNotComplete.test(line = bankIn.readLine()))
                ;
        } catch (final IOException e) {
            printTrace("Exception trying to parse server output: " + e);
        }
        return isSuccess.test(line);
    }

    // Parsing server output for a list of accounts
    private static List<AccountModel> parseAccounts() {

        final List<AccountModel> retrieved = new ArrayList<>();
        final Pattern p = Pattern.compile(REGEX_ACCOUNT);
        String line = "";
        System.err.print(BankClient.class.getName() + ": Accounts ");
        try {
            while (responseNotComplete.test(line = bankIn.readLine())) {
                if (!line.matches(IS_COMMENT)) {
                    final Matcher m = p.matcher(line);
                    if (m.find()) {
                        retrieved.add(new AccountModel(m.group(1), m.group(2),
                                m.group(3), m.group(4)));
                        System.err.print("v");
                    } else
                        System.err.print("x");
                }
            }
        } catch (final IOException e) {
            printTrace("Exception trying to parse accounts: " + e);
        }
        System.err.println();
        return retrieved;
    }

    // Parsing server output for a list of users
    private static List<Pair<String, String>> parseUsers() {

        final List<Pair<String, String>> retrieved = new ArrayList<>();
        final Pattern p = Pattern.compile(REGEX_USER);
        String line = "";
        System.err.print(BankClient.class.getName() + ": Users ");
        try {
            while (responseNotComplete.test(line = bankIn.readLine())) {
                if (!line.matches(IS_COMMENT)) {
                    final Matcher m = p.matcher(line);
                    if (m.find()) {
                        retrieved.add(new Pair<>(m.group(1), m.group(2)));
                        System.err.print("v");
                    } else
                        System.err.print("x");
                }
            }
        } catch (final IOException e) {
            printTrace("Exception trying to parse users: " + e);
        }
        System.err.println();
        return retrieved;
    }

    // Getting accounts
    public static List<AccountModel> getAccounts() {

        bankOut.println("SHOWMYACCOUNTS");
        return parseAccounts();
    }

    // Creating a new account
    public static boolean newAccount(final String accountname) {

        bankOut.println("ADDACCOUNT " + accountname);
        return parseServerOutcome();
    }

    // Depositing money
    public static boolean deposit(final String accountname,
            final String amount) {

        bankOut.println("DEPOSIT " + accountname + " " + amount);
        return parseServerOutcome();
    }

    // Withdrawing money
    public static boolean withdraw(final String accountname,
            final String amount) {

        bankOut.println("WITHDRAW " + accountname + " " + amount);
        return parseServerOutcome();
    }

    // Moving money from account to account
    public static boolean move(final String amount, final String accountname,
            final String toAccount) {

        bankOut.println("MOVE " + amount + " " + accountname + " " + toAccount);
        return parseServerOutcome();
    }

    // Paying another user
    public static boolean pay(final String amount, final String accountname,
            final String toUser) {

        bankOut.println("PAY " + amount + " " + accountname + " " + toUser);
        return parseServerOutcome();
    }

    // Changing the password
    public static boolean changePassword(final String oldPass,
            final String newPass) {

        bankOut.println("CHANGEPASSWORD " + oldPass + " " + newPass);
        return parseServerOutcome();
    }

    // Admin adding user
    public static boolean addUser(final String username,
            final String password) {

        bankOut.println("ADDUSER " + username + " " + password);
        return parseServerOutcome();
    }

    // Admin listing users
    public static List<Pair<String, String>> listUsers() {

        bankOut.println("LISTUSERS");
        return parseUsers();
    }

    // Admin listing accounts
    public static List<AccountModel> listAccounts() {

        bankOut.println("LISTACCOUNTS");
        return parseAccounts();
    }

    /*
     * Auxiliary trace method to print in the console for traceability
     */
    private static void printTrace(final String message) {

        System.err.println(BankClient.class.getName() + ": " + message);
    }
}