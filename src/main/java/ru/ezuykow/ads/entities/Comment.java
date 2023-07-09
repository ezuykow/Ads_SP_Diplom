package ru.ezuykow.ads.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @author ezuykow
 */
@Entity
@Table(name = "comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ad_id")
    private Ad ad;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @Column(name = "creating_time")
    private Long creatingTime;

    @Column(name = "comment_text")
    private String text;

    public Comment(Ad ad, User author, Long creatingTime, String text) {
        this.ad = ad;
        this.author = author;
        this.creatingTime = creatingTime;
        this.text = text;
    }
}
