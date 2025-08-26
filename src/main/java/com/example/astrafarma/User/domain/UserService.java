package com.example.astrafarma.User.domain;

import com.example.astrafarma.Mail.domain.MailService;
import com.example.astrafarma.User.dto.UserDTO;
import com.example.astrafarma.exception.UserNotFoundException;
import com.example.astrafarma.mapper.UserMapper;
import com.example.astrafarma.User.dto.UserRequestDto;
import com.example.astrafarma.User.repository.UserRepository;
import com.example.astrafarma.security.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailService mailService;

    public UserDTO updateAuthenticatedUser(UserRequestDto userRequestDto) {
        User user = AuthUtils.getAuthenticatedUser();
        if (user != null) {
            user.setFullName(userRequestDto.getFullName());
            user.setPhoneNumber(userRequestDto.getPhoneNumber());
            user.setBirthday(userRequestDto.getBirthday());
            user.setGender(userRequestDto.getGender());
            user.setPassword(userRequestDto.getPassword());
            user.setEmail(userRequestDto.getEmail());
            userRepository.save(user);
            return userMapper.userToUserDTO(user);
        }
        return null;
    }

    public UserDTO getAuthenticatedUserInfo() {
        User user = AuthUtils.getAuthenticatedUser();
        if (user == null) {
            throw new UserNotFoundException("Usuario autenticado no encontrado.");
        }
        return userMapper.userToUserDTO(user);
    }

    public boolean verifyUser(String token) {
        Optional<User> userOpt = userRepository.findAll().stream()
                .filter(u -> token.equals(u.getVerificationToken()))
                .findFirst();
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setVerified(true);
            user.setVerificationToken(null);
            userRepository.save(user);

            try {
                mailService.sendWelcomeMail(user.getEmail(), user.getFullName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public boolean deleteAuthenticatedUser() {
        User user = AuthUtils.getAuthenticatedUser();
        if (user != null && userRepository.existsById(user.getId())) {
            userRepository.deleteById(user.getId());
            return true;
        }
        return false;
    }

    public List<UserCategoryStats> getTop3CategoriesForAuthenticatedUser() {
        User authUser = AuthUtils.getAuthenticatedUser();
        if (authUser != null) {
            User user = userRepository.findByEmail(authUser.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));
            List<UserCategoryStats> stats = user.getCategoryStats();
            return stats.stream()
                    .sorted(Comparator.comparingInt(UserCategoryStats::getInteractionCount).reversed())
                    .limit(3)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::userToUserDTO)
                .collect(Collectors.toList());
    }
}