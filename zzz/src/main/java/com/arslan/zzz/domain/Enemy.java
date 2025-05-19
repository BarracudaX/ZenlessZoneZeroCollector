package com.arslan.zzz.domain;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@SequenceGenerator(name = "enemies_sequence",allocationSize = 1)
@Table(name = "enemies")
@Entity
public class Enemy {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "enemies_sequence")
    private Long id;

    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "weaknesses")
    @ElementCollection
    @Column(name = "attribute")
    private Set<Attribute> weaknesses = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "resistances")
    @ElementCollection
    @Column(name = "attribute")
    private Set<Attribute> resistances = new HashSet<>();

    private long hp;

    private String name;

    private String img;

    protected Enemy(){

    }

    public Enemy(Set<Attribute> weaknesses, Set<Attribute> resistances, long hp, String img,String name) {
        this.weaknesses.addAll(weaknesses);
        this.resistances.addAll(resistances);
        this.hp = hp;
        this.img = img;
        this.name = name;
    }
}
