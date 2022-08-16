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
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.maxryazan.vaadin.model.Client;
import ru.maxryazan.vaadin.service.CrmService;


public class AddClientForm extends FormLayout {
    private Client client;

    private final CrmService crmService;

    Binder<Client> binder = new BeanValidationBinder<>(Client.class);

    TextField phoneNumber = new TextField("Phone number");
    TextField firstName = new TextField("First name");
    TextField lastName = new TextField("Last name");
    EmailField email = new EmailField("Email");


    Button saveBtn = new Button("Save", new Icon(VaadinIcon.USER_CHECK));
    Button deleteBtn = new Button("Delete", new Icon(VaadinIcon.TRASH));
    Button closeBtn = new Button("Close");


    public AddClientForm(CrmService crmService) {
        this.crmService = crmService;

        binder.bindInstanceFields(this);

        add(phoneNumber,
                firstName,
                lastName,
                email,
                createBtns());
    }

    public HorizontalLayout createBtns() {
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
        if (crmService.checkPhoneNumber(phoneNumber.getValue()) && isUniquePhone(phoneNumber.getValue()) && isUniqueEmail(email.getValue())) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            Client cl = new Client();
            cl.setPhoneNumber(phoneNumber.getValue());
            cl.setFirstName(firstName.getValue());
            cl.setLastName(lastName.getValue());
            cl.setEmail(email.getValue());
            cl.setBalance(0);
            cl.setBalanceEUR(0);
            cl.setBalanceUSD(0);
            cl.setPinCode(passwordEncoder.encode("123"));
            try {
                binder.writeBean(cl);
                fireEvent(new SaveEvent(this, cl));
            } catch (ValidationException e) {
                System.out.println("Validate and save error");
                e.printStackTrace();
            }
        } else {
            try {
                if (crmService.checkPhoneNumber(phoneNumber.getValue())) {
                    binder.writeBean(client);
                    fireEvent(new SaveEvent(this, client));
                }
                else throw  new IllegalArgumentException("phone not valid");
            }
            catch(ValidationException e){
                System.out.println("Validate and save error");
                e.printStackTrace();
            }

        }
    }

    /* ------------------------------------------------------------------------------- */


    public static abstract class ClientFormEvent extends ComponentEvent<AddClientForm> {
        private final Client client;

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

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    public boolean isUniquePhone(String phone){
      return crmService.findByPhoneNumber(phone) == null;
    }
    public boolean isUniqueEmail(String email){
        return crmService.findByEmail(email) == null;
    }
}
