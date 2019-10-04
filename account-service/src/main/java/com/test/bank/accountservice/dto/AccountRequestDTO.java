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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Required information to open an account.")
public class AccountRequestDTO implements Serializable {

    private static final long serialVersionUID = 7574902247947747576L;

    @ApiModelProperty(notes = "The account holder first name")
    private String firstName;

    @ApiModelProperty(notes = "The account holder last name")
    private String lastName;

    @ApiModelProperty(notes = "The account pin")
    private String accountPin;

    @ApiModelProperty(notes = "The account pin confirmation")
    private String confAccountPin;

    @ApiModelProperty(notes = "The account holder ID (SSN, Voter Card ID)")
    private String holderIdNumber;

}
