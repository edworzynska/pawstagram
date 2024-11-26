package com.example.instargam.service;

import com.example.instargam.model.User;
import com.example.instargam.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void createsUser() {
        User test = userService.createUser("email@email.com", "test_name", "passsword1!");
        assertNotNull(test);
        assertTrue(userRepository.existsById(test.getId()));
        assertEquals("test_name", test.getUsername());
        assertNotNull(test.getDate());
        System.out.println(test.getDate());

        assertNull(test.getBio());
        assertNull(test.getProfileImgUrl());
    }

    @Test
    void throwsAnErrorIfCreatingUserWithInvalidEmailAddress() {
        InvalidParameterException e = assertThrows(InvalidParameterException.class, ()->userService.createUser("email", "Test Name", "Password55#"));
        assertEquals("Provided email address is invalid!", e.getMessage());
    }
    @Test
    void throwsAnErrorIfCreatingUserWithInvalidEmailAddress2() {
        InvalidParameterException e = assertThrows(InvalidParameterException.class, ()->userService.createUser("e  mail@email.com", "Test Name", "Password55#"));
        assertEquals("Provided email address is invalid!", e.getMessage());
    }

    @Test
    void throwsAnErrorIfEmailAddressAlreadyInUse() {
        User user = userService.createUser("email@email.com", "test_name", "passsword1!");
        EntityExistsException e = assertThrows(EntityExistsException.class, ()->userService.createUser("email@email.com", "Test User", "passsword1!"));
        assertEquals("An account with this email address already exists!", e.getMessage());
    }

    @Test
    void throwsAnErrorIfPasswordIsEmpty() {
        InvalidParameterException e = assertThrows(InvalidParameterException.class, ()-> userService.createUser("", "Test User", "pass"));
        assertEquals("Provided email address is invalid!", e.getMessage());
    }

    @Test
    void throwsAnErrorIfPasswordIsInvalid1() {
        SecurityException e = assertThrows(SecurityException.class, ()-> userService.createUser("email@email.com", "test_user", "pass"));
        assertEquals("Password must be at least 8 characters long, must contain at least one special character, one letter and one number!", e.getMessage());
    }
    @Test
    void throwsAnErrorIfPasswordIsInvalid2() {
        SecurityException e = assertThrows(SecurityException.class, ()-> userService.createUser("email@email.com", "test_user", "passwordd"));
        assertEquals("Password must be at least 8 characters long, must contain at least one special character, one letter and one number!", e.getMessage());
    }
    @Test
    void throwsAnErrorIfPasswordIsInvalid3() {
        SecurityException e = assertThrows(SecurityException.class, ()-> userService.createUser("email@email.com", "test_user", "11111111"));
        assertEquals("Password must be at least 8 characters long, must contain at least one special character, one letter and one number!", e.getMessage());
    }
    @Test
    void throwsAnErrorIfPasswordIsInvalid4() {
        SecurityException e = assertThrows(SecurityException.class, ()-> userService.createUser("email@email.com", "test_user", "$$$$$$$$"));
        assertEquals("Password must be at least 8 characters long, must contain at least one special character, one letter and one number!", e.getMessage());
    }

    @Test
    void throwsAnErrorIfCreatingUserWithEmptyName() {
        InvalidParameterException e = assertThrows(InvalidParameterException.class, ()-> userService.createUser("email@email.com", "", "$$$Pass1$$$$$"));
        assertEquals("Username must be 3-15 characters long and contain only letters, numbers and underscores.", e.getMessage());

    }
    @Test
    void throwsAnErrorIfCreatingUserWithTooShortUsername() {
        InvalidParameterException e = assertThrows(InvalidParameterException.class, ()-> userService.createUser("email@email.com", "ew", "$$$Pass1$$$$$"));
        assertEquals("Username must be 3-15 characters long and contain only letters, numbers and underscores.", e.getMessage());

    }
    @Test
    void throwsAnErrorIfCreatingUserWithTooLongUsername() {
        InvalidParameterException e = assertThrows(InvalidParameterException.class, ()-> userService.createUser("email@email.com", "userrrrrrrrrrrrrrrrrrrrrrrrrrrrrr", "$$$Pass1$$$$$"));
        assertEquals("Username must be 3-15 characters long and contain only letters, numbers and underscores.", e.getMessage());

    }
    @Test
    void throwsAnErrorIfCreatingUserWithUsernameWithForbiddenCharacters() {
        InvalidParameterException e = assertThrows(InvalidParameterException.class, ()-> userService.createUser("email@email.com", "test.test", "$$$Pass1$$$$$"));
        assertEquals("Username must be 3-15 characters long and contain only letters, numbers and underscores.", e.getMessage());
    }

    @Test
    void addsBio() {
        User test = userService.createUser("email@email.com", "test_name", "passsword1!");
        userService.addBio("some bio", "test_name");
        assertEquals("some bio", test.getBio());
    }
    @Test
    void throwsAnErrorIfAddingBlankBio() {
        User test = userService.createUser("email@email.com", "test_name", "passsword1!");

        InvalidParameterException e = assertThrows(InvalidParameterException.class, ()-> userService.addBio("   ", "test_name"));
        assertEquals("Bio cannot be empty!", e.getMessage());
    }
    @Test
    void throwsAnErrorIfAddingEmptyBio() {
        User test = userService.createUser("email@email.com", "test_name", "passsword1!");

        InvalidParameterException e = assertThrows(InvalidParameterException.class, ()-> userService.addBio("", "test_name"));
        assertEquals("Bio cannot be empty!", e.getMessage());
    }
    @Test
    void throwsAnErrorIfAddingBioOver150CharactersLong() {
        User test = userService.createUser("email@email.com", "test_name", "passsword1!");

        InvalidParameterException e = assertThrows(InvalidParameterException.class, ()-> userService.addBio("doo bee doo bee doo bee doo bee doo bee doo bee doo bee doo bee " +
                "doo bee doo bee doo bee doo bee doo bee doo bee doo bee doo bee doo bee doo bee doo bee doo bee doo bee doo bee doo bee doo bee doo bee doo bee doo bee doo bee ", "test_name"));
        assertEquals("Invalid length! Maximum number of characters: 150", e.getMessage());
    }

    @Test
    void returnsUserInfoAsDto() {
        User test = userService.createUser("email@email.com", "test_name", "passsword1!");
        userService.addBio("some bio", "test_name");
        var result = userService.getUserInfo("test_name");
        assertEquals(test.getId(), result.getId());
        assertEquals(test.getUsername(), result.getUsername());
        assertEquals(test.getProfileImgUrl(), result.getProfileImgUrl());
    }
    @Test
    void returnsUserInfoAsDtoIfNoBio() {
        User test = userService.createUser("email@email.com", "test_name", "passsword1!");
        var result = userService.getUserInfo("test_name");
        assertEquals(test.getId(), result.getId());
        assertEquals(test.getUsername(), result.getUsername());
        assertEquals(test.getProfileImgUrl(), result.getProfileImgUrl());
        assertNull(result.getBio());
    }
}