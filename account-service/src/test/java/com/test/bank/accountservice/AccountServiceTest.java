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
package com.test.bank.accountservice;

import com.test.bank.accountservice.dto.AccountDTO;
import com.test.bank.accountservice.dto.AccountRequestDTO;
import com.test.bank.accountservice.dto.ResponseDTO;
import com.test.bank.accountservice.dto.TransactionRequestDTO;
import com.test.bank.accountservice.enums.AccountStatus;
import com.test.bank.accountservice.exception.ApiException;
import com.test.bank.accountservice.model.Account;
import com.test.bank.accountservice.repository.AccountRepository;
import com.test.bank.accountservice.service.AccountService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:db-test.properties")
public class AccountServiceTest {

    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;

    private Account account;

    @Before
    public void init() {
        account = new Account();
        account.setFirstName("Jaime");
        account.setLastName("Flores");
        account.setHolderId("522665465785546963");
        account.setPin("1234");
        account.setStatus(AccountStatus.ACTIVE);

    }

    @Test
    public void testSearchFoundAccount() {
        account.setAccountNumber(UUID.randomUUID().toString());
        accountRepository.save(account);
        AccountDTO accountSaved = accountService.searchAccount(account.getAccountNumber(), "1234");
        Assert.assertNotNull(accountSaved);
        Assert.assertEquals(account.getAccountNumber(), accountSaved.getAccountNumber());
        Assert.assertNotNull(accountService.findAccount(accountSaved.getAccountId()));
    }

    @Test(expected = ApiException.class)
    public void testSearchWithoutAccountNumber() {
        account.setAccountNumber(UUID.randomUUID().toString());
        accountRepository.save(account);
        accountService.searchAccount("", "12314");
    }

    @Test(expected = ApiException.class)
    public void testSearchWithoutAccountPin() {
        account.setAccountNumber(UUID.randomUUID().toString());
        accountRepository.save(account);
        accountService.searchAccount("98765432", "");
    }

    @Test(expected = ApiException.class)
    public void testSearchNotFoundAccount() {
        account.setAccountNumber(UUID.randomUUID().toString());
        accountRepository.save(account);
        accountService.searchAccount("9876225432", "1236");
    }

    @Test
    public void testOpenSuccessAccount() {
        final AccountRequestDTO accountRequestDTO = new AccountRequestDTO();
        accountRequestDTO.setAccountPin("1234");
        accountRequestDTO.setConfAccountPin("1234");
        accountRequestDTO.setFirstName("Jaime");
        accountRequestDTO.setLastName("Flores");
        accountRequestDTO.setHolderIdNumber("1235454SN123");

        ResponseDTO response = accountService.openAccount(accountRequestDTO);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getAccountNumber());
        Assert.assertNotNull(response.getPin());
        Assert.assertEquals(accountRequestDTO.getAccountPin(), response.getPin());
        Assert.assertNotNull(accountService.searchAccount(response.getAccountNumber(), response.getPin()));
    }

    @Test
    public void testValidationExceptionsAreCorrectFired() {
        AccountRequestDTO accountRequestDTO = createRequest();
        accountRequestDTO.setAccountPin("");
        try{
            accountService.openAccount(accountRequestDTO);
        }catch (ApiException ex){
            Assert.assertEquals("Pin number is mandatory.", ex.getMessage());
        }

        accountRequestDTO = createRequest();
        accountRequestDTO.setAccountPin("0456");
        try{
            accountService.openAccount(accountRequestDTO);
        }catch (ApiException ex){
            Assert.assertEquals("Pin number should be of 4 numeric digits with non zero values.", ex.getMessage());
        }

        accountRequestDTO = createRequest();
        accountRequestDTO.setAccountPin("1234");
        accountRequestDTO.setConfAccountPin("1233");
        try{
            accountService.openAccount(accountRequestDTO);
        }catch (ApiException ex){
            Assert.assertEquals("Pin and Pin Confirmation does not match.", ex.getMessage());
        }

        accountRequestDTO = createRequest();
        accountRequestDTO.setFirstName("");
        try{
            accountService.openAccount(accountRequestDTO);
        }catch (Exception ex){
            Assert.assertTrue(ex instanceof ConstraintViolationException);
        }

        accountRequestDTO = createRequest();
        accountRequestDTO.setLastName("");
        try{
            accountService.openAccount(accountRequestDTO);
        }catch (Exception ex){
            Assert.assertTrue(ex instanceof ConstraintViolationException);
        }
    }

    @Test
    public void testCloseAccount() {
        account.setAccountNumber(UUID.randomUUID().toString());
        accountRepository.save(account);
        Assert.assertNotNull(accountService.closeAccount(account.getId()).getAccountNumber());
        Account accountSaved = accountRepository.findById(account.getId()).get();
        Assert.assertEquals(AccountStatus.CLOSED, accountSaved.getStatus());
    }

    @Test
    @Transactional
    public void testMakeDepositAndCheckBalance() {
        account.setAccountNumber(UUID.randomUUID().toString());
        accountRepository.save(account);

        BigDecimal amount = new BigDecimal(50);
        String description = "Gasoline";
        Assert.assertNotNull(accountService.makeDeposit(account.getId(), new TransactionRequestDTO(amount, description)));

        amount = new BigDecimal(50);
        description = "Tickets";
        Assert.assertNotNull(accountService.makeDeposit(account.getId(), new TransactionRequestDTO(amount, description)));

        Assert.assertEquals(new BigDecimal(100).stripTrailingZeros(), accountRepository.getRunningBalance(account.getId()).stripTrailingZeros());
    }

    @Test
    public void testMakeWithdrawalAndCheckBalance() {
        account.setAccountNumber(UUID.randomUUID().toString());
        accountRepository.save(account);

        BigDecimal amount = new BigDecimal(50);
        String description = "Gasoline";
        accountService.makeDeposit(account.getId(), new TransactionRequestDTO(amount, description));

        BigDecimal tickets = new BigDecimal(25);
        description = "Tickets";
        accountService.makeWithdrawal(account.getId(), new TransactionRequestDTO(tickets, description));

        Assert.assertEquals(new BigDecimal(25).stripTrailingZeros(), accountRepository.getRunningBalance(account.getId()).stripTrailingZeros());
    }

    @Test
    public void testProcessCheckAndCheckBalance() {
        account.setAccountNumber(UUID.randomUUID().toString());
        accountRepository.save(account);

        BigDecimal amount = new BigDecimal(50);
        String description = "Gasoline";
        Assert.assertNotNull(accountService.makeDeposit(account.getId(), new TransactionRequestDTO(amount, description)));

        amount = new BigDecimal(100);
        description = "Tickets";
        Assert.assertNotNull(accountService.processCheck(account.getId(), new TransactionRequestDTO(amount, "DEBIT", description)));

        Assert.assertEquals(new BigDecimal(150).stripTrailingZeros(),accountRepository.getRunningBalance(account.getId()).stripTrailingZeros());
    }

    @Test
    public void testProcessDebitAndCheckBalance() {
        account.setAccountNumber(UUID.randomUUID().toString());
        accountRepository.save(account);

        BigDecimal amount = new BigDecimal(50);
        String description = "Gasoline";
        Assert.assertNotNull(accountService.makeDeposit(account.getId(), new TransactionRequestDTO(amount, description)));

        amount = new BigDecimal(50);
        description = "Tickets";
        Assert.assertNotNull(accountService.processCheck(account.getId(), new TransactionRequestDTO(amount, "CREDIT", description)));

        Assert.assertEquals(BigDecimal.ZERO.stripTrailingZeros(), accountRepository.getRunningBalance(account.getId()).stripTrailingZeros());
    }

    private AccountRequestDTO createRequest() {
        AccountRequestDTO accountRequestDTO = new AccountRequestDTO();
        accountRequestDTO.setAccountPin("1234");
        accountRequestDTO.setConfAccountPin("1234");
        accountRequestDTO.setFirstName("Jaime");
        accountRequestDTO.setLastName("Flores");
        accountRequestDTO.setHolderIdNumber("1235454SN123");
        return accountRequestDTO;
    }


}
