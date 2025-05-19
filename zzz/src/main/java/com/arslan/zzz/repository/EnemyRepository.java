package com.arslan.zzz.repository;

import com.arslan.zzz.domain.Enemy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnemyRepository extends JpaRepository<Enemy,Long> {
}
