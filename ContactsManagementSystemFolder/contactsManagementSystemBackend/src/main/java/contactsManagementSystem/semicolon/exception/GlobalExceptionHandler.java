package contactsManagementSystem.semicolon.exception;

import contactsManagementSystem.semicolon.dtos.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return new ResponseEntity<>(
                new ApiResponse(ex.getMessage(), false),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleOtherExceptions(Exception ex) {
        return new ResponseEntity<>(
                new ApiResponse("Internal server error: " + ex.getMessage(), false),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
