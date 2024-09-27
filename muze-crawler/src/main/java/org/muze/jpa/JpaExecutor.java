package org.muze.jpa;

import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.muze.domain.Actor;
import org.muze.domain.Casting;
import org.muze.domain.Musical;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JpaExecutor {

    private static final Logger log = LoggerFactory.getLogger(JpaExecutor.class);
    private static final JpaExecutor INSTANCE = new JpaExecutor();
    private final EntityManagerFactory emf;


    public JpaExecutor() {
        String emUnit = "playDB-dev";
        this.emf = Persistence.createEntityManagerFactory(emUnit);
        log.info("{} EntityManagerFactory created", emUnit);
    }

    public static JpaExecutor getInstance() {
        return INSTANCE;
    }

    public void saveMusicals(Map<Musical, List<Actor>> musicals) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            for (Map.Entry<Musical, List<Actor>> entry : musicals.entrySet()) {
                Musical musical = entry.getKey();
                em.persist(musical);  // Musical을 먼저 저장

                for (Actor actor : entry.getValue()) {
                    em.persist(actor);  // 각 Actor를 저장

                    // 새로운 Casting 생성
                    Casting casting = Casting.builder()
                            .musical(musical)
                            .actor(actor)
                            .role(actor.getRole())
                            .build();
                    // Casting을 저장
                    em.persist(casting);

                }
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
