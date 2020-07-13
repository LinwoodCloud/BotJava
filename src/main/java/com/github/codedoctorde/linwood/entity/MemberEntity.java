package com.github.codedoctorde.linwood.entity;

import org.hibernate.Session;

import javax.persistence.*;

/**
 * @author CodeDoctorDE
 */
@Entity
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "memberId")
    private long memberId;
    @Column(name = "guildId")
    private long guildId;
    private String locale = null;
    private int points;
    private int likes = 0;
    private int dislikes = 0;

    public MemberEntity(long guildId, long memberId) {
        this.guildId = guildId;
        this.memberId = memberId;
    }
    public MemberEntity(){}

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public long getMemberId() {
        return memberId;
    }

    public void save(Session session) {
        var t = session.beginTransaction();
        session.saveOrUpdate(this);
        t.commit();
    }
}
