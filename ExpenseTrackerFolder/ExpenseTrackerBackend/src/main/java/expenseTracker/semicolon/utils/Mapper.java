package expenseTracker.semicolon.utils;

import expenseTracker.semicolon.data.models.User;
import expenseTracker.semicolon.dtos.requests.UserSignupRequest;

public class Mapper {

    public static User map(UserSignupRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(PasswordUtils.hashPassword(request.getPassword()));
        user.setAge(request.getAge());
        return user;
    }
}
