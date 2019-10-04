/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.test.bank.accountservice.model;

import com.test.bank.accountservice.enums.AccountStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "account")
public class Account implements Serializable {

    private static final long serialVersionUID = 78717340920010074L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "account_number")
    private String accountNumber;

    @NotBlank(message = "First name is required.")
    @Column(name = "first_name")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Column(name = "last_name")
    private String lastName;

    @Size(max = 4, message = "Pin number should have max 4 digits.")
    @Column(name = "pin")
    private String pin;

    @Column(name = "holder_id")
    private String holderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AccountStatus status;

    @Getter(AccessLevel.NONE)
    @Column(name = "balance")
    private BigDecimal balance = new BigDecimal(0);

    public synchronized void withdraw(BigDecimal amount) {
        this.balance = getBalance().subtract(amount);
    }

    public synchronized void deposit(BigDecimal amount) {
        balance = getBalance().add(amount);
    }

    public synchronized BigDecimal getBalance() {
        return this.balance;
    }

    public Account(){}

}
