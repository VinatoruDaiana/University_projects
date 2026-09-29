package bll;
import dao.BillDAO;
import model.Bill;

import java.sql.SQLException;
import java.util.List;
public class BillBLL {

    private static BillDAO billDAO = new BillDAO();

    public static int insertBill(Bill bill) throws SQLException {
        return billDAO.insertBill(bill);
    }

    public static List<Bill> listAllBills() throws SQLException {
        return billDAO.listAllBills();
    }
}
