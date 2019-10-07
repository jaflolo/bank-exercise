# Bank Exercise

This project was created to show some Spring Boot features.

## 1. Tools and Technologies Used

- OS: Ubuntu Xenial Version 18.04 
- Maven: Version 3.6.0
- Java: Version 1.8.0_222
- Spring Boot: Version 2.1.8.RELEASE
- IntelliJ IDEA: Version 2019.2 (Community Edition)
- Swagger: Version 2+

## 2. Compile Project

The project can be compiled by executing the following maven command inside account-service folder.

```bash
mvn clean compile

```
or by using the maven wrapper
```bash
./mvnw clean compile
```

## 3. Execute Tests
To execute all tests 

```bash
mvn clean test
```
or by using the maven wrapper
```bash
./mvnw clean test
```
## 4. Generate Jar File

If previous steps were ok, next step is to generate jar file without tests (as we already executed before). Execute the next maven command.

```bash
mvn clean package -Dmaven.test.skip=true
```
or by using the maven wrapper
```bash
./mvnw clean package -Dmaven.test.skip=true

```
## 5. Run the project
Once the jar file is generated, we will proceed to run the project.  AS you can see we have two scripts, the first one is `run-service.sh` which will start up the spring boot project with all REST services and the other one is `run-client.sh` which will run the command line program interface.

Please execute both scripts in different terminals with the following commands:
```bash
sh run-service.sh
sh run-client.sh
```
## 6. Console Interface Usage
The Client is a very simple command line interface which allows you to execute functionality based on short menus like this: 

#### a. Welcome Menu
```
================================================
============ Welcome to Test Bank ==============
================================================
1. Open a new account
2. Login
3. Exit
Select the desired option:  __
```
By capturing the proper option from the menu you will be prompted to capture required information. For example, lets open an account:
```
================================================
============ Welcome to Test Bank ==============
================================================
1. Open a new account
2. Login
3. Exit
Select the desired option:  1
First Name: Foo
Last Name: Bar
PIN: 1234
Confirm PIN: 1234
ID (SSN, Voter Card ID): 54654654654
Transaction executed successfully
Account number: 132016815
Pin number: 1234
```
#### b. User Home
Once the user has been authenticated, next screen will appear, from here user can decide what to do from personal checks account.
```
===================================================================================
 Welcome Foo Bar
===================================================================================
Date: 04-Oct-2019 03:22:10
Account Number: 132016815

What do you want to do?
1. Make a deposit
2. Make a withdrawal
3. Get Account Statement
4. Log out
===================================================================================
-> Select an option:  1
```
#### c. Make a Deposit
User can always interact with his personal account. 
```
Amount: 20
Description: Incoming Transfer
Transaction executed successfully with id: 6e7a52cb-c8ea-42f3-bea6-689192953b4c

1. Go Back
2. Exit
-> Select an option:  __
```
#### d. Get Account Statement
User can always interact with his personal account. 
```
===================================================================================
                                  ACCOUNT STATEMENT                                  
===================================================================================
Account Number: 132016815
Holder Name: Foo Bar
Account Pin: 1234
Holder Account Id: 54654654654
Current Balance: 20.00
===================================================================================
                                  LAST TRANSACTIONS                                
===================================================================================
+------------------------+---------------+----------+-----------------------------+
| Date                   | Type          | Amount   | Description                 |
+------------------------+---------------+----------+-----------------------------+
| 04-Oct-2019 03:25:39   | DEPOSIT       | 20.00    | Incoming                    |
+------------------------+---------------+----------+-----------------------------+

1. Go Back
2. Exit
-> Select an option:  
```
That's it, feel free to interact with the client interface.

## Extra. Simple Command Line Interface
There was also added a command line interface where you can execute simple commands to execute operations on a checks banck account.

For help information, please run the following command
```
$ ./bank.sh --help
```
and you we'll see something like this:
```
Available Commands
    open             Open an account
          open  [First Name] [Last Name] [Pin] [Confirm Pin] [ID SSN]   
    login            Login to an existing account
          login [Account number] [PIN] 
    close            Close an account
    deposit          Makes a deposit
          deposit [amount] [description] 
    withdraw         Makes a withdraw
          withdraw [amount] [description] 
    balance          Get the current balance for actual logged in account
    logout           Logout the actual logged int account
```
Once you have read the available commands, then you can start executing functionality like the following full example
```
$ ./bank.sh open Jaime Flores 1234 1234 asdasdasd123123
Transaction executed successfully
Account number: 320292025
Pin number: 1234
$ ./bank.sh login 320292025 1234
OK...
$ ./bank.sh deposit 1000 "Salary" && ./bank.sh deposit 20 "Gasonline" && ./bank.sh deposit 100 "Concert tickets"
Transaction ok  47063dce-71ab-4f03-9ba4-a0ee258ccb9b
Transaction ok  180b59d6-4dbf-4348-8d03-88a521e7efdd
Transaction ok  0ddad77f-2bfb-4f91-a89b-2c5abe61c6db
$ ./bank.sh balance
Current balance is 1120.00
$ ./bank.sh logout
Logout ok...
```

## Lauch Swagger
While the spring boot proyect is started, you can lauch swagger UI, it is available at http://localhost:8080/swagger-ui.html. Here is the Home Page of Swagger where you can see all the resources that are exposed.

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[Apache License Version 2.0](http://www.apache.org/licenses/)
