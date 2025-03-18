package com.example.controller;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;
import java.util.Optional;

@RestController
public class SocialMediaController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageService messageService;

    @PostMapping("/register")
    public ResponseEntity<Account> register(@RequestBody Account account) {
        try {
            return ResponseEntity.status(200).body(accountService.register(account));
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("Username already exists")) {
                return ResponseEntity.status(409).build();
            }
            return ResponseEntity.status(400).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Account account) {
        Optional<Account> loggedInAccount = accountService.login(account.getUsername(), account.getPassword());
        return loggedInAccount.map(value -> ResponseEntity.status(200).body(value))
                .orElseGet(() -> ResponseEntity.status(401).build());
    }

    @PostMapping("/messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        if (message.getMessageText() == null || message.getMessageText().isBlank() || message.getMessageText().length() > 255) {
            return ResponseEntity.status(400).build();
        }
        try {
            return ResponseEntity.status(200).body(messageService.createMessage(message));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).build();
        }
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        return ResponseEntity.status(200).body(messageService.getAllMessages());
    }

    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable Integer messageId) {
        Optional<Message> message = messageService.getMessageById(messageId);
        return message.map(value -> ResponseEntity.status(200).body(value))
                .orElseGet(() -> ResponseEntity.status(200).body(null));
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Integer> deleteMessage(@PathVariable Integer messageId) {
        int rowsDeleted = messageService.deleteMessage(messageId);
        if (rowsDeleted == 0) {
            return ResponseEntity.status(200).build();
        }
        return ResponseEntity.status(200).body(rowsDeleted);
    }

    @PatchMapping("/messages/{messageId}")
public ResponseEntity<Integer> updateMessageText(@PathVariable Integer messageId,
                                                 @RequestBody Map<String, String> payload) {
    String messageText = payload.get("messageText"); 
    if (messageText == null || messageText.isBlank() || messageText.length() > 255) {
        return ResponseEntity.status(400).build();
    }
    try {
        int rowsUpdated = messageService.updateMessageText(messageId, messageText);
        if (rowsUpdated == 1) {
            return ResponseEntity.status(200).body(rowsUpdated);
        } else {
            return ResponseEntity.status(400).build(); 
        }
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(400).build(); 
    }
}

    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getMessagesByAccountId(@PathVariable Integer accountId) {
        return ResponseEntity.status(200).body(messageService.getMessagesByPostedBy(accountId));
    }
}