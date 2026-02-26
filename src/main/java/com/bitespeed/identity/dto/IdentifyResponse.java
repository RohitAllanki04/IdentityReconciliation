package com.bitespeed.identity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class IdentifyResponse {
    private ContactPayload contact;

    @Data
    @AllArgsConstructor
    public static class ContactPayload {
        private Long primaryContatctId;
        private List<String> emails;
        private List<String> phoneNumbers;
        private List<Long> secondaryContactIds;
    }
}