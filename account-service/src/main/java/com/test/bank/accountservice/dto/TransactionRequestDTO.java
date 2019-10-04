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
package com.test.bank.accountservice.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@ApiModel(description = "Generic information to make a transaction.")
public class TransactionRequestDTO implements Serializable {
    private static final long serialVersionUID = 3078353904952199958L;

    @ApiModelProperty(notes = "Positive amount of the transaction")
    private BigDecimal amount;

    @ApiModelProperty(notes = "Indicates if transaction is DEBIT or CREDIT useful for debit and checks operations")
    private String type;

    @ApiModelProperty(notes = "A description for the transaction")
    private String description;

    public TransactionRequestDTO(BigDecimal amount, String description) {
        this.amount = amount;
        this.description = description;
    }

    public TransactionRequestDTO(BigDecimal amount, String type, String description) {
        this.amount = amount;
        this.type = type;
        this.description = description;
    }

}
