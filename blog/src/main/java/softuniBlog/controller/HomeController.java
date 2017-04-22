package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import softuniBlog.entity.Article;
import softuniBlog.repository.ArticleRepository;
import softuniBlog.repository.UserRepository;
import softuniBlog.service.UserSearch;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private UserSearch userSearch;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String index(Model model) {
        List<Article> articles = this.articleRepository.findAll();

       Object currentUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

       if ("anonymousUser".equals(currentUser.toString())) {
           model.addAttribute("username","GUEST");
       } else {
           UserDetails user = (UserDetails) currentUser;
           String name = this.userRepository.findByEmail(user.getUsername()).getFullName();
           model.addAttribute("username",name);
       }

        model.addAttribute("view", "home/index");
        model.addAttribute("articles", articles);

        return "base-layout";
    }


    /**
     * Show search results for the given query.
     *
     * @param search The search query.
     */
    @RequestMapping(value = "/search", method = {RequestMethod.GET})

    public String search(@RequestParam("search") String search, Model model) {
        List<Article> searchResults = null;



        searchResults = userSearch.search(search);
        if (searchResults.isEmpty()){
            return "redirect:/noresult";
        }
        Object currentUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails user = (UserDetails) currentUser;
        String name = this.userRepository.findByEmail(user.getUsername()).getFullName();
        model.addAttribute("username",name);

        model.addAttribute("view", "home/search");
        model.addAttribute("searchResults", searchResults);

        return "base-layout";
    }
    @GetMapping("/about")
    public String about(Model model) {

        Object currentUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails user = (UserDetails) currentUser;
        String name = this.userRepository.findByEmail(user.getUsername()).getFullName();
        model.addAttribute("username",name);

        model.addAttribute("view", "home/about");
        return "base-layout";
    }
    @GetMapping("/noresult")
    public String noResult(Model model) {

        Object currentUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails user = (UserDetails) currentUser;
        String name = this.userRepository.findByEmail(user.getUsername()).getFullName();
        model.addAttribute("username",name);

        model.addAttribute("view", "home/noresult");
        return "base-layout";
    }







}
