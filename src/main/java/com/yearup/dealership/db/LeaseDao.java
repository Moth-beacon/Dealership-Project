package com.yearup.dealership.db;

import com.yearup.dealership.models.LeaseContract;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LeaseDao {
    private DataSource dataSource;

    public LeaseDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void addLeaseContract(LeaseContract leaseContract) {
        String sql = "INSERT INTO lease_contracts (vin, lease_start, lease_end, monthly_payment) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, leaseContract.getVin());
            stmt.setDate(2, java.sql.Date.valueOf(leaseContract.getLeaseStart()));
            stmt.setDate(3, java.sql.Date.valueOf(leaseContract.getLeaseEnd()));
            stmt.setDouble(4, leaseContract.getMonthlyPayment());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}