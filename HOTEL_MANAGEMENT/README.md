# Hotel Management System

A Java university project that models hotel rooms, reservations, feedback and nearby-hotel search functionality.

## Description

The application contains a set of Java classes for managing core hotel-related entities and operations.

The project supports:

- representing hotels and their rooms;
- searching for nearby hotels based on geographic coordinates;
- calculating distances between locations using the Haversine formula;
- displaying available rooms;
- reserving one or more rooms;
- cancelling an existing room reservation;
- changing a reserved room when the check-in time allows it;
- representing reservation information;
- storing hotel feedback and ratings.

## Technologies

- Java 21
- Maven
- Java Collections Framework
- IntelliJ IDEA

## Project Structure

```text
HOTEL_MANAGEMENT/
│
├── src/
│   └── main/
│       └── java/
│           ├── Control/
│           │   ├── HotelFinder.java
│           │   ├── ReadFromFile.java
│           │   └── ReservationRoom.java
│           │
│           └── View/
│               ├── Feedback.java
│               ├── Hotel.java
│               ├── Reservation.java
│               ├── Room.java
│               └── RoomType.java
│
├── pom.xml
├── .gitignore
└── README.md
```

## Main Components

### `Hotel`

Represents a hotel and stores:

- hotel ID;
- hotel name;
- latitude;
- longitude;
- a list of rooms.

Rooms can be added to a hotel using the `addRoom()` method.

### `Room`

Represents a hotel room and stores:

- room number;
- room type;
- price;
- availability status.

### `RoomType`

Defines the available room categories:

```java
SINGLE
DOUBLE
SUITE
MATRIMONIAL
```

### `Reservation`

Represents reservation information, including:

- reservation ID;
- room ID;
- check-in date;
- check-out date.

### `Feedback`

Represents feedback left for a hotel and stores:

- feedback ID;
- hotel ID;
- comment;
- rating.

### `HotelFinder`

Provides functionality for locating nearby hotels.

The method:

```java
findHotelsNearby(double userLatitude, double userLongitude, double radiusKm)
```

returns the hotels located within the specified radius.

Distances are calculated using the Haversine formula.

Example:

```java
HotelFinder finder = new HotelFinder(hotels);

List<Hotel> nearbyHotels =
        finder.findHotelsNearby(46.7523, 23.6060, 10);
```

This searches for hotels within a 10 km radius of the provided coordinates.

### `ReservationRoom`

Contains the room reservation logic.

Available rooms can be displayed using:

```java
reservationRoom.displayAvailableRooms(hotel);
```

Rooms can be reserved using:

```java
reservationRoom.reserveRooms(
        hotel,
        List.of(101, 102)
);
```

A reservation can be cancelled using:

```java
reservationRoom.cancelReservation(hotel, 101);
```

A reserved room can also be changed:

```java
reservationRoom.changeReservedRoom(
        hotel,
        101,
        103,
        checkInDate
);
```

The application checks that the room change is requested at least two hours before check-in.

## Example

A hotel and several rooms can be created as follows:

```java
Hotel hotel = new Hotel(
        1,
        "Example Hotel",
        46.7523,
        23.6060,
        new ArrayList<>()
);

hotel.addRoom(
        new Room(
                101,
                RoomType.SINGLE,
                250,
                true
        )
);

hotel.addRoom(
        new Room(
                102,
                RoomType.DOUBLE,
                400,
                true
        )
);
```

The available rooms can then be displayed:

```java
ReservationRoom reservationRoom = new ReservationRoom();

reservationRoom.displayAvailableRooms(hotel);
```

A room can be reserved with:

```java
reservationRoom.reserveRooms(
        hotel,
        List.of(101)
);
```

## Hotel Search Logic

The project calculates the geographic distance between two positions using latitude and longitude.

The Haversine formula is used to determine the distance in kilometers between the user's location and each hotel.

The application can therefore check whether a hotel is located inside a specified search radius.

## How to Use

The project currently contains the domain and control classes for hotel search and reservation management.

To test the functionality:

1. Open the project in IntelliJ IDEA or another Java IDE.
2. Load the Maven project from `pom.xml`.
3. Create a Java class containing a `main` method.
4. Instantiate hotels and rooms.
5. Use `HotelFinder` for nearby-hotel searches.
6. Use `ReservationRoom` for reservation operations.

## Purpose

This project was developed as a university assignment to practice:

- object-oriented programming in Java;
- working with Java collections;
- modeling entities and relationships;
- geographic distance calculations;
- room reservation logic;
- Maven project organization.
