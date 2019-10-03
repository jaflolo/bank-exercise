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
package com.test.bank.accountservice.service;

import com.test.bank.accountservice.dto.*;
import com.test.bank.accountservice.enums.AccountStatus;
import com.test.bank.accountservice.enums.TransactionType;
import com.test.bank.accountservice.exception.ApiException;
import com.test.bank.accountservice.model.Account;
import com.test.bank.accountservice.model.AccountBalance;
import com.test.bank.accountservice.model.AccountTransaction;
import com.test.bank.accountservice.repository.AccountBalanceRepository;
import com.test.bank.accountservice.repository.AccountRepository;
import com.test.bank.accountservice.repository.AccountTransactionRepository;
import com.test.bank.accountservice.util.Constants;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import static com.test.bank.accountservice.enums.TransactionType.*;
import static com.test.bank.accountservice.util.GeneralUtils.formatDateTimeToString;
import static com.test.bank.accountservice.util.GeneralUtils.generateAccountNumber;

@Service
@Transactional(rollbackOn = Exception.class)
public class AccountServiceImpl implements AccountService {
    private static Random random = new Random();

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountTransactionRepository accountTransactionRepository;

    @Autowired
    private AccountBalanceRepository accountBalanceRepository;

    @Override
    public AccountDetailDTO findAccount(Long accountId) {
        final Optional<Account> optionalAccount = accountRepository.findById(accountId);
        if(!optionalAccount.isPresent()){
            throw new ApiException("The account does not exist");
        }
        return mapAccountToDTO(optionalAccount.get());
    }

    @Override
    public AccountDTO searchAccount(String number, String pin) {
        assertValidNumber(number);
        assertValidPin(pin);

        final Account account = accountRepository.findByAccountNumberAndPin(number, pin);
        final AccountDTO dto = new AccountDTO();
        dto.setAccountId(account.getId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setHolderFullName(String.format("%s %s", account.getFirstName(),account.getLastName()));
        return dto;
    }

    private AccountDetailDTO mapAccountToDTO(Account account) {
        if(account == null){
            throw new ApiException("Account does not exist");
        }

        final AccountDetailDTO detailsDTO = new AccountDetailDTO();
        detailsDTO.setAccountId(account.getId());
        detailsDTO.setAccountNumber(account.getAccountNumber());
        detailsDTO.setAccountPin(account.getPin());

        final AccountBalanceDTO currentBalance = this.getCurrentBalance(account.getId());
        detailsDTO.setCurrentBalance(currentBalance.getBalance());
        detailsDTO.setHolderFullName(String.format("%s %s", account.getFirstName(),account.getLastName()));
        detailsDTO.setHolderId(account.getHolderId());

        final List<AccountTransaction> accountTransactions = accountTransactionRepository
                .findTop5ByAccountIdOrderByTransactionDateDesc(account.getId());

        if(!CollectionUtils.isEmpty(accountTransactions)){
            detailsDTO.setLastTransactions(accountTransactions
                    .stream()
                    .map(this::mapTransactionToDTO)
                    .collect(Collectors.toList()));
        }

        return detailsDTO;
    }

    private void assertValidPin(String pin) {
        if(StringUtils.isBlank(pin)){
            throw new ApiException("Pin number is required");
        }
    }

    private void assertValidNumber(String number) {
        if(StringUtils.isBlank(number)){
            throw new ApiException("Account number is required");
        }
    }

    private TransactionDetailDTO mapTransactionToDTO(AccountTransaction accountTransaction) {
        final TransactionDetailDTO transactionDTO = new TransactionDetailDTO();
        transactionDTO.setAmount(accountTransaction.getAmount());
        transactionDTO.setTransactionDate(formatDateTimeToString(accountTransaction.getTransactionDate()));
        transactionDTO.setDescription(accountTransaction.getDescription());
        transactionDTO.setTransactionType(accountTransaction.getTransactionType().toString());
        return transactionDTO;
    }

    @Override
    public ResponseDTO openAccount(AccountRequestDTO account) {
        assertValidData(account);

        final Account accountToSave = new Account();
        accountToSave.setFirstName(account.getFirstName());
        accountToSave.setLastName(account.getLastName());
        accountToSave.setHolderId(account.getHolderIdNumber());
        accountToSave.setPin(account.getAccountPin());
        accountToSave.setAccountNumber(generateAccountNumber());
        accountToSave.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(accountToSave);

        final AccountBalance accountBalance = new AccountBalance();
        accountBalance.setAccount(accountToSave);
        accountBalance.setBalanceDate(LocalDateTime.now());
        accountBalance.setBalance(BigDecimal.ZERO);
        accountBalanceRepository.save(accountBalance);

        final ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setAccountNumber(accountToSave.getAccountNumber());
        responseDTO.setPin(accountToSave.getPin());
        return responseDTO;
    }

    private void assertValidData(AccountRequestDTO account) {
        if(!account.getAccountPin().equals(account.getConfAccountPin())){
            throw new ApiException("Pin and Pin Confirmation does not match.");
        }
    }

    @Override
    public ResponseDTO closeAccount(long accountId) {
        final Optional<Account> accountOptional = accountRepository.findById(accountId);
        final ResponseDTO responseDTO = new ResponseDTO();
        assertExistAccount(accountOptional);

        final Account account = accountOptional.get();
        final AccountBalanceDTO currentBalance = this.getCurrentBalance(account.getId());

        if(currentBalance.getBalance().compareTo(BigDecimal.ZERO) < 0){
            throw new ApiException("The account can not be closed due to it is overdrawn.");
        }

        account.setStatus(AccountStatus.CLOSED);
        accountRepository.save(account);
        responseDTO.setAccountNumber(account.getAccountNumber());

        return responseDTO;
    }

    @Override
    public AccountBalanceDTO getCurrentBalance(long accountId) {
        if(!accountRepository.existsById(accountId)){
            throw new ApiException("Account does not exist");
        }

        final AccountBalance currentBalance = accountBalanceRepository.findTopByAccountIdOrderByBalanceDateDesc(accountId);
        final AccountBalanceDTO balanceDTO = new AccountBalanceDTO();

        if(currentBalance != null){
            balanceDTO.setAccountNumber(currentBalance.getAccount().getAccountNumber());
            balanceDTO.setBalance(currentBalance.getBalance());
        }

        return balanceDTO;
    }

    @Override
    public synchronized ResponseDTO makeDeposit(Long accountId, TransactionRequestDTO transaction) {
        transaction.setType(Constants.TRANSACTION_DEBIT);
        return processTransaction(accountId, transaction, DEPOSIT);
    }

    private ResponseDTO processTransaction(Long accountId, TransactionRequestDTO transaction, TransactionType transactionType){
        final ResponseDTO responseDTO = new ResponseDTO();
        final Optional<Account> accountOptional = accountRepository.findById(accountId);

        assertExistAccount(accountOptional);
        assertTypeNotNull(transaction.getType());
        assertValidType(transaction.getType());

        final Account account = accountOptional.get();
        final BigDecimal amount = calculateSignedAmount(transaction);

        final AccountBalanceDTO currentBalance = this.getCurrentBalance(account.getId());
        assertAccountNotOverdrawn(currentBalance.getBalance(), amount);

        final String transactionId = saveTransaction(transactionType, transaction, account, amount);
        responseDTO.setTransactionId(transactionId);

        updateAccountBalance(currentBalance.getBalance(), account, amount);

        return responseDTO;
    }

    private void assertValidType(String type) {
        if(!Arrays.asList(
                Constants.TRANSACTION_CREDIT,Constants.TRANSACTION_DEBIT)
                .contains(type)){
            throw new ApiException("Transaction type [DEBIT, CREDIT] is required to process current operation.");
        }
    }

    private void updateAccountBalance(BigDecimal currentBalance, Account account, BigDecimal amount) {
        final BigDecimal newBalance = currentBalance.add(amount);
        accountBalanceRepository.save(buildAccountBalance(newBalance, account));
    }

    private String saveTransaction(TransactionType transactionType,
                                   TransactionRequestDTO transaction,
                                   Account account, BigDecimal amount) {
        final AccountTransaction accountTransaction = buildTransaction(transactionType, transaction, account);
        accountTransaction.setAmount(amount);
        accountTransactionRepository.save(accountTransaction);
        return accountTransaction.getId();
    }

    private void assertAccountNotOverdrawn(BigDecimal currentBalance, BigDecimal amount) {
        final BigDecimal newBalance = currentBalance.add(amount);
        if(newBalance.compareTo(BigDecimal.ZERO) < 0){
            throw new ApiException("Operation cancelled due to insufficient funds.");
        }
    }

    private BigDecimal calculateSignedAmount(TransactionRequestDTO transaction) {
        BigDecimal localAmount = transaction.getAmount();
        if(transaction.getType().equals(Constants.TRANSACTION_CREDIT)){
            localAmount = transaction.getAmount().multiply(new BigDecimal(-1));
        }
        return localAmount;
    }

    private AccountBalance buildAccountBalance(BigDecimal currentBalance, Account account) {
        final AccountBalance accountBalance = new AccountBalance();
        accountBalance.setAccount(account);
        accountBalance.setBalance(currentBalance);
        accountBalance.setBalanceDate(LocalDateTime.now());
        return accountBalance;
    }

    private void assertTypeNotNull(String type) {
        if(type == null){
            throw new ApiException("Transaction Type is mandatory [DEBIT,CREDIT]");
        }
    }

    private void assertExistAccount(Optional<Account> accountOptional) {
        if(!accountOptional.isPresent()){
            throw new ApiException(String.format("Account with provided id does not exist"));
        }
    }

    private AccountTransaction buildTransaction(TransactionType type, TransactionRequestDTO transaction, Account account) {
        final AccountTransaction accountTransaction = new AccountTransaction();
        accountTransaction.setTransactionType(type);
        accountTransaction.setDescription(transaction.getDescription());
        accountTransaction.setAccount(account);
        accountTransaction.setTransactionDate(LocalDateTime.now());
        return accountTransaction;
    }

    @Override
    public synchronized ResponseDTO makeWithdrawal(Long accountId, TransactionRequestDTO transaction) {
        transaction.setType(Constants.TRANSACTION_CREDIT);
        return processTransaction(accountId, transaction, WITHDRAWAL);
    }

    @Override
    public synchronized ResponseDTO processCheck(Long accountId, TransactionRequestDTO transaction) {
        return processTransaction(accountId, transaction, CHECKS);
    }

    @Override
    public synchronized ResponseDTO processDebit(Long accountId, TransactionRequestDTO transaction) {
        return processTransaction(accountId, transaction, DEBIT);
    }
}
