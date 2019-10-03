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

import com.test.bank.accountservice.enums.AccountStatus;
import com.test.bank.accountservice.model.Account;
import com.test.bank.accountservice.model.AccountBalance;
import com.test.bank.accountservice.repository.AccountBalanceRepository;
import com.test.bank.accountservice.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootApplication
@EnableTransactionManagement
public class AccountServiceApplication {

	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private AccountBalanceRepository accountBalanceRepository;

	public static void main(String[] args) {
		SpringApplication.run(AccountServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(){
		return args -> {
			final Account account = new Account();
			account.setAccountNumber("12345678");
			account.setFirstName("Jaime");
			account.setLastName("Flores");
			account.setHolderId("522665465785546963");
			account.setPin("1234");
			account.setStatus(AccountStatus.ACTIVE);
			accountRepository.save(account);

			final AccountBalance accountBalance = new AccountBalance();
			accountBalance.setBalanceDate(LocalDateTime.now());
			accountBalance.setBalance(BigDecimal.ZERO);
			accountBalance.setAccount(account);
			accountBalanceRepository.save(accountBalance);
		};
	}

}
