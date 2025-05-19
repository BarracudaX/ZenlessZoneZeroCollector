package com.arslan.zzz;

import com.arslan.zzz.domain.Attribute;
import com.arslan.zzz.domain.Enemy;
import com.arslan.zzz.multitenancy.TenantContext;
import com.arslan.zzz.repository.EnemyRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Profile("dev")
@Service
public class DevRunner implements ApplicationRunner {

    private final EnemyRepository enemyRepository;


    @Value("${multitenant.schemas}")
    private List<String> schemas;

    public DevRunner(EnemyRepository enemyRepository) {
        this.enemyRepository = enemyRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        schemas.forEach( schema -> {
            TenantContext.setTenantID(schema);
            if (enemyRepository.count() == 0) {
                var enemy = new Enemy(Set.of(Attribute.FIRE, Attribute.ETHER), Set.of(Attribute.ELECTRIC, Attribute.ICE), 1_000_000, "", "Enemy");
                enemyRepository.save(enemy);
            }
            TenantContext.clear();
        });
    }
}
