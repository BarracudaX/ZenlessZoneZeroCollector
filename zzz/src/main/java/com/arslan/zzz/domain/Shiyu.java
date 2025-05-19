package com.arslan.zzz.domain;

import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

@SequenceGenerator(name = "shiyu_sequence", allocationSize = 1)
@Table(name = "shiyu")
@Entity
public class Shiyu {

    @Id
    private Long id;

}
