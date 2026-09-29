package View;

import java.util.ArrayList;
import java.util.List;

public class Hotel {
    private int idHotel;
    private String nameHotel;
    private double latitude;
    private double longitude;
    private List<Room> rooms;

    // Constructor
    public Hotel(int idHotel, String nameHotel, double latitude, double longitude, List<Room> rooms) {
        this.idHotel = idHotel;
        this.nameHotel = nameHotel;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rooms = new ArrayList<>();
    }

    // Metoda pentru adăugarea unei camere în hotel
    public void addRoom(Room room) {
        rooms.add(room);
    }

    // Metoda pentru a obține lista de camere din hotel
    public List<Room> getRooms() {
        return rooms;
    }

    // Getters și Setters pentru celelalte atribute
    public int getIdHotel() {
        return idHotel;
    }

    public void setIdHotel(int idHotel) {
        this.idHotel = idHotel;
    }

    public String getNameHotel() {
        return nameHotel;
    }

    public void setNameHotel(String nameHotel) {
        this.nameHotel = nameHotel;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}