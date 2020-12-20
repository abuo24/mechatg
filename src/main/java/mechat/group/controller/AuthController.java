package mechat.group.controller;

import lombok.extern.flogger.Flogger;
import mechat.group.entity.User;
import mechat.group.model.Result;
import mechat.group.model.ResultSucces;
import mechat.group.repository.UserRepo;
import mechat.group.security.JwtTokenFilter;
import mechat.group.security.JwtTokenProvider;
import mechat.group.service.UserServiceImp;
import mechat.group.vm.LoginVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserServiceImp userServiceImp;

    private final UserRepo userRepository;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);


    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          UserRepo userRepository, UserServiceImp userServiceImp, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.userServiceImp = userServiceImp;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity create(@RequestBody User user, HttpServletResponse response) {
        if (!checkPasswordLength(user.getPassword())) {
            return new ResponseEntity(new Result(false, "minimum password length=6"), BAD_REQUEST);
        }
        if (userServiceImp.checkUsername(user.getUsername())) {
            return new ResponseEntity(new Result(false, "duplicate username"), BAD_REQUEST);
        }
        User user1 = userServiceImp.create(user);
        if (user1 != null) {
            return ResponseEntity.ok(new ResultSucces(true, user1));
        }
        return new ResponseEntity(new Result(false, "user not create"), BAD_REQUEST);
    }


    @PutMapping("/user/updatedetails")
    public ResponseEntity edit(@RequestBody User user, HttpServletRequest request) {
        User user1 = userServiceImp.WhoAmI(request);
        if (user1 == null) {
            return new ResponseEntity(new Result(false, "token is invalid"), BAD_REQUEST);
        }
        if (!checkPasswordLength(user.getPassword())) {
            return new ResponseEntity(new Result(false, "Parol uzunligi 6 dan kam"), BAD_REQUEST);
        }
        if (!user1.getUsername().equals(user.getUsername())) {
            if (userServiceImp.checkUsername(user.getUsername())) {
                return new ResponseEntity(new Result(false, "Bu username band"), BAD_REQUEST);
            }
        }
        User user2 = userServiceImp.update(user1.getId(), user);
        String token1 = jwtTokenProvider.createToken(user2.getUsername(), user2.getRoles());
        Map<Object, Object> map = new HashMap<>();
        map.put("succes", true);
        map.put("username", user.getUsername());
        map.put("token", token1);
        return ResponseEntity.ok(map);
    }

    @DeleteMapping("/user/delete")
    public ResponseEntity delete(HttpServletRequest httpServletRequest) {
        User user1 = userServiceImp.WhoAmI(httpServletRequest);
        return ResponseEntity.ok(userServiceImp.delete(user1.getId()));
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginVM loginVM) {
        User user = userRepository.findByUsername(loginVM.getUsername());
        if (user == null) {
            return new ResponseEntity(new Result(false, "User not available"), BAD_REQUEST);
        }
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginVM.getUsername(), loginVM.getPassword()));
            String token = jwtTokenProvider.createToken(user.getUsername(), user.getRoles());
            Map<Object, Object> map = new HashMap<>();
            map.put("succes", true);
            map.put("username", user.getUsername());
            map.put("token", token);
            return ResponseEntity.ok(map);
        } catch (Exception e) {
            return new ResponseEntity(new Result(false, e.getLocalizedMessage()), BAD_REQUEST);
        }
    }


    @GetMapping("/getme")
    public ResponseEntity getUser(HttpServletRequest request) {
        User user = userServiceImp.WhoAmI(request);
        return user != null ? ResponseEntity.ok(new ResultSucces(true, user))
                : (new ResponseEntity(new Result(false, "token is invalid"), BAD_REQUEST));
    }


    private boolean checkPasswordLength(String password) {
        return password.length() >= 6;
    }

}