package core.models;

public class ResponceBooking {
    private int bookingid;
    private NewBooking booking;

    public int getBookingid() {
        return bookingid;
    }

    public void setBookingid(int bookingid) {
        this.bookingid = bookingid;
    }

    public NewBooking getBooking() {
        return booking;
    }

    public void setBooking(NewBooking booking) {
        this.booking = booking;
    }
}
