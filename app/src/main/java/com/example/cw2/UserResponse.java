package com.example.cw2;

import com.google.gson.annotations.SerializedName;

public class UserResponse {

    @SerializedName("_id")
    private String id;

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("firstname")
    private String firstName;

    @SerializedName("lastname")
    private String lastName;

    @SerializedName("email")
    private String email;

    @SerializedName("contact")
    private String contact;

    @SerializedName("usertype")
    private String userType;

    // Empty constructor for Retrofit
    public UserResponse() {}

    // Constructor for creating new users
    public UserResponse(String username, String password, String firstName,
                        String lastName, String email, String contact, String userType) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.contact = contact;
        this.userType = userType;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isStaff() {
        if (userType == null) return false;
        String type = userType.toLowerCase();
        return type.equals("staff") ||
                type.equals("admin") ||
                type.equals("manager") ||
                type.equals("employee");
    }

    public boolean isGuest() {
        if (userType == null) return true; // Default to guest
        String type = userType.toLowerCase();
        return type.equals("guest") ||
                type.equals("customer") ||
                type.equals("vip") ||
                type.equals("student") ||
                type.equals("regular");
    }
}