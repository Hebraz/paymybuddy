package com.paymybuddy.application.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "transaction")
public class ConnectionTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_transaction")
    private int idTransaction;

    private Instant date;

    @Column(name = "total_amount")
    private long totalAmount;  /*unit is cent*/

    private String description;

    @Column(name = "fee_amount")
    private long feeAmount; /*unit is cent*/

    /*Bidirectional mapping*/
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "payer_id")
    private User payer;

    /*Bidirectional mapping*/
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "credit_id")
    private User credit;

    public ConnectionTransfer(){}
    public ConnectionTransfer(Instant date, long totalAmount, String description, long feeAmount) {
        this.date = date;
        this.totalAmount = totalAmount;
        this.description = description;
        this.feeAmount = feeAmount;
    }
}
