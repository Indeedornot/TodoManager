package com.bmisiek.todomanager.unit.areas.security.service;

import com.bmisiek.todomanager.areas.security.config.SecurityProperties;
import com.bmisiek.todomanager.areas.security.dto.SignUpDto;
import com.bmisiek.todomanager.areas.security.entity.Role;
import com.bmisiek.todomanager.areas.security.entity.RoleEnum;
import com.bmisiek.todomanager.areas.security.entity.User;
import com.bmisiek.todomanager.areas.security.repository.UserRepository;
import com.bmisiek.todomanager.areas.security.service.RoleService;
import com.bmisiek.todomanager.areas.security.service.UserCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserCreatorTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private RoleService roleService;
    
    @Mock
    private SecurityProperties securityProperties;

    private UserCreator userCreator;

    @BeforeEach
    public void setUp() {
        userCreator = new UserCreator(userRepository, passwordEncoder, roleService, securityProperties);
    }

    @Test
    public void Should_CreateUser_WithUserRole() {
        SignUpDto signUpDto = new SignUpDto();
        signUpDto.setName("Test User");
        signUpDto.setUsername("testuser");
        signUpDto.setEmail("test@example.com");
        signUpDto.setPassword("password");
        
        User user = new User();

        Role userRole = new Role();
        userRole.setName(RoleEnum.ROLE_USER.getName());
        
        Mockito.when(userRepository.existsByUsername(ArgumentMatchers.anyString())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(ArgumentMatchers.anyString())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(ArgumentMatchers.anyString())).thenReturn("encodedPassword");
        Mockito.when(roleService.findByEnum(RoleEnum.ROLE_USER)).thenReturn(Optional.of(userRole));
        savesUserWithId(user, 1L);

        Long userId = userCreator.create(signUpDto);
        
        Assertions.assertEquals(1L, userId);
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        Assertions.assertEquals("Test User", savedUser.getName());
        Assertions.assertEquals("testuser", savedUser.getUsername());
        Assertions.assertEquals("test@example.com", savedUser.getEmail());
        Assertions.assertEquals("encodedPassword", savedUser.getPassword());
        Assertions.assertTrue(savedUser.getRoles().contains(userRole));
    }

    private void savesUserWithId(User user, Long id) {
        Mockito.when(userRepository.save(ArgumentMatchers.any(User.class))).then(f -> {
            var u = f.getArgument(0, User.class);
            u.setId(id);
            return u;
        }).thenReturn(user);
    }

    @Test
    public void Should_CreateAdminUser_WhenValidPassKey() {
        SignUpDto signUpDto = new SignUpDto();
        signUpDto.setName("Admin User");
        signUpDto.setUsername("adminuser");
        signUpDto.setEmail("admin@example.com");
        signUpDto.setPassword("password");
        signUpDto.setPassKey("validPassKey");
        
        User user = new User();
        
        Role adminRole = new Role();
        adminRole.setName(RoleEnum.ROLE_ADMIN.getName());
        
        Mockito.when(securityProperties.getPassKey()).thenReturn("validPassKey");
        Mockito.when(userRepository.existsByUsername(ArgumentMatchers.anyString())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(ArgumentMatchers.anyString())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(ArgumentMatchers.anyString())).thenReturn("encodedPassword");
        Mockito.when(roleService.findByEnum(RoleEnum.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));
        savesUserWithId(user, 1L);

        Long userId = userCreator.create(signUpDto);

        Assertions.assertEquals(1L, userId);
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        Assertions.assertEquals("Admin User", savedUser.getName());
        Assertions.assertEquals("adminuser", savedUser.getUsername());
        Assertions.assertEquals("admin@example.com", savedUser.getEmail());
        Assertions.assertEquals("encodedPassword", savedUser.getPassword());
        Assertions.assertTrue(savedUser.getRoles().contains(adminRole));
    }

    @Test
    public void Should_ThrowException_WhenUsernameExists() {
        SignUpDto signUpDto = new SignUpDto();
        signUpDto.setName("Test User");
        signUpDto.setUsername("existinguser");
        signUpDto.setEmail("test@example.com");
        signUpDto.setPassword("password");
        
        Mockito.when(userRepository.existsByUsername("existinguser")).thenReturn(true);
        
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> userCreator.create(signUpDto));
        
        Assertions.assertEquals("Username is already taken!", exception.getMessage());
        Mockito.verify(userRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    public void Should_ThrowException_WhenEmailExists() {
        SignUpDto signUpDto = new SignUpDto();
        signUpDto.setName("Test User");
        signUpDto.setUsername("testuser");
        signUpDto.setEmail("existing@example.com");
        signUpDto.setPassword("password");
        
        Mockito.when(userRepository.existsByUsername(ArgumentMatchers.anyString())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);
        
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> userCreator.create(signUpDto));
        
        Assertions.assertEquals("Email is already taken!", exception.getMessage());
        Mockito.verify(userRepository, Mockito.never()).save(ArgumentMatchers.any());
    }

    @Test
    public void Should_CreateUserWithSpecificRole() {
        SignUpDto signUpDto = new SignUpDto();
        signUpDto.setName("Test User");
        signUpDto.setUsername("testuser");
        signUpDto.setEmail("test@example.com");
        signUpDto.setPassword("password");
        
        User user = new User();
        
        Role adminRole = new Role();
        adminRole.setName(RoleEnum.ROLE_ADMIN.getName());
        
        Mockito.when(userRepository.existsByUsername(ArgumentMatchers.anyString())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(ArgumentMatchers.anyString())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(ArgumentMatchers.anyString())).thenReturn("encodedPassword");
        Mockito.when(roleService.findByEnum(RoleEnum.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));
        savesUserWithId(user, 1L);

        Long userId = userCreator.create(signUpDto, RoleEnum.ROLE_ADMIN);
        
        Assertions.assertEquals(1L, userId);
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository).save(userCaptor.capture());
        
        User savedUser = userCaptor.getValue();
        Assertions.assertEquals("Test User", savedUser.getName());
        Assertions.assertEquals("testuser", savedUser.getUsername());
        Assertions.assertEquals("test@example.com", savedUser.getEmail());
        Assertions.assertEquals("encodedPassword", savedUser.getPassword());
        Assertions.assertTrue(savedUser.getRoles().contains(adminRole));
    }
}
