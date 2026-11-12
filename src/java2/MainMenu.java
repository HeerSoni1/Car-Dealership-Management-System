package java2;

import java.util.Scanner;

public class MainMenu {

    Scanner sc = new Scanner(System.in);

    LoginMenu login = new LoginMenu();
    CarMenu carMenu = new CarMenu();
    CustomerMenu customerMenu = new CustomerMenu();
    SalesMenu salesMenu = new SalesMenu(carMenu,customerMenu);
    ReportMenu reportMenu = new ReportMenu();
PaymentMenu paymentMenu=new PaymentMenu();
    public void start() {

        salesMenu.setPaymentMenu(paymentMenu);



            login.lock();

            while (true) {

                System.out.println("\n========================================");
                System.out.println("     CAR DEALER MANAGEMENT SYSTEM");
                System.out.println("========================================");
                System.out.println("1. Car Management");
                System.out.println("2. Customer Management");
                System.out.println("3. Sales Management");
                System.out.println("4. Reports");
                System.out.println("5. Log out");
                System.out.println("6. Exit");
                System.out.print("\nEnter Choice : ");

                int choice;

                try {
                    choice = Integer.parseInt(sc.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Input!");
                    continue;
                }

                switch (choice) {

                    case 1 -> carMenu.carMenu();
                    case 2 -> customerMenu.customerMenu();
                    case 3 -> salesMenu.salesMenu();
                    case 4 -> reportMenu.reportsMenu();

                    case 5 -> login.logout();



                    case 6 -> {
                        System.out.println("\nThank You For Using Car Dealer Management System");
                        login.logout(1);
                        return;
                    }

                    default -> System.out.println("\nInvalid Choice.");
                }

            }

        }

    }


