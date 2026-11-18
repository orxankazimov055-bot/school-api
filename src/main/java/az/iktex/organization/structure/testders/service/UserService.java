
package az.iktex.organization.structure.testders.service;

import az.iktex.organization.structure.testders.entity.User;
import az.iktex.organization.structure.testders.entity.Passport;
import az.iktex.organization.structure.testders.enums.Gender;
import az.iktex.organization.structure.testders.enums.ErrorCode;
import az.iktex.organization.structure.testders.exception.NotFoundException;
import az.iktex.organization.structure.testders.exception.AlreadyExistsException;
import az.iktex.organization.structure.testders.mapper.UserMapper;
import az.iktex.organization.structure.testders.mapper.PassportMapper;
import az.iktex.organization.structure.testders.repository.UserRepository;
import az.iktex.organization.structure.testders.repository.PassportRepository;
import az.iktex.organization.structure.testders.request.UserRequest;
import az.iktex.organization.structure.testders.request.PassportRequest;
import az.iktex.organization.structure.testders.response.UserResponse;
import az.iktex.organization.structure.testders.response.PassportResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PassportRepository passportRepository;
    private final PassportMapper passportMapper;

    // USER CREATE
    @Transactional
    public UserResponse create(UserRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new AlreadyExistsException(
                    ErrorCode.USER_EMAIL_EXISTS,
                    request.email()
            );
        }

        User user = userMapper.toEntity(request);

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    // USER GET BY ID
    public UserResponse getById(Long id) {

        User user = userRepository.findDetailById(id)
                .orElseThrow(() ->
                        new NotFoundException(
                                ErrorCode.USER_NOT_FOUND,
                                id
                        )
                );

        return userMapper.toResponse(user);
    }

    // USER GET ALL
    public List<UserResponse> getAll(Gender gender) {

        List<User> users;

        if (gender == null) {
            users = userRepository.findAllWithPassport();
        } else {
            users = userRepository.findAllByGender(gender);
        }

        return userMapper.toResponseList(users);
    }

    // USER GET BY EMAIL
    public UserResponse getByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new NotFoundException(
                                ErrorCode.USER_NOT_FOUND,
                                email
                        )
                );

        return userMapper.toResponse(user);
    }

    // USERS WITHOUT PASSPORT
    public List<UserResponse> getWithoutPassport() {

        return userMapper.toResponseList(
                userRepository.findAllByPassportIsNull()
        );
    }

    // USER UPDATE
    @Transactional
    public UserResponse update(Long id, UserRequest request) {

        User user = userRepository.findDetailById(id)
                .orElseThrow(() ->
                        new NotFoundException(
                                ErrorCode.USER_NOT_FOUND,
                                id
                        )
                );

        if (userRepository.existsByEmailAndIdNot(
                request.email(), id)) {

            throw new AlreadyExistsException(
                    ErrorCode.USER_EMAIL_USED,
                    request.email()
            );
        }

        userMapper.updateEntity(request, user);

        // Dirty checking:
        // save() lazım deyil.
        return userMapper.toResponse(user);
    }

    // USER DELETE
    @Transactional
    public void delete(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(
                                ErrorCode.USER_NOT_FOUND,
                                id
                        )
                );

        userRepository.delete(user);
    }

    // ADD PASSPORT
    @Transactional
    public PassportResponse addPassport(
            Long userId,
            PassportRequest request) {

        User user = userRepository.findDetailById(userId)
                .orElseThrow(() ->
                        new NotFoundException(
                                ErrorCode.USER_NOT_FOUND,
                                userId
                        )
                );

        if (user.getPassport() != null) {
            throw new AlreadyExistsException(
                    ErrorCode.USER_PASSPORT_EXISTS
            );
        }

        if (passportRepository.existsBySerialNumber(
                request.serialNumber())) {

            throw new AlreadyExistsException(
                    ErrorCode.PASSPORT_SERIAL_EXISTS,
                    request.serialNumber()
            );
        }

        Passport passport = passportMapper.toEntity(request);

        // OneToOne əlaqəsinin hər iki tərəfini bağlayırıq.
        user.setPassport(passport);

        // CascadeType.ALL sayəsində Passport saxlanılır.
        // User managed entity olduğu üçün save() lazım deyil.
        return passportMapper.toResponse(passport);
    }

    // GET PASSPORT
    public PassportResponse getPassport(Long userId) {

        User user = userRepository.findDetailById(userId)
                .orElseThrow(() ->
                        new NotFoundException(
                                ErrorCode.USER_NOT_FOUND,
                                userId
                        )
                );

        if (user.getPassport() == null) {
            throw new NotFoundException(
                    ErrorCode.PASSPORT_NOT_FOUND,
                    userId
            );
        }

        return passportMapper.toResponse(user.getPassport());
    }

    // DELETE PASSPORT
    @Transactional
    public void deletePassport(Long userId) {

        User user = userRepository.findDetailById(userId)
                .orElseThrow(() ->
                        new NotFoundException(
                                ErrorCode.USER_NOT_FOUND,
                                userId
                        )
                );

        if (user.getPassport() == null) {
            throw new NotFoundException(
                    ErrorCode.PASSPORT_NOT_FOUND,
                    userId
            );
        }

        // orphanRemoval = true:
        // Əlaqəni qıranda Passport bazadan silinir.
        user.setPassport(null);
    }
}
