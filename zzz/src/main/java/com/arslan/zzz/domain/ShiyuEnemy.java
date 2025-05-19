package com.arslan.zzz.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;

@Embeddable
public record ShiyuEnemy(
        @ManyToOne
        Enemy enemy,

        int count
) { }
