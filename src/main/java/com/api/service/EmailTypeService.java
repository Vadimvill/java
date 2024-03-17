package com.api.service;

import com.api.component.Cache;
import com.api.component.CustomLogger;
import com.api.dao.EmailRepository;
import com.api.dao.EmailTypeRepository;
import com.api.dto.DomainDTO;
import com.api.entity.Email;
import com.api.entity.EmailType;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Service
public class EmailTypeService {
    private final EmailRepository emailRepository;
    private static final String EXCEPTION_MSG = "Domain has slave emails";
    private final Cache cache;
    private final CustomLogger customLogger;
    private final EmailTypeRepository emailTypeRepository;


    public EmailTypeService(EmailRepository emailRepository, Cache cache, CustomLogger customLogger, EmailTypeRepository emailTypeRepository){

        this.emailRepository = emailRepository;
        this.cache = cache;
        this.customLogger = customLogger;
        this.emailTypeRepository = emailTypeRepository;
    }
    @Transactional
    public boolean updateDomain(Long id, String newDomain) {

        Optional<EmailType> emailTypeEntityOptional = emailTypeRepository.findById(id);

        if (emailTypeEntityOptional.isPresent() && newDomain != null && !newDomain.isEmpty() && checkDomain(newDomain)) {
            cache.remove(emailTypeEntityOptional.get().getDomain());
            List<Email> emails = emailTypeEntityOptional.get().getEmails();
            if(!emails.isEmpty()){
                customLogger.logError(EXCEPTION_MSG);
                return false;
            }
            Iterator<Email> iterator = emails.iterator();
            while (iterator.hasNext()) {
                Email email = iterator.next();
                emailRepository.delete(email);
                iterator.remove();
            }

            emailTypeEntityOptional.get().setDomain(newDomain);
            customLogger.logCachePut(emailTypeEntityOptional.get().getDomain());
            emailTypeRepository.save(emailTypeEntityOptional.get());
            return true;
        }

        return false;
    }

    @Transactional
    public boolean updateDomain(String domain,String newDomain){
        EmailType emailType = emailTypeRepository.findByDomain(domain);

        if (emailType != null && newDomain != null && !newDomain.isEmpty() && checkDomain(newDomain)) {
            List<Email> emails = emailType.getEmails();
            if(!emails.isEmpty()){
                customLogger.logError(EXCEPTION_MSG);
                return false;
            }
            cache.remove(emailType.getDomain());
            Iterator<Email> iterator = emails.iterator();
            while (iterator.hasNext()) {
                Email email = iterator.next();
                emailRepository.delete(email);
                iterator.remove();
            }

            emailType.setDomain(newDomain);
            cache.put(emailType.getDomain(),emailType);
            emailTypeRepository.save(emailType);
            return true;
        }

        return false;
    }
    private boolean checkDomain(String text){
        String reg = "[a-z0-9.-]+\\.[a-z]{2,}\\b";
        Pattern emailPattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        Matcher emailMatcher = emailPattern.matcher(text);
        return emailMatcher.find();
    }
    @Transactional
    public boolean addDomain(String domain){
        if(checkDomain(domain) && cache.get(domain) == null){
            if(emailTypeRepository.findByDomain(domain) != null){
                customLogger.logError("Domain was in database");
                return false;
            }
            EmailType emailType = new EmailType(domain);
            emailTypeRepository.save(emailType);
            cache.put(emailType.getDomain(),emailType);
            customLogger.logCachePut(emailType.getDomain());
            return true;
        }
        else {
            customLogger.logInfo("Is not domain or value was in cache");
        }
        return false;
    }

    @Transactional
    public List<DomainDTO> getDomains(){
        List<EmailType> emailTypes = emailTypeRepository.findAll();
        List<DomainDTO> result = new ArrayList<>();
        for(int i = 0; i< emailTypes.size(); i++){
            cache.put(emailTypes.get(i).getDomain(),emailTypes.get(i));
            customLogger.logCachePut(emailTypes.get(i).getDomain());
            result.add(new DomainDTO(emailTypes.get(i).getId()
                    + ". " + emailTypes.get(i).getDomain()));
        }
        return result;
    }
    @Transactional
    public boolean deleteDomain(Long id){
        Optional<EmailType> emailTypeEntity = emailTypeRepository.findById(id);
        if(emailTypeEntity.isPresent()){
            if(!emailTypeEntity.get().getEmails().isEmpty()){
                customLogger.logError(EXCEPTION_MSG);
                return false;
            }
            emailTypeRepository.delete(emailTypeEntity.get());
            cache.remove(emailTypeEntity.get().getDomain());
            customLogger.logCacheRemove(emailTypeEntity.get().getDomain());
            return true;
        }
        else {
            return false;
        }
    }
    @Transactional
    public boolean deleteDomain(String name){
        Optional<EmailType> optionalEmailTypeEntity;
        if(cache.get(name) != null){
            optionalEmailTypeEntity = (Optional<EmailType>)cache.get(name);
            customLogger.logInfo("Value from cache");
        }
        else optionalEmailTypeEntity = Optional.ofNullable(emailTypeRepository.findByDomain(name));
        if(optionalEmailTypeEntity.isPresent()) {
            if(!optionalEmailTypeEntity.get().getEmails().isEmpty()){
                customLogger.logError(EXCEPTION_MSG);
                return false;
            }
            customLogger.logCacheRemove(optionalEmailTypeEntity.get().getDomain());
            emailTypeRepository.delete(emailTypeRepository.findByDomain(name));
            cache.remove(optionalEmailTypeEntity.get().getDomain());
            return true;
        }
        else {
            return false;
        }
    }
}
