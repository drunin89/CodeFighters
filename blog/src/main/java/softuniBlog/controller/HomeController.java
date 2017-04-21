package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import softuniBlog.entity.Article;
import softuniBlog.repository.ArticleRepository;
import softuniBlog.service.UserSearch;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private UserSearch userSearch;

    @GetMapping("/")
    public String index(Model model) {
        List<Article> articles = this.articleRepository.findAll();

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



        model.addAttribute("view", "home/search");
        model.addAttribute("searchResults", searchResults);
        return "base-layout";
    }
    @GetMapping("/about")
    public String about(Model model) {

        model.addAttribute("view", "home/about");
        return "base-layout";
    }
    @GetMapping("/noresult")
    public String noResult(Model model) {

        model.addAttribute("view", "home/noresult");
        return "base-layout";
    }



}
