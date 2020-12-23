package mechat.group.controller;

import mechat.group.entity.Admins;
import mechat.group.model.Result;
import mechat.group.model.ResultSucces;
import mechat.group.repository.AdminRepo;
import mechat.group.service.AdminServiceImp;
import mechat.group.service.UserServiceImp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("api/owner")
public class OwnerController {
    private final UserServiceImp userServiceImp;
    private final AdminServiceImp adminServiceImp;
    private final AdminRepo adminRepository;

    public OwnerController(UserServiceImp userServiceImp, AdminServiceImp adminServiceImp, AdminRepo adminRepository) {
        this.userServiceImp = userServiceImp;
        this.adminServiceImp = adminServiceImp;
        this.adminRepository = adminRepository;
    }


    private boolean checkPasswordLength(String password) {
        return password.length() >= 6;
    }

    @GetMapping("/admins")
    public ResponseEntity getAllAdmins() {
        return ResponseEntity.ok(new ResultSucces(true, adminServiceImp.getAllAdmin()));
    }

    @PostMapping("/admin/add")
    public ResponseEntity create(@RequestBody Admins admins) {
        if (!checkPasswordLength(admins.getPassword())) {
            return new ResponseEntity(new Result(false, "min password length = 6"), HttpStatus.BAD_REQUEST);
        }
        if (userServiceImp.checkUsername(admins.getUsername())) {
            return new ResponseEntity(new Result(false, "duplicate username"), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(new ResultSucces(true, adminServiceImp.create(admins)));
    }

    @PutMapping("/admin/add/{hashId}")
    public ResponseEntity addUserToAdmin(@PathVariable String hashId) {
        return ResponseEntity.ok(adminServiceImp.addAdmin(hashId));
    }

    @PutMapping("/admin/updatedetails/{hashId}")
    public ResponseEntity editUser(@PathVariable String hashId, @RequestBody Admins admin) {
        if (!checkPasswordLength(admin.getPassword())) {
            return new ResponseEntity(new Result(false, "min password length = 6"), HttpStatus.BAD_REQUEST);
        }
        if (!admin.getUsername().equals(adminRepository.findByHashId(hashId).get().getUsername())) {
            if (userServiceImp.checkUsername(admin.getUsername())) {
                return new ResponseEntity(new Result(false, "duplicate username"), HttpStatus.BAD_REQUEST);
            }
        }
        return ResponseEntity.ok(new ResultSucces(true, adminServiceImp.update(hashId, admin)));
    }


    @DeleteMapping("/admin/delete/{hashId}")
    public ResponseEntity delete(@PathVariable String hashId) {
        try {
            return ResponseEntity.ok(userServiceImp.delete(hashId));
        } catch (Exception e) {
            return new ResponseEntity(new Result(false, e.getLocalizedMessage()), BAD_REQUEST);
        }
    }
}
