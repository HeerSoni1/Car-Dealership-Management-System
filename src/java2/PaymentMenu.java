package java2;

import dbms.DBConnection;

import java.sql.*;
import java.util.*;
import java.io.*;

class Payment implements Displayable {

    private final int paymentId;
    private final int saleId;
    private final String paymentMode;
    private final String paymentStatus;

    public Payment(int paymentId, int saleId, String paymentMode, String paymentStatus) {

        this.paymentId = paymentId;
        this.saleId = saleId;
        this.paymentMode = paymentMode;
        this.paymentStatus = paymentStatus;

    }

    public int getPaymentId() {
        return paymentId;
    }

    public int getSaleId() {
        return saleId;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    private static final String FORMAT = "| %-9s | %-6s | %-15s | %-14s |%n";
    private static final String BORDER =
            "+-----------+--------+-----------------+----------------+";

    public static void printHeader() {

        System.out.println(BORDER);
        System.out.printf(FORMAT, "PaymentID", "SaleID", "PaymentMode", "PaymentStatus");
        System.out.println(BORDER);

    }

    public void display() {

        System.out.printf(FORMAT, paymentId, saleId, paymentMode, paymentStatus);

    }

    public static void printFooter() {
        System.out.println(BORDER);
    }

    @Override
    public String toString() {

        return paymentId + "|" + saleId + "|" + paymentMode + "|" + paymentStatus;

    }

}

public class PaymentMenu {

    private final ArrayList<Payment> paymentList;
    private final HashMap<Integer, Payment> paymentBySaleId;

    private int nextPaymentId = 1;


    public PaymentMenu() {

        paymentList = new ArrayList<>();
        paymentBySaleId = new HashMap<>();

        loadPaymentsFromDB();

    }

    private void loadPaymentsFromDB() {

        String sql = "SELECT paymentid, saleid, paymentmode, paymentstatus FROM payment";

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {

            con = DBConnection.getConnection();
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {

                int paymentId = rs.getInt("paymentid");
                int saleId = rs.getInt("saleid");
                String mode = rs.getString("paymentmode");
                String status = rs.getString("paymentstatus");

                Payment payment = new Payment(paymentId, saleId, mode, status);

                paymentList.add(payment);
                paymentBySaleId.put(saleId, payment);

                if (paymentId >= nextPaymentId) {
                    nextPaymentId = paymentId + 1;
                }

            }

        } catch (SQLException e) {

            System.out.println("Database Error : " + e.getMessage());

        } finally {

            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                System.out.println("Error Closing Connection : " + e.getMessage());
            }

        }

    }

    public Payment recordPayment(int saleId, double amount, String mode) {

        if (paymentBySaleId.containsKey(saleId)) {

            System.out.println("Payment Already Recorded For This Sale.");
            return paymentBySaleId.get(saleId);

        }

        int paymentId = nextPaymentId++;
        String status = "Paid";

        String sql = "INSERT INTO payment (paymentid, saleid, paymentmode, paymentstatus) VALUES (?, ?, ?, ?)";


        Connection con = null;
        PreparedStatement pst = null;

        try {

            con = DBConnection.getConnection();
            pst = con.prepareStatement(sql);

            pst.setInt(1, paymentId);
            pst.setInt(2, saleId);
            pst.setString(3, mode);
            pst.setString(4, status);

            pst.executeUpdate();

            Payment payment = new Payment(paymentId, saleId, mode, status);

            paymentList.add(payment);
            paymentBySaleId.put(saleId, payment);

            System.out.println("\nPayment Recorded Successfully.");
            System.out.println("Payment ID : " + paymentId);
            System.out.println("Amount Paid : ₹" + amount);

            return payment;

        } catch (SQLException e) {

            System.out.println("Database Error : " + e.getMessage());
            return null;

        } finally {

            try {
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                System.out.println("Error Closing Connection : " + e.getMessage());
            }

        }

    }

    public Payment getPaymentBySaleId(int saleId) {

        return paymentBySaleId.get(saleId);

    }

    public void generateReceipt(int saleId, double amount) {

        Payment payment = paymentBySaleId.get(saleId);

        if (payment == null) {

            System.out.println("No Payment Found For This Sale. Receipt Not Generated.");
            return;

        }

        try {

            FileWriter fw = new FileWriter("Receipt_" + payment.getPaymentId() + ".txt");

            fw.write("=====================================\n");
            fw.write("            PAYMENT RECEIPT\n");
            fw.write("=====================================\n");
            fw.write("Payment ID     : " + payment.getPaymentId() + "\n");
            fw.write("Sale ID        : " + payment.getSaleId() + "\n");
            fw.write("Payment Mode   : " + payment.getPaymentMode() + "\n");
            fw.write("Payment Status : " + payment.getPaymentStatus() + "\n");
            fw.write("Amount Paid    : ₹" + amount + "\n");
            fw.write("\nThank You For Your Payment.\n");

            fw.close();

            System.out.println("Receipt Generated Successfully.");
            System.out.println("File Name : Receipt_" + payment.getPaymentId() + ".txt");

        } catch (IOException e) {

            System.out.println("File Error : " + e.getMessage());

        }

    }

}
