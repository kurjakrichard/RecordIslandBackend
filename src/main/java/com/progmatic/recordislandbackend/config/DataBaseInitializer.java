package com.progmatic.recordislandbackend.config;

import com.progmatic.recordislandbackend.domain.Authority;
import com.progmatic.recordislandbackend.dto.RegistrationDto;
import com.progmatic.recordislandbackend.exception.AlreadyExistsException;
import com.progmatic.recordislandbackend.service.UserService;;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DataBaseInitializer {

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private UserService userService;
    
    @Transactional
    public void init() throws AlreadyExistsException {
        if (em.createQuery("SELECT aut FROM Authority aut").getResultList().isEmpty()) {
            em.persist(new Authority("ROLE_USER"));
            em.persist(new Authority("ROLE_ADMIN"));
            em.flush();
            RegistrationDto registration = new RegistrationDto("admin", "admin", "admin@recordisland.com");
            userService.createUser(registration);
        }
    }

    @EventListener(classes = ContextRefreshedEvent.class)
    public void onAppStartup(ContextRefreshedEvent ev) throws AlreadyExistsException {
        DataBaseInitializer dbInitializer = ev.getApplicationContext().getBean(DataBaseInitializer.class);
        dbInitializer.init();
    }

}
