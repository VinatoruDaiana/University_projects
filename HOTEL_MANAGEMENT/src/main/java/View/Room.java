package View;

public class Room {
    private int roomNumber;
    private RoomType roomType; // Tipul camerei: SINGLE, DOUBLE, SUITE, MATRIMONIAL
    private int price;
    private boolean isAvailable;

    // Constructor
    public Room(int roomNumber, RoomType roomType, int price, boolean isAvailable) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.price = price;
        this.isAvailable = isAvailable;
    }

    // Getters și setters
    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomNumber=" + roomNumber +
                ", roomType=" + roomType +
                ", price=" + price +
                ", isAvailable=" + isAvailable +
                '}';
    }
}