package com.shepherdmoney.interviewproject.service;

import com.shepherdmoney.interviewproject.model.*;
import com.shepherdmoney.interviewproject.repository.CreditCardRepository;
import com.shepherdmoney.interviewproject.repository.UserRepository;
import com.shepherdmoney.interviewproject.vo.request.AddCreditCardToUserPayload;
import com.shepherdmoney.interviewproject.vo.request.CreateUserPayload;
import com.shepherdmoney.interviewproject.vo.request.UpdateBalancePayload;
import com.shepherdmoney.interviewproject.vo.response.CreditCardView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.*;

@Service
public class TransactionService {

    // NOTE: All methods are designed for Transactional management to maintain data integrity

    private CreditCardRepository creditCardRepository;
    private UserRepository userRepository;
    @Autowired
    public TransactionService(CreditCardRepository creditCardRepository, UserRepository userRepository) {
        this.creditCardRepository = creditCardRepository;
        this.userRepository = userRepository;
    }

    /**
     * Adds a user in a transaction.
     *
     * Saves the new user as an entity.
     *
     * @param payload A CreateUserPayload object containing the new user information.
     * @return A ResponseEntity indicating HttpStatus.OK (200) and the
     *         corresponding user ID if the update is successful.
     */
    @Transactional
    public ResponseEntity<Integer> saveUserInTransaction(CreateUserPayload payload) {
        User newUser = new User(payload.getName(), payload.getEmail());
        userRepository.save(newUser);

        return new ResponseEntity<Integer>(newUser.getId(), HttpStatus.OK);
    }

    /**
     * Deletes the user in a transaction.
     *
     * Searches for the user ID and deletes the entity.
     *
     * @param userId An int containing the user ID.
     * @return A ResponseEntity indicating the result:
     *         - HttpStatus.OK (200) and a message if the deletion is successful.
     *         - HttpStatus.BAD_REQUEST (400) and a message if the user does not exist.
     */
    @Transactional
    public ResponseEntity<String> deleteUserInTransaction(int userId) {
        String responseBody;
        HttpStatus responseStatus;

        if(userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            responseBody = "User successfully deleted.";
            responseStatus = HttpStatus.OK;
        } else {
            responseBody = "User not found.";
            responseStatus = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<String>(responseBody, responseStatus);
    }

    /**
     * Saves a new credit card in a transaction
     *
     * Searches for the user and saves a new credit card with them as the owner.
     *
     * @param payload An AddCreditCardToUserPayload object with information for the new card entity.
     * @return A ResponseEntity indicating the result:
     *         - HttpStatus.OK (200) and the corresponding card ID if the update is successful.
     *         - HttpStatus.BAD_REQUEST (400) if the user does not exist.
     */
    @Transactional
    public ResponseEntity<Integer> saveCardInTransaction(AddCreditCardToUserPayload payload) {
        //Check to see if user exists
        Optional<User> checkUser = userRepository.findById(payload.getUserId());

        //If the user exists
        if (checkUser.isPresent()) {
            //Save the card
            CreditCard newCard = new CreditCard(payload.getUserId(), payload.getCardIssuanceBank(), payload.getCardNumber());
            creditCardRepository.save(newCard);

            //Add the cardId to the user's creditCardIdList
            User currUser = checkUser.get();
            currUser.appendCreditCardIdList(newCard.getId());

            return new ResponseEntity<Integer>(newCard.getId(), HttpStatus.OK);
        }

        return new ResponseEntity<Integer>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Gets all the credit cards owned by the user in a transaction.
     *
     * Searches for the user ID and lists all the credit cards they own.
     *
     * @param userId An int containing the user ID.
     * @return A ResponseEntity indicating the result:
     *         - HttpStatus.OK (200) and the list of credit cards if the query is successful.
     *         - HttpStatus.BAD_REQUEST (400) if the user does not exist.
     */
    @Transactional
    public ResponseEntity<List<CreditCardView>> getAllCardOfUserInTransaction(int userId) {
        List<CreditCardView> listOfCards = new ArrayList<CreditCardView>();

        //Check to see if user exists
        Optional<User> checkUser = userRepository.findById(userId);

        //If the user exists
        if (checkUser.isPresent()) {
            User currUser = checkUser.get();
            List<Integer> currUserCardList = currUser.getCreditCardIdList();
            CreditCard tempCard;

            //Iterate through every card
            for (int i = 0; i < currUserCardList.size(); i++) {
                //Find the Card in the repository
                tempCard = creditCardRepository.findById(currUserCardList.get(i)).get();

                //Create a CreditCardView object and append the list
                listOfCards.add(new CreditCardView(tempCard.getIssuanceBank(), tempCard.getNumber()));
            }
        }

        return new ResponseEntity<List<CreditCardView>>(listOfCards, HttpStatus.OK);
    }

    /**
     * Gets the user ID for a credit card in a transaction.
     *
     * Searches for the user ID of the user that owns the provided credit card.
     *
     * @param creditCardNumber A string containing the credit card number.
     * @return A ResponseEntity indicating the result:
     *         - HttpStatus.OK (200) and the corresponding user ID if the query is successful.
     *         - HttpStatus.BAD_REQUEST (400) if the credit card does not exist.
     */
    @Transactional
    public ResponseEntity<Integer> getUserIdForCreditCardInTransaction(String creditCardNumber) {
        //Query repository for Entity with the correct creditCardNumber attribute
        CreditCard currCard = creditCardRepository.findByNumber(creditCardNumber);

        if (currCard == null) {
            return new ResponseEntity<Integer>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Integer>(currCard.getUserId(), HttpStatus.OK);
    }

    /**
     * Updates the balance of a credit card in a transaction.
     *
     * Updates the balance of the desired credit card based on the provided
     * transaction payloads. The method also records balance history for each transaction.
     *
     * @param payload An array of UpdateBalancePayload objects containing transaction details.
     * @return A ResponseEntity with an HTTP status code indicating the result:
     *         - HttpStatus.OK (200) if the update is successful.
     *         - HttpStatus.BAD_REQUEST (400) if the credit card does not exist.
     */
    @Transactional
    public ResponseEntity<Integer> updateBalanceInTransaction(@RequestBody UpdateBalancePayload[] payload) {

        //Find the relevant card using the first item in the payload
        CreditCard currCard = creditCardRepository.findByNumber(payload[0].getCreditCardNumber());

        if (currCard == null) {
            return new ResponseEntity<Integer>(HttpStatus.BAD_REQUEST);
        }

        List<BalanceHistory> currBalanceList = currCard.getBalanceHistoryList();
        double newBalance = (currBalanceList.size() == 0) ? 0 : currBalanceList.get(0).getBalance();

        //Update the currBalanceList with every transaction in the payload
        for(int i = 0; i < payload.length; i++) {

            newBalance += payload[i].getTransactionAmount();
            currBalanceList.add(0, new BalanceHistory(payload[i].getTransactionTime(), newBalance));
        }

        currCard.setBalanceHistoryList(currBalanceList);

        return new ResponseEntity<Integer>(HttpStatus.OK);
    }
}
