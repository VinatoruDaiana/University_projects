package Control;

import View.Hotel;
import View.Room;

import java.util.List;
import java.util.Date;

public class ReservationRoom {


    // Metoda pentru a afișa camerele disponibile ale unui hotel dat
    public void displayAvailableRooms(Hotel hotel) {
        List<Room> rooms = hotel.getRooms();

        if (rooms.isEmpty()) {
            System.out.println("Hotelul nu are camere disponibile.");
        } else {
            System.out.println("Camerele disponibile ale hotelului " + hotel.getNameHotel() + " sunt:");
            for (Room room : rooms) {
                if (room.isAvailable()) {
                    System.out.println("Camera " + room.getRoomNumber() + ": Tip " + room.getRoomType() + ", Preț: " + room.getPrice());
                }
            }
        }
    }

    // Metoda pentru a rezerva camerele disponibile ale unui hotel dat
    public void reserveRooms(Hotel hotel, List<Integer> roomNumbersToReserve) {
        List<Room> rooms = hotel.getRooms();
        boolean allRoomsAvailable = true;

        // Verificăm dacă toate camerele selectate sunt disponibile
        for (int roomNumber : roomNumbersToReserve) {
            boolean roomAvailable = false;
            for (Room room : rooms) {
                if (room.getRoomNumber() == roomNumber && room.isAvailable()) {
                    roomAvailable = true;
                    break;
                }
            }
            if (!roomAvailable) {
                allRoomsAvailable = false;
                System.out.println("Camera " + roomNumber + " nu este disponibilă.");
            }
        }

        // Dacă toate camerele selectate sunt disponibile, le rezervăm
        if (allRoomsAvailable) {
            for (int roomNumber : roomNumbersToReserve) {
                for (Room room : rooms) {
                    if (room.getRoomNumber() == roomNumber) {
                        room.setAvailable(false);
                        System.out.println("Camera " + roomNumber + " a fost rezervată cu succes.");
                        break;
                    }
                }
            }
        } else {
            System.out.println("Rezervarea nu a putut fi finalizată. Unele camere nu sunt disponibile.");
        }
    }


    // Metoda pentru a anula o rezervare
    public void cancelReservation(Hotel hotel, int roomNumber) {
        List<Room> rooms = hotel.getRooms();

        for (Room room : rooms) {
            if (room.getRoomNumber() == roomNumber && !room.isAvailable()) {
                room.setAvailable(true);
                System.out.println("Rezervarea camerei " + roomNumber + " a fost anulată cu succes.");
                return;
            }
        }

        System.out.println("Camera " + roomNumber + " nu a fost găsită sau nu este rezervată.");
    }

    // Metoda pentru a schimba camera rezervată
    public void changeReservedRoom(Hotel hotel, int oldRoomNumber, int newRoomNumber, Date checkInDate) {
        // Verificăm dacă este cu cel puțin două ore înainte de check-in
        long currentTime = new Date().getTime();
        long checkInTime = checkInDate.getTime();
        long twoHoursInMillis = 2 * 60 * 60 * 1000; // două ore în milisecunde
        if (checkInTime - currentTime < twoHoursInMillis) {
            System.out.println("Nu puteți schimba camera rezervată cu mai puțin de două ore înainte de check-in.");
            return;
        }

        // Anulăm rezervarea camerei vechi și rezervăm camera nouă
        cancelReservation(hotel, oldRoomNumber);
        List<Room> rooms = hotel.getRooms();
        for (Room room : rooms) {
            if (room.getRoomNumber() == newRoomNumber && room.isAvailable()) {
                room.setAvailable(false);
                System.out.println("Camera " + newRoomNumber + " a fost rezervată în locul camerei " + oldRoomNumber + ".");
                return;
            }
        }

        // Afisăm un mesaj dacă camera nouă nu este disponibilă
        System.out.println("Camera " + newRoomNumber + " nu este disponibilă.");
    }
}
