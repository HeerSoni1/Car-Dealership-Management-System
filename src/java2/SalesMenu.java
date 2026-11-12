package java2;

import dbms.DBConnection;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.io.*;
import java.util.Scanner;

class Sale implements Displayable {

    private final int saleId;
    private final int customerId;
    private final int carId;
    private final double amount;
    private final LocalDate saleDate;

    public Sale(int saleId,
                int customerId,
                int carId,
                double amount,
                LocalDate saleDate) {

        this.saleId = saleId;
        this.customerId = customerId;
        this.carId = carId;
        this.amount = amount;
        this.saleDate = saleDate;
    }

    public int getSaleId() {
        return saleId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getCarId() {
        return carId;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    private static final String FORMAT = "| %-6s | %-10s | %-5s | %12s | %-10s |%n";
    private static final String BORDER =
            "+--------+------------+-------+--------------+------------+";

    public static void printHeader() {

        System.out.println(BORDER);
        System.out.printf(FORMAT, "SaleID", "CustomerID", "CarID", "Amount", "SaleDate");
        System.out.println(BORDER);

    }

    public void display() {

        System.out.printf(FORMAT, saleId, customerId, carId, String.format("%,.2f", amount), saleDate);

    }

    public static void printFooter() {
        System.out.println(BORDER);
    }

    @Override
    public String toString() {

        return saleId + "|" + customerId + "|" + carId + "|" + amount + "|" + saleDate;

    }

}

class SalesMenu {

    private static final double STANDARD_INTEREST_RATE = 12.0;
    private static final String CARD_OTP_FILE = "cardotp.txt";

    private final ArrayList<Sale> saleList;
    private final ArrayList<Car> carList;

    private final HashMap<Integer, Sale> salesRecord;

    private final Scanner sc;

    private final CarMenu carMenu;
    private final CustomerMenu customerMenu;

    private PaymentMenu paymentMenu;

    private int nextSaleId = 1;


    public SalesMenu(CarMenu carMenu, CustomerMenu customerMenu) {

        this.carMenu = carMenu;
        this.customerMenu = customerMenu;
        this.carList = carMenu.getCarList();

        saleList = new ArrayList<>();
        salesRecord = new HashMap<>();

        sc = new Scanner(System.in);

        loadSalesFromDB();

    }

    public void setPaymentMenu(PaymentMenu paymentMenu) {

        this.paymentMenu = paymentMenu;

    }

    private void loadSalesFromDB() {

        String sql = "SELECT saleid, customerid, carid, amount, saledate FROM sales";

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {

            con = DBConnection.getConnection();
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {

                int saleId = rs.getInt("saleid");
                int customerId = rs.getInt("customerid");
                int carId = rs.getInt("carid");
                double amount = rs.getDouble("amount");

                Date dbDate = rs.getDate("saledate");
                LocalDate saleDate = (dbDate != null) ? dbDate.toLocalDate() : null;

                Sale sale = new Sale(saleId, customerId, carId, amount, saleDate);

                saleList.add(sale);
                salesRecord.put(saleId, sale);

                if (saleId >= nextSaleId) {
                    nextSaleId = saleId + 1;
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

    public void salesMenu() {

        while (true) {

            System.out.println("\n==================================");
            System.out.println("        SALES MANAGEMENT");
            System.out.println("==================================");
            System.out.println("1. Sell Car");
            System.out.println("2. Generate Invoice");
            System.out.println("3. View Sales");
            System.out.println("4. Store Sales Records");
            System.out.println("5. Search Sale");
            System.out.println("6. Sort Sales");
            System.out.println("7. Back");
            System.out.print("Enter Choice : ");

            try {

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {

                    case 1:
                        sellCar();
                        break;

                    case 2:
                        generateInvoice();
                        break;

                    case 3:
                        viewSaleRecords();
                        break;

                    case 4:
                        storeSalesRecords();
                        break;

                    case 5:
                        searchSale();
                        break;

                    case 6:
                        sortSales();
                        break;

                    case 7:
                        return;

                    default:
                        System.out.println("Invalid Choice.");

                }

            }

            catch (InputMismatchException e) {

                System.out.println("Please enter a valid number.");
                sc.nextLine();

            }

        }

    }

    private int generateSaleId() {

        return nextSaleId++;

    }

    private void sellCar() {

        if (carList.isEmpty()) {

            System.out.println("No Cars Available.");
            return;

        }

        try {

            carMenu.viewCars();
            System.out.print("Enter Car ID : ");
            int carId = sc.nextInt();
            sc.nextLine();

            Car car = carMenu.searchCar(carId);

            if (car == null) {

                System.out.println("Car Not Available");
                return;

            }

            if (car.getQuantity() == 0) {

                System.out.println("Car Out Of Stock.");
                return;

            }

            customerMenu.viewCustomers();
            System.out.print("Enter Customer ID : ");
            int customerId = sc.nextInt();
            sc.nextLine();

            Customer customer = customerMenu.searchCustomer(customerId);

            if (customer == null) {

                System.out.println("Customer Not Found. Please Add The Customer First.");
                return;

            }

            int saleId = generateSaleId();
            double amount = car.getPrice();

            LocalDate saleDate = LocalDate.now();

            String sql = "INSERT INTO sales (saleid, customerid, carid, amount, saledate) VALUES (?, ?, ?, ?, ?)";

            Connection con = null;
            PreparedStatement pst = null;
            boolean saleInserted = false;

            try {

                con = DBConnection.getConnection();
                pst = con.prepareStatement(sql);

                pst.setInt(1, saleId);
                pst.setInt(2, customerId);
                pst.setInt(3, carId);
                pst.setDouble(4, amount);
                pst.setDate(5, Date.valueOf(saleDate));

                pst.executeUpdate();

                saleInserted = true;

            } catch (SQLException e) {

                System.out.println("Database Error : " + e.getMessage());

            } finally {

                try {
                    if (pst != null) pst.close();
                    if (con != null) con.close();
                } catch (SQLException e) {
                    System.out.println("Error Closing Connection : " + e.getMessage());
                }

            }

            if (!saleInserted) {
                return;
            }

            Sale sale = new Sale(saleId, customerId, carId, amount, saleDate);

            saleList.add(sale);
            salesRecord.put(saleId, sale);

            int newQuantity = car.getQuantity() - 1;
            car.setQuantity(newQuantity);

            carMenu.updateQuantityInDB(carId, newQuantity);

            System.out.println("\nSale Successful.");
            System.out.println("Sale ID : " + saleId);
            System.out.println("Customer : " + customer.getName());

            System.out.println("\n===== PAYMENT =====");
            System.out.println("Total Amount Due : \u20B9" + String.format("%.2f", amount));

            String modesUsed = processPayment(amount);

            if (paymentMenu != null) {

                paymentMenu.recordPayment(saleId, amount, modesUsed);

            }

            writeInvoiceFile(saleId);

            if (paymentMenu != null) {

                paymentMenu.generateReceipt(saleId, amount);

            }

        }

        catch (InputMismatchException e) {

            System.out.println("Invalid Input.");
            sc.nextLine();

        }

    }

    private String processPayment(double totalAmount) {

        double remaining = totalAmount;
        StringBuilder modesUsed = new StringBuilder();

        while (remaining > 0.001) {

            System.out.println("\nAmount Remaining : \u20B9" + String.format("%.2f", remaining));
            System.out.println("\nSelect Payment Mode :");
            System.out.println("1. Cash");
            System.out.println("2. Card");
            System.out.println("3. Opt For Loan");
            System.out.print("Enter Choice : ");

            int modeChoice;

            try {

                modeChoice = sc.nextInt();
                sc.nextLine();

            } catch (InputMismatchException e) {

                System.out.println("Please Enter A Valid Number.");
                sc.nextLine();
                continue;

            }

            switch (modeChoice) {

                case 1:
                case 2: {

                    String mode = (modeChoice == 1) ? "Cash" : "Card";

                    if (modeChoice == 2 && !verifyCard()) {

                        System.out.println("Card Payment Cancelled.");
                        continue;

                    }

                    System.out.print("Enter Amount To Pay (Max \u20B9" + String.format("%.2f", remaining) + ") : ");

                    double payAmount;

                    try {

                        payAmount = sc.nextDouble();
                        sc.nextLine();

                    } catch (InputMismatchException e) {

                        System.out.println("Invalid Amount.");
                        sc.nextLine();
                        continue;

                    }

                    if (payAmount <= 0) {

                        System.out.println("Amount Must Be Greater Than Zero.");
                        continue;

                    }

                    if (payAmount > remaining) {

                        System.out.println("Amount Exceeds Remaining Balance. Charging Only \u20B9"
                                + String.format("%.2f", remaining) + ".");
                        payAmount = remaining;

                    }

                    remaining -= payAmount;
                    addModeUsed(modesUsed, mode);

                    System.out.println("\u20B9" + String.format("%.2f", payAmount) + " Paid Via " + mode + ".");

                    if (remaining <= 0.001) {
                        System.out.println("\nPayment Done. Car Fully Paid.");
                    } else {
                        System.out.println("Remaining Balance : \u20B9" + String.format("%.2f", remaining));
                    }

                    break;

                }

                case 3: {

                    System.out.print("Enter Loan Amount (Must Be Less Than Or Equal To Car Price \u20B9"
                            + String.format("%.2f", totalAmount) + ") : ");

                    double loanAmount;

                    try {

                        loanAmount = sc.nextDouble();
                        sc.nextLine();

                    } catch (InputMismatchException e) {

                        System.out.println("Invalid Amount.");
                        sc.nextLine();
                        continue;

                    }

                    if (loanAmount <= 100000 || loanAmount > totalAmount) {

                        System.out.println("Loan Amount Must Be Greater Than 1 Lakh And Not More Than The Car Price.");
                        continue;

                    }

                    if (loanAmount > remaining) {

                        System.out.println("Loan Amount Cannot Exceed The Remaining Balance Of \u20B9"
                                + String.format("%.2f", remaining) + ".");
                        continue;

                    }

                    System.out.print("Enter Loan Duration In Years (1 - 10) : ");

                    int duration;

                    try {

                        duration = sc.nextInt();
                        sc.nextLine();

                    } catch (InputMismatchException e) {

                        System.out.println("Invalid Duration.");
                        sc.nextLine();
                        continue;

                    }

                    if (duration < 1 || duration > 10) {

                        System.out.println("Loan Duration Must Be Between 1 And 10 Years.");
                        continue;

                    }

                    double interestRate = STANDARD_INTEREST_RATE;

                    double totalInterest = loanAmount * interestRate * duration / 100.0;
                    double totalPayable = loanAmount + totalInterest;
                    double emi = totalPayable / (duration * 12);

                    remaining -= loanAmount;
                    addModeUsed(modesUsed, "Loan");
                    try {
                        System.out.println();
                        System.out.print("Verifying credentials");
                        Thread.sleep(500);
                        System.out.print(".");
                        Thread.sleep(500);
                        System.out.print(".");
                        Thread.sleep(500);
                        System.out.println(".");
                        System.out.print("Processing transaction");
                        Thread.sleep(500);
                        System.out.print(".");
                        Thread.sleep(500);
                        System.out.print(".");
                        Thread.sleep(500);
                        System.out.println(".");

                    }
                    catch (Exception e) {
                        System.out.println("Transaction failed.");
                    }
                    System.out.println("\nLoan Approved Successfully.");
                    System.out.println("Loan Amount    : \u20B9" + String.format("%.2f", loanAmount));
                    System.out.println("Duration       : " + duration + " Year(s)");
                    System.out.println("Interest Rate  : " + interestRate + "%");
                    System.out.println("Total Interest : \u20B9" + String.format("%.2f", totalInterest));
                    System.out.println("Total Payable  : \u20B9" + String.format("%.2f", totalPayable));
                    System.out.println("EMI (Monthly)  : \u20B9" + String.format("%.2f", emi));

                    if (loanAmount == totalAmount || remaining <= 0.001) {
                        System.out.println("\nPayment Done.");
                    } else {
                        System.out.println("Remaining Balance : \u20B9" + String.format("%.2f", remaining));
                    }

                    break;

                }

                default:

                    System.out.println("Invalid Choice. Please Select 1, 2 Or 3.");

            }

        }

        return modesUsed.toString();

    }

    private boolean verifyCard() {

        System.out.print("Enter 16-Digit Card Number : ");
        String cardNumber = sc.nextLine().trim();

        if (!cardNumber.matches("\\d{16}")) {

            System.out.println("Invalid Card Number. It Must Be Exactly 16 Digits.");
            return false;

        }

        String otp = generateCardOtp();
        writeCardOtpToFile(otp);

        System.out.println("An OTP Has Been Sent. Check " + CARD_OTP_FILE + " To View It.");
        System.out.print("Enter OTP : ");
        String enteredOtp = sc.nextLine().trim();

        if (!enteredOtp.equals(otp)) {

            System.out.println("Incorrect OTP.");
            return false;

        }

        System.out.println("Card Verified Successfully.");
        return true;

    }

    private String generateCardOtp() {

        int otp = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(otp);

    }

    private void writeCardOtpToFile(String otp) {

        try (FileWriter writer = new FileWriter(CARD_OTP_FILE)) {

            writer.write("Card Payment OTP: " + otp);

        } catch (IOException e) {

            System.out.println("Error Writing OTP File : " + e.getMessage());

        }

    }

    private void addModeUsed(StringBuilder modesUsed, String mode) {

        if (modesUsed.indexOf(mode) < 0) {

            if (modesUsed.length() > 0) {
                modesUsed.append("+");
            }

            modesUsed.append(mode);

        }

    }

    public Sale getSaleById(int id) {

        return salesRecord.get(id);

    }

    private void searchSale() {

        if (salesRecord.isEmpty()) {

            System.out.println("No Sales Available.");
            return;

        }

        try {

            System.out.print("Enter Sale ID : ");
            int id = sc.nextInt();
            sc.nextLine();

            Sale sale = getSaleById(id);

            if (sale == null) {

                System.out.println("Sale Not Found.");
                return;

            }

            printSaleTableHeader();
            printSaleRow(sale);
            printSaleTableFooter();

        }

        catch (InputMismatchException e) {

            System.out.println("Invalid Input.");
            sc.nextLine();

        }

    }

    private void viewSaleRecords() {

        if (saleList.isEmpty()) {

            System.out.println("\nNo Sales Available.");
            return;

        }

        System.out.println("\n========== SALES ==========");

        printSaleTableHeader();

        for (Sale sale : saleList) {

            printSaleRow(sale);

        }

        printSaleTableFooter();

    }

    private static final String SALE_ROW_FORMAT =
            "| %-6s | %-10s | %-5s | %12s | %-10s | %-15s | %-14s |%n";
    private static final String SALE_BORDER =
            "+--------+------------+-------+--------------+------------+-----------------+----------------+";

    private void printSaleTableHeader() {

        System.out.println(SALE_BORDER);
        System.out.printf(SALE_ROW_FORMAT, "SaleID", "CustomerID", "CarID", "Amount", "SaleDate", "PaymentMode", "PaymentStatus");
        System.out.println(SALE_BORDER);

    }

    private void printSaleTableFooter() {
        System.out.println(SALE_BORDER);
    }

    private void printSaleRow(Sale sale) {

        String mode = "Not Recorded";
        String status = "Not Recorded";

        if (paymentMenu != null) {

            Payment payment = paymentMenu.getPaymentBySaleId(sale.getSaleId());

            if (payment != null) {
                mode = payment.getPaymentMode();
                status = payment.getPaymentStatus();
            }

        }

        System.out.printf(SALE_ROW_FORMAT, sale.getSaleId(), sale.getCustomerId(), sale.getCarId(),
                String.format("%,.2f", sale.getAmount()), sale.getSaleDate(), mode, status);

    }

    private void generateInvoice() {

        if (saleList.isEmpty()) {

            System.out.println("No Sales Available.");
            return;

        }

        try {

            System.out.print("Enter Sale ID : ");
            int saleId = sc.nextInt();
            sc.nextLine();

            writeInvoiceFile(saleId);

        }

        catch (InputMismatchException e) {

            System.out.println("Invalid Input.");
            sc.nextLine();

        }

    }

    private void writeInvoiceFile(int saleId) {

        Sale sale = getSaleById(saleId);

        if (sale == null) {

            System.out.println("Sale Not Found.");
            return;

        }

        Car car = carMenu.searchCar(sale.getCarId());

        if (car == null)
            return;

        Customer customer = customerMenu.searchCustomer(sale.getCustomerId());

        if (customer == null)
            return;

        Payment payment = (paymentMenu != null) ? paymentMenu.getPaymentBySaleId(saleId) : null;

        try {

            FileWriter fw = new FileWriter("Invoice_" + saleId + ".txt");

            fw.write("=====================================\n");
            fw.write("         CAR SALES INVOICE\n");
            fw.write("=====================================\n");
            fw.write("Sale ID : " + sale.getSaleId() + "\n");
            fw.write("Sale Date : " + sale.getSaleDate() + "\n\n");

            fw.write("Customer Name : " + customer.getName() + "\n");
            fw.write("Phone : " + customer.getPhone() + "\n\n");

            fw.write("Car ID : " + car.getCarId() + "\n");
            fw.write("Brand : " + car.getBrand() + "\n");
            fw.write("Model : " + car.getModel() + "\n");
            fw.write("Amount : ₹" + sale.getAmount() + "\n\n");

            if (payment != null) {

                fw.write("Payment Mode   : " + payment.getPaymentMode() + "\n");
                fw.write("Payment Status : " + payment.getPaymentStatus() + "\n");

            } else {

                fw.write("Payment Status : Not Recorded\n");

            }

            fw.write("\nThank You For Purchasing.\n");

            fw.close();

            System.out.println("Invoice Generated Successfully.");
            System.out.println("File Name : Invoice_" + saleId + ".txt");

        }

        catch (IOException e) {

            System.out.println("File Error : " + e.getMessage());

        }

    }

    private void storeSalesRecords() {

        if (salesRecord.isEmpty()) {

            System.out.println("No Sales Available.");
            return;

        }

        System.out.println("\n========== STORED SALES RECORDS ==========\n");

        try {

            FileWriter fw = new FileWriter("D://SalesRecords.txt");

            Sale.printHeader();
            fw.write("SaleID|CustomerID|CarID|Amount|SaleDate\n");

            for (Sale sale : salesRecord.values()) {

                sale.display();

                fw.write(sale.toString() + "\n");

            }

            Sale.printFooter();

            fw.close();

            System.out.println("\nSales Records Stored Successfully.");

        }

        catch (IOException e) {

            System.out.println("File Error : " + e.getMessage());

        }

    }

    private void sortSales() {

        if (saleList.isEmpty()) {

            System.out.println("No Sales Available.");
            return;

        }

        System.out.println("\n1. Sort By Amount");
        System.out.println("2. Sort By Sale ID");
        System.out.print("Enter Choice : ");

        try {

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:

                    saleList.sort(Comparator.comparingDouble(Sale::getAmount));

                    break;

                case 2:

                    saleList.sort(Comparator.comparingInt(Sale::getSaleId));

                    break;

                default:

                    System.out.println("Invalid Choice.");
                    return;

            }

            System.out.println("\nSales Sorted Successfully.\n");

            viewSaleRecords();

        }

        catch (InputMismatchException e) {

            System.out.println("Invalid Input.");
            sc.nextLine();

        }

    }

}
