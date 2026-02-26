package com.bitespeed.identity.controller;

import com.bitespeed.identity.dto.IdentifyRequest;
import com.bitespeed.identity.dto.IdentifyResponse;
import com.bitespeed.identity.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/identify")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<IdentifyResponse> identify(@RequestBody IdentifyRequest request) {
        return ResponseEntity.ok(contactService.identify(request));
    }
}