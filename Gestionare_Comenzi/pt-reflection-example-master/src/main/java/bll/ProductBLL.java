package bll;

import dao.ProductDAO;
import model.Product;

import java.util.List;
import java.util.NoSuchElementException;
public class ProductBLL {

    private static ProductDAO productDAO = new ProductDAO();


    public static Product findProductById(int id) {
        Product p = productDAO.findById(id);
        if (p == null) {
            throw new NoSuchElementException("The product with id =" + id + " was not found!");
        }
        return p;
    }

    public static int insertProduct(Product p) {
        return productDAO.insert(p);
    }


    public static int deleteProduct(int id) {
        return productDAO.delete(id);
    }

    public static int updateProduct(Product product) {
        return productDAO.update(product);
    }

    public static List<Product> findAllProducts() {
        return productDAO.findAll();
    }

}
