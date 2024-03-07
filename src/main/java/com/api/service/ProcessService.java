package com.api.service;

import com.api.dao.EmailRepository;
import com.api.dao.EmailTypeRepository;
import com.api.dto.DomainDTO;
import com.api.dto.EmailDTO;
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
public class ProcessService {
    private final EmailRepository emailRepository;
    private static final String EMAIL_REGEX = "\\b[a-z0-9._-]+@[a-z0-9.-]+\\.[a-z]{2,}\\b";
    private static String adresRegex = "@[a-z0-9.-]+\\.[a-z]{2,}\\b";
    private final EmailTypeRepository emailTypeRepository;


    public ProcessService(EmailRepository emailRepository, EmailTypeRepository emailTypeRepository){

        this.emailRepository = emailRepository;

        this.emailTypeRepository = emailTypeRepository;
    }
    @Transactional
    public boolean updateDomain(Long id, String newDomain) {
        Optional<EmailType> emailTypeEntityOptional = emailTypeRepository.findById(id);

        if (emailTypeEntityOptional.isPresent() && newDomain != null && !newDomain.isEmpty() && checkDomain(newDomain)) {

                List<Email> emails = emailTypeEntityOptional.get().getEmails();
                if(!emails.isEmpty()) return false;
                Iterator<Email> iterator = emails.iterator();
                while (iterator.hasNext()) {
                    Email email = iterator.next();
                    emailRepository.delete(email);
                    iterator.remove();
                }

                emailTypeEntityOptional.get().setDomain(newDomain);

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
                if(!emails.isEmpty()) return false;
                Iterator<Email> iterator = emails.iterator();
                while (iterator.hasNext()) {
                    Email email = iterator.next();
                    emailRepository.delete(email);
                    iterator.remove();
                }

                emailType.setDomain(newDomain);

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
        if(checkDomain(domain)){
            if(emailTypeRepository.findByDomain(domain) != null){
                return false;
            }
            emailTypeRepository.save(new EmailType(domain));
            return true;
        }
        return false;
    }
    @Transactional
    public boolean deleteEmail(Long id) {
        Optional<Email> optionalEmail = emailRepository.findById(id);

        if (optionalEmail.isPresent()) {
            Email email = optionalEmail.get();

            emailRepository.delete(email);
            return true;
        }
        else {
            return false;
        }
    }

    public boolean deleteEmail(String email) {
        Optional<Email> optionalEmail = Optional.ofNullable(emailRepository.findByName(email));

        if (optionalEmail.isPresent()) {
            Email emailEntity = optionalEmail.get();

            emailRepository.delete(emailEntity);
            return true;
        }
        else {
            return false;
        }

    }
    @Transactional
    public List<DomainDTO> getDomains(){
        List<EmailType> emailTypes = emailTypeRepository.findAll();
        List<DomainDTO> result = new ArrayList<>();
        for(int i = 0; i< emailTypes.size(); i++){
            result.add(new DomainDTO(emailTypes.get(i).getId()
                    + ". " + emailTypes.get(i).getDomain()));
        }
        return result;
    }
    @Transactional
    public boolean deleteDomain(Long id){
        Optional<EmailType> emailTypeEntity = emailTypeRepository.findById(id);
        if(emailTypeEntity.isPresent()){
            if(!emailTypeEntity.get().getEmails().isEmpty()) return false;
            emailTypeRepository.delete(emailTypeEntity.get());
            return true;
        }
        else {
            return false;
        }
    }
    @Transactional
    public boolean deleteDomain(String name){
        Optional<EmailType> optionalEmailTypeEntity =  Optional.ofNullable
                (emailTypeRepository.findByDomain(name));
        if(optionalEmailTypeEntity.isPresent()) {
            if(!optionalEmailTypeEntity.get().getEmails().isEmpty()) return false;
            emailTypeRepository.delete(emailTypeRepository.findByDomain(name));
            return true;
        }
        else {
            return false;
        }
    }
    @Transactional
    public boolean updateEmail(Long id,String newEmail){
        Pattern emailPattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher emailMatcher = emailPattern.matcher(newEmail);
        if(!emailMatcher.find()) return false;
        Optional<Email> emailEntity = emailRepository.findById(id);
        if(emailEntity.isPresent()){
            Pattern adresPattern = Pattern.compile(adresRegex, Pattern.CASE_INSENSITIVE);
            Matcher adresMatcher;
            adresMatcher = adresPattern.matcher(newEmail);
            if(adresMatcher.find()){
                String domain = adresMatcher.group(1);
                EmailType emailType = emailTypeRepository.findByDomain(domain);
                if(emailType != null){
                    emailEntity.get().setEmail(newEmail);
                    emailEntity.get().setEmailTypeEntity(emailType);
                }
                else {
                    emailType = new EmailType(domain);
                    emailTypeRepository.save(emailType);
                    emailEntity.get().setEmail(newEmail);
                    emailEntity.get().setEmailTypeEntity(emailType);
                }

            }
            return true;

        }
        return false;
    }

    @Transactional
    public boolean updateEmail(String email,String newEmail) {
        Pattern emailPattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher emailMatcher = emailPattern.matcher(newEmail);
        if (!emailMatcher.find()) return false;
        Email emailEntity = emailRepository.findByName(email);
        if (emailEntity != null) {
            Pattern adresPattern = Pattern.compile(adresRegex, Pattern.CASE_INSENSITIVE);
            Matcher adresMatcher;
            adresMatcher = adresPattern.matcher(newEmail);
            if (adresMatcher.find()) {
                String domain = adresMatcher.group(1);
                EmailType emailType = emailTypeRepository.findByDomain(domain);
                if (emailType != null) {
                    emailEntity.setEmail(newEmail);
                    emailEntity.setEmailTypeEntity(emailType);
                } else {
                    emailType = new EmailType(domain);
                    emailTypeRepository.save(emailType);
                    emailEntity.setEmail(newEmail);
                    emailEntity.setEmailTypeEntity(emailType);
                }

            }
            return true;

        }
        return false;
    }
    @Transactional
    public List<EmailDTO> getEmails(String text){
        List<EmailDTO> strings = new ArrayList<>();
        List<Email> emailEntities;
        if(text.equals("all")){
           emailEntities = emailRepository.findAll();
        }
        else {
            EmailType emailType = emailTypeRepository.findByDomain(text);
            if (emailType != null) {
                emailEntities = emailType.getEmails();
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

            Pattern emailPattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
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
                    EmailType findEntity = emailTypeRepository.findByDomain(domain);
                    if(emailRepository.findByName(email) != null) continue;
                    if(findEntity == null){
                        EmailType emailType = new EmailType(domain);

                        emailTypeRepository.save(emailType);

                        Email emailEntity = new Email(email, emailType);
                        emailRepository.save(emailEntity);

                    }
                    else {
                        Email emailEntity = new Email(email, findEntity);
                        emailRepository.save(emailEntity);
                    }

                }
            }
        return text;
    }
}


