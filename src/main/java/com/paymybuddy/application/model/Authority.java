package com.paymybuddy.application.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Authority entity
 */
@Entity
@Getter
@Setter
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
