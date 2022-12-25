package setupAndUtilitys.util.jdbc;

import org.testng.Assert;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReadFromDB {

    /**
     * @DB_URL -> This is used to point to the location of the DB with which you want to interact.
     * @USER - > Username for the DB.
     * @PASS -> Password for the DB.
     */
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASS = "Start123!";
    private static String referenceID;
    private static String subscriptionID;

    /**
     * @param type      refers to the type of the Transaction you wish to check. Ex. Successful, Incomplete...etc...
     * @param paymentID is the used to filter out the row from which you wish to extract in this case referenceID
     */
    public static void checkTransactions(String type, String paymentID) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement statement = conn.createStatement();
             ResultSet resultPayment = statement.executeQuery("SELECT * FROM payments WHERE reference = '" + paymentID + "'")) {

            while (resultPayment.next()) {
                referenceID = resultPayment.getString("id");
            }
            checkTransactionStatus(type);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param type refers to the type of the Transaction you wish to check. Ex. Successful, Incomplete...etc...
     */
    private static void checkTransactionStatus(String type) {
        String QUERYTransactions = "SELECT * FROM transactions WHERE payment_id = '" + referenceID + "'";
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement statement = connection.createStatement();
             ResultSet transactionsResult = statement.executeQuery(QUERYTransactions)) {

            List<String> transactionTypeList = new ArrayList<>();
            while (transactionsResult.next()) {
                transactionTypeList.add(transactionsResult.getString("type"));
            }

            switch (type) {
                case "successful":
                    checkForSuccessfulStatus(transactionTypeList);
                    break;
                case "incomplete":
                    checkForIncompleteStatus(transactionTypeList);
                    break;
                default:
                    break;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param transactionTypeList based on this we do our checks.
     *                            We will first check size od of the list and then all elements of the list.
     *                            For each element we know what to expect depending on the state of the transaction.
     */
    private static void checkForSuccessfulStatus(List<String> transactionTypeList) {
        if (transactionTypeList.size() == 2) {
            boolean auth = transactionTypeList.stream().anyMatch(str -> str.equals("authorize"));
            boolean capt = transactionTypeList.stream().anyMatch(str -> str.equals("capture"));
            if (auth) {
                if (!capt) {
                    Assert.fail("Capture wasn't triggered.");
                }
            } else {
                Assert.fail("Authorize wasn't triggered.");
            }
        } else {
            Assert.fail("The transaction should have two types, instead it has: " + transactionTypeList.size() + " | " + transactionTypeList.stream().map(Object::toString).collect(Collectors.joining(" and ")));
        }
    }

    /**
     * @param transactionTypeList based on this we do our checks.
     *                            We will first check size od of the list and then all elements of the list.
     *                            For each element we know what to expect depending on the state of the transaction.
     */
    private static void checkForIncompleteStatus(List<String> transactionTypeList) {
        if (transactionTypeList.size() == 1) {
            boolean auth = transactionTypeList.stream().anyMatch(str -> str.equals("authorize"));
            if (!auth) {
                Assert.fail("Authorize wasn't triggered.");
            }
        } else {
            Assert.fail("The transaction should have only one type but it has: " + transactionTypeList.size() + " | " + transactionTypeList.stream().map(Object::toString).collect(Collectors.joining(" and ")));
        }
    }

    /**
     * @param state     this is used to determine which state to check, if the sub should be created or not.
     * @param paymentID is the used to filter out the row from which you wish to extract in this case referenceID
     */
    public static void checkIfSubscriptionIsCreated(Boolean state, String paymentID) {
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement statement = connection.createStatement();
             ResultSet resultPayment = statement.executeQuery("SELECT * FROM payments WHERE reference = '" + paymentID + "'")) {

            iterateThroughPaymentResults(state, resultPayment);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param state         this is used to determine which state to check, if the sub should be created or not.
     * @param resultPayment this is the row which was selected by reference. It is used to iterate through in order to extract subscriptionID.
     * @throws SQLException this is because we're using .next() that reads from next row in the table.
     */
    private static void iterateThroughPaymentResults(Boolean state, ResultSet resultPayment) throws SQLException {

        while (resultPayment.next()) {

            subscriptionID = resultPayment.getString("subscription_id");

            if (state) {
                checkCreatedSubscription();

            } else {
                confirmThatSubscriptionIsNotCreated();
            }
        }
    }

    /**
     * In this case sub should be created, and we are checking if the field == null, if yes, we are failing this step.
     */
    private static void checkCreatedSubscription() {
        if (subscriptionID == null) {
            Assert.fail("The subscription is not created but it should be, and subscriptionID is null");
        }
    }

    /**
     * In this case sub shouldn't be created, and we are checking if the field != null, if yes, we are failing this step.
     */
    private static void confirmThatSubscriptionIsNotCreated() {
        if (subscriptionID != null) {
            Assert.fail("The subscription is created but it shouldn't be, and subscriptionID is: " + subscriptionID);

        }
    }

    public static String extractBillingPlanReference(String clientID) {

        String billingReference = null;
        String QUERYBilling = "SELECT * FROM billing_plans WHERE client_id = '" + clientID + "'";
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement statement = connection.createStatement();
             ResultSet billingResult = statement.executeQuery(QUERYBilling)) {
            while (billingResult.next()) {
                billingReference = billingResult.getString("reference");
            }

            return billingReference;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return billingReference;
    }
}
