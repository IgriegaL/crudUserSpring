package com.springDemo;

import com.springDemo.seguridad.LoginRequest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Date;
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

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 864_000_000)) // 10 days
                .signWith(SignatureAlgorithm.HS512, "MyJwtSecret")
                .compact();

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        System.out.println(response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encripta la contraseña
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

    @PostConstruct
    public void createDefaultUser() {
        // Verifica si el usuario predeterminado ya existe
        String defaultEmail = "default.user@example.com";
        User existingUser = userRepository.findByEmail(defaultEmail);

        // Si el usuario no existe, crea uno nuevo
        if (existingUser == null) {
            User defaultUser = new User();
            defaultUser.setName("Default User");
            defaultUser.setEmail(defaultEmail);
            defaultUser.setPassword(passwordEncoder.encode("default_password"));
            defaultUser.setModified(LocalDateTime.now());
            defaultUser.setActive(true);

            userRepository.save(defaultUser);
            System.out.println("Usuario predeterminado creado");
        } else {
            System.out.println("El usuario predeterminado ya existe");
        }
    }
}
