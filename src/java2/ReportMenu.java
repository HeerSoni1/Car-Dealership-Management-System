package java2;

import dbms.DBConnection;

import java.sql.*;
import java.util.*;

public class ReportMenu {

    private final Scanner sc;


    public ReportMenu() {

        sc = new Scanner(System.in);

    }

    public void reportsMenu() {

        while (true) {

            System.out.println("\n==================================");
            System.out.println("             REPORTS");
            System.out.println("==================================");
            System.out.println("1. Total Cars Sold");
            System.out.println("2. Available Inventory");
            System.out.println("3. Monthly Sales");
            System.out.println("4. Revenue Report");
            System.out.println("5. Back");
            System.out.print("Enter Choice : ");

            try {

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {

                    case 1:
                        totalCarsSold();
                        break;

                    case 2:
                        availableInventory();
                        break;

                    case 3:
                        monthlySales();
                        break;

                    case 4:
                        revenueReport();
                        break;

                    case 5:
                        return;

                    default:
                        System.out.println("Invalid Choice.");

                }

            } catch (InputMismatchException e) {

                System.out.println("Please Enter A Valid Number.");
                sc.nextLine();

            }

        }

    }


    private void totalCarsSold() {

        String sql = "SELECT COUNT(*) AS totalSold FROM sales";

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {

            con = DBConnection.getConnection();
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            if (rs.next()) {

                System.out.println("\n========== TOTAL CARS SOLD ==========");
                System.out.println("Total Cars Sold : " + rs.getInt("totalSold"));

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


    private void availableInventory() {

        String sql = "SELECT carid, brand, model, quantity FROM car";

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {

            con = DBConnection.getConnection();
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            System.out.println("\n========== AVAILABLE INVENTORY ==========\n");

            String format = "| %-6s | %-8s | %-22s | %8s |%n";
            String border = "+--------+----------+------------------------+----------+";

            int totalUnits = 0;
            boolean any = false;

            while (rs.next()) {

                if (!any) {
                    System.out.println(border);
                    System.out.printf(format, "CarID", "Brand", "Model", "Quantity");
                    System.out.println(border);
                }

                any = true;

                int carId = rs.getInt("carid");
                String brand = rs.getString("brand");
                String model = rs.getString("model");
                int quantity = rs.getInt("quantity");

                totalUnits += quantity;

                System.out.printf(format, carId, brand, model, quantity);

            }

            if (!any) {

                System.out.println("No Cars Available.");

            } else {

                System.out.println(border);
                System.out.println("\nTotal Units In Stock : " + totalUnits);

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


    private void monthlySales() {

        String sql = "SELECT DATE_FORMAT(saledate, '%Y-%m') AS salemonth, " +
                "COUNT(*) AS totalSales, " +
                "SUM(amount) AS totalRevenue " +
                "FROM sales " +
                "GROUP BY DATE_FORMAT(saledate, '%Y-%m') " +
                "ORDER BY salemonth";

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {

            con = DBConnection.getConnection();
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            System.out.println("\n========== MONTHLY SALES ==========");

            boolean any = false;

            while (rs.next()) {

                any = true;

                String month = rs.getString("salemonth");
                int totalSales = rs.getInt("totalSales");
                double totalRevenue = rs.getDouble("totalRevenue");

                System.out.println(month +
                        " | Cars Sold : " + totalSales +
                        " | Revenue : ₹" + totalRevenue);

            }

            if (!any) {

                System.out.println("No Sales Available.");

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



    private void revenueReport() {

        String totalSql = "SELECT SUM(amount) AS totalRevenue FROM sales";
        String breakdownSql =
                "SELECT p.paymentmode, SUM(s.amount) AS modeTotal " +
                        "FROM sales s JOIN payment p ON s.saleid = p.saleid " +
                        "GROUP BY p.paymentmode";

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {

            con = DBConnection.getConnection();


            pst = con.prepareStatement(totalSql);
            rs = pst.executeQuery();

            System.out.println("\n========== REVENUE REPORT ==========");

            if (rs.next()) {

                double total = rs.getDouble("totalRevenue");
                System.out.println("Total Revenue : ₹" + total);

            }

            rs.close();
            pst.close();
   pst = con.prepareStatement(breakdownSql);
            rs = pst.executeQuery();

            System.out.println("\nRevenue By Payment Mode :");

            boolean any = false;

            while (rs.next()) {

                any = true;

                String mode = rs.getString("paymentmode");
                double modeTotal = rs.getDouble("modeTotal");

                System.out.println(mode + " : ₹" + modeTotal);

            }

            if (!any) {

                System.out.println("No Payments Recorded Yet.");

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

}
