package com.example.cw2;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Reservation implements Parcelable {
    private int id;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private int numberOfPeople;
    private Date reservationDate;
    private String reservationTime;
    private String specialRequests;
    private String status; // "confirmed", "pending", "cancelled"

    // Constructor
    public Reservation(int id, String customerName, String customerPhone, String customerEmail,
                       int numberOfPeople, Date reservationDate, String reservationTime,
                       String specialRequests, String status) {
        this.id = id;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.numberOfPeople = numberOfPeople;
        this.reservationDate = reservationDate;
        this.reservationTime = reservationTime;
        this.specialRequests = specialRequests;
        this.status = status;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public int getNumberOfPeople() { return numberOfPeople; }
    public void setNumberOfPeople(int numberOfPeople) { this.numberOfPeople = numberOfPeople; }

    public Date getReservationDate() { return reservationDate; }
    public void setReservationDate(Date reservationDate) { this.reservationDate = reservationDate; }

    public String getReservationTime() { return reservationTime; }
    public void setReservationTime(String reservationTime) { this.reservationTime = reservationTime; }

    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Helper methods
    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault());
        return reservationDate != null ? sdf.format(reservationDate) : "N/A";
    }

    public String getFormattedTime() {
        return reservationTime != null ? reservationTime : "N/A";
    }

    // Parcelable implementation
    protected Reservation(Parcel in) {
        id = in.readInt();
        customerName = in.readString();
        customerPhone = in.readString();
        customerEmail = in.readString();
        numberOfPeople = in.readInt();
        reservationDate = new Date(in.readLong());
        reservationTime = in.readString();
        specialRequests = in.readString();
        status = in.readString();
    }

    public static final Creator<Reservation> CREATOR = new Creator<Reservation>() {
        @Override
        public Reservation createFromParcel(Parcel in) {
            return new Reservation(in);
        }

        @Override
        public Reservation[] newArray(int size) {
            return new Reservation[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(customerName);
        dest.writeString(customerPhone);
        dest.writeString(customerEmail);
        dest.writeInt(numberOfPeople);
        dest.writeLong(reservationDate != null ? reservationDate.getTime() : 0);
        dest.writeString(reservationTime);
        dest.writeString(specialRequests);
        dest.writeString(status);
    }
    public String getSimpleDetailsForGuest() {
        return "Name: " + customerName + "\n" +
                "Phone: " + customerPhone + "\n" +
                "Date: " + getFormattedDate() + "\n" +
                "Time: " + reservationTime + "\n" +
                "Party: " + numberOfPeople + " people\n" +
                "Status: " + status.toUpperCase();
    }

    public String getFullDetailsForStaff() {
        return "Customer: " + customerName + "\n" +
                "Phone: " + customerPhone + "\n" +
                "Email: " + customerEmail + "\n" +
                "Date: " + getFormattedDate() + "\n" +
                "Time: " + reservationTime + "\n" +
                "Party Size: " + numberOfPeople + " people\n" +
                "Status: " + status.toUpperCase() + "\n" +
                "Special Requests: " +
                (specialRequests.isEmpty() ? "None" : specialRequests);
    }
}
