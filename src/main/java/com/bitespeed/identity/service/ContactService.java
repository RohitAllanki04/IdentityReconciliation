package com.bitespeed.identity.service;

import com.bitespeed.identity.dto.IdentifyRequest;
import com.bitespeed.identity.dto.IdentifyResponse;
import com.bitespeed.identity.model.Contact;
import com.bitespeed.identity.model.Contact.LinkPrecedence;
import com.bitespeed.identity.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

    @Transactional
    public IdentifyResponse identify(IdentifyRequest request) {
        String email = request.getEmail();
        String phone = request.getPhoneNumber();

        // Step 1: Find matching contacts
        List<Contact> matches = contactRepository.findByEmailOrPhone(email, phone);

        // Step 2: No match → create new primary
        if (matches.isEmpty()) {
            Contact newContact = new Contact(email, phone, null, LinkPrecedence.primary);
            contactRepository.save(newContact);
            return buildResponse(newContact, Collections.emptyList());
        }

        // Step 3: Collect all primary IDs from matched contacts
        Set<Long> primaryIds = new HashSet<>();
        for (Contact c : matches) {
            if (c.getLinkPrecedence() == LinkPrecedence.primary) {
                primaryIds.add(c.getId());
            } else {
                primaryIds.add(c.getLinkedId());
            }
        }

        // Step 4: Load full cluster for each primary
        List<Contact> allContacts = new ArrayList<>();
        for (Long pid : primaryIds) {
            contactRepository.findById(pid).ifPresent(allContacts::add);
            allContacts.addAll(contactRepository.findByLinkedId(pid));
        }

        // Step 5: Find the oldest primary
        Contact primary = allContacts.stream()
                .filter(c -> c.getLinkPrecedence() == LinkPrecedence.primary)
                .min(Comparator.comparing(Contact::getCreatedAt))
                .orElseThrow();

        // Step 6: Demote newer primaries to secondary
        for (Contact c : allContacts) {
            if (c.getLinkPrecedence() == LinkPrecedence.primary
                    && !c.getId().equals(primary.getId())) {
                c.setLinkPrecedence(LinkPrecedence.secondary);
                c.setLinkedId(primary.getId());
                contactRepository.save(c);
            }
        }

        // Step 7: Create secondary if request has new info
        boolean emailExists = email == null ||
                allContacts.stream().anyMatch(c -> email.equals(c.getEmail()));
        boolean phoneExists = phone == null ||
                allContacts.stream().anyMatch(c -> phone.equals(c.getPhoneNumber()));

        if (!emailExists || !phoneExists) {
            Contact secondary = new Contact(email, phone, primary.getId(), LinkPrecedence.secondary);
            contactRepository.save(secondary);
            allContacts.add(secondary);
        }

        // Step 8: Build response
        List<Contact> secondaries = allContacts.stream()
                .filter(c -> c.getLinkPrecedence() == LinkPrecedence.secondary)
                .collect(Collectors.toList());

        return buildResponse(primary, secondaries);
    }

    private IdentifyResponse buildResponse(Contact primary, List<Contact> secondaries) {
        List<String> emails = new ArrayList<>();
        if (primary.getEmail() != null) emails.add(primary.getEmail());
        secondaries.stream()
                .map(Contact::getEmail)
                .filter(e -> e != null && !emails.contains(e))
                .forEach(emails::add);

        List<String> phones = new ArrayList<>();
        if (primary.getPhoneNumber() != null) phones.add(primary.getPhoneNumber());
        secondaries.stream()
                .map(Contact::getPhoneNumber)
                .filter(p -> p != null && !phones.contains(p))
                .forEach(phones::add);

        List<Long> secondaryIds = secondaries.stream()
                .map(Contact::getId)
                .collect(Collectors.toList());

        return new IdentifyResponse(
                new IdentifyResponse.ContactPayload(
                        primary.getId(), emails, phones, secondaryIds)
        );
    }
}