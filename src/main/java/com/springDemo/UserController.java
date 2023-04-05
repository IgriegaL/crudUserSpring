package com.springDemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhoneRepository phoneRepository;

    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        user.setModified(LocalDateTime.now());
        user.setActive(true);
        User savedUser = userRepository.save(user);
        for (Phone phone : user.getPhones()) {
            phone.setUser(savedUser);
            phoneRepository.save(phone);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "User created successfully");
        response.put("userId", savedUser.getId().toString());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id, @RequestBody User user) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "User not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else {
            existingUser.setName(user.getName());
            existingUser.setPassword(user.getPassword());
            existingUser.setModified(LocalDateTime.now());
            existingUser.setActive(user.getActive());
            phoneRepository.deleteAll(existingUser.getPhones());
            existingUser.getPhones().clear();
            for (Phone phone : user.getPhones()) {
                phone.setUser(existingUser);
                existingUser.getPhones().add(phone);
            }
            userRepository.save(existingUser);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User updated successfully");
            response.put("userId", existingUser.getId().toString());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        } else {
            phoneRepository.deleteAll(user.getPhones());
            userRepository.delete(user);
            return new ResponseEntity<>("User has been deleted", HttpStatus.OK);
        }
    }
}
