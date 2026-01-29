package com.example.cw2;

import com.example.cw2.UserResponse;
import com.example.cw2.UsersListResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @POST("create_user/student_20192089")
    Call<UserResponse> createUser(@Body UserResponse user);

    @GET("read_all_users/student_20192089")
    Call<UsersListResponse> getAllUsers();

    @GET("read_user/student_20192089/{user_id}")
    Call<UserResponse> getUserById(@Path("user_id") String userId);

    @PUT("update_user/student_20192089/{user_id}")
    Call<UserResponse> updateUser(@Path("user_id") String userId, @Body UserResponse user);
}