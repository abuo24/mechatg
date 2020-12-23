package mechat.group.controller;

import mechat.group.entity.Admins;
import mechat.group.entity.User;
import mechat.group.model.Result;
import mechat.group.model.ResultSucces;
import mechat.group.repository.AdminRepo;
import mechat.group.repository.UserRepo;
import mechat.group.security.JwtTokenProvider;
import mechat.group.service.AdminServiceImp;
import mechat.group.service.UserServiceImp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("api/admin")
public class AdminController {
    private final UserServiceImp userServiceImp;
    private final AdminRepo adminRepository;
    private final AdminServiceImp adminServiceImp;
    private final UserRepo userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public AdminController(UserServiceImp userServiceImp, AdminRepo adminRepository, AdminServiceImp adminServiceImp, UserRepo userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userServiceImp = userServiceImp;
        this.adminRepository = adminRepository;
        this.adminServiceImp = adminServiceImp;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/users")
    public ResponseEntity getAllUsers(HttpServletRequest request) {
        return ResponseEntity.ok(new ResultSucces(true, userServiceImp.getAll()));
    }

    @PostMapping("/user/add")
    public ResponseEntity create(@RequestBody User user, HttpServletRequest request) {
        User user1 = userServiceImp.WhoAmI(request);
        if (user1 == null) {
            return new ResponseEntity(new Result(false, "token is invalid"), BAD_REQUEST);
        }
        if (!checkPasswordLength(user.getPassword())) {
            return new ResponseEntity(new Result(false, "Parol uzunligi 6 dan kam"), HttpStatus.BAD_REQUEST);
        }
        if (userServiceImp.checkUsername(user.getUsername())) {
            return new ResponseEntity(new Result(false, "Bu username band"), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(new ResultSucces(true, userServiceImp.create(user)));
    }

    @PutMapping("/user/updatedetails/{hashId}")
    public ResponseEntity editUser(@PathVariable String hashId, @RequestBody User user) {
        if (!checkPasswordLength(user.getPassword())) {
            return new ResponseEntity(new Result(false, "Parol uzunligi 6 dan kam"), HttpStatus.BAD_REQUEST);
        }
        if (!user.getUsername().equals(userRepository.findByHashId(hashId).get().getUsername())) {
            if (userServiceImp.checkUsername(user.getUsername())) {
                return new ResponseEntity(new Result(false, "Bu username band"), HttpStatus.BAD_REQUEST);
            }
        }
        User user1 = userServiceImp.update(hashId, user);
        if (user1 == null) {
            return ResponseEntity.ok(new ResultSucces(true, "not updated"));
        }
        return ResponseEntity.ok(new ResultSucces(true, user1));
    }

    @PutMapping("/updatedetails")
    public ResponseEntity editAdmin(@RequestBody Admins admin, HttpServletRequest request) {
        try {
            User user = userServiceImp.WhoAmI(request);
            Admins admins = adminRepository.findByUsername(user.getUsername());
            if (!checkPasswordLength(admin.getPassword())) {
                return new ResponseEntity(new Result(false, "Parol uzunligi 6 dan kam"), HttpStatus.BAD_REQUEST);
            }
            if (!admins.getUsername().equals(admin.getUsername())) {
                if (userServiceImp.checkUsername(admin.getUsername())) {
                    return new ResponseEntity(new Result(false, "Bu username band"), HttpStatus.BAD_REQUEST);
                }
            }
            Admins admins1 = adminServiceImp.update(admins.getHashId(), admin);
            String token1 = jwtTokenProvider.createToken(admins1.getUsername(), admins1.getRoles());
            Map<Object, Object> map = new HashMap<>();
            map.put("succes", true);
            map.put("username", user.getUsername());
            map.put("token", token1);
            return ResponseEntity.ok(map);
        } catch (Exception e) {
            return new ResponseEntity(new Result(false, e.getMessage()), BAD_REQUEST);
        }
    }

    @DeleteMapping("/user/delete/{hashId}")
    public ResponseEntity delete(@PathVariable String hashId) {
        return ResponseEntity.ok(userServiceImp.delete(hashId));
    }

    private boolean checkPasswordLength(String password) {
        return password.length() >= 6;
    }

}
