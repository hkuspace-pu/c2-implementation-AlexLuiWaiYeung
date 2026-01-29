package com.example.cw2;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UsersListResponse {

    @SerializedName("users")
    private List<UserResponse> users;

    public List<UserResponse> getUsers() {
        return users;
    }

    public void setUsers(List<UserResponse> users) {
        this.users = users;
    }
}