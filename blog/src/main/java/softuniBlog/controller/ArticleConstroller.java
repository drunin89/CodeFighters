package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import softuniBlog.bindingModel.ArticleBindingModel;
import softuniBlog.entity.Article;
import softuniBlog.entity.User;
import softuniBlog.repository.ArticleRepository;
import softuniBlog.repository.UserRepository;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Controller
public class ArticleConstroller {
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/article/create")
    @PreAuthorize("isAuthenticated()")
    public String create(Model model){
        model.addAttribute("view", "/article/create");
        return "base-layout";
    }


    @PostMapping("/article/create")
    @PreAuthorize("isAuthenticated()")
    public String createProcess(ArticleBindingModel articleBindingModel){

        UserDetails user = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User userEntity = this.userRepository.findByEmail(user.getUsername());

        String databasePathImage = null;

        String[] allowedContentTypes = {
                "image/png",
                "image/jpg",
                "image/jpeg"
        };

        boolean isContentTypeAllowed =
                Arrays.asList(allowedContentTypes)
                        .contains(articleBindingModel.getImage().getContentType());

        if(isContentTypeAllowed){
            String imagesPath = "\\src\\main\\resources\\static\\img\\";
            String imagePathRoot = System.getProperty("user.dir");
            String imageSaveDirectory = imagePathRoot + imagesPath;

            String fileName = articleBindingModel.getImage().getOriginalFilename();

            String savePath = imageSaveDirectory + fileName;

            File imageFile = new File(savePath);
            try {
                articleBindingModel.getImage().transferTo(imageFile);
                databasePathImage = "/img/" + fileName;
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }


        }


        Article articleEntity = new Article(
                articleBindingModel.getTitle(),
                articleBindingModel.getContent(),
                userEntity,
                databasePathImage
        );
        this.articleRepository.saveAndFlush(articleEntity);
        return "redirect:/";

    }


    @GetMapping("/article/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String edit(@PathVariable Integer id, Model model){
        if(!this.articleRepository.exists(id)){
            return "redirect:/";
        }
        Article article = this.articleRepository.findOne(id);

        if(!this.isUserAuthorOrAdmin(article)){
            return "redirect:/";
        }

        model.addAttribute("view", "article/edit");
        model.addAttribute("article", article);

        return "base-layout";
    }



    @GetMapping("/article/{id}")
    public String details(Model model, @PathVariable Integer id) {
        if(!this.articleRepository.exists(id)){
            return "redirect:/";
        }

        if(!(SecurityContextHolder.getContext().getAuthentication()
                instanceof AnonymousAuthenticationToken)){
            UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();

            User entityUser = this.userRepository.findByEmail(principal.getUsername());

            model.addAttribute("user", entityUser);
        }

        Article article = this.articleRepository.findOne(id);
        model.addAttribute("view", "article/details");
        model.addAttribute("article", article);

        return "base-layout";
    }

    @PostMapping("/article/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editProcess(@PathVariable Integer id, ArticleBindingModel articleBindingModel) {
        if(!this.articleRepository.exists(id)){
            return "redirect:/";
        }
        Article article = this.articleRepository.findOne(id);
        if(!this.isUserAuthorOrAdmin(article)){
            return "redirect:/";
        }
        article.setTitle(articleBindingModel.getTitle());
        article.setContent(articleBindingModel.getContent());
        this.articleRepository.saveAndFlush(article);
        return "redirect:/article/" + article.getId();

    }

    @GetMapping("/article/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String delete(@PathVariable Integer id, Model model){
        if(!this.articleRepository.exists(id)){
            return "redirect:/";
        }

        Article article = this.articleRepository.findOne(id);
        if(!this.isUserAuthorOrAdmin(article)){
            return "redirect:/";
        }
        model.addAttribute("article", article);
        model.addAttribute("view", "article/delete");

        return "base-layout";
    }

    @PostMapping("/article/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteProcess(@PathVariable Integer id) {
        if(!this.articleRepository.exists(id)){
            return "redirect:/";
        }
        Article article = this.articleRepository.findOne(id);
        if(!this.isUserAuthorOrAdmin(article)){
            return "redirect:/";
        }
        this.articleRepository.delete(article);

        return "redirect:/";

    }

    private boolean isUserAuthorOrAdmin(Article article){
        UserDetails user = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User userEntity = this.userRepository.findByEmail(user.getUsername());
        return userEntity.isAdmin() || userEntity.isAuthor(article);
    }
}
