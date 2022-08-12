package ru.maxryazan.vaadin;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import ru.maxryazan.vaadin.model.Client;
import ru.maxryazan.vaadin.repository.ClientRepository;

import java.util.List;

@Service

public class CrmService {
    private final ClientRepository clientRepository;

    public CrmService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> findAllClients(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return clientRepository.findAll();
        } else {
            return clientRepository.search(stringFilter);
        }
    }


    public void deleteClient(Client client) {
        clientRepository.delete(client);
    }

    public void saveClient(Client client) {
        if (client == null) {
            System.err.println("Client is null. Are you sure you have connected your form to the application?");
            return;
        }
        clientRepository.save(client);
    }




}
