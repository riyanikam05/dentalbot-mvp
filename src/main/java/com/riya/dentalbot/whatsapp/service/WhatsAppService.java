package com.riya.dentalbot.whatsapp.service;

public interface WhatsAppService {

    void sendMessage(String toPatientPhone, String body);

}