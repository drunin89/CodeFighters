package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import softuniBlog.repository.UserRepository;

@ControllerAdvice
public class UserControllerAdvice {
    @Autowired
    private UserRepository userRepository;

    @ModelAttribute
    public void addUserAttribute(Model model) {

        Object currentUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if ("anonymousUser".equals(currentUser.toString())) {
            model.addAttribute("username", "GUEST");
        } else {
            UserDetails user = (UserDetails) currentUser;
            String name = this.userRepository.findByEmail(user.getUsername()).getFullName();
            model.addAttribute("username", name);
        }
    }
}