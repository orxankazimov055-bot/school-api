
package az.iktex.organization.structure.testders.controller;

import az.iktex.organization.structure.testders.request.AuthorRequest;
import az.iktex.organization.structure.testders.response.AuthorResponse;
import az.iktex.organization.structure.testders.service.AuthorService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponse create(
            @RequestBody AuthorRequest request) {

        return authorService.create(request);
    }

    @GetMapping
    public List<AuthorResponse> getAll() {

        return authorService.getAll();
    }

    // Query Cache - olkeye gore muellifleri axtarir
    @GetMapping("/by-country")
    public List<AuthorResponse> getByCountry(
            @RequestParam String country) {

        return authorService.getByCountry(country);
    }

    @GetMapping("/{id}")
    public AuthorResponse getById(
            @PathVariable Long id) {

        return authorService.getById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id) {

        authorService.delete(id);
    }
}
