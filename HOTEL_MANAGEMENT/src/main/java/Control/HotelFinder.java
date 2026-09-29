package Control;
import  View.Hotel;
import java.util.ArrayList;
import java.util.List;


public class HotelFinder {
    private List<Hotel> hotels;

    // Constructor
    public HotelFinder(List<Hotel> hotels) {
        this.hotels = hotels;
    }

    // Metoda pentru a găsi hotelurile din apropiere în funcție de o anumită rază
    public List<Hotel> findHotelsNearby(double userLatitude, double userLongitude, double radiusKm) {
        List<Hotel> nearbyHotels = new ArrayList<>();

        for (Hotel hotel : hotels) {
            double distance = calculateDistance(userLatitude, userLongitude, hotel.getLatitude(), hotel.getLongitude());
            if (distance <= radiusKm) {
                nearbyHotels.add(hotel);
            }
        }

        return nearbyHotels;
    }

    // Metoda pentru a calcula distanța între două puncte geografice folosind formula Haversine
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Raza Pământului în kilometri

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        return distance;
    }

    // Metoda pentru a converti latitudinea și longitudinea din grade în metri
    private double[] convertCoordinatesToMeters(double latitude, double longitude) {
        // Conversia din grade în metri depinde de latitudine, dar pentru o aproximare simplă, putem folosi aproximativ 111 km pentru un grad de latitudine și 111 km pentru un grad de longitudine
        double latMeters = latitude * 111000;
        double lonMeters = longitude * 111000;
        return new double[]{latMeters, lonMeters};
    }

    // Metoda pentru a verifica dacă poziția se află în raza specificată
    public boolean isPositionInRadius(double userLatitude, double userLongitude, double hotelLatitude, double hotelLongitude, double radiusKm) {
        double distance = calculateDistance(userLatitude, userLongitude, hotelLatitude, hotelLongitude);
        return distance <= radiusKm;
    }

    // Metoda pentru a converti toate pozițiile hotelurilor în metri
    public List<Hotel> convertHotelPositionsToMeters() {
        List<Hotel> hotelsInMeters = new ArrayList<>();

        for (Hotel hotel : hotels) {
            double[] meters = convertCoordinatesToMeters(hotel.getLatitude(), hotel.getLongitude());
            hotel.setLatitude(meters[0]);
            hotel.setLongitude(meters[1]);
            hotelsInMeters.add(hotel);
        }

        return hotelsInMeters;
    }


    // Metoda pentru a calcula poziția curentă a utilizatorului
    //Nu stiu sa folosesc API-uri sau biblioteci pt rezolvarea acestei metode
    //asa ca returnez doar o val  care stiu ca se afla in tabelul meu
    public double[] getCurrentUserPosition() {

        return new double[]{46.7523, 23.606};
    }

}
