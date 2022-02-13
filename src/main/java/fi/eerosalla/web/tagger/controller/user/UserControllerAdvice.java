package fi.eerosalla.web.tagger.controller.user;

import fi.eerosalla.web.tagger.model.response.ErrorResponse;
import fi.eerosalla.web.tagger.repository.user.UserEntry;
import fi.eerosalla.web.tagger.repository.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@ControllerAdvice(assignableTypes = {UserController.class})
public class UserControllerAdvice {

    public UserControllerAdvice(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private static class UserNotFoundException extends Exception {
    }

    private final UserRepository userRepository;

    @ModelAttribute
    public void injectUser(
        final @PathVariable Map<String, String> pathVariables,
        final Model model) throws UserNotFoundException {
        String userIdStr = pathVariables.get("userId");
        if (userIdStr != null) {
            int userId = Integer.parseInt(userIdStr);

            UserEntry user = userRepository.queryForId(userId);

            if (user == null) {
                throw new UserNotFoundException();
            }

            model.addAttribute(user);
        }
    }

    @ResponseBody
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleException() {
        return new ResponseEntity<>(
            new ErrorResponse("User not found"),
            HttpStatus.NOT_FOUND
        );
    }
}
