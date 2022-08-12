package ru.maxryazan.vaadin;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;
import ru.maxryazan.vaadin.model.Client;
import javax.annotation.security.PermitAll;



@org.springframework.stereotype.Component
@Scope("prototype")
@Route("")
@PageTitle("Clients | Vaadin CRM")
@PermitAll
@StyleSheet("/ru/maxryazan/vaadin/style.css")
public class ListView extends VerticalLayout {
    Grid<Client> clientGrid = new Grid<>(Client.class);
    TextField searchField = new TextField();
    AddClientForm addClientForm;
    CrmService service;


    public ListView(CrmService service) {  // конструктор класса
        this.service = service;
        addClassName("list-view");
        setSizeFull();
        gridConfig();
        addClientForm = new AddClientForm();
        addClientForm.setWidth("25rem");
        addClientForm.addListener(AddClientForm.SaveEvent.class, this::saveClient);
        addClientForm.addListener(AddClientForm.DeleteEvent.class, this::deleteClient);
        addClientForm.addListener(AddClientForm.CloseEvent.class, e -> closeEditor());
        addClientForm.addClassNames("ms-2", "color: red");

        FlexLayout content = new FlexLayout(clientGrid, addClientForm);
        content.setFlexGrow(2, clientGrid);
        content.setFlexGrow(1, addClientForm);
        content.setFlexShrink(0, addClientForm);
        content.addClassNames("content", "gap-m");
        content.setSizeFull();


        add(printToolBar(), content);
        updateList();
        closeEditor();
        clientGrid.asSingleSelect().addValueChangeListener(event ->
                editClient(event.getValue()));
    }


    public void gridConfig(){    // создали таблицу
        clientGrid.addClassName("clientGrid");
        clientGrid.setWidthFull();
        clientGrid.setColumns("id", "phoneNumber","firstName", "lastName", "email", "balance");
        clientGrid.getColumns().forEach(clientColumn -> clientColumn.setAutoWidth(true));
        clientGrid.setHeight("100%");
    }


    public HorizontalLayout printToolBar(){   // гориз. "хидер" с поиском и кнопкой
        searchField.setPlaceholder("8...");
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> updateList());

        Button findClientBtn = new Button("Search");
        findClientBtn.addClickListener(click -> addClient());

        HorizontalLayout toolbar = new HorizontalLayout(searchField, findClientBtn);
        toolbar.addClassName("toolbar");
        return toolbar;
    }


    private void saveClient(AddClientForm.SaveEvent event) {
        service.saveClient(event.getClient());
        updateList();
        closeEditor();
    }

    private void deleteClient(AddClientForm.DeleteEvent event) {
        service.deleteClient(event.getClient());
        updateList();
        closeEditor();
    }

    public void editClient(Client client) {
        if (client == null) {
            closeEditor();
        } else {
            addClientForm.setClient(client);
            addClientForm.setVisible(true);
            addClassName("editing");
        }
    }

    void addClient() {
        clientGrid.asSingleSelect().clear();
        editClient(new Client());
    }

    private void closeEditor() {
        addClientForm.setClient(null);
        addClientForm.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        clientGrid.setItems(service.findAllClients(searchField.getValue()));
    }
}
