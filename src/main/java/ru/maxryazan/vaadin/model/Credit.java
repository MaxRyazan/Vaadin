package ru.maxryazan.vaadin.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;



@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "credits")
public class Credit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "credit_number", unique = true, nullable = false)
    private String numberOfCreditContract;

    @Column(name = "credit_sum")
    private int sumOfCredit;

    @Column(name = "date_of_begin")
    private String dateOfBegin;

    @Column(name = "credit_percent")
    private double creditPercent;

    @Column(name = "sum_with_percents")
    private double sumWithPercents;

    @Column(name = "monthly_payment")
    private double everyMonthPay;

    @Column(name = "number_of_pays")
    private int numberOfPays;

    @Column(name = "rest_of_credit")
    private double restOfCredit;

    @ManyToOne
    @JoinColumn(name = "borrower_id")
    private Client borrower;

    @Enumerated(value = EnumType.STRING)
    private Status status;
}
