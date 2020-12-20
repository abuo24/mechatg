package mechat.group.service;

import mechat.group.entity.Admins;
import mechat.group.entity.Role;
import mechat.group.model.Result;
import mechat.group.repository.AdminRepo;
import mechat.group.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class AdminServiceImp {

    @Autowired
    private AdminRepo adminRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Admins> getAllAdmin() {
        try {
            return adminRepository.findAll();
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public Admins create(Admins user) {
        try {
            user.setPassword(
                    passwordEncoder.encode(
                            user.getPassword()
                    )
            );
            Set<Role> roles=new HashSet<>();
            roles.add(roleRepository.findByName("ROLE_USER"));
            roles.add(roleRepository.findByName("ROLE_ADMIN"));
            user.setRoles(roles);
            return adminRepository.save(user);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public Admins update(Long id, Admins admins) {
        try {
            Admins user1 = adminRepository.findById(id).get();
            user1.setFullname(admins.getFullname());
            user1.setUsername(admins.getUsername());
            user1.setAbout(admins.getAbout());
            user1.setFacebook(admins.getFacebook());
            user1.setInstagram(admins.getInstagram());
            user1.setTelegram(admins.getTelegram());
            user1.setTelegram(admins.getTelegram());
            user1.setPassword(
                    passwordEncoder.encode(
                            admins.getPassword()
                    )
            );

            return adminRepository.save(user1);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public Result delete(Long id) {
        try {
            adminRepository.deleteById(id);
            return new Result(true, "admin deleted");
        } catch (Exception e) {
            System.out.println(e);
            return new Result(false, e.getMessage());
        }
    }

}