package com.riya.dentalbot.util;

public final class PhoneNumberUtil {

    private static final String DEFAULT_COUNTRY_CODE = "91"; // India

    private PhoneNumberUtil() {
    }

    /**
     * Converts a raw 10-digit Indian number into Twilio's "whatsapp:+91XXXXXXXXXX"
     * format.
     */
    public static String toWhatsAppFormat(String rawPhone) {

        String digitsOnly = rawPhone.replaceAll("[^0-9]", "");

        if (digitsOnly.length() == 10) {
            digitsOnly = DEFAULT_COUNTRY_CODE + digitsOnly;
        }

        return "whatsapp:+" + digitsOnly;
    }

    /**
     * Converts Twilio's "whatsapp:+91XXXXXXXXXX" back into a bare 10-digit number
     * for DB lookups.
     */
    public static String fromWhatsAppFormat(String whatsAppAddress) {

        String digitsOnly = whatsAppAddress
                .replace("whatsapp:", "")
                .replace("+", "")
                .replaceAll("[^0-9]", "");

        if (digitsOnly.startsWith(DEFAULT_COUNTRY_CODE) && digitsOnly.length() == 12) {
            digitsOnly = digitsOnly.substring(2);
        }

        return digitsOnly;
    }
}