package com.dedalus.interview.config;

import com.dedalus.interview.entity.Department;
import com.dedalus.interview.entity.Employee;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.logging.Logger;

@ApplicationScoped
public class DataInitializer {

    private static final Logger logger = Logger.getLogger(DataInitializer.class.getName());

    @Inject
    EntityManager entityManager;

    @Transactional
    void onStart(@Observes StartupEvent ev) {
        logger.info("Initializing sample data...");

        // Check if data already exists
        long count = entityManager.createQuery("SELECT COUNT(d) FROM Department d", Long.class)
                .getSingleResult();

        if (count > 0) {
            logger.info("Sample data already exists, skipping initialization");
            return;
        }

        // Create departments
        Department engineering = new Department("Engineering");
        Department hr = new Department("Human Resources");
        Department marketing = new Department("Marketing");
        Department finance = new Department("Finance");

        entityManager.persist(engineering);
        entityManager.persist(hr);
        entityManager.persist(marketing);
        entityManager.persist(finance);

        // Create employees
        Employee john = new Employee("John Doe", "123 Main St, Anytown, USA", "+1-555-0101", "john.doe@company.com");
        john.setDepartment(engineering);
        entityManager.persist(john);

        Employee jane = new Employee("Jane Smith", "456 Oak Ave, Somewhere, USA", "+1-555-0102", "jane.smith@company.com");
        jane.setDepartment(engineering);
        entityManager.persist(jane);

        Employee bob = new Employee("Bob Johnson", "789 Pine St, Anywhere, USA", "+1-555-0103", "bob.johnson@company.com");
        bob.setDepartment(hr);
        entityManager.persist(bob);

        Employee alice = new Employee("Alice Brown", "321 Elm St, Nowhere, USA", "+1-555-0104", "alice.brown@company.com");
        alice.setDepartment(marketing);
        entityManager.persist(alice);

        Employee charlie = new Employee("Charlie Wilson", "654 Cedar Ave, Everywhere, USA", "+1-555-0105", "charlie.wilson@company.com");
        charlie.setDepartment(engineering);
        entityManager.persist(charlie);

        Employee diana = new Employee("Diana Davis", "987 Birch St, Somewhere Else, USA", "+1-555-0106", "diana.davis@company.com");
        diana.setDepartment(finance);
        entityManager.persist(diana);

        // Unassigned employees
        Employee eve = new Employee("Eve Anderson", "147 Maple Ave, Another Place, USA", "+1-555-0107", "eve.anderson@company.com");
        entityManager.persist(eve);

        Employee frank = new Employee("Frank Miller", "258 Spruce St, Different Town, USA", "+1-555-0108", "frank.miller@company.com");
        entityManager.persist(frank);

        logger.info("Sample data initialized successfully");
    }
}