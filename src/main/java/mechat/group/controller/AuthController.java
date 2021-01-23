package mechat.group.controller;

import mechat.group.entity.User;
import mechat.group.model.Result;
import mechat.group.model.ResultSucces;
import mechat.group.repository.ConfirmationRepository;
import mechat.group.repository.UserRepo;
import mechat.group.security.ConfirmationToken;
import mechat.group.security.JwtTokenProvider;
import mechat.group.service.UserServiceImp;
import mechat.group.twilio.TwilioService;
import mechat.group.vm.LoginVM;
import mechat.group.vm.SmsPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserServiceImp userServiceImp;

    private final UserRepo userRepository;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final ConfirmationRepository confirmationRepository;
    private final TwilioService twilioService;

    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          UserRepo userRepository, UserServiceImp userServiceImp, ConfirmationRepository confirmationRepository, TwilioService twilioService, PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.userServiceImp = userServiceImp;
        this.confirmationRepository = confirmationRepository;
        this.twilioService = twilioService;
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

    @PostMapping("/forgotpassword")
    public ResponseEntity forgotPassword(@RequestBody String phone) {
        User existingUser = userRepository.findByPhoneNumber(phone);
        ConfirmationToken confirmationToken = confirmationRepository.findByUser(existingUser);
        if (confirmationToken != null) {
            confirmationRepository.delete(confirmationToken);
        }
        if (existingUser != null) {

            ConfirmationToken confirmationToken1 = new ConfirmationToken();
            confirmationToken1.setUser(existingUser);

            double a = 1000 + Math.random() * 10000;
            confirmationToken1.setCode(String.valueOf((int) Math.ceil(a)));
            confirmationRepository.save(confirmationToken1);

            SmsPayload smsPayload = new SmsPayload(phone, confirmationToken1.getCode());
            twilioService.sendSms(smsPayload);

            return ResponseEntity.ok(new ResultSucces(true, "we send you a confirmation code"));
        }
        return new ResponseEntity(new Result(false, "phone not found from database"), BAD_REQUEST);
    }

    @PostMapping("/checkcode/{code}")
    public ResponseEntity forgotPassword(@RequestBody String phone, @PathVariable String code) {
        User user = userRepository.findByPhoneNumber(phone);
        ConfirmationToken confirmationToken = confirmationRepository.findByUser(user);
        if (confirmationToken != null) {
            if (confirmationToken.getCode().equals(code) && confirmationToken.getUser().getId().equals(user.getId())) {
                return ResponseEntity.ok(new ResultSucces(true, "code true"));
            }
            return new ResponseEntity(new Result(false, "please rewrite confirmation code"), BAD_REQUEST);
        }
        return new ResponseEntity(new Result(false, "phone is wrong"), BAD_REQUEST);
    }

    @PostMapping("/editpassword")
    public ResponseEntity forgotPassword(@RequestBody String password, HttpServletRequest request) {
        User user = userRepository.findByPhoneNumber(request.getHeader("phone"));
        ConfirmationToken confirmationToken = confirmationRepository.findByUser(user);
        if (confirmationToken.getCode().equals(request.getHeader("code")) && user != null && password.length() >= 6) {
            user.setPassword(password);
            confirmationRepository.delete(confirmationToken);
            User user2 = userServiceImp.update(user.getId(), user);
            String token1 = jwtTokenProvider.createToken(user2.getUsername(), user2.getRoles());
            Map<Object, Object> map = new HashMap<>();
            map.put("succes", true);
            map.put("username", user.getUsername());
            map.put("token", token1);
            return ResponseEntity.ok(map);
        }
        return new ResponseEntity(new Result(false, "something wrong"), BAD_REQUEST);
    }


    private boolean checkPasswordLength(String password) {
        return password.length() >= 6;
    }

}