package com.example.cw2;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    public interface AuthCallback {
        void onSuccess(User user);
        void onError(String errorMessage);
    }

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    private final Context context;
    private final ApiService apiService;
    private final SharedPreferences sharedPreferences;

    public AuthRepository(Context context) {
        this.context = context.getApplicationContext();
        this.sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        this.apiService = ApiClient.getInstance().getApiService();
    }

    // ==================== AUTHENTICATION ====================

    public void login(String username, String password, boolean isStaff, AuthCallback callback) {
        Log.d("Auth", "Attempting login for: " + username);

        // Fetch all users and authenticate locally
        getAllUsers(new ApiCallback<List<UserResponse>>() {
            @Override
            public void onSuccess(List<UserResponse> users) {
                authenticateLocally(users, username, password, isStaff, callback);
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    private void authenticateLocally(List<UserResponse> users, String username,
                                     String password, boolean isStaff, AuthCallback callback) {
        Log.d("Auth", "Authenticating locally with " + users.size() + " users");

        for (UserResponse userResponse : users) {
            Log.d("Auth", "Checking user: " + userResponse.getUsername() +
                    ", Type: " + userResponse.getUserType());

            if (userResponse.getUsername().equals(username) &&
                    userResponse.getPassword().equals(password)) {

                Log.d("Auth", "Credentials match for: " + username);

                // Check if user type matches selection
                boolean userIsStaff = userResponse.isStaff();

                if ((isStaff && userIsStaff) || (!isStaff && userResponse.isGuest())) {
                    Log.d("Auth", "User type matches selection");

                    // Save session
                    saveUserSession(userResponse);

                    // Convert to User model
                    User user = convertToUser(userResponse);

                    callback.onSuccess(user);
                    return;
                } else {
                    Log.d("Auth", "User type mismatch. Expected staff: " + isStaff +
                            ", Actual is staff: " + userIsStaff);
                    callback.onError("Please select correct user type");
                    return;
                }
            }
        }

        // No matching user found
        Log.d("Auth", "No matching user found");
        callback.onError("Invalid username or password");
    }

    // ==================== USER MANAGEMENT ====================

    public void getAllUsers(ApiCallback<List<UserResponse>> callback) {
        Call<UsersListResponse> call = apiService.getAllUsers();

        call.enqueue(new Callback<UsersListResponse>() {
            @Override
            public void onResponse(Call<UsersListResponse> call, Response<UsersListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UserResponse> users = response.body().getUsers();
                    Log.d("API", "Successfully fetched " + users.size() + " users");
                    callback.onSuccess(users);
                } else {
                    String error = "Failed to fetch users: " + response.code();
                    Log.e("API", error);
                    callback.onError(error);
                }
            }

            @Override
            public void onFailure(Call<UsersListResponse> call, Throwable t) {
                String error = "Network error: " + t.getMessage();
                Log.e("API", error);
                callback.onError(error);
            }
        });
    }

    public void createUser(UserResponse user, ApiCallback<UserResponse> callback) {
        Call<UserResponse> call = apiService.createUser(user);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API", "User created: " + response.body().getUsername());
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to create user: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void updateUser(String userId, UserResponse user, ApiCallback<UserResponse> callback) {
        Call<UserResponse> call = apiService.updateUser(userId, user);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API", "User updated: " + response.body().getUsername());
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to update user: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getUserById(String userId, ApiCallback<UserResponse> callback) {
        Call<UserResponse> call = apiService.getUserById(userId);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to get user: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // ==================== SESSION MANAGEMENT ====================

    private void saveUserSession(UserResponse userResponse) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_id", userResponse.getId());
        editor.putString("username", userResponse.getUsername());
        editor.putString("email", userResponse.getEmail());
        editor.putString("contact", userResponse.getContact());
        editor.putString("first_name", userResponse.getFirstName());
        editor.putString("last_name", userResponse.getLastName());
        editor.putString("user_type", userResponse.getUserType());
        editor.putBoolean("is_staff", userResponse.isStaff());
        editor.putBoolean("is_logged_in", true);
        editor.apply();

        Log.d("Auth", "Session saved for: " + userResponse.getUsername());
    }

    private User convertToUser(UserResponse userResponse) {
        return new User(
                userResponse.getUsername(),
                userResponse.getEmail(),
                userResponse.getContact(),
                userResponse.getFullName(),
                userResponse.isStaff(),
                true
        );
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean("is_logged_in", false);
    }

    public User getCurrentUser() {
        if (!isLoggedIn()) {
            Log.d("Auth", "No user logged in");
            return null;
        }

        User user = new User(
                sharedPreferences.getString("username", ""),
                sharedPreferences.getString("email", ""),
                sharedPreferences.getString("contact", ""),
                sharedPreferences.getString("first_name", "") + " " +
                        sharedPreferences.getString("last_name", ""),
                sharedPreferences.getBoolean("is_staff", false),
                true
        );

        Log.d("Auth", "Retrieved logged in user: " + user.getUsername());
        return user;
    }

    public String getCurrentUserId() {
        return sharedPreferences.getString("user_id", "");
    }

    public void logout() {
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();  // Clear all saved data
        editor.apply();
    }

    // ==================== DEMO MODE ====================

    private User createDemoUser(String username, boolean isStaff) {
        return new User(
                username,
                username + "@demo.com",
                "555-0000",
                "Demo User",
                isStaff,
                true
        );
    }

    public void demoLogin(String username, String password, boolean isStaff, AuthCallback callback) {
        if (username.equals("admin") && password.equals("123456")) {
            User demoUser = createDemoUser(username, isStaff);
            callback.onSuccess(demoUser);
        } else {
            callback.onError("Demo credentials: admin / 123456");
        }
    }

}