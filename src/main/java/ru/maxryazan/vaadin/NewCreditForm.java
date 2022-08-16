package ru.maxryazan.vaadin;


import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import ru.maxryazan.vaadin.model.Client;
import ru.maxryazan.vaadin.model.Credit;
import ru.maxryazan.vaadin.model.Status;
import ru.maxryazan.vaadin.service.CreditService;
import ru.maxryazan.vaadin.service.CrmService;


public class NewCreditForm extends FormLayout {

    private final CrmService crmService;
    private final CreditService creditService;

    Credit credit;

    TextField sumOfCredit = new TextField("Сумма кредита");
    NumberField creditPercent = new NumberField("Процент по кредиту");
    TextField numberOfPays = new TextField("Количество платежей");
    TextField borrowerPhone = new TextField("Телефон заёмщика");
    TextField borrowerEmail = new TextField("Эл. почта заёмщика");

    Button ok = new Button("Оформить");
    Button close = new Button("Отмена");

    Binder<Credit> binder = new BeanValidationBinder<>(Credit.class);


    public NewCreditForm(CrmService crmService, CreditService creditService){
        this.crmService = crmService;
        this.creditService = creditService;
        binder.bindInstanceFields(this);
        add(sumOfCredit, creditPercent,
                numberOfPays, borrowerPhone,
                borrowerEmail, createButtons());
    }


    public HorizontalLayout createButtons() {
        ok.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        close.addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
        ok.addClassName("btn");
        close.addClassName("btn");
        ok.addClickListener(event -> confirmCredit());
        close.addClickListener(event -> cancelCredit());
        return new HorizontalLayout(ok, close);
    }

    private void cancelCredit() {
        setValuesToDefault();
    }

    public void setValuesToDefault(){
        sumOfCredit.setValue("");
        creditPercent.setValue((double) 0);
        numberOfPays.setValue("");
        borrowerPhone.setValue("");
        borrowerEmail.setValue("");
    }

    private void confirmCredit() {
         credit = new Credit();
        if (crmService.checkPhoneNumber(borrowerPhone.getValue())) {
            Client client = crmService.findByPhoneNumber(borrowerPhone.getValue());
            if(!client.getEmail().equals(borrowerEmail.getValue())){
                throw new IllegalArgumentException("pair 'client phone - client email' not match with DB client");
            }
            credit.setSumOfCredit(Integer.parseInt(sumOfCredit.getValue()));
            credit.setCreditPercent(creditPercent.getValue());
            credit.setNumberOfPays(Integer.parseInt(numberOfPays.getValue()));
            credit.setNumberOfCreditContract(creditService.generateRandomUniqueNumber());
            credit.setDateOfBegin(creditService.generateDateDMY());
            credit.setSumWithPercents(creditService.generateSumWithPercent(sumOfCredit.getValue(), creditPercent.getValue(), numberOfPays.getValue()));
            credit.setEveryMonthPay(creditService.generateEveryMonthPay(credit.getSumWithPercents(), numberOfPays.getValue()));
            credit.setRestOfCredit(Double.parseDouble(sumOfCredit.getValue()));
            credit.setBorrower(client);
            credit.setStatus(Status.ACTIVE);
            creditService.save(credit);
            setValuesToDefault();
        }
    }
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
