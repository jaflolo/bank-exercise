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
package com.test.bank.accountservice.controller;

import com.test.bank.accountservice.dto.*;
import com.test.bank.accountservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/v1/accounts")
    public ResponseEntity<AccountDTO> findAccount(@RequestParam String accountNumber, @RequestParam String pin){
        return ResponseEntity.ok(accountService.searchAccount(accountNumber, pin));
    }

    @GetMapping("/v1/accounts/{accountId}")
    public ResponseEntity<AccountDetailDTO> findAccount(@PathVariable("accountId") Long id){
        return ResponseEntity.ok(accountService.findAccount(id));
    }

    @GetMapping("/v1/accounts/{accountId}/balance")
    public ResponseEntity<AccountBalanceDTO> getCurrentBalance(@PathVariable("accountId") Long accountId){
        return ResponseEntity.ok(accountService.getCurrentBalance(accountId));
    }

    @PostMapping("/v1/accounts")
    public ResponseEntity<ResponseDTO> openAccount(@Valid @RequestBody AccountRequestDTO accountRequestDTO){
        return ResponseEntity.ok(accountService.openAccount(accountRequestDTO));
    }

    @PutMapping("/v1/accounts/{accountId}/close")
    public ResponseEntity<ResponseDTO> closeAccount(@PathVariable("accountId") Long accountId){
        return ResponseEntity.ok(accountService.closeAccount(accountId));
    }

    @PutMapping("/v1/accounts/{accountId}/deposit")
    public ResponseEntity<ResponseDTO> makeDeposit(@PathVariable("accountId") Long accountId,
                                                   @RequestBody TransactionRequestDTO transactionRequestDTO){
        return ResponseEntity.ok(accountService.makeDeposit(accountId, transactionRequestDTO));
    }

    @PutMapping("/v1/accounts/{accountId}/withdrawal")
    public ResponseEntity<ResponseDTO> makeWithdrawal(@PathVariable("accountId") Long accountId,
                                                      @RequestBody TransactionRequestDTO transactionRequestDTO){
        return ResponseEntity.ok(accountService.makeWithdrawal(accountId, transactionRequestDTO));
    }

    @PutMapping("/v1/accounts/{accountId}/debit")
    public ResponseEntity<ResponseDTO> processDebit(@PathVariable("accountId") Long accountId,
                                                    @RequestBody TransactionRequestDTO transactionRequestDTO){
        return ResponseEntity.ok(accountService.processDebit(accountId, transactionRequestDTO));
    }

    @PutMapping("/v1/accounts/{accountId}/check")
    public ResponseEntity<ResponseDTO> processCheck(@PathVariable("accountId") Long accountId,
                                                    @RequestBody TransactionRequestDTO transactionRequestDTO){
        return ResponseEntity.ok(accountService.processCheck(accountId, transactionRequestDTO));
    }
}
