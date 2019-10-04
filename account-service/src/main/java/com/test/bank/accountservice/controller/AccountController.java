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
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(value="Account Service", description="Operations to allow to maintain a personal checking account")
@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @ApiOperation(value = "Allows to find an account by number and pin", response = AccountDTO.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Account Successfully Found"),
        @ApiResponse(code = 400, message = "The account you were trying to reach is not found")
    })
    @GetMapping("/v1/accounts")
    public ResponseEntity<AccountDTO> findAccount(@ApiParam(value = "The account number", required = true) @RequestParam String accountNumber,
                                                  @ApiParam(value = "The account pin number", required = true) @RequestParam String pin){
        return ResponseEntity.ok(accountService.searchAccount(accountNumber, pin));
    }

    @ApiOperation(value = "Get the account details by Id", response = AccountDetailDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Account Successfully Found"),
            @ApiResponse(code = 400, message = "The account you were trying to reach is not found")
    })
    @GetMapping("/v1/accounts/{accountId}")
    public ResponseEntity<AccountDetailDTO> findAccount(
            @ApiParam(value = "Account id from which account details will be fetch", required = true)
            @PathVariable("accountId") Long id){
        return ResponseEntity.ok(accountService.findAccount(id));
    }

    @ApiOperation(value = "Get the current balance of an account", response = AccountBalanceDTO.class)
    @GetMapping("/v1/accounts/{accountId}/balance")
    public ResponseEntity<AccountBalanceDTO> getCurrentBalance(
            @ApiParam(value = "Account id from which account balance will be fetch", required = true)
            @PathVariable("accountId") Long accountId){
        return ResponseEntity.ok(accountService.getCurrentBalance(accountId));
    }

    @ApiOperation(value = "Open a new account.", response = ResponseDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Account Successfully Opened"),
            @ApiResponse(code = 400, message = "A constraint validation failure was fired")
    })

    @PostMapping("/v1/accounts")
    public ResponseEntity<ResponseDTO> openAccount(
            @ApiParam(value = "Request object with the information to be stored in database", required = true)
            @RequestBody AccountRequestDTO accountRequestDTO){
        return ResponseEntity.ok(accountService.openAccount(accountRequestDTO));
    }

    @ApiOperation(value = "Close an existing account.", response = ResponseDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Account Successfully Closed"),
            @ApiResponse(code = 400, message = "A constraint validation failure was fired")
    })

    @PutMapping("/v1/accounts/{accountId}/close")
    public ResponseEntity<ResponseDTO> closeAccount(
            @ApiParam(value = "The account id", required = true)
            @PathVariable("accountId") Long accountId){
        return ResponseEntity.ok(accountService.closeAccount(accountId));
    }

    @ApiOperation(value = "Makes a deposit into current account.", response = ResponseDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Deposit Successfully Processed"),
            @ApiResponse(code = 400, message = "A constraint validation failure was fired")
    })
    @PutMapping("/v1/accounts/{accountId}/deposit")
    public ResponseEntity<ResponseDTO> makeDeposit(
            @ApiParam(value = "The account id", required = true)
            @PathVariable("accountId") Long accountId,
            @ApiParam(value = "The transaction request to be processed", required = true)
            @RequestBody TransactionRequestDTO transactionRequestDTO){
        return ResponseEntity.ok(accountService.makeDeposit(accountId, transactionRequestDTO));
    }

    @ApiOperation(value = "Makes a withdrawal from the current account.", response = ResponseDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Withdrawal Successfully Processed"),
            @ApiResponse(code = 400, message = "A constraint validation failure was fired")
    })
    @PutMapping("/v1/accounts/{accountId}/withdrawal")
    public ResponseEntity<ResponseDTO> makeWithdrawal(
            @ApiParam(value = "The account id", required = true)
            @PathVariable("accountId") Long accountId,
            @ApiParam(value = "The transaction request to be processed", required = true)
            @RequestBody TransactionRequestDTO transactionRequestDTO){
        return ResponseEntity.ok(accountService.makeWithdrawal(accountId, transactionRequestDTO));
    }

    @ApiOperation(value = "Makes a debit operation to current account from external source.", response = ResponseDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Debit operation Successfully Processed"),
            @ApiResponse(code = 400, message = "A constraint validation failure was fired")
    })
    @PutMapping("/v1/accounts/{accountId}/debit")
    public ResponseEntity<ResponseDTO> processDebit(
            @ApiParam(value = "The account id", required = true)
            @PathVariable("accountId") Long accountId,
            @ApiParam(value = "The transaction request to be processed", required = true)
            @RequestBody TransactionRequestDTO transactionRequestDTO){
        return ResponseEntity.ok(accountService.processDebit(accountId, transactionRequestDTO));
    }

    @ApiOperation(value = "Makes an operation with check to current account from external source.", response = ResponseDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Operation with check Successfully Processed"),
            @ApiResponse(code = 400, message = "A constraint validation failure was fired")
    })
    @PutMapping("/v1/accounts/{accountId}/check")
    public ResponseEntity<ResponseDTO> processCheck(
            @ApiParam(value = "The account id", required = true)
            @PathVariable("accountId") Long accountId,
            @ApiParam(value = "The transaction request to be processed", required = true)
            @RequestBody TransactionRequestDTO transactionRequestDTO){
        return ResponseEntity.ok(accountService.processCheck(accountId, transactionRequestDTO));
    }
}
