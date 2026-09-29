package dao;


import connection.ConnectionFactory;
import model.Bill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
public class BillDAO {

    protected static final Logger LOGGER = Logger.getLogger(BillDAO.class.getName());

    public int insertBill(Bill bill) throws SQLException {

        String insertStatementString = "INSERT INTO Log (client_name, product_name, quantity,price) VALUES (?, ?, ?)";

        try (Connection dbConnection = ConnectionFactory.getConnection();
             PreparedStatement insertStatement = dbConnection.prepareStatement(insertStatementString)) {

            insertStatement.setString(1, bill.client_id());
            insertStatement.setString(2, bill.product_id());
            insertStatement.setInt(3, bill.quantity());
            insertStatement.setDouble(3, bill.price());

            insertStatement.executeUpdate();
        }
        return 0;
    }

    public List<Bill> listAllBills() throws SQLException {


        String listStatementString = "SELECT client_name, product_name, quantity,price FROM Log";
        List<Bill> billList = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement listStatement = connection.prepareStatement(listStatementString);
             ResultSet resultSet = listStatement.executeQuery()) {

            while (resultSet.next()) {
                String client_name = resultSet.getString("ClientName");
                String product_name = resultSet.getString("ProductName");
                int quantity = resultSet.getInt("Quantity");
                double price = resultSet.getDouble("Price");

                Bill bill = new Bill(client_name, product_name, quantity,price);

                billList.add(bill);
            }
        }

        return billList;
    }
}


