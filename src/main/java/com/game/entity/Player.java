package com.game.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;


@Entity
@Table(name = "player")
public class Player {
    private static final AtomicInteger CLIENT_ID_HOLDER = new AtomicInteger();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Long id;
    @Column(name = "name")
    String name;

    @Column(name = "title")
    String title;
    @Column(name = "race")
    @Enumerated(EnumType.STRING)
    Race race;
    @Column(name = "profession")
    @Enumerated(EnumType.STRING)
    Profession profession;
    @Column(name = "birthday")
    Date birthday;
    @Column(name = "banned")
    boolean banned;
    @Column(name = "experience")
    Integer experience;
    @Column(name = "level")
    Integer level;
    @Column(name = "untilNextLevel")
    Integer untilNextLevel;

    public Player() {
    }

    public Player(String name, String title, Race race, Profession profession, Date birthday, Integer experience) {
        this.id = (long)CLIENT_ID_HOLDER.incrementAndGet();
        this.name = name;
        this.title = title;
        this.race = race;
        this.profession = profession;
        this.birthday = birthday;
        this.experience = experience;
        this.banned = false;
        this.level = calculateLevel(experience);
        this.untilNextLevel = calculateUntilNextLvl(experience);
    }

    public Player(String name, String title, Race race, Profession profession, Date birthday, boolean banned, Integer experience) {
        this.id = (long)CLIENT_ID_HOLDER.incrementAndGet();
        this.name = name;
        this.title = title;
        this.race = race;
        this.profession = profession;
        this.birthday = birthday;
        this.banned = banned;
        this.experience = experience;
        this.level = calculateLevel(experience);
        this.untilNextLevel = calculateUntilNextLvl(experience);
    }

    public Player(Long id, String name, String title, Race race, Profession profession, Date birthday, boolean banned, Integer experience) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.race = race;
        this.profession = profession;
        this.birthday = birthday;
        this.banned = banned;
        this.experience = experience;
        this.level = calculateLevel(experience);
        this.untilNextLevel = calculateUntilNextLvl(experience);
    }

    public Integer calculateLevel(Integer experience){
        return (int)((Math.sqrt( 2500 + 200 * experience) - 50 ) / 100);
    }

    public Integer calculateUntilNextLvl(Integer experience){
        return (50 * (this.level + 1) * (this.level + 2)) - this.experience;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getUntilNextLevel() {
        return untilNextLevel;
    }

    public void setUntilNextLevel(Integer untilNextLevel) {
        this.untilNextLevel = untilNextLevel;
    }
}
