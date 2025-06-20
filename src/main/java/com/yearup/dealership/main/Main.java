package com.yearup.dealership.main;

import com.yearup.dealership.db.InventoryDao;
import com.yearup.dealership.db.LeaseDao;
import com.yearup.dealership.db.SalesDao;
import com.yearup.dealership.db.VehicleDao;
import com.yearup.dealership.models.LeaseContract;
import com.yearup.dealership.models.SalesContract;
import com.yearup.dealership.models.Vehicle;
import org.apache.commons.dbcp2.BasicDataSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        String username = args[0];
        String password = args[1];

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/car_dealership");
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        VehicleDao vehicleDao = new VehicleDao(dataSource);
        InventoryDao inventoryDao = new InventoryDao(dataSource);
        SalesDao salesDao = new SalesDao(dataSource);
        LeaseDao leaseDao = new LeaseDao(dataSource);

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("Main Menu:");
            System.out.println("1. Search vehicles");
            System.out.println("2. Add a vehicle");
            System.out.println("3. Add a contract");
            System.out.println("4. Remove a vehicle");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> searchVehiclesMenu(vehicleDao, scanner);
                case 2 -> addVehicleMenu(vehicleDao, inventoryDao, scanner);
                case 3 -> addContractMenu(salesDao, leaseDao, scanner);
                case 4 -> removeVehicleMenu(vehicleDao, inventoryDao, scanner);
                case 5 -> exit = true;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }

    private static void addContractMenu(SalesDao salesDao, LeaseDao leaseDao, Scanner scanner) {
        System.out.print("Enter the VIN of the vehicle to add a contract: ");
        String vin = scanner.nextLine();

        System.out.println("\nSelect a contract type:");
        System.out.println("1. Sales Contract");
        System.out.println("2. Lease Contract");
        System.out.print("Enter your choice: ");

        int contractTypeChoice = scanner.nextInt();
        scanner.nextLine();

        switch (contractTypeChoice) {
            case 1 -> addSalesContract(salesDao, vin, scanner);
            case 2 -> addLeaseContract(leaseDao, vin, scanner);
            default -> System.out.println("Invalid choice. Contract not added.");
        }
    }

    private static void addSalesContract(SalesDao salesDao, String vin, Scanner scanner) {
        System.out.print("Enter the sale date (YYYY-MM-DD): ");
        LocalDate saleDate = LocalDate.parse(scanner.nextLine());

        System.out.print("Enter the price: ");
        double price = scanner.nextDouble();
        scanner.nextLine();

        SalesContract contract = new SalesContract(vin, saleDate, price);
        salesDao.addSalesContract(contract);
        System.out.println("Sales contract added successfully.");
    }

    private static void addLeaseContract(LeaseDao leaseDao, String vin, Scanner scanner) {
        System.out.print("Enter the lease start date (YYYY-MM-DD): ");
        LocalDate start = LocalDate.parse(scanner.nextLine());

        System.out.print("Enter the lease end date (YYYY-MM-DD): ");
        LocalDate end = LocalDate.parse(scanner.nextLine());

        System.out.print("Enter the monthly payment: ");
        double monthlyPayment = scanner.nextDouble();
        scanner.nextLine();

        LeaseContract contract = new LeaseContract(vin, start, end, monthlyPayment);
        leaseDao.addLeaseContract(contract);
        System.out.println("Lease contract added successfully.");
    }

    private static void searchVehiclesMenu(VehicleDao vehicleDao, Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\nSearch Vehicles:");
            System.out.println("1. By price range");
            System.out.println("2. By make/model");
            System.out.println("3. By year range");
            System.out.println("4. By color");
            System.out.println("5. By mileage range");
            System.out.println("6. By type");
            System.out.println("7. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> searchByPriceRange(vehicleDao, scanner);
                case 2 -> searchByMakeAndModel(vehicleDao, scanner);
                case 3 -> searchByYearRange(vehicleDao, scanner);
                case 4 -> searchByColor(vehicleDao, scanner);
                case 5 -> searchByMileageRange(vehicleDao, scanner);
                case 6 -> searchByType(vehicleDao, scanner);
                case 7 -> back = true;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void searchByPriceRange(VehicleDao dao, Scanner sc) {
        System.out.print("Enter the minimum price: ");
        double min = sc.nextDouble();
        System.out.print("Enter the maximum price: ");
        double max = sc.nextDouble();
        sc.nextLine();
        displaySearchResults(dao.searchByPriceRange(min, max));
    }

    private static void searchByMakeAndModel(VehicleDao dao, Scanner sc) {
        System.out.print("Enter the make: ");
        String make = sc.nextLine();
        System.out.print("Enter the model: ");
        String model = sc.nextLine();
        displaySearchResults(dao.searchByMakeModel(make, model));
    }

    private static void searchByYearRange(VehicleDao dao, Scanner sc) {
        System.out.print("Enter the minimum year: ");
        int min = sc.nextInt();
        System.out.print("Enter the maximum year: ");
        int max = sc.nextInt();
        sc.nextLine();
        displaySearchResults(dao.searchByYearRange(min, max));
    }

    private static void searchByColor(VehicleDao dao, Scanner sc) {
        System.out.print("Enter the color: ");
        String color = sc.nextLine();
        displaySearchResults(dao.searchByColor(color));
    }

    private static void searchByMileageRange(VehicleDao dao, Scanner sc) {
        System.out.print("Enter the minimum mileage: ");
        int min = sc.nextInt();
        System.out.print("Enter the maximum mileage: ");
        int max = sc.nextInt();
        sc.nextLine();
        displaySearchResults(dao.searchByMileageRange(min, max));
    }

    private static void searchByType(VehicleDao dao, Scanner sc) {
        System.out.print("Enter the vehicle type: ");
        String type = sc.nextLine();
        displaySearchResults(dao.searchByType(type));
    }

    private static void displaySearchResults(List<Vehicle> vehicles) {
        if (vehicles.isEmpty()) {
            System.out.println("No vehicles found.");
        } else {
            System.out.println("\nSearch Results:");
            vehicles.forEach(System.out::println);
        }
    }

    private static void addVehicleMenu(VehicleDao vehicleDao, InventoryDao inventoryDao, Scanner scanner) {
        String vin = generateRandomVin();

        System.out.print("Enter the make: ");
        String make = scanner.nextLine();
        System.out.print("Enter the model: ");
        String model = scanner.nextLine();
        System.out.print("Enter the year: ");
        int year = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter the color: ");
        String color = scanner.nextLine();
        System.out.print("Enter the mileage: ");
        int mileage = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter the price: ");
        double price = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter the type: ");
        String type = scanner.nextLine();
        System.out.print("Enter the dealership ID: ");
        int dealershipId = scanner.nextInt();
        scanner.nextLine();

        Vehicle vehicle = new Vehicle(vin, make, model, year, false, color, type, mileage, price);
        vehicleDao.addVehicle(vehicle);
        inventoryDao.addVehicleToInventory(vin, dealershipId);
        System.out.println("Vehicle added successfully.");
    }

    private static void removeVehicleMenu(VehicleDao dao, InventoryDao inventoryDao, Scanner sc) {
        System.out.print("Enter the VIN of the vehicle to remove: ");
        String vin = sc.nextLine();
        inventoryDao.removeVehicleFromInventory(vin);
        dao.removeVehicle(vin);
        System.out.println("Vehicle removed successfully.");
    }

    public static String generateRandomVin() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 17).toUpperCase();
    }
}