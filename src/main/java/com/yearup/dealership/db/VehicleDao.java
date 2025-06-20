package com.yearup.dealership.db;

import com.yearup.dealership.models.Vehicle;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class VehicleDao {
    private DataSource dataSource;

    public VehicleDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void addVehicle(Vehicle vehicle) {
        String sql = "INSERT INTO vehicles (vin, make, model, year, sold, color, vehicleType, odometer, price) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, vehicle.getVin());
            stmt.setString(2, vehicle.getMake());
            stmt.setString(3, vehicle.getModel());
            stmt.setInt(4, vehicle.getYear());
            stmt.setBoolean(5, vehicle.isSold());
            stmt.setString(6, vehicle.getColor());
            stmt.setString(7, vehicle.getVehicleType());
            stmt.setInt(8, vehicle.getOdometer());
            stmt.setDouble(9, vehicle.getPrice());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeVehicle(String vin) {
        String sql = "DELETE FROM vehicles WHERE vin = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, vin);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Vehicle> searchByPriceRange(double min, double max) {
        return search("SELECT * FROM vehicles WHERE price BETWEEN ? AND ?", stmt -> {
            stmt.setDouble(1, min);
            stmt.setDouble(2, max);
        });
    }

    public List<Vehicle> searchByMakeModel(String make, String model) {
        return search("SELECT * FROM vehicles WHERE make = ? AND model = ?", stmt -> {
            stmt.setString(1, make);
            stmt.setString(2, model);
        });
    }

    public List<Vehicle> searchByYearRange(int minYear, int maxYear) {
        return search("SELECT * FROM vehicles WHERE year BETWEEN ? AND ?", stmt -> {
            stmt.setInt(1, minYear);
            stmt.setInt(2, maxYear);
        });
    }

    public List<Vehicle> searchByColor(String color) {
        return search("SELECT * FROM vehicles WHERE color = ?", stmt -> stmt.setString(1, color));
    }

    public List<Vehicle> searchByMileageRange(int min, int max) {
        return search("SELECT * FROM vehicles WHERE odometer BETWEEN ? AND ?", stmt -> {
            stmt.setInt(1, min);
            stmt.setInt(2, max);
        });
    }

    public List<Vehicle> searchByType(String type) {
        return search("SELECT * FROM vehicles WHERE vehicleType = ?", stmt -> stmt.setString(1, type));
    }

    private List<Vehicle> search(String sql, SQLConsumer<PreparedStatement> paramSetter) {
        List<Vehicle> results = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            paramSetter.accept(stmt);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(createVehicleFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    private Vehicle createVehicleFromResultSet(ResultSet rs) throws SQLException {
        return new Vehicle(
                rs.getString("vin"),
                rs.getString("make"),
                rs.getString("model"),
                rs.getInt("year"),
                rs.getBoolean("sold"),
                rs.getString("color"),
                rs.getString("vehicleType"),
                rs.getInt("odometer"),
                rs.getDouble("price")
        );
    }

    private interface SQLConsumer<T> {
        void accept(T t) throws SQLException;
    }
}
