import java.util.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

class Room {
    private int roomNumber;
    private String category;
    private double pricePerNight;
    private boolean isAvailable;

    public Room(int roomNumber, String category, double pricePerNight) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.pricePerNight = pricePerNight;
        this.isAvailable = true;
    }

    // Getters and setters
    public int getRoomNumber() { return roomNumber; }
    public String getCategory() { return category; }
    public double getPricePerNight() { return pricePerNight; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + category + ") - $" + pricePerNight + "/night";
    }
}

class Reservation {
    private static int nextId = 1;
    private int reservationId;
    private Room room;
    private String guestName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double totalPrice;

    public Reservation(Room room, String guestName, LocalDate checkInDate, LocalDate checkOutDate) {
        this.reservationId = nextId++;
        this.room = room;
        this.guestName = guestName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = calculateTotalPrice();
    }

    private double calculateTotalPrice() {
        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        return nights * room.getPricePerNight();
    }

    // Getters
    public int getReservationId() { return reservationId; }
    public Room getRoom() { return room; }
    public String getGuestName() { return guestName; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public double getTotalPrice() { return totalPrice; }

    @Override
    public String toString() {
        return "Reservation " + reservationId + ": " + guestName + " - " + room + 
               " from " + checkInDate + " to " + checkOutDate + 
               " (Total: $" + totalPrice + ")";
    }
}

class Hotel {
    private List<Room> rooms;
    private List<Reservation> reservations;

    public Hotel() {
        rooms = new ArrayList<>();
        reservations = new ArrayList<>();
        initializeRooms();
    }

    private void initializeRooms() {
        rooms.add(new Room(101, "Standard", 100));
        rooms.add(new Room(102, "Standard", 100));
        rooms.add(new Room(201, "Deluxe", 150));
        rooms.add(new Room(202, "Deluxe", 150));
        rooms.add(new Room(301, "Suite", 250));
    }

    public List<Room> searchAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (isRoomAvailable(room, checkIn, checkOut)) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }

    private boolean isRoomAvailable(Room room, LocalDate checkIn, LocalDate checkOut) {
        for (Reservation reservation : reservations) {
            if (reservation.getRoom().getRoomNumber() == room.getRoomNumber()) {
                if ((checkIn.isBefore(reservation.getCheckOutDate()) || checkIn.isEqual(reservation.getCheckOutDate())) &&
                    (checkOut.isAfter(reservation.getCheckInDate()) || checkOut.isEqual(reservation.getCheckInDate()))) {
                    return false;
                }
            }
        }
        return true;
    }

    public Reservation makeReservation(Room room, String guestName, LocalDate checkIn, LocalDate checkOut) {
        if (isRoomAvailable(room, checkIn, checkOut)) {
            Reservation reservation = new Reservation(room, guestName, checkIn, checkOut);
            reservations.add(reservation);
            return reservation;
        }
        return null;
    }

    public Reservation getReservation(int reservationId) {
        for (Reservation reservation : reservations) {
            if (reservation.getReservationId() == reservationId) {
                return reservation;
            }
        }
        return null;
    }

    public boolean cancelReservation(int reservationId) {
        Reservation reservation = getReservation(reservationId);
        if (reservation != null) {
            reservations.remove(reservation);
            return true;
        }
        return false;
    }

    public boolean processPayment(int reservationId, double amount) {
        Reservation reservation = getReservation(reservationId);
        if (reservation != null && amount >= reservation.getTotalPrice()) {
            // In a real system, you would integrate with a payment gateway here
            System.out.println("Payment of $" + amount + " processed successfully for reservation " + reservationId);
            return true;
        }
        return false;
    }
}

public class HotelReservationSystem {
    private static Hotel hotel = new Hotel();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- Hotel Reservation System ---");
            System.out.println("1. Search for available rooms");
            System.out.println("2. Make a reservation");
            System.out.println("3. View reservation details");
            System.out.println("4. Cancel reservation");
            System.out.println("5. Process payment");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    searchRooms();
                    break;
                case 2:
                    makeReservation();
                    break;
                case 3:
                    viewReservation();
                    break;
                case 4:
                    cancelReservation();
                    break;
                case 5:
                    processPayment();
                    break;
                case 6:
                    System.out.println("Thank you for using the Hotel Reservation System. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void searchRooms() {
        System.out.print("Enter check-in date (YYYY-MM-DD): ");
        LocalDate checkIn = LocalDate.parse(scanner.nextLine());
        System.out.print("Enter check-out date (YYYY-MM-DD): ");
        LocalDate checkOut = LocalDate.parse(scanner.nextLine());

        List<Room> availableRooms = hotel.searchAvailableRooms(checkIn, checkOut);
        if (availableRooms.isEmpty()) {
            System.out.println("No rooms available for the selected dates.");
        } else {
            System.out.println("Available rooms:");
            for (Room room : availableRooms) {
                System.out.println(room);
            }
        }
    }

    private static void makeReservation() {
        System.out.print("Enter check-in date (YYYY-MM-DD): ");
        LocalDate checkIn = LocalDate.parse(scanner.nextLine());
        System.out.print("Enter check-out date (YYYY-MM-DD): ");
        LocalDate checkOut = LocalDate.parse(scanner.nextLine());

        List<Room> availableRooms = hotel.searchAvailableRooms(checkIn, checkOut);
        if (availableRooms.isEmpty()) {
            System.out.println("No rooms available for the selected dates.");
            return;
        }

        System.out.println("Available rooms:");
        for (int i = 0; i < availableRooms.size(); i++) {
            System.out.println((i + 1) + ". " + availableRooms.get(i));
        }

        System.out.print("Select a room (enter the number): ");
        int roomChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (roomChoice < 1 || roomChoice > availableRooms.size()) {
            System.out.println("Invalid room selection.");
            return;
        }

        Room selectedRoom = availableRooms.get(roomChoice - 1);

        System.out.print("Enter guest name: ");
        String guestName = scanner.nextLine();

        Reservation reservation = hotel.makeReservation(selectedRoom, guestName, checkIn, checkOut);
        if (reservation != null) {
            System.out.println("Reservation created successfully:");
            System.out.println(reservation);
        } else {
            System.out.println("Failed to create reservation.");
        }
    }

    private static void viewReservation() {
        System.out.print("Enter reservation ID: ");
        int reservationId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Reservation reservation = hotel.getReservation(reservationId);
        if (reservation != null) {
            System.out.println(reservation);
        } else {
            System.out.println("Reservation not found.");
        }
    }

    private static void cancelReservation() {
        System.out.print("Enter reservation ID to cancel: ");
        int reservationId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        boolean cancelled = hotel.cancelReservation(reservationId);
        if (cancelled) {
            System.out.println("Reservation " + reservationId + " has been cancelled.");
        } else {
            System.out.println("Failed to cancel reservation. Please check the reservation ID.");
        }
    }

    private static void processPayment() {
        System.out.print("Enter reservation ID for payment: ");
        int reservationId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Reservation reservation = hotel.getReservation(reservationId);
        if (reservation == null) {
            System.out.println("Reservation not found.");
            return;
        }

        System.out.println("Total amount due: $" + reservation.getTotalPrice());
        System.out.print("Enter payment amount: $");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        boolean paymentProcessed = hotel.processPayment(reservationId, amount);
        if (paymentProcessed) {
            System.out.println("Payment processed successfully.");
        } else {
            System.out.println("Payment processing failed. Please check the amount and try again.");
        }
    }
}