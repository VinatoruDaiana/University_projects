package bll;

import dao.OrderDAO;
import model.Order;
import java.util.List;
import java.util.NoSuchElementException;

public class OrderBLL {


    private static OrderDAO OrderDAO = new OrderDAO();

    public static Order findOrderById(int id) {
       Order o = OrderDAO.findById(id);
        if (o == null) {
            throw new NoSuchElementException("The product order with id =" + id + " was not found!");
        }
        return o;
    }

    public static int deleteOrder(int id) {
        return OrderDAO.delete(id);
    }

    public static int insertOrder(Order o) {

        return OrderDAO.insert(o);
    }
    public static int updateOrder(Order o) {
        return OrderDAO.update(o);
    }

    public static List<Order> findAllOrders() {
        return OrderDAO.findAll();
    }
}
