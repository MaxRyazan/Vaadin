package ru.maxryazan.vaadin.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "balance", nullable = false)
    private double balance;

    @Column(name = "hash_pin", nullable = false)
    private String pinCode;

    @Column(name = "balance_usd", nullable = false)
    private double balanceUSD = 0;

    @Column(name = "balance_eur", nullable = false)
    private double balanceEUR = 0;

}
