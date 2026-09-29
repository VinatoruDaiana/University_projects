package model;

public class Order {

    private int id_order;
    private int id_client;
    private int id_product;
    private String product_name;
    private String client_name;
    private int price;
    private int quantity;


    public Order(){

    }

    public Order(int id_order,int client,int produs,int quantity){

        super();
        this.id_order=id_order;
        this.id_client=client;
        this.id_product=produs;
        this.quantity=quantity;

    }


    public Order(int client,int produs,int quantity){

        super();

        this.id_client=client;
        this.id_product=produs;
        this.quantity=quantity;

    }




    public int getId_order() {
        return id_order;
    }

    public void setId_order(int id_order) {
        this.id_order = id_order;
    }

    public int getId_client() {
        return id_client;
    }

    public void setId_client(int id_client) {
        this.id_client = id_client;
    }

    public int getId_product() {
        return id_product;
    }

    public void setId_product(int id_product) {
        this.id_product = id_product;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Product [id=" + id_order + ", client=" + client_name + ", product=" + product_name + ", quantity=" + quantity + ", price=" + price
                + "]";
    }
}
