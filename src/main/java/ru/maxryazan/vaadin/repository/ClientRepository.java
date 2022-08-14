package ru.maxryazan.vaadin.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.maxryazan.vaadin.model.Client;
import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("select c from Client c WHERE c.phoneNumber = (:searchTerm)")
    List<Client> search(@Param("searchTerm") String searchTerm);

    Client findByPhoneNumber(String phone);
    Client findByEmail(String email);
}
