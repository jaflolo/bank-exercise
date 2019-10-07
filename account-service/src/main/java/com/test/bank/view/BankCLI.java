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
package com.test.bank.view;

import com.test.bank.accountservice.dto.*;
import com.test.bank.view.exception.ClientException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Available commands
 * open [First Name] [Last Name] [Pin] [Confirm Pin] [ID SSN]
 * login [Account number] [PIN]
 * close
 * deposit [amount] [description]
 * withdraw [amount] [description]
 * balance
 * logout
 */
public class BankCLI {

    private static final AccountServiceClient client = new AccountServiceClient();
    private AccountDTO selectedAccount;

    private enum Command {
        open, login, close, deposit, withdraw, balance, logout, unknown
    }

    public static void main(String... args){
        final BankCLI cli = new BankCLI();


        try{
            cli.openFile();
            cli.init(args);
        } catch (ClientException e){
            System.err.println(e.getMessage());
        }
    }

    private void init(String... args) throws ClientException {
        if(args.length == 0){
            throw new ClientException("Invalid command");
        }

        final String command = args[0];
        printHelpIfNecessary(command);

        final Command parsedCommand = parseCommand(command);

        switch (parsedCommand){
            case open:
                processOpenCommand(args);
                break;
            case login:
                processLoginCommand(args);
                break;
            case close:
                processCloseCommand();
                break;
            case deposit:
                processDepositCommand(args);
                break;
            case withdraw:
                processWithdrawCommand(args);
                break;
            case balance:
                processBalanceCommand();
                break;
            case logout:
                processLogoutCommand();
                break;
            default:
                throw new ClientException(String.format("Unknown command %s", command));
        }
    }

    private void verifyIfLoggedIn() throws ClientException {
        if(selectedAccount == null){
            throw new ClientException("You should be logged in to execute this command");
        }
    }

    private void processLogoutCommand() throws ClientException {
        verifyIfLoggedIn();
        this.selectedAccount = null;
        final File file = new File("loggedin");

        if(file.delete()){
            System.out.println("Logout ok...");
        }else{
            System.out.println("Logout operation is failed.");
        }
    }

    private void processBalanceCommand() throws ClientException {
        verifyIfLoggedIn();
        final AccountBalanceDTO accountBalanceDTO = client.getCurrentBalanceForAccount(this.selectedAccount.getAccountId());
        if(accountBalanceDTO.getBalance() != null){
            System.out.println(String.format("Current balance is %s", accountBalanceDTO.getBalance().toString()));
        }
    }

    private void processWithdrawCommand(String[] args) throws ClientException {
        verifyIfLoggedIn();
        final int maxArgumentsCommand = 3;
        verifyArgumentsLength(args, maxArgumentsCommand);

        final BigDecimal amount = readBigDecimal(args[1]);
        final String description = args[2];
        final ResponseDTO responseDTO = client.makeWithdrawal(this.selectedAccount.getAccountId(), new TransactionRequestDTO(amount, description));

        if(responseDTO.getTransactionId() != null && !responseDTO.getTransactionId().isEmpty()){
            System.out.println(String.format("Transaction ok  %s", responseDTO.getTransactionId()));
        }
    }

    private BigDecimal readBigDecimal(String arg) throws ClientException {
        try{
            return new BigDecimal(arg);
        } catch (Exception ex){
            throw new ClientException("Amount has an invalid format");
        }
    }

    private void processDepositCommand(String[] args) throws ClientException {
        verifyIfLoggedIn();
        final int maxArgumentsCommand = 3;
        verifyArgumentsLength(args, maxArgumentsCommand);

        final BigDecimal amount = readBigDecimal(args[1]);
        final String description = args[2];
        final ResponseDTO responseDTO = client.makeDeposit(this.selectedAccount.getAccountId(), new TransactionRequestDTO(amount, description));

        if(responseDTO.getTransactionId() != null && !responseDTO.getTransactionId().isEmpty()){
            System.out.println(String.format("Transaction ok  %s", responseDTO.getTransactionId()));
        }
    }

    private void processCloseCommand() throws ClientException {
        verifyIfLoggedIn();

        ResponseDTO responseDTO = client.closeAccount(this.selectedAccount.getAccountId());

        if(responseDTO.getAccountNumber() != null && !responseDTO.getAccountNumber().isEmpty()){
            System.out.println("Account Closed ok");
        }
    }

    private void processLoginCommand(String[] args) throws ClientException {
        final int maxArgumentsCommand = 3;
        verifyArgumentsLength(args, maxArgumentsCommand);

        String accountNumber = args[1];
        String pinNumber = args[2];

        final AccountDTO accountDTO = client.authenticate(accountNumber, pinNumber);
        if(accountDTO.getAccountNumber() != null && !accountDTO.getAccountNumber().isEmpty()){
            writeFile(accountDTO);
            System.out.println("OK...");
        }
    }

    private void writeFile(AccountDTO accountDTO) {
        final File file = new File("loggedin");
        try {
            FileWriter fileWriter = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(accountDTO.getAccountId());
            printWriter.println(accountDTO.getAccountNumber());
            printWriter.close();
        } catch (IOException e) {}
    }

    private void openFile() {
        final String fileName = "loggedin";
        if(Files.exists(Paths.get(fileName))){
            List<String> list;
            try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
                list = stream.collect(Collectors.toList());
                if(list != null && !list.isEmpty()){
                    this.selectedAccount = new AccountDTO();
                    this.selectedAccount.setAccountId(Long.valueOf(list.get(0)));
                    this.selectedAccount.setAccountNumber(list.get(1));
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void verifyArgumentsLength(String[] args, int length) {
        if(args.length < length){
            System.err.println("Missing arguments in command, please execute help command");
            System.exit(0);
        }
    }

    private void processOpenCommand(String[] args) throws ClientException {
        final int maxArgumentsCommand = 6;
        verifyArgumentsLength(args, maxArgumentsCommand);

        final String firstName = args[1];
        final String lastName = args[2];
        final String pin = args[3];
        final String confPin = args[4];
        final String holderId = args[5];

        final ResponseDTO responseDTO = client.openNewAccount(
            new AccountRequestDTO(firstName, lastName, pin, confPin, holderId)
        );
        System.out.println("Transaction executed successfully");
        System.out.println("Account number: " + responseDTO.getAccountNumber());
        System.out.println("Pin number: " + responseDTO.getPin());
    }

    private Command parseCommand(String command) {
        try{
            return Command.valueOf(command);
        } catch (Exception e){
            return Command.unknown;
        }
    }

    private void printHelpIfNecessary(String command) {
        if(isHelp(command)){
            printHelp();
            System.exit(0);
        }
    }

    private boolean isHelp(String command) {
        return command != null && command.contains("--help");
    }

    private void printHelp() {
        System.out.println("Available Commands");
        System.out.println("    open             Open an account");
        System.out.println("          open  [First Name] [Last Name] [Pin] [Confirm Pin] [ID SSN]   ");
        System.out.println("    login            Login to an existing account");
        System.out.println("          login [Account number] [PIN] ");
        System.out.println("    close            Close an account");
        System.out.println("    deposit          Makes a deposit");
        System.out.println("          deposit [amount] [description] ");
        System.out.println("    withdraw         Makes a withdraw");
        System.out.println("          withdraw [amount] [description] ");
        System.out.println("    balance          Get the current balance for actual logged in account");
        System.out.println("    logout           Logout the actual logged int account");
    }

}
