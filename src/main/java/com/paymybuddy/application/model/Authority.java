package com.paymybuddy.application.model;

import javax.persistence.*;

/**
 * Authority entity
 */
@Entity
@Table(name = "authorities")
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String authority;

    public Authority(String authority) {
        this.authority = authority;
    }

    public Authority(){}
}
