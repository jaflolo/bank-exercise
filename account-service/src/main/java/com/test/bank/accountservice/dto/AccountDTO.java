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

@Data
@ApiModel(description = "Lightweight information about account.")
public class AccountDTO implements Serializable {

    private static final long serialVersionUID = 4023402792612616081L;

    @ApiModelProperty(notes = "The account ID")
    private Long accountId;

    @ApiModelProperty(notes = "The account number")
    private String accountNumber;

    @ApiModelProperty(notes = "The account holder full name")
    private String holderFullName;

}
