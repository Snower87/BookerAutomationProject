package core.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ArrayBooking {
    private Booking[] bookings;

    public ArrayBooking() {
    }

    @JsonCreator
    //public ArrayBooking(Booking[] bookings) {
    public ArrayBooking(@JsonProperty("bookings") Booking[] bookings) {
        this.bookings = bookings;
    }

    public Booking[] getBookings() {
        return bookings;
    }

    public void setBookings(Booking[] bookings) {
        this.bookings = bookings;
    }
}
