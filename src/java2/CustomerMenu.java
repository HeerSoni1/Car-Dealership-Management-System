package java2;

import dbms.DBConnection;
import ds.*;

import java.util.*;
import java.sql.*;

public class CustomerMenu {

    private final CustomerLinkedList customerList;
    private final Scanner sc;


    public CustomerMenu() {

        customerList = new CustomerLinkedList();
        sc = new Scanner(System.in);
        loadCustomersFromDB();

    }

    private void loadCustomersFromDB() {

        String sql = "SELECT customerid, name, phone, email, address FROM customer";

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {

            con = DBConnection.getConnection();
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("customerid");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                String address = rs.getString("address");

                Customer customer =
                        new Customer(id, name, phone, email, address);

                customerList.add(customer);

            }

        } catch (SQLException e) {

            System.out.println("Database Error : " + e.getMessage());

        } finally {

            try {

                if (rs != null)
                    rs.close();

                if (pst != null)
                    pst.close();

                if (con != null)
                    con.close();

            } catch (SQLException e) {

                System.out.println("Error Closing Connection : " + e.getMessage());

            }

        }

    }

    public void customerMenu() {

        while (true) {

            System.out.println("\n-------------------------");
            System.out.println(" CUSTOMER MANAGEMENT");
            System.out.println("-------------------------");
            System.out.println("1. Add Customer");
            System.out.println("2. View Customers");
            System.out.println("3. Search Customer");
            System.out.println("4. Update Customer");
            System.out.println("5. Delete Customer");
            System.out.println("6. Back");
            System.out.print("Enter Choice : ");

            try {

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {

                    case 1 -> addCustomer();

                    case 2 -> viewCustomers();

                    case 3 -> searchCustomer();

                    case 4 -> updateCustomer();

                    case 5 -> deleteCustomer();

                    case 6 -> {
                        return;
                    }

                    default -> System.out.println("Invalid Choice.");

                }

            } catch (Exception e) {

                System.out.println("Please Enter A Valid Number.");
                sc.nextLine();

            }

        }

    }

    private void addCustomer() {

        try {

            System.out.print("Enter Customer Name : ");
            String name = sc.nextLine();

            if (name.trim().isEmpty()) {

                System.out.println("Name Cannot Be Empty.");
                return;

            }

            if (!name.matches("[A-Za-z ]+")) {

                System.out.println("Name Can Contain Only Alphabets And Spaces.");
                return;

            }

            System.out.print("Enter Phone Number : ");
            String phone = sc.nextLine();

            if (!phone.matches("\\d{10}")) {

                System.out.println("Phone Number Must Contain 10 Digits.");
                return;

            }

            System.out.print("Enter Email : ");
            String email = sc.nextLine();

            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {

                System.out.println("Invalid Email Address.");
                return;

            }

            System.out.print("Enter Address : ");
            String address = sc.nextLine();

            if (address.trim().isEmpty()) {

                System.out.println("Address Cannot Be Empty.");
                return;

            }

            String sql = "INSERT INTO customer(name,phone,email,address) VALUES(?,?,?,?)";

            String sql1 = "Select customerid from customer where  name=? and email=? and phone=?  and address= ? ;";


            Connection con = null;
            PreparedStatement pst = null;
            PreparedStatement pst1 = null;
            int id;
            try {

                con = DBConnection.getConnection();

                pst = con.prepareStatement(sql);
                pst1 = con.prepareStatement(sql1);

                pst.setString(1, name);
                pst.setString(2, phone);
                pst.setString(3, email);
                pst.setString(4, address);

                pst.executeUpdate();

                pst1.setString(1,name);
                pst1.setString(2, email);
                pst1.setString(3, phone);
                pst1.setString(4, address);
                ResultSet r=pst1.executeQuery();

                if(r.next()) {
                    id = r.getInt(1);
                }
                else {
                    return;
                }

                Customer customer = new Customer(id, name, phone, email, address);
                customerList.add(customer);

                System.out.println("\nCustomer Added Successfully.");

            } catch (SQLException e) {

                System.out.println("Database Error : " + e.getMessage());

            } finally {

                try {

                    if (pst != null)
                        pst.close();

                    if (con != null)
                        con.close();

                } catch (SQLException e) {

                    System.out.println("Error Closing Connection : " + e.getMessage());

                }

            }

        } catch (Exception e) {

            System.out.println("Invalid Input.");
            sc.nextLine();

        }

    }

    private boolean isDuplicateId(int id) {

        return customerList.isDuplicateId(id);

    }

    public void viewCustomers() {

        if (customerList.isEmpty()) {

            System.out.println("\nNo Customers Available.");
            return;

        }

        System.out.println("\n============= CUSTOMER LIST =============");

        customerList.display();

    }


    private void searchCustomer() {

        if (customerList.isEmpty()) {

            System.out.println("\nNo Customers Available.");
            return;

        }

        System.out.println("\n------ Search Customer ------");
        System.out.println("1. Search by ID");
        System.out.println("2. Search by Name");
        System.out.print("Enter Choice : ");

        try {

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1 -> {

                    viewCustomers();
                    System.out.print("Enter Customer ID : ");
                    int id = sc.nextInt();
                    sc.nextLine();

                    searchCustomer(id);

                }

                case 2 -> {

                    System.out.print("Enter Customer Name : ");
                    String name = sc.nextLine();

                    searchCustomer(name);

                }

                default -> System.out.println("Invalid Choice.");

            }

        }

        catch (Exception e) {

            System.out.println("Invalid Input.");
            sc.nextLine();

        }

    }



    public Customer searchCustomer(int id) {

        Customer customer = customerList.searchById(id);

        if (customer != null) {

            System.out.println("\nCustomer Found\n");

            Customer.printHeader();
            customer.display();
            Customer.printFooter();

            return customer;

        }

        System.out.println("Customer Not Found.");

        return null;

    }


    private void searchCustomer(String name) {

        boolean found = customerList.searchByName(name);

        if (!found) {

            System.out.println("Customer Not Found.");

        }

    }

    private void updateCustomer() {

        if (customerList.isEmpty()) {

            System.out.println("\nNo Customers Available.");
            return;

        }

        try {

            viewCustomers();
            System.out.print("\nEnter Customer ID to Update : ");
            int id = sc.nextInt();
            sc.nextLine();

            Customer customerToUpdate = customerList.searchById(id);

            if (customerToUpdate == null) {

                System.out.println("Customer Not Found.");
                return;

            }

            System.out.print("Enter New Name : ");
            String name = sc.nextLine();

            if (name.trim().isEmpty()) {

                System.out.println("Name Cannot Be Empty.");
                return;

            }

            if (!name.matches("[A-Za-z ]+")) {

                System.out.println("Name Can Contain Only Alphabets And Spaces.");
                return;

            }

            System.out.print("Enter New Phone : ");
            String phone = sc.nextLine();

            if (!phone.matches("\\d{10}")) {

                System.out.println("Phone Number Must Contain 10 Digits.");
                return;

            }

            System.out.print("Enter New Email : ");
            String email = sc.nextLine();

            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {

                System.out.println("Invalid Email.");
                return;

            }

            System.out.print("Enter New Address : ");
            String address = sc.nextLine();

            if (address.trim().isEmpty()) {

                System.out.println("Address Cannot Be Empty.");
                return;

            }

            String sql =
                    "UPDATE customer SET name=?, phone=?, email=?, address=? WHERE customerid=?";

            Connection con = null;
            PreparedStatement pst = null;

            try {

                con = DBConnection.getConnection();

                pst = con.prepareStatement(sql);

                pst.setString(1, name);
                pst.setString(2, phone);
                pst.setString(3, email);
                pst.setString(4, address);
                pst.setInt(5, id);

                int rows = pst.executeUpdate();

                if (rows > 0) {

                    customerToUpdate.setName(name);
                    customerToUpdate.setPhone(phone);
                    customerToUpdate.setEmail(email);
                    customerToUpdate.setAddress(address);

                    System.out.println("\nCustomer Updated Successfully.");

                } else {

                    System.out.println("Customer Not Found In Database.");

                }

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

        } catch (Exception e) {

            System.out.println("Invalid Input.");
            sc.nextLine();

        }

    }



    private void deleteCustomer() {

        if (customerList.isEmpty()) {

            System.out.println("\nNo Customers Available.");
            return;

        }

        try {

            viewCustomers();
            System.out.print("\nEnter Customer ID to Delete : ");
            int id = sc.nextInt();
            sc.nextLine();

            Customer customer = customerList.searchById(id);

            if (customer == null) {

                System.out.println("Customer Not Found.");
                return;

            }

            System.out.print("Are You Sure? (Y/N) : ");
            char choice = sc.next().charAt(0);
            sc.nextLine();

            if (choice == 'Y' || choice == 'y') {

                String sql = "DELETE FROM customer WHERE customerid=?";

                Connection con = null;
                PreparedStatement pst = null;

                try {

                    con = DBConnection.getConnection();

                    pst = con.prepareStatement(sql);

                    pst.setInt(1, id);

                    int rows = pst.executeUpdate();

                    if (rows > 0) {

                        customerList.delete(id);

                        System.out.println("Customer Deleted Successfully.");

                    }

                } catch (SQLException e) {

                    System.out.println("Database Error : " + e.getMessage());

                } finally {

                    try {

                        if (pst != null)
                            pst.close();

                        if (con != null)
                            con.close();

                    }

                    catch(SQLException e){

                        System.out.println(e.getMessage());

                    }

                }

            }

        } catch (Exception e) {

            System.out.println("Invalid Input.");
            sc.nextLine();

        }

    }
}
