
package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.User;
import az.iktex.organization.structure.testders.enums.Gender;
import az.iktex.organization.structure.testders.mapper.UserMapper;
import az.iktex.organization.structure.testders.repository.UserRepository;
import az.iktex.organization.structure.testders.request.UserRequest;
import az.iktex.organization.structure.testders.response.UserResponse;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import az.iktex.organization.structure.testders.entity.Passport;
import az.iktex.organization.structure.testders.mapper.PassportMapper;
import az.iktex.organization.structure.testders.repository.PassportRepository;
import az.iktex.organization.structure.testders.request.PassportRequest;
import az.iktex.organization.structure.testders.response.PassportResponse;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PassportRepository passportRepository;
    private final PassportMapper passportMapper;

    @Transactional
    public UserResponse create(UserRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalStateException(
                    "Bu email artıq mövcuddur!"
            );
        }

        User user = userMapper.toEntity(request);
        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    public UserResponse getById(Long id) {

        User user = userRepository.findDetailById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User tapılmadı: " + id
                        )
                );

        return userMapper.toResponse(user);
    }

    public List<UserResponse> getAll(Gender gender) {

        List<User> users;

        if (gender == null) {
            users = userRepository.findAllWithPassport();
        } else {
            users = userRepository.findAllByGender(gender);
        }

        return userMapper.toResponseList(users);
    }

    public UserResponse getByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User tapılmadı: " + email
                        )
                );

        return userMapper.toResponse(user);
    }

    public List<UserResponse> getWithoutPassport() {

        return userMapper.toResponseList(
                userRepository.findAllByPassportIsNull()
        );
    }

    @Transactional
    public UserResponse update(Long id, UserRequest request) {

        User user = userRepository.findDetailById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User tapılmadı: " + id
                        )
                );

        if (userRepository.existsByEmailAndIdNot(
                request.email(), id)) {

            throw new IllegalStateException(
                    "Bu email başqa istifadəçiyə məxsusdur!"
            );
        }

        userMapper.updateEntity(request, user);

        // Dirty checking:
        // save() lazım deyil.
        return userMapper.toResponse(user);
    }

    @Transactional
    public void delete(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User tapılmadı: " + id
                        )
                );

        userRepository.delete(user);
    }

    @Transactional
    public PassportResponse addPassport(
            Long userId,
            PassportRequest request) {

        User user = userRepository.findDetailById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User tapılmadı: " + userId
                        )
                );

        if (user.getPassport() != null) {
            throw new IllegalStateException(
                    "Bu istifadəçinin artıq pasportu var!"
            );
        }

        if (passportRepository.existsBySerialNumber(
                request.serialNumber())) {

            throw new IllegalStateException(
                    "Bu seriya nömrəsi artıq mövcuddur!"
            );
        }

        Passport passport = passportMapper.toEntity(request);

        // OneToOne əlaqəsinin hər iki tərəfini bağlayırıq.
        user.setPassport(passport);

        // CascadeType.ALL sayəsində Passport da saxlanılır.
        // User managed entity olduğu üçün save() çağırmırıq.
        return passportMapper.toResponse(passport);
    }

    public PassportResponse getPassport(Long userId) {

        User user = userRepository.findDetailById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User tapılmadı: " + userId
                        )
                );

        if (user.getPassport() == null) {
            throw new EntityNotFoundException(
                    "Bu istifadəçinin pasportu yoxdur!"
            );
        }

        return passportMapper.toResponse(user.getPassport());
    }

    @Transactional
    public void deletePassport(Long userId) {

        User user = userRepository.findDetailById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "User tapılmadı: " + userId
                        )
                );

        if (user.getPassport() == null) {
            throw new EntityNotFoundException(
                    "Silinəcək pasport tapılmadı!"
            );
        }

        // orphanRemoval = true:
        // Əlaqəni qıranda Passport bazadan silinir.
        user.setPassport(null);
    }

}
