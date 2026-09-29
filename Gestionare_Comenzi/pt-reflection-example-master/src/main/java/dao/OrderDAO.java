package dao;
import model.Order;

import javax.swing.*;
import java.util.List;
public class OrderDAO  extends AbstractDAO<Order>{

    public OrderDAO() {
        super();
    }

    @Override
    public Order findById(int id) {
        return super.findById(id);
    }

    @Override
    public List<Order> findAll() {
        return super.findAll();
    }

    @Override
    public int insert(Order order) {
        return super.insert(order);
    }

    @Override
    public int delete(int id_order) {
        return super.delete(id_order);
    }

    @Override
    public int update(Order order) {
        return super.update(order);
    }

    @Override
    public void generateTable(JTable table, List<Order> o) {
        super.generateTable(table, o);
    }
}
