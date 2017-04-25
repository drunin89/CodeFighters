package softuniBlog.entity;


import org.aspectj.weaver.GeneratedReferenceTypeDelegate;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;
import javax.validation.constraints.NotNull;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Indexed
@Table(name = "articles")
public class Article {

    private Integer id;
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    @NotNull
    private String title;
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    @NotNull
    private String content;


    private Date date;

    private User author;

    private String imagePath;

    private Set<Comment> comments;

    public Article(String title, String content, User author, String imagePath) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.imagePath = imagePath;
        this.date = new Date();

        this.comments=new HashSet<>();
    }

    public Article(){

    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "title", nullable = false)

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "content", nullable = false, columnDefinition = "text")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Column(name = "date", nullable = true)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @ManyToOne()
    @JoinColumn(nullable = false, name = "authorId")
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
    @Transient
    public String getSummary(){
        return this.getContent().substring(0, this.getContent().length()/2) + "...";
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @OneToMany(mappedBy = "article")
    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }
}
