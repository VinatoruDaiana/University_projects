package model;

public record Bill(String client_id, String product_id, int quantity, double price) {
}
