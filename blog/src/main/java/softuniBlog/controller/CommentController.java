package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
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
import softuniBlog.bindingModel.CommentBindingModel;
import softuniBlog.entity.Article;
import softuniBlog.entity.Comment;
import softuniBlog.entity.User;
import softuniBlog.repository.ArticleRepository;
import softuniBlog.repository.CommentRepository;
import softuniBlog.repository.UserRepository;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Controller
public class CommentController {
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;

    @GetMapping("/article/comment/{id}")
    @PreAuthorize("isAuthenticated()")
    public String createComment(Model model, @PathVariable Integer id){

        Object currentUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails user = (UserDetails) currentUser;
        String name = this.userRepository.findByEmail(user.getUsername()).getFullName();
        model.addAttribute("username",name);
        model.addAttribute("view", "/article/details");
        return "base-layout";
    }


    @PostMapping("/article/comment/{id}")
    @PreAuthorize("isAuthenticated()")
    public String createCommentProcess(CommentBindingModel commentBindingModel,@PathVariable Integer id){

        UserDetails user = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        User userEntity = this.userRepository.findByEmail(user.getUsername());

        Article article=this.articleRepository.findOne(id);

        Comment commentEntity = new Comment(
                commentBindingModel.getContent(),
                userEntity,
                article
        );

        this.commentRepository.saveAndFlush(commentEntity);
        return "redirect:/article/{id}";

    }



}
