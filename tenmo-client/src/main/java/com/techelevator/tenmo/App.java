package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TenmoService;
import com.techelevator.tenmo.services.TransferService;

import java.math.BigDecimal;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final TransferService transferService = new TransferService();
    private AuthenticatedUser currentUser; //when make a call to server, use this person with token
    TenmoService tenmoService = new TenmoService();

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }

    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        } else {
            tenmoService.setAuthToken(currentUser.getToken());
            transferService.setAuthToken(currentUser.getToken()); // need to pass token to server
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
//            } else if (menuSelection == 6) {
//                viewTransferById();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }
    private void viewCurrentBalance() {
        // TODO Auto-generated method stub
        System.out.println(tenmoService.getBalance()); //using the principal
    }
    private void viewTransferHistory() {
        Transfer[] transferList = transferService.getTransferList();
        consoleService.printTransactionHeader();
        for (Transfer eachTransfer : transferList) {
            System.out.println(eachTransfer.toString());
        }
        consoleService.printTransactionHeaderBottom();
        int transferId = consoleService.promptForInt("Enter transfer id to view transaction (0 to cancel): ");
        if (transferId == 0) {
            consoleService.printMainMenu();
        } else {
            consoleService.printTransactionDetailsHeader();
            Transfer transfer = new Transfer();
            transfer = transferService.getTransferById(transferId);
            System.out.println(transfer.toStringForTransferDetails());
            consoleService.printTransactionHeaderBottom();
        }
    }
    private void viewPendingRequests() {
        // TODO Auto-generated method stub
    }

    private void sendBucks() {
        User[] userList = tenmoService.getAllUsersForSendingMoney();
        consoleService.printSendTEBucksHeader();
        for (User eachUser : userList) {
            System.out.println(eachUser.toString()); //will print out each user down on the list

            /**
             * match id boolean
             * while true
             * prmpty enter id the send money to
             *
             */
        }
        boolean matchId = false;
        consoleService.printTransactionHeaderBottom();
        while (!matchId) {
            int userToId = consoleService.promptForInt("\nEnter user id to send money to (0 to cancel): ");

                for (User user : userList) {
                    if (user.getId() == userToId && userToId != 0) {

                        if (!user.getUsername().equals(currentUser)) {
                            matchId = true;
                        }

                    } else if (userToId == 0){  consoleService.printMainMenu(); return; } // break breaks out of loop
                }                                                                         // return breaks out of the method

                if (matchId) {
                    double inputAmount = consoleService.promptForDouble("\nEnter Dollar amount including decimal: $");
                    BigDecimal transferAmount = BigDecimal.valueOf(inputAmount);
                    Transfer transfer = new Transfer();
                    transfer.setAmount(transferAmount);
                    transfer.setReceiverId(userToId);
                    transferService.sendMoney(transfer);

                } else {
                    System.out.println("User ID is not found");
                    break;
                }
            }
        }


        private void requestBucks() {
            // TODO Auto-generated method stub
        }
    }

