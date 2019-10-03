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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * This DTO is used to send information to open an account
 *
 * Please see the {@link AccountRequestDTO} class for
 * @author jaflolo
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequestDTO implements Serializable {

    private static final long serialVersionUID = 7574902247947747576L;

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @NotBlank(message = "Pin number is mandatory")
    private String accountPin;

    @NotBlank(message = "Pin confirmation number is mandatory")
    private String confAccountPin;

    private String holderIdNumber;

}
