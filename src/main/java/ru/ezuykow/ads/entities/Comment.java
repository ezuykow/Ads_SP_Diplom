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

    @Column(name = "ad_id")
    private Integer adId;

    @Column(name = "author_id")
    private Integer authorId;

    @Column(name = "creating_time")
    private Long creatingTime;

    @Column(name = "comment_text")
    private String text;
}
