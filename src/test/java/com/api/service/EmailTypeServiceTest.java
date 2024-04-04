package com.api.service;

import com.api.component.Cache;
import com.api.component.CustomLogger;
import com.api.dao.EmailRepository;
import com.api.dao.EmailTypeRepository;
import com.api.dto.DomainDTO;
import com.api.entity.Email;
import com.api.entity.EmailType;
import com.api.exceptions.ServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.configuration.injection.MockInjection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailTypeServiceTest {
    @InjectMocks
    EmailTypeService emailTypeService;

    @Mock
    EmailTypeRepository emailTypeRepository;

    @Mock
    CustomLogger customLogger;

    @Mock
    Cache cache;

    @Mock
    EmailRepository emailRepository;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testDeleteDomain_ByValidIdAndEmptyEmails_Success() {
        // Mock data
        Long id = 1L;
        EmailType emailTypeEntity = new EmailType("example.com");
        emailTypeEntity.setId(id);

        // Mock repository behavior
        when(emailTypeRepository.findById(id)).thenReturn(Optional.of(emailTypeEntity));

        // Invoke method
        emailTypeService.deleteDomain(id);

        // Verify interactions and assertions
        verify(emailTypeRepository).findById(id);
        verify(emailTypeRepository).delete(emailTypeEntity);
        verify(cache).remove(emailTypeEntity.getDomain());
        verify(customLogger).logCacheRemove(emailTypeEntity.getDomain());
    }

    @Test
    public void testDeleteDomain_ByValidNameAndEmptyEmails_Success() {
        // Mock data
        String domain = "example.com";
        EmailType emailTypeEntity = new EmailType(domain);

        // Mock repository behavior
        when(cache.get(domain)).thenReturn(Optional.of(emailTypeEntity));
        when(emailTypeRepository.findByDomain(domain)).thenReturn(emailTypeEntity); // Mocking findByDomain()

        // Invoke method
        emailTypeService.deleteDomain(domain);

        // Verify interactions and assertions
        verify(cache, times(2)).get(domain);// Ensure findByDomain() is called
        verify(emailTypeRepository).delete(emailTypeEntity);
        verify(cache).remove(emailTypeEntity.getDomain());
        verify(customLogger).logCacheRemove(emailTypeEntity.getDomain());

}

    @Test
    public void testAddDomain_ValidDomain_Success() {
        // Mock data
        String domain = "example.com";

        // Mock cache behavior
        when(cache.get(domain)).thenReturn(null);
        // Mock repository behavior
        when(emailTypeRepository.findByDomain(domain)).thenReturn(null);

        // Invoke method
        emailTypeService.addDomain(domain);

        // Verify interactions and assertions
        verify(cache).get(domain);
        verify(emailTypeRepository).findByDomain(domain);
        verify(emailTypeRepository).save(any(EmailType.class));
        verify(customLogger).logCachePut(domain);
    }

    @Test
    public void testAddDomain_DuplicateDomain_ThrowsServiceException() {
        // Mock data
        String domain = "example.com";

        // Mock cache behavior
        when(cache.get(domain)).thenReturn(null);
        // Mock repository behavior
        when(emailTypeRepository.findByDomain(domain)).thenReturn(new EmailType(domain));

        // Invoke method - this should throw ServiceException
        assertThrows(ServiceException.class, () -> emailTypeService.addDomain(domain));

        // Verify interactions and assertions
        verify(cache).get(domain);
        verify(emailTypeRepository).findByDomain(domain);
        // ServiceException expected
    }

    @Test
    public void testAddDomain_InvalidDomain_ThrowsServiceException() {
        // Mock data
        String domain = "invalid_domain";

        // Invoke method - this should throw ServiceException
        ;
        assertThrows(ServiceException.class, () -> emailTypeService.addDomain(domain));

        // Verify interactions and assertions
        // ServiceException expected
    }

    @Test
    public void testGetDomains_Success() {
        // Mock data
        List<EmailType> emailTypes = new ArrayList<>();
        emailTypes.add(new EmailType( "example1.com"));
        emailTypes.add(new EmailType( "example2.com"));

        // Mock repository behavior
        when(emailTypeRepository.findAll()).thenReturn(emailTypes);

        // Invoke method
        List<DomainDTO> result = emailTypeService.getDomains();

        // Verify interactions and assertions
        assertEquals(emailTypes.size(), result.size());
        verify(emailTypeRepository).findAll();
        for (EmailType emailType : emailTypes) {
            verify(cache).put(emailType.getDomain(), emailType);
            verify(customLogger).logCachePut(emailType.getDomain());
        }
    }
    public void testUpdateDomain_ById_Success() {
        // Mock data
        Long id = 1L;
        String newDomain = "newexample.com";
        EmailType emailTypeEntity = new EmailType( "example.com");

        // Mock repository behavior
        when(emailTypeRepository.findById(id)).thenReturn(Optional.of(emailTypeEntity));

        // Invoke method
        emailTypeService.updateDomain(id, newDomain);

        // Verify interactions and assertions
        verify(cache).remove("example.com");
        verify(emailTypeRepository).findById(id);
        verify(emailTypeRepository).save(emailTypeEntity);
        verify(customLogger).logCachePut("newexample.com");
    }

    @Test
    public void testUpdateDomain_ByDomain_Success() {
        // Mock data
        String domain = "example.com";
        String newDomain = "newexample.com";
        EmailType emailTypeEntity = new EmailType(domain);

        // Mock repository behavior
        when(emailTypeRepository.findByDomain(domain)).thenReturn(emailTypeEntity);

        // Invoke method
        assertThrows(NullPointerException.class, () ->  emailTypeService.updateDomain(domain, newDomain));

        // Verify interactions and assertions
        verify(emailTypeRepository).findByDomain(domain);
    }
}
