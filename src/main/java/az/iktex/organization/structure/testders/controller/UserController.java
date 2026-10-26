
package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.enums.Gender;
import az.iktex.organization.structure.testders.request.UserRequest;
import az.iktex.organization.structure.testders.response.UserResponse;
import az.iktex.organization.structure.testders.service.UserService;
import az.iktex.organization.structure.testders.request.PassportRequest;
import az.iktex.organization.structure.testders.response.PassportResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Yeni istifadəçi yaratmaq
    @PostMapping
    public ResponseEntity<UserResponse> create(
            @RequestBody UserRequest request) {

        UserResponse response = userService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // ID ilə istifadəçi tapmaq
    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    // Bütün istifadəçilər və gender filteri
    @GetMapping
    public List<UserResponse> getAll(
            @RequestParam(required = false) Gender gender) {

        return userService.getAll(gender);
    }

    // Email ilə axtarış
    @GetMapping("/by-email")
    public UserResponse getByEmail(
            @RequestParam String email) {

        return userService.getByEmail(email);
    }

    // Pasportu olmayan istifadəçilər
    @GetMapping("/without-passport")
    public List<UserResponse> getWithoutPassport() {

        return userService.getWithoutPassport();
    }

    // İstifadəçi məlumatlarını yeniləmək
    @PutMapping("/{id}")
    public UserResponse update(
            @PathVariable Long id,
            @RequestBody UserRequest request) {

        return userService.update(id, request);
    }

    // İstifadəçini silmək
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        userService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/passport")
    public ResponseEntity<PassportResponse> addPassport(
            @PathVariable Long id,
            @RequestBody PassportRequest request) {

        PassportResponse response =
                userService.addPassport(id, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}/passport")
    public PassportResponse getPassport(
            @PathVariable Long id) {

        return userService.getPassport(id);
    }

    @DeleteMapping("/{id}/passport")
    public ResponseEntity<Void> deletePassport(
            @PathVariable Long id) {

        userService.deletePassport(id);

        return ResponseEntity.noContent().build();
    }

}
