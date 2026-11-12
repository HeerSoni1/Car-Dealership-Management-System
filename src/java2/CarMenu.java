package java2;

import dbms.DBConnection;


import java.util.*;
import java.io.*;
import java.sql.*;

public class CarMenu {

    private final ArrayList<Car> carList;
    private final Scanner sc;

    private final TreeMap<Integer, Car> carById;     // BST keyed by Car ID
    private final TreeMap<String, Car> carByModel;   // BST keyed by Model name (case-insensitive)

    private static final String COMPANY_BRAND = "Nissan";

    public CarMenu() {
        carList = new ArrayList<>();
        carById = new TreeMap<>();
        carByModel = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sc = new Scanner(System.in);
        loadCarsFromDB();

    }



    private void loadCarsFromDB() {

        String sql = "SELECT carid, brand, model, fuelType, price, quantity FROM car";

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {

            con = DBConnection.getConnection();
            pst = con.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {

                int id = rs.getInt("carid");
                String brand = rs.getString("brand");
                String model = rs.getString("model");
                String fuel = rs.getString("fuelType");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");

                Car car = new Car(id, brand, model, fuel, price, quantity);
                carList.add(car);
                carById.put(id, car);
                carByModel.put(model, car);

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


    public void carMenu() {

        while (true) {

            System.out.println("\n-------------------------");
            System.out.println("      CAR MANAGEMENT");
            System.out.println("-------------------------");
            System.out.println("1. Add Car");
            System.out.println("2. View Cars");
            System.out.println("3. Search Car");
            System.out.println("4. Update Car");
            System.out.println("5. Delete Car");
            System.out.println("6. Add Stock");
            System.out.println("7. Back");
            System.out.print("Enter Choice : ");

            try {

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {

                    case 1 -> addCar();

                    case 2 -> viewCars();

                    case 3 -> searchCar();

                    case 4 -> updateCar();

                    case 5 -> deleteCar();

                    case 6 -> addStock();

                    case 7 -> {
                        return;
                    }

                    default -> System.out.println("Invalid Choice.");

                }

            } catch (Exception e) {

                System.out.println("Please enter a valid number.");
                sc.nextLine();

            }

        }

    }

    private void addCar() {

        try {


            System.out.print("Enter Model : ");
            String model = sc.nextLine();

            System.out.print("Enter Fuel Type : ");
            String fuel = sc.nextLine();

            System.out.print("Enter Price : ");
            double price = sc.nextDouble();



            if (price < 500000) {
                System.out.println("Price cannot be that low.");
                return;
            }

            System.out.print("Enter Quantity : ");
            int quantity = sc.nextInt();




            String sql = "INSERT INTO car ( brand, model, fuelType, price, quantity) VALUES ( ?, ?, ?, ?, ?)";
            String sql1 = "Select carid from car where  brand=? and model=? and fuelType=?  and price= ? and quantity=? ;";

            Connection con = null;
            PreparedStatement pst = null;
            PreparedStatement pst1 = null;
            int id=0;
            try {

                con = DBConnection.getConnection();
                pst = con.prepareStatement(sql);
                pst1 = con.prepareStatement(sql1);

                pst.setString(1, COMPANY_BRAND);
                pst.setString(2, model);
                pst.setString(3, fuel);
                pst.setDouble(4, price);
                pst.setInt(5, quantity);
                pst.executeUpdate();

                pst1.setString(1, COMPANY_BRAND);
                pst1.setString(2, model);
                pst1.setString(3, fuel);
                pst1.setDouble(4, price);
                pst1.setInt(5, quantity);
                ResultSet r=pst1.executeQuery();

                if(r.next()) {
                    id = r.getInt(1);
                }
                else {
                    return;
                }
                Car car = new Car(id, COMPANY_BRAND, model, fuel, price, quantity);
                carList.add(car);
                carById.put(id, car);
                carByModel.put(model, car);

                System.out.println("\nCar Added Successfully.");

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

    private boolean isDuplicateId(int id) {

        for (Car car : carList) {

            if (car.getCarId() == id)
                return true;

        }
        return false;

    }


    public void viewCars() {

        if (carList.isEmpty()) {

            System.out.println("No Cars Available.");
            return;
        }

        System.out.println("\n============= CAR LIST =============");

        Car.printHeader();

        Iterator<Car> iterator = carList.iterator();

        while (iterator.hasNext()) {

            Car car = iterator.next();
            car.display();

        }

        Car.printFooter();

    }

    private void searchCar() {

        if (carList.isEmpty()) {
            System.out.println("\nNo Cars Available.");
            return;
        }

        System.out.println("\n------ Search Car ------");
        System.out.println("1. Search by ID");
        System.out.println("2. Search by Model");
        System.out.print("Enter Choice : ");

        try {

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1 -> {

                    viewCars();
                    System.out.print("Enter Car ID : ");
                    int id = sc.nextInt();
                    sc.nextLine();

                    searchCar(id);

                }

                case 2 -> {

                    System.out.print("Enter Model : ");
                    String model = sc.nextLine();

                    searchCarByModel(model);

                }

                default -> System.out.println("Invalid Choice.");

            }

        }

        catch (Exception e) {

            System.out.println("Invalid Input.");
            sc.nextLine();

        }

    }

    public Car searchCar(int id) {

        Car car = carById.get(id);

        if (car != null) {

            System.out.println("\nCar Found\n");
            Car.printHeader();
            car.display();
            Car.printFooter();
            return car;

        }

        System.out.println("Car Not Found.");
        return null;
    }

    private void searchCarByModel(String modelPrefix) {

        boolean found = false;

        String from = modelPrefix;
        String to = modelPrefix + Character.MAX_VALUE;
        SortedMap<String, Car> matches = carByModel.subMap(from, to);

        for (Map.Entry<String, Car> entry : matches.entrySet()) {

            if (!found) {
                Car.printHeader();
            }

            entry.getValue().display();
            found = true;

        }

        if (found)
            Car.printFooter();
        else
            System.out.println("No Cars Found.");

    }




    private void updateCar() {

        if (carList.isEmpty()) {
            System.out.println("\nNo Cars Available.");
            return;
        }

        try {

            viewCars();
            System.out.print("\nEnter Car ID to Update : ");
            int id = sc.nextInt();
            sc.nextLine();

            Car carToUpdate = null;

            for (Car car : carList) {

                if (car.getCarId() == id) {
                    carToUpdate = car;
                    break;
                }

            }

            if (carToUpdate == null) {
                System.out.println("Car Not Found.");
                return;
            }

            String oldModel = carToUpdate.getModel();

            System.out.print("Enter New Model : ");
            String model = sc.nextLine();

            System.out.print("Enter New Fuel Type : ");
            String fuel = sc.nextLine();

            System.out.print("Enter New Price : ");
            double price = sc.nextDouble();

            if (price < 0) {
                System.out.println("Price cannot be negative.");
                return;
            }

            System.out.print("Enter New Quantity : ");
            int quantity = sc.nextInt();

            if (quantity < 0) {
                System.out.println("Quantity cannot be negative.");
                return;
            }

            String sql = "UPDATE car SET brand = ?, model = ?, fuelType = ?, price = ?, quantity = ? WHERE carid = ?";

            Connection con = null;
            PreparedStatement pst = null;

            try {

                con = DBConnection.getConnection();
                pst = con.prepareStatement(sql);

                pst.setString(1, COMPANY_BRAND);
                pst.setString(2, model);
                pst.setString(3, fuel);
                pst.setDouble(4, price);
                pst.setInt(5, quantity);
                pst.setInt(6, id);

                int rows = pst.executeUpdate();

                if (rows > 0) {

                    carToUpdate.setBrand(COMPANY_BRAND);
                    carToUpdate.setModel(model);
                    carToUpdate.setFuelType(fuel);
                    carToUpdate.setPrice(price);
                    carToUpdate.setQuantity(quantity);

                    carByModel.remove(oldModel);
                    carByModel.put(model, carToUpdate);

                    System.out.println("\nCar Updated Successfully.");

                } else {

                    System.out.println("Car Not Found In Database.");

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



    private void deleteCar() {

        if (carList.isEmpty()) {
            System.out.println("\nNo Cars Available.");
            return;
        }

        try {

            viewCars();
            System.out.print("\nEnter Car ID to Delete : ");
            int id = sc.nextInt();
            sc.nextLine();

            Iterator<Car> iterator = carList.iterator();

            while (iterator.hasNext()) {

                Car car = iterator.next();

                if (car.getCarId() == id) {

                    System.out.print("Are you sure you want to delete this car? (Y/N): ");
                    char choice = sc.next().charAt(0);
                    sc.nextLine();

                    if (choice == 'Y' || choice == 'y') {

                        String sql = "DELETE FROM car WHERE carid = ?";

                        Connection con = null;
                        PreparedStatement pst = null;

                        try {

                            con = DBConnection.getConnection();
                            pst = con.prepareStatement(sql);

                            pst.setInt(1, id);

                            int rows = pst.executeUpdate();

                            if (rows > 0) {

                                iterator.remove();
                                carById.remove(car.getCarId());
                                carByModel.remove(car.getModel());
                                System.out.println("Car Deleted Successfully.");

                            } else {

                                System.out.println("Car Not Found In Database.");

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

                    } else {
                        System.out.println("Deletion Cancelled.");

                    }

                    return;

                }

            }

            System.out.println("Car Not Found.");

        } catch (Exception e) {

            System.out.println("Invalid Input.");
            sc.nextLine();

        }

    }


    private void addStock() {

        if (carList.isEmpty()) {
            System.out.println("\nNo Cars Available.");
            return;
        }

        try {

            viewCars();
            System.out.print("\nEnter Car ID to Add Stock : ");
            int id = sc.nextInt();
            sc.nextLine();

            Car carToUpdate = null;

            for (Car car : carList) {

                if (car.getCarId() == id) {
                    carToUpdate = car;
                    break;
                }

            }

            if (carToUpdate == null) {
                System.out.println("Car Not Found.");
                return;
            }

            System.out.print("Enter Quantity To Add : ");
            int quantityToAdd = sc.nextInt();
            sc.nextLine();

            if (quantityToAdd <= 0) {
                System.out.println("Quantity To Add Must Be Greater Than Zero.");
                return;
            }

            int newQuantity = carToUpdate.getQuantity() + quantityToAdd;

            boolean updated = updateQuantityInDB(id, newQuantity);

            if (updated) {

                carToUpdate.setQuantity(newQuantity);
                System.out.println("\nStock Added Successfully.");
                System.out.println("New Quantity : " + newQuantity);

            } else {

                System.out.println("Car Not Found In Database.");

            }

        } catch (Exception e) {

            System.out.println("Invalid Input.");
            sc.nextLine();

        }

    }


    public boolean updateQuantityInDB(int carId, int quantity) {

        String sql = "UPDATE car SET quantity = ? WHERE carid = ?";

        Connection con = null;
        PreparedStatement pst = null;

        try {

            con = DBConnection.getConnection();
            pst = con.prepareStatement(sql);

            pst.setInt(1, quantity);
            pst.setInt(2, carId);

            int rows = pst.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {

            System.out.println("Database Error : " + e.getMessage());
            return false;

        } finally {

            try {
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                System.out.println("Error Closing Connection : " + e.getMessage());
            }

        }

    }

    public ArrayList<Car> getCarList() {
        return carList;
    }
}


class Car implements Displayable {

    private int carId;
    private String brand;
    private String model;
    private String fuelType;
    private double price;
    private int quantity;


    public Car(int carId, String brand, String model,
               String fuelType, double price, int quantity) {

        this.carId = carId;
        this.brand = brand;
        this.model = model;

        this.fuelType = fuelType;
        this.price = price;
        this.quantity = quantity;
    }

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }


    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    private static final String FORMAT = "| %-6s | %-8s | %-20s | %-10s | %12s | %-8s |%n";
    private static final String BORDER =
            "+--------+----------+----------------------+------------+--------------+----------+";

    public static void printHeader() {

        System.out.println(BORDER);
        System.out.printf(FORMAT, "CarID", "Brand", "Model", "FuelType", "Price", "Quantity");
        System.out.println(BORDER);

    }

    public void display() {

        System.out.printf(FORMAT, carId, brand, model, fuelType,
                String.format("%,.2f", price), quantity);

    }

    public static void printFooter() {
        System.out.println(BORDER);
    }
}
