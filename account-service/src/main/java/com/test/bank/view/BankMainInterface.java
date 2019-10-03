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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Scanner;

public class BankMainInterface {

    private static final AccountServiceClient client = new AccountServiceClient();
    private AccountDTO selectedAccount;

    public static void main(String[] args){
        final BankMainInterface mainInterface = new BankMainInterface();
        mainInterface.start();
    }

    private void start(){
        while(true){
            printMainMenu();
        }
    }

    private void printMainMenu() {
        System.out.println("\n");
        System.out.println("================================================");
        System.out.println("============ Welcome to Test Bank ==============");
        System.out.println("================================================");
        System.out.println("1. Open a new account");
        System.out.println("2. Login");
        System.out.println("3. Exit");

        final Scanner in = new Scanner(System.in);
        System.out.print("Select the desired option:  ");
        int option = readOption(in);

        switch (option){
            case 1:
                openAccount();
                break;
            case 2:
                login();
                break;
            case 3:
                System.exit(0);
                break;
            default:{
                System.err.println("Option is not correct. Please try again.");
            }

        }
    }

    private int readOption(Scanner in) {
        try{
            return in.nextInt();
        }catch (InputMismatchException e){
            return 0;
        }
    }

    private void login() {
        Scanner in = new Scanner(System.in);
        System.out.print("Account Number: ");
        String accountNumber = in.next();

        System.out.print("Pin: ");
        in = new Scanner(System.in);
        String pinNumber = in.next();

        try {
            this.selectedAccount = client.authenticate(accountNumber, pinNumber);
            printHomePage();
        } catch (ClientException e) {
            System.err.println("\nError: " + e.getMessage());
        }
    }

    private void printHomePage() {
        System.out.println("\n===================================================================================");
        System.out.println(" Welcome " + this.selectedAccount.getHolderFullName());
        System.out.println("===================================================================================");
        System.out.println(String.format("Date: %s", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss"))));
        System.out.println(String.format("Account Number: %s", this.selectedAccount.getAccountNumber()));
        System.out.println("\nWhat do you want to do?");
        System.out.println("1. Make a deposit");
        System.out.println("2. Make a withdrawal");
        System.out.println("3. Get Account Statement");
        System.out.println("4. Log out");
        System.out.println("===================================================================================");

        final Scanner in = new Scanner(System.in);
        System.out.print("\n-> Select an option:  ");
        int option = readOption(in);

        switch (option){
            case 1:
                makeDeposit();
                break;
            case 2:
                makeWithdrawal();
                break;
            case 3:
                printAccountDetails();
                break;
            case 4: {
                this.selectedAccount = null;
                printMainMenu();
                break;
            }
            default: {
                System.err.println("Option is not correct. Please try again.");
                printHomePage();
            }
        }
    }

    private void printAccountDetails() {
        AccountDetailDTO detailDTO = new AccountDetailDTO();
        try {
            detailDTO = client.findAccountById(this.selectedAccount.getAccountId());
        } catch (ClientException e) {
            System.err.println("\nError: " + e.getMessage());
        }

        System.out.println("\n===================================================================================");
        System.out.println("                                  ACCOUNT STATEMENT                                  ");
        System.out.println("===================================================================================");
        System.out.println(String.format("Account Number: %s", detailDTO.getAccountNumber()));
        System.out.println(String.format("Holder Name: %s", detailDTO.getHolderFullName()));
        System.out.println(String.format("Account Pin: %s", detailDTO.getAccountPin()));
        System.out.println(String.format("Holder Account Id: %s", detailDTO.getHolderId()));
        System.out.println(String.format("Current Balance: %s", detailDTO.getCurrentBalance()));
        System.out.println("===================================================================================");

        if(detailDTO.getLastTransactions() != null && !detailDTO.getLastTransactions().isEmpty()){
            System.out.println("                                  LAST TRANSACTIONS                                ");
            System.out.println("===================================================================================");

            String leftAlignFormat = "| %-22s | %-13s | %-8s | %-27s |%n";
            System.out.format("+------------------------+---------------+----------+-----------------------------+%n");
            System.out.format("| Date                   | Type          | Amount   | Description                 |%n");
            System.out.format("+------------------------+---------------+----------+-----------------------------+%n");
            for (TransactionDetailDTO transaction : detailDTO.getLastTransactions()) {
                System.out.format(leftAlignFormat, transaction.getTransactionDate(), transaction.getTransactionType(), transaction.getAmount().toString(), transaction.getDescription());
            }
            System.out.format("+------------------------+---------------+----------+-----------------------------+%n");
        }

        printReturnHomeMenu();
    }

    private void printReturnHomeMenu() {
        System.out.println("\n1. Go Back");
        System.out.println("2. Exit");

        final Scanner in = new Scanner(System.in);
        System.out.print("-> Select an option:  ");
        int option = readOption(in);

        switch (option){
            case 1:
                printHomePage();
                break;
            case 2:
                System.exit(0);
                break;
            default: {
                System.err.println("Option is not correct. Please try again.");
                printReturnHomeMenu();
            }
        }
    }

    private void makeDeposit() {
        Scanner in = new Scanner(System.in);
        System.out.print("Amount: ");
        BigDecimal amount = in.nextBigDecimal();

        System.out.print("Description: ");
        in = new Scanner(System.in);
        String description = in.next();

        try {
            final ResponseDTO detailDTO = client.makeDeposit(this.selectedAccount.getAccountId(),
                new TransactionRequestDTO(amount, description)
            );
            System.out.println("Transaction executed successfully with id: " + detailDTO.getTransactionId());
        } catch (ClientException e) {
            System.err.println("\nError: " + e.getMessage());
        }

        printReturnHomeMenu();
    }

    private void makeWithdrawal() {
        Scanner in = new Scanner(System.in);
        System.out.print("Amount: ");
        BigDecimal amount = in.nextBigDecimal();

        System.out.print("Description: ");
        in = new Scanner(System.in);
        String description = in.next();

        try {
            final ResponseDTO detailDTO = client.makeWithdrawal(this.selectedAccount.getAccountId(),
                    new TransactionRequestDTO(amount, description)
            );
            System.out.println("Transaction executed successfully with id: " + detailDTO.getTransactionId());
        } catch (ClientException e) {
            System.err.println("\nError: " + e.getMessage());
        }

        printReturnHomeMenu();
    }

    private void openAccount() {
        Scanner in = new Scanner(System.in);
        System.out.print("First Name: ");
        String firstName = in.next();

        System.out.print("Last Name: ");
        in = new Scanner(System.in);
        String lastName = in.next();

        System.out.print("PIN: ");
        in = new Scanner(System.in);
        String pin = in.next();

        System.out.print("Confirm PIN: ");
        in = new Scanner(System.in);
        String confPin = in.next();

        System.out.print("ID (SSN, Voter Card ID): ");
        in = new Scanner(System.in);
        String holderId = in.next();

        try {
            final ResponseDTO detailDTO = client.openNewAccount(new AccountRequestDTO(
                    firstName, lastName, pin, confPin, holderId
            ));
            System.out.println("Transaction executed successfully");
            System.out.println("Account number: " + detailDTO.getAccountNumber());
            System.out.println("Pin number: " + detailDTO.getPin());
        } catch (ClientException e) {
            System.err.println("\nError: " + e.getMessage());
        }

        printMainMenu();
    }
}
