package fi.eerosalla.web.tagger.controller.user;

import fi.eerosalla.web.tagger.model.form.UserCreateForm;
import fi.eerosalla.web.tagger.model.form.UserEditForm;
import fi.eerosalla.web.tagger.model.response.ErrorResponse;
import fi.eerosalla.web.tagger.repository.user.UserEntry;
import fi.eerosalla.web.tagger.repository.user.UserRepository;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@RolesAllowed("ADMIN")
@RestController
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(final UserRepository userRepository,
                          final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @SneakyThrows
    @GetMapping("/api/users")
    public Object getAllUsers() {
        return userRepository.getHandle().queryForAll();
    }

    @PostMapping("/api/users")
    public Object createUser(
        final @Validated @RequestBody UserCreateForm userCreateForm) {
        String username = userCreateForm.getUsername().toLowerCase();
        String password = userCreateForm.getPassword();

        if (userRepository.queryForUsername(username) != null) {
            return new ResponseEntity<>(
                new ErrorResponse("Username is already in use"),
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        UserEntry user = new UserEntry();
        user.setName(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole("ADMIN"); // TODO: add role setting

        userRepository.create(user);

        return user;
    }

    @DeleteMapping("/api/users/{userId}")
    public Object deleteUser(
        final @ModelAttribute UserEntry user) {
        userRepository.deleteById(user.getId());

        return user;
    }

    @PatchMapping("/api/users/{userId}")
    public Object editUser(
        final @ModelAttribute UserEntry user,
        final @Validated @RequestBody UserEditForm userEditForm) {
        boolean edit = false;

        String newUsername = userEditForm.getUsername();
        if (newUsername != null
            && !newUsername.equals(user.getName())) {
            user.setName(newUsername);

            edit = true;
        }

        String newPassword = userEditForm.getPassword();
        if (newPassword != null) {
            String newPasswordHash =
                passwordEncoder.encode(newPassword);

            user.setPasswordHash(newPasswordHash);

            edit = true;
        }

        if (edit) {
            userRepository.update(user);
        }

        return user;
    }
}
