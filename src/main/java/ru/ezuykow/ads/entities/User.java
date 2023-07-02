package ru.ezuykow.ads.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.ezuykow.ads.dto.Role;

import javax.persistence.*;
import java.util.List;

/**
 * @author ezuykow
 */
@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "email")
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "image")
    private String image;

    @Column(name = "encoded_password")
    private String encodedPassword;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Ad> ads;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Comment> comments;

    public User(String email, String firstName, String lastName, String phone, Role role, String image, String encodedPassword, List<Ad> ads, List<Comment> comments) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.role = role;
        this.image = image;
        this.encodedPassword = encodedPassword;
        this.ads = ads;
        this.comments = comments;
    }
}
