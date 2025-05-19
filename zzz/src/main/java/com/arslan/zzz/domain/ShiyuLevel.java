package com.arslan.zzz.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collection;

@SequenceGenerator(name = "shiyu_levels_sequence", allocationSize = 1)
@Table(name = "shiyu_levels")
@Entity
public class ShiyuLevel {

    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private Floor floor;

    @Enumerated(EnumType.STRING)
    private Round round;

    @ManyToOne
    private Shiyu shiyu;

    @CollectionTable(name = "shiyu_level_enemies")
    @ElementCollection
    private Collection<ShiyuEnemy> enemies = new ArrayList<>();

}
