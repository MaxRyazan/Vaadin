package ru.maxryazan.vaadin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.maxryazan.vaadin.model.Credit;
@Repository
public interface CreditRepository extends JpaRepository<Credit, Long> {
    Credit findByNumberOfCreditContract(String num);
}
