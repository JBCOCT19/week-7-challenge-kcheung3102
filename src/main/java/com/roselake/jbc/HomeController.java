package com.roselake.jbc;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String index(Model model){
        if (userService.getUser() != null) {
            model.addAttribute("user_id", userService.getUser().getId());
        }
        model.addAttribute("messages", messageRepository.findAll());
        return "index";
    }

    @GetMapping("/add")
    public String addMessage(Model model) {
        model.addAttribute("message", new Message());
        return "messageform";
    }

    @PostMapping("/process")
    public String processForm(@Valid @ModelAttribute Message message, @RequestParam("file") MultipartFile file){
        if(!file.isEmpty()){
            try {
                Map uploadResult = cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
                message.setImage(uploadResult.get("url").toString());
                messageRepository.save(message);
            } catch (IOException e) {
                e.printStackTrace();
                return "redirect:/add";
            }
        }
        else{
            messageRepository.save(message);
        }
        return "redirect:/secure";
    }


    @RequestMapping("/login")
    public String login(Model model){

        // this bit of code is especially good here, as logout redirects to "/login?logout"
        if (userService.getUser() != null) {
            model.addAttribute("user_id", userService.getUser().getId());
        }

        return "login";

    }

    @RequestMapping("/admin")
    public String admin(Principal principal, Model model){
        String username = principal.getName();
        model.addAttribute("user", userRepository.findByUsername(username));
        if (userService.getUser() != null) {
            model.addAttribute("user_id", userService.getUser().getId());
        }
        return "admin";
    }

    @RequestMapping("/secure")
    public String secure(Principal principal, Model model){
        String username = principal.getName();
        model.addAttribute("user", userRepository.findByUsername(username));
        if (userService.getUser() != null) {
            model.addAttribute("user_id", userService.getUser().getId());
        }
        model.addAttribute("messages", messageRepository.findAll());
        return "secure";
    }

    @GetMapping("/register")
    public String showRegistrationPage(Model model){
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/register")
    public String processRegistrationPage(@Valid @ModelAttribute("user") User user,
                                          BindingResult result,
                                          Model model) {

        // if there are validation errors ::
        if (result.hasErrors()) { return "registration"; }

        // if the username already exists ::
        if (userService.countByUsername(user.getUsername()) > 0) {
            model.addAttribute("message", "username already exists");
            return "registration";
        }

        // if the email already exists ::
        if (userService.countByEmail(user.getEmail()) > 0) {
            model.addAttribute("message", "this email has already been used to register");
            return "registration";
        }

        // if we get this far, then the username and email are valid, and we can move on...
        userService.saveUser(user);
        model.addAttribute("message", "User Account Created");
        model.addAttribute("user", user);
        model.addAttribute("user_id", userService.getUser().getId());
        return "index";

    }

    @RequestMapping("/update/{id}")
    public String updateMessage(@PathVariable("id") long id,
                                Model model){
        model.addAttribute("message", messageRepository.findById(id).get());
        return "messageform";
    }

    @RequestMapping("/delete/{id}")
    public String deleteMessage(@PathVariable("id") long id){
        messageRepository.deleteById(id);
        return "redirect:/secure";
    }

    @RequestMapping("/detail/{id}")
    public String showMessage(@PathVariable("id") long id, Model model) {
        model.addAttribute("message" , messageRepository.findById(id).get());
        return "show";
    }

    }


