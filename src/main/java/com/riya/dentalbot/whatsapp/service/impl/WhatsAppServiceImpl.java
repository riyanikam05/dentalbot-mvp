package com.riya.dentalbot.whatsapp.service.impl;

import com.riya.dentalbot.util.PhoneNumberUtil;
import com.riya.dentalbot.whatsapp.service.WhatsAppService;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WhatsAppServiceImpl implements WhatsAppService {

    @Value("${twilio.whatsapp-from}")
    private String whatsAppFrom;

    @Override
    public void sendMessage(String toPatientPhone, String body) {

        String to = PhoneNumberUtil.toWhatsAppFormat(toPatientPhone);

        Message message = Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(whatsAppFrom),
                body)
                .create();

        log.info("WhatsApp message sent, sid={}", message.getSid());
    }
}