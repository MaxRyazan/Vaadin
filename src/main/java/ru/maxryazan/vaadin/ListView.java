package ru.maxryazan.vaadin;


import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Scope;
import ru.maxryazan.vaadin.model.Client;
import ru.maxryazan.vaadin.service.CreditService;
import ru.maxryazan.vaadin.service.CrmService;

import javax.annotation.security.PermitAll;

@org.springframework.stereotype.Component
@Scope("prototype")
@Route("")
@PageTitle("Clients | Vaadin CRM")
@PermitAll
@CssImport("./my-styles/styles.css")
public class ListView extends VerticalLayout {

    Grid<Client> clientGrid = new Grid<>(Client.class);
    TextField searchField = new TextField();
    AddClientForm addClientForm;
    NewCreditForm newCreditForm;
    private final CrmService service;


    public ListView(CrmService service, CreditService creditService) {  // конструктор класса
        this.service = service;
        addClassName("list-view");
        setSizeFull();
        gridConfig();
        addClientForm = new AddClientForm(service);
        newCreditForm = new NewCreditForm(service, creditService);

        newCreditForm.setWidth("25rem");
        addClientForm.setWidth("25rem");
        addClientForm.addListener(AddClientForm.SaveEvent.class, this::saveClient);
        addClientForm.addListener(AddClientForm.DeleteEvent.class, this::deleteClient);
        addClientForm.addListener(AddClientForm.CloseEvent.class, e -> closeEditor());
        addClientForm.addClassName("client-form");

        showHideButton.setText("Редактировать/добавить клиента");
        showHideButton2.setText("Оформить кредит");
        showHideButton.addClickListener(buttonClickEvent -> buttonClick(addClientForm));
        showHideButton2.addClickListener(buttonClickEvent -> buttonClick(newCreditForm));

        VerticalLayout menu = new VerticalLayout(showHideButton, addClientForm);
        VerticalLayout addCredit = new VerticalLayout(showHideButton2, newCreditForm);
        menu.setWidth("25rem");
        menu.addClassName("menu");
        addCredit.setWidth("25rem");
        addCredit.addClassName("menu");

        VerticalLayout twoMenus = new VerticalLayout(menu, addCredit);
        twoMenus.setWidth("25rem");
        twoMenus.addClassName("menu");

        FlexLayout content = new FlexLayout(clientGrid, twoMenus);
        content.setSizeFull();

        add(printToolBar(), content);
        updateList();
        closeEditor();
        clientGrid.asSingleSelect().addValueChangeListener(event ->
                editClient(event.getValue()));


    }

    Button showHideButton = createShowHideButton();
    Button showHideButton2 = createShowHideButton();
    public Button createShowHideButton(){
        Button button = new Button();
        button.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        button.addClassName("showHideButton");
        button.setIconAfterText(true);
        button.setIcon(new Icon(VaadinIcon.BULLETS));
        return button;
    }

    public void buttonClick(FormLayout formLayout) {
        formLayout.setVisible(!formLayout.isVisible());
    }

    public void gridConfig(){    // создали таблицу
        clientGrid.addClassName("clientGrid");
        clientGrid.setWidthFull();
        clientGrid.setColumns("id", "phoneNumber","firstName", "lastName", "email", "balance");
        clientGrid.getColumns().forEach(clientColumn -> clientColumn.setAutoWidth(true));
        clientGrid.setHeight("100%");
        clientGrid.getColumnByKey("id").setHeader(new Html
                ("<div style='color: green; font-weight: bold; background-color: LightCyan;'>id</div>"));
        clientGrid.getColumnByKey("phoneNumber").setHeader(new Html
                ("<div style='color: green; font-weight: bold; background-color: LightCyan;'>Phone number</div>"));
        clientGrid.getColumnByKey("firstName").setHeader(new Html
                ("<div style='color: green; font-weight: bold; background-color: LightCyan;'>First name</div>"));
        clientGrid.getColumnByKey("lastName").setHeader(new Html
                ("<div style='color: green; font-weight: bold; background-color: LightCyan;'>Last name</div>"));
        clientGrid.getColumnByKey("email").setHeader(new Html
                ("<div style='color: green; font-weight: bold; background-color: LightCyan;'>Email</div>"));
        clientGrid.getColumnByKey("balance").setHeader(new Html
                ("<div style='color: green; font-weight: bold; background-color: LightCyan;'>Balance</div>"));
    }


    public HorizontalLayout printToolBar(){   // гориз. "хидер" с поиском и кнопкой
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> updateList());
        searchField.addClassName("inputField");
        searchField.addThemeVariants(TextFieldVariant.LUMO_SMALL);

        Button findClientBtn = new Button("Search");
        findClientBtn.addClickListener(click -> addClient());
        findClientBtn.addClassName("searchBtn");
        findClientBtn.addThemeVariants(ButtonVariant.LUMO_SMALL);
        HorizontalLayout toolbar = new HorizontalLayout(searchField, findClientBtn);
        toolbar.addClassName("toolbar");
        return toolbar;
    }


    private void saveClient(AddClientForm.SaveEvent event) {
        Client client = event.getClient();
        service.saveClient(client);
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
