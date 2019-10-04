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

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(description = "All information detailed about a single account transaction.")
public class TransactionDetailDTO implements Serializable {

    private static final long serialVersionUID = -2154381290294551470L;

    @ApiModelProperty(notes = "The signed amount of the transaction")
    private BigDecimal amount;

    @ApiModelProperty(notes = "The transaction type (deposit, withdrawal, debit, checks)")
    private String transactionType;

    @ApiModelProperty(notes = "A description for this transaction")
    private String description;

    @ApiModelProperty(notes = "A date time for transaction")
    private String transactionDate;

}
