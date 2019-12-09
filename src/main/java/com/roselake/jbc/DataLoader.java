package com.roselake.jbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... strings) throws Exception {

        roleRepository.save(new Role("USER"));
        roleRepository.save(new Role("ADMIN"));

        Role adminRole = roleRepository.findByRole("ADMIN");
        Role userRole = roleRepository.findByRole("USER");

        User kevin = new User("kevin", "cheung", "Kevin", "Cheung", "kcheung@cheung.com", true);
        kevin.setRoles(Arrays.asList(adminRole));
        userRepository.save(kevin);

        User user = new User("user", "user", "User", "Last", "user@user.com", true);
        user.setRoles(Arrays.asList(userRole));
        userRepository.save(user);


        Message message1 = new Message("stuff 1","12/09/2019","https://images.unsplash.com/photo-1476820865390-c52aeebb9891?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1050&q=80","Kevin");
        messageRepository.save(message1);

    }
}
//
//        //Creating Messages and saving them
//       messageRepository.save(new Message("content", "12/09/2019"));
//       messageRepository.save(new Message("another content", "12/09/2019"));
//
//
//        //linking the message to the user
//        kevin.setMessage(message);
//        userRepository.save(kevin);
//
//        user.setMessage(message);
//        userRepository.save(user);
//
//
//
//    }
//}
