package com.api.service;

import com.api.dao.EmailRepository;
import com.api.dao.EmailIdRepository;
import com.api.dto.DomainDTO;
import com.api.dto.EmailDTO;
import com.api.entity.EmailEntity;
import com.api.entity.EmailTypeEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Service
public class ProcessService {
    private final EmailRepository emailRepository;
    private static final String EMAIL_REGEX_PATTERN = "\\b[a-z0-9._-]+@[a-z0-9.-]+\\.[a-z]{2,}\\b";
    private static String adresRegex = "@([^.]*)\\.";
    private final EmailIdRepository emailIdRepository;


    public ProcessService(EmailRepository emailRepository, EmailIdRepository emailIdRepository){

        this.emailRepository = emailRepository;

        this.emailIdRepository = emailIdRepository;
    }
    @Transactional
    public boolean deleteEmail(Long id) {
        Optional<EmailEntity> optionalEmail = emailRepository.findById(id);

        if (optionalEmail.isPresent()) {
            EmailEntity emailEntity = optionalEmail.get();

            emailRepository.delete(emailEntity);
            return true;
        }
        else {
            return false;
        }
    }

    public boolean deleteEmail(String email) {
        Optional<EmailEntity> optionalEmail = Optional.ofNullable(emailRepository.findByEmail(email));

        if (optionalEmail.isPresent()) {
            EmailEntity emailEntity = optionalEmail.get();

            emailRepository.delete(emailEntity);
            return true;
        }
        else {
            return false;
        }

    }
    @Transactional
    public List<DomainDTO> getDomains(){
        List<EmailTypeEntity> emailTypeEntitys = emailIdRepository.findAll();
        List<DomainDTO> result = new ArrayList<>();
        for(int i = 0;i<emailTypeEntitys.size();i++){
            result.add(new DomainDTO(emailTypeEntitys.get(i).getTypeEmailId()
                    + ". " + emailTypeEntitys.get(i).getDomain()));
        }
        return result;
    }
    @Transactional
    public boolean deleteDomain(Long id){
        Optional<EmailTypeEntity> emailTypeEntity = emailIdRepository.findById(id);
        if(emailTypeEntity.isPresent()){
            emailIdRepository.delete(emailTypeEntity.get());
            return true;
        }
        else {
            return false;
        }
    }
    @Transactional
    public boolean deleteDomain(String name){
        Optional<EmailTypeEntity> optionalEmailTypeEntity =  Optional.ofNullable
                (emailIdRepository.findByDomain(name));
        if(optionalEmailTypeEntity.isPresent()) {
            emailIdRepository.delete(emailIdRepository.findByDomain(name));
            return true;
        }
        else {
            return false;
        }
    }
    @Transactional
    public boolean updateEmail(Long id,String newEmail){
        Pattern emailPattern = Pattern.compile(EMAIL_REGEX_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher emailMatcher = emailPattern.matcher(newEmail);
        if(!emailMatcher.find()) return false;
        Optional<EmailEntity> emailEntity = emailRepository.findById(id);
        if(emailEntity.isPresent()){
            Pattern adresPattern = Pattern.compile(adresRegex, Pattern.CASE_INSENSITIVE);
            Matcher adresMatcher;
            adresMatcher = adresPattern.matcher(newEmail);
            if(adresMatcher.find()){
                String domain = adresMatcher.group(1);
                EmailTypeEntity emailTypeEntity = emailIdRepository.findByDomain(domain);
                if(emailTypeEntity != null){
                    emailEntity.get().setEmail(newEmail);
                    emailEntity.get().setEmailTypeEntity(emailTypeEntity);
                }
                else {
                    emailTypeEntity = new EmailTypeEntity(domain);
                    emailIdRepository.save(emailTypeEntity);
                    emailEntity.get().setEmail(newEmail);
                    emailEntity.get().setEmailTypeEntity(emailTypeEntity);
                }

            }
            return true;

        }
        return false;
    }

    @Transactional
    public boolean updateEmail(String email,String newEmail) {
        Pattern emailPattern = Pattern.compile(EMAIL_REGEX_PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher emailMatcher = emailPattern.matcher(newEmail);
        if (!emailMatcher.find()) return false;
        EmailEntity emailEntity = emailRepository.findByEmail(email);
        if (emailEntity != null) {
            Pattern adresPattern = Pattern.compile(adresRegex, Pattern.CASE_INSENSITIVE);
            Matcher adresMatcher;
            adresMatcher = adresPattern.matcher(newEmail);
            if (adresMatcher.find()) {
                String domain = adresMatcher.group(1);
                EmailTypeEntity emailTypeEntity = emailIdRepository.findByDomain(domain);
                if (emailTypeEntity != null) {
                    emailEntity.setEmail(newEmail);
                    emailEntity.setEmailTypeEntity(emailTypeEntity);
                } else {
                    emailTypeEntity = new EmailTypeEntity(domain);
                    emailIdRepository.save(emailTypeEntity);
                    emailEntity.setEmail(newEmail);
                    emailEntity.setEmailTypeEntity(emailTypeEntity);
                }

            }
            return true;

        }
        return false;
    }
    @Transactional
    public List<EmailDTO> getEmails(String text){
        List<EmailDTO> strings = new ArrayList<>();
        List<EmailEntity> emailEntities;
        if(text.equals("all")){
           emailEntities = emailRepository.findAll();
        }
        else {
            EmailTypeEntity emailTypeEntity = emailIdRepository.findByDomain(text);
            if (emailTypeEntity != null) {
                emailEntities = emailTypeEntity.getEmails();
            }
            else {
                return null;
            }
        }
        for(int i = 0;i<emailEntities.size();i++){
            strings.add(new EmailDTO(emailEntities.get(i).getId().toString() + ". " + emailEntities.get(i).getEmail()));
        }
        return strings;
    }

    @Transactional
    public String getConfidentialText(String text) {
      
            List<String> list = new ArrayList<>();
            String phoneRegex = "\\b(?:\\+\\d{1,3}[-.\\s]?)?(\\d{1,4}[-.\\s]?){1,2}\\d{1,9}\\b";
            Pattern phonePattern = Pattern.compile(phoneRegex);
            Matcher phoneMatcher = phonePattern.matcher(text);
            text = phoneMatcher.replaceAll("");

            Pattern emailPattern = Pattern.compile(EMAIL_REGEX_PATTERN, Pattern.CASE_INSENSITIVE);
            Matcher emailMatcher = emailPattern.matcher(text);


            while (emailMatcher.find()) {
                list.add(emailMatcher.group());
            }
            text = emailMatcher.replaceAll("");

            Pattern adresPattern = Pattern.compile(adresRegex, Pattern.CASE_INSENSITIVE);
            Matcher adresMatcher;


            for (String email : list) {
                adresMatcher = adresPattern.matcher(email);

                if (adresMatcher.find()) {

                    String domain = adresMatcher.group(1);
                    EmailTypeEntity findEntity = emailIdRepository.findByDomain(domain);
                    if(emailRepository.findByEmail(email) != null) continue;
                    if(findEntity == null){
                        EmailTypeEntity emailTypeEntity = new EmailTypeEntity(domain);

                        emailIdRepository.save(emailTypeEntity);

                        EmailEntity emailEntity = new EmailEntity(email, emailTypeEntity);
                        emailRepository.save(emailEntity);

                    }
                    else {
                        EmailEntity emailEntity = new EmailEntity(email, findEntity);
                        emailRepository.save(emailEntity);
                    }

                }
            }
 
        return text;

    }

}


