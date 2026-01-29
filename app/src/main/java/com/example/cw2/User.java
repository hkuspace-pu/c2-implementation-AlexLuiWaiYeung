package com.example.cw2;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String username;
    private String email;
    private String phone;
    private String fullName;
    private boolean isStaff;
    private boolean notificationsEnabled;

    public User(String username, String email, String phone, String fullName,
                boolean isStaff, boolean notificationsEnabled) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.fullName = fullName;
        this.isStaff = isStaff;
        this.notificationsEnabled = notificationsEnabled;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public boolean isStaff() { return isStaff; }
    public void setStaff(boolean staff) { isStaff = staff; }

    public boolean isNotificationsEnabled() { return notificationsEnabled; }
    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    // Parcelable implementation
    protected User(Parcel in) {
        username = in.readString();
        email = in.readString();
        phone = in.readString();
        fullName = in.readString();
        isStaff = in.readByte() != 0;
        notificationsEnabled = in.readByte() != 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(fullName);
        dest.writeByte((byte) (isStaff ? 1 : 0));
        dest.writeByte((byte) (notificationsEnabled ? 1 : 0));
    }
}