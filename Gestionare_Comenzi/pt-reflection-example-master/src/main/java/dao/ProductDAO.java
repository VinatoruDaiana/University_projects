package dao;

import model.Product;

import javax.swing.*;
import java.util.List;
public class ProductDAO  extends AbstractDAO<Product> {

    public ProductDAO() {
        super();
    }

    @Override
    public Product findById(int id) {
        return super.findById(id);
    }

    @Override
    public List<Product> findAll() {
        return super.findAll();
    }

    @Override
    public int insert(Product product) {
        return super.insert(product);
    }

    @Override
    public int delete(int id_product) {
        return super.delete(id_product);
    }

    @Override
    public int update(Product product) {
        return super.update(product);
    }

    @Override
    public void generateTable(JTable table, List<Product> products) {
        super.generateTable(table, products);
    }
}
