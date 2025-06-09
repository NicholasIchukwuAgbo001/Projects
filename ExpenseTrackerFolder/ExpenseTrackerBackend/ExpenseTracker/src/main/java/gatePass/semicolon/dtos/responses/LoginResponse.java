package gatePass.semicolon.dtos.responses;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponse {
    private boolean success;
    private String message;
    private UserData data;

    @Setter
    @Getter
    public static class UserData {
        // Getters and Setters
        private Long id;
        private String email;
        private String name;

        @Override
        public String toString() {
            return "UserData{" +
                    "id=" + id +
                    ", email='" + email + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
