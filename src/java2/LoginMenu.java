package java2;

import dbms.DBConnection;
import ds.*;

import java.sql.*;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;

public class LoginMenu {


    private static final String OTP_FILE = "otp.txt";


    private final MyStack sessionStack = new MyStack();

    public void lock() {

        Scanner sc = new Scanner(System.in);
        System.out.println();
        System.out.println("========================================");
        System.out.println("     LOGIN MENU");
        System.out.println("========================================");

        while (true) {

            try {

                System.out.println("Enter username: ");
                String username = sc.next();

                if (!isValidAdmin(username)) {

                    System.out.println("Entered wrong username. Try again!");
                    continue;

                }

                String otp = generateOtp();
                writeOtpToFile(otp);

                System.out.println("An OTP has been generated. Check " + OTP_FILE + " to view it.");
                System.out.println("Enter OTP: ");
                String enteredOtp = sc.next();

                if (enteredOtp.equals(otp)) {

                    sessionStack.push(username);

                    System.out.println("Welcome Admin: " + username);
                    System.out.println();
                    System.out.println();

                    return;

                } else {

                    System.out.println("Entered wrong OTP. Try again!");

                }

            } catch (Exception e) {

                System.out.println("Invalid input! Try again!");

            }

        }

    }


    private boolean isValidAdmin(String username) {

        String sql = "SELECT * FROM admin WHERE username = ?";

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {

            con = DBConnection.getConnection();
            pst = con.prepareStatement(sql);

            pst.setString(1, username);

            rs = pst.executeQuery();

            return rs.next();

        } catch (SQLException e) {

            System.out.println("Database Error : " + e.getMessage());
            return false;

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

    private String generateOtp() {

        // Math.random() returns [0.0, 1.0). Scale it so the OTP always
        // lands in the 100000-999999 range, i.e. a 6-digit code.
        int otp = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(otp);

    }

    private void writeOtpToFile(String otp) {

        try (FileWriter writer = new FileWriter(OTP_FILE)) {

            writer.write("OTP: " + otp);

        } catch (IOException e) {

            System.out.println("Error Writing OTP File : " + e.getMessage());

        }

    }


    public void logout() {

        if (sessionStack.isEmpty()) {
            System.out.println("No admin currently logged in.");
            return;
        }

        String username = sessionStack.pop();
        System.out.println(username + " logging out...");
        lock();
    }

    public void logout(int i) {

        if (sessionStack.isEmpty()) {
            System.out.println("No admin currently logged in.");
            return;
        }

        String username = sessionStack.pop();
        System.out.println(username + " logging out...");

    }

}
