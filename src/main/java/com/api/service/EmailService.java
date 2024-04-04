package com.api.service;

import com.api.component.Cache;
import com.api.component.CustomLogger;
import com.api.dao.EmailRepository;
import com.api.dao.EmailTypeRepository;
import com.api.dto.EmailDTO;
import com.api.entity.Email;
import com.api.entity.EmailType;
import com.api.exceptions.ServiceException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EmailService {
    private final CustomLogger customLogger;
    private final EmailRepository emailRepository;
    private final Cache cache;
    private static final String EMAIL_REGEX = "\\b[a-z0-9._-]+@[a-z0-9.-]+\\.[a-z]{2,}\\b";
    private static String adresRegex = "[a-z0-9.-]+\\.[a-z]{2,}\\b";
    private final EmailTypeRepository emailTypeRepository;

    public EmailService(CustomLogger customLogger, EmailRepository emailRepository, Cache cache, EmailTypeRepository emailTypeRepository) {
        this.customLogger = customLogger;
        this.emailRepository = emailRepository;
        this.cache = cache;
        this.emailTypeRepository = emailTypeRepository;
    }

    @Transactional
    public void addEmails(List<String> list) {
            list.stream()
                    .forEach(this::putEmail);
    }

    @Transactional
    protected void putEmail(String email) {
        // Проверка, что email соответствует регулярному выражению
        Pattern emailPattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher emailMatcher = emailPattern.matcher(email);
        if (!emailMatcher.find()) {
            // Если email не соответствует регулярному выражению, бросаем ServiceException
            throw new ServiceException();
        } else {
            // Ищем в базе данных email с таким же именем
            Email existingEmail = emailRepository.findByName(email);
            if (existingEmail == null) {
                // Создаем новый объект Email и сохраняем его в репозитории
                Email newEmail = new Email();
                newEmail.setEmail(email);
                EmailType emailType = createOrRetrieveEmailType(email);
                newEmail.setEmailTypeEntity(emailType);
                emailRepository.save(newEmail);
            } else {
                // Если email уже существует в базе данных, логируем информацию и не делаем ничего
                customLogger.logInfo("Email " + email + " already exists in the database");
            }
        }
    }

    @Transactional
    protected EmailType createOrRetrieveEmailType(String email) {
        // Получаем домен из email
        String domain = email.substring(email.indexOf('@') + 1);
        // Ищем EmailType по домену в кэше
        EmailType emailType = (EmailType) cache.get(domain);
        if (emailType == null) {
            // Если EmailType не найден в кэше, ищем его в базе данных
            emailType = emailTypeRepository.findByDomain(domain);
            if (emailType == null) {
                // Если EmailType не найден в базе данных, создаем новый и сохраняем его
                emailType = new EmailType();
                emailType.setDomain(domain);
                emailTypeRepository.save(emailType);
            }
            // Помещаем EmailType в кэш
            cache.put(domain, emailType);
        }
        return emailType;
    }

    @Transactional
    public void deleteEmail(Long id) {
        Optional<Email> optionalEmail = emailRepository.findById(id);

        if (optionalEmail.isPresent()) {
            Email email = optionalEmail.get();

            // Удаление электронной почты из кэша
            cache.remove(email.getEmail());
            customLogger.logCacheRemove(optionalEmail.get().getEmail());
            // Удаление электронной почты из репозитория
            emailRepository.delete(email);
        } else {
            customLogger.logError("Email is not found");
            throw new ServiceException();
        }
    }

    @Transactional
    public void deleteEmail(String email) {
        Optional<Email> optionalEmail;
        if (cache.get(email) != null) {
            optionalEmail = (Optional<Email>) cache.get(email);
            customLogger.logInfo("Value from cache");
        } else {
            optionalEmail = Optional.ofNullable(emailRepository.findByName(email));
        }
        if (optionalEmail.isPresent()) {
            Email emailEntity = optionalEmail.get();

            // Удаляем электронную почту из кэша только если она была найдена
            cache.remove(emailEntity.getEmail());
            customLogger.logCacheRemove(emailEntity.getEmail());
            // Удаляем электронную почту из репозитория
            emailRepository.delete(emailEntity);

        } else {
            customLogger.logError("Email is not found");
            throw new ServiceException();
        }
    }

    @Transactional
    public void updateEmail(Long id, String newEmail) {
        Pattern emailPattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher emailMatcher = emailPattern.matcher(newEmail);
        if (!emailMatcher.find()) {
            customLogger.logError("Is not domain");
            throw new ServiceException();
        }

        Optional<Email> emailEntity = emailRepository.findById(id);
        if (emailEntity.isPresent()) {
            Pattern adresPattern = Pattern.compile(adresRegex, Pattern.CASE_INSENSITIVE);
            Matcher adresMatcher;
            adresMatcher = adresPattern.matcher(newEmail);
            if (adresMatcher.find()) {
                String domain = adresMatcher.group(0);
                EmailType emailType = emailTypeRepository.findByDomain(domain);
                if (emailType != null) {
                    cache.remove(emailEntity.get().getEmail());
                    cache.put(newEmail, new Email(newEmail));
                    emailEntity.get().setEmail(newEmail);
                    emailEntity.get().setEmailTypeEntity(emailType);
                } else {
                    emailType = new EmailType(domain);
                    emailTypeRepository.save(emailType);
                    emailEntity.get().setEmail(newEmail);
                    emailEntity.get().setEmailTypeEntity(emailType);
                    cache.put(emailType.getDomain(), emailType);
                }
            }
        } else {
            throw new ServiceException();
        }
    }

    @Transactional
    public void updateEmail(String email, String newEmail) {
        Pattern emailPattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher emailMatcher = emailPattern.matcher(newEmail);
        if (!emailMatcher.find()) {
            customLogger.logError("Is not domain");
            throw new ServiceException();
        }
        Email emailEntity = emailRepository.findByName(email);
        if (emailEntity != null) {
            Pattern adresPattern = Pattern.compile(adresRegex, Pattern.CASE_INSENSITIVE);
            Matcher adresMatcher;
            adresMatcher = adresPattern.matcher(newEmail);
            if (adresMatcher.find()) {
                String domain = adresMatcher.group(0);
                EmailType emailType = emailTypeRepository.findByDomain(domain);
                if (emailType != null) {
                    cache.remove(emailEntity.getEmail());
                    cache.put(newEmail, new Email(newEmail));
                    emailEntity.setEmail(newEmail);
                    emailEntity.setEmailTypeEntity(emailType);
                    customLogger.logCachePut(emailType.getDomain());

                } else {
                    emailType = new EmailType(domain);
                    emailTypeRepository.save(emailType);
                    emailEntity.setEmail(newEmail);
                    emailEntity.setEmailTypeEntity(emailType);
                    cache.put(emailType.getDomain(), emailType);
                    cache.put(emailEntity.getEmail(), emailEntity);
                    customLogger.logCachePut(emailType.getDomain());
                    customLogger.logCachePut(emailEntity.getEmail());
                }
            }
        } else {
            throw new ServiceException();
        }
    }

    @Transactional
    public List<EmailDTO> getEmails(String text) {
        List<EmailDTO> strings = new ArrayList<>();
        List<Email> emailEntities;
        if (text.equals("all")) {

            emailEntities = emailRepository.findAll();
            for (int i = 0; i < emailEntities.size(); i++) {
                customLogger.logCachePut(emailEntities.get(i).getEmail());
                cache.put(emailEntities.get(i).getEmail(), emailEntities.get(i));
                strings.add(new EmailDTO(emailEntities.get(i).getId().toString() + ". " + emailEntities.get(i).getEmail()));
            }
        } else {
            customLogger.logError("Wrong arguments");
            throw new ServiceException();
        }
        return strings;
    }

    @Transactional
    public List<EmailDTO> getEmailsByEmailType(String text) {
        List<EmailDTO> strings = new ArrayList<>();
        List<Email> emailEntities;
        emailEntities = emailRepository.findByEmailTypeDomain(text);
        for (int i = 0; i < emailEntities.size(); i++) {
            cache.put(emailEntities.get(i).getEmail(), emailEntities.get(i));
            customLogger.logCachePut(emailEntities.get(i).getEmail());
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

                String domain = adresMatcher.group();
                EmailType findEntity = emailTypeRepository.findByDomain(domain);
                if (emailRepository.findByName(email) != null) {
                    continue;
                }
                if (findEntity == null) {
                    EmailType emailType = new EmailType(domain);

                    emailTypeRepository.save(emailType);

                    Email emailEntity = new Email(email, emailType);
                    emailRepository.save(emailEntity);
                    cache.put(emailEntity.getEmail(), emailEntity);
                    cache.put(emailType.getDomain(), emailType);
                    customLogger.logCachePut(emailEntity.getEmail());
                    customLogger.logCachePut(emailType.getDomain());

                } else {
                    Email emailEntity = new Email(email, findEntity);
                    emailRepository.save(emailEntity);
                    cache.put(emailEntity.getEmail(), emailEntity);
                    customLogger.logCachePut(emailEntity.getEmail());
                }

            }
        }
        return text;
    }
}