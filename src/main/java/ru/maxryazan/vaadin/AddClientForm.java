package ru.maxryazan.vaadin;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.maxryazan.vaadin.model.Client;
import ru.maxryazan.vaadin.repository.ClientRepository;


public class AddClientForm extends FormLayout {
     private Client client;

    Binder<Client> binder = new BeanValidationBinder<>(Client.class);

    TextField phoneNumber = new TextField("Phone number");
    TextField firstName = new TextField("First name");
    TextField lastName = new TextField("Last name");
    EmailField email = new EmailField("Email");


    Button saveBtn = new Button("Save", new Icon(VaadinIcon.USER_CHECK));
    Button deleteBtn = new Button("Delete", new Icon(VaadinIcon.TRASH));
    Button closeBtn = new Button("Close");


    public AddClientForm(){
        binder.bindInstanceFields(this);
        phoneNumber.addClassName("inputFieldsColor");
        firstName.addClassName("inputFieldsColor");
        lastName.addClassName("inputFieldsColor");
        email.addClassName("inputFieldsColor");

        add(phoneNumber,
                firstName,
                lastName,
                email,
                createBtns());
    }

    public HorizontalLayout createBtns(){
        saveBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        saveBtn.addClassName("btn");
        saveBtn.setIconAfterText(true);

        deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        deleteBtn.addClassName("btn");
        deleteBtn.setIconAfterText(true);

        closeBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        closeBtn.addClassName("btn");

        saveBtn.addClickShortcut(Key.ENTER);
        closeBtn.addClickShortcut(Key.ESCAPE);

        saveBtn.addClickListener(event -> validateAndSave());
        deleteBtn.addClickListener(event -> fireEvent(new DeleteEvent(this, client)));
        closeBtn.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> saveBtn.setEnabled(binder.isValid()));

        return new HorizontalLayout(saveBtn, deleteBtn, closeBtn);
    }

    public void setClient(Client client) {
        this.client = client;
        binder.readBean(client);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(client);
            fireEvent(new SaveEvent(this, client));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }



    /* ------------------------------------------------------------------------------- */


    public static abstract class ClientFormEvent extends ComponentEvent<AddClientForm> {
        private Client client;

        protected ClientFormEvent(AddClientForm addClientForm, Client client) {
            super(addClientForm, false);
            this.client = client;
        }

        public Client getClient() {
            return client;
        }
    }

    public static class SaveEvent extends ClientFormEvent {
        SaveEvent(AddClientForm addClientForm, Client client) {
            super(addClientForm, client);
        }
    }

    public static class DeleteEvent extends ClientFormEvent {
        DeleteEvent(AddClientForm addClientForm, Client client) {
            super(addClientForm, client);
        }

    }

    public static class CloseEvent extends ClientFormEvent {
        CloseEvent(AddClientForm addClientForm) {
            super(addClientForm, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
