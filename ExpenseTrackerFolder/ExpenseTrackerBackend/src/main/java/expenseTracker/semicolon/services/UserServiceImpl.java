package expenseTracker.semicolon.services;

import expenseTracker.semicolon.Exceptions.UserAlreadyExistsException;
import expenseTracker.semicolon.data.models.User;
import expenseTracker.semicolon.data.repository.UserRepository;
import expenseTracker.semicolon.dtos.requests.UserLoginRequest;
import expenseTracker.semicolon.dtos.requests.UserSignupRequest;
import expenseTracker.semicolon.utils.Mapper;
import expenseTracker.semicolon.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(UserSignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with email already exists");
        }

        User user = Mapper.map(request);
        userRepository.save(user);
    }

    @Override
    public boolean authenticateUser(UserLoginRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        return user.isPresent() &&
                user.get().getPassword().equals(PasswordUtils.hashPassword(request.getPassword()));
    }
}
