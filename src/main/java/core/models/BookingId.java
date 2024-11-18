package core.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BookingId {
    private String firstname;
    private String lastname;
    private int totalprice;
    private boolean depositpaid;
    private CheckDate bookingdates;
    private String additionalneeds;

    //Конструктор
    @JsonCreator
    public BookingId(@JsonProperty("firstname") String firstname,
                     @JsonProperty("lastname") String lastname,
                     @JsonProperty("totalprice") int totalprice,
                     @JsonProperty("depositpaid") boolean depositpaid,
                     @JsonProperty("bookingdates") CheckDate bookingdates,
                     @JsonProperty("additionalneeds") String additionalneeds) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.totalprice = totalprice;
        this.depositpaid = depositpaid;
        this.bookingdates = bookingdates;
        this.additionalneeds = additionalneeds;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(int totalprice) {
        this.totalprice = totalprice;
    }

    public boolean isDepositpaid() {
        return depositpaid;
    }

    public void setDepositpaid(boolean depositpaid) {
        this.depositpaid = depositpaid;
    }

    public CheckDate getBookingdates() {
        return bookingdates;
    }

    public void setBookingdates(CheckDate bookingdates) {
        this.bookingdates = bookingdates;
    }

    public String getAdditionalneeds() {
        return additionalneeds;
    }

    public void setAdditionalneeds(String additionalneeds) {
        this.additionalneeds = additionalneeds;
    }
}
