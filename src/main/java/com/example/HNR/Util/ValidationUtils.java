package com.example.HNR.Util;

import java.util.regex.Pattern;

public class ValidationUtils {

    // Expressions régulières
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^(\\+212|0)[5-7][0-9]{8}$"); // Format téléphone marocain

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$");

    // Validation email
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    // Validation téléphone
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    // Validation mot de passe
    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    // Validation coordonnées géographiques
    public static boolean isValidLatitude(Double latitude) {
        return latitude != null && latitude >= -90 && latitude <= 90;
    }

    public static boolean isValidLongitude(Double longitude) {
        return longitude != null && longitude >= -180 && longitude <= 180;
    }

    // Validation chaîne non vide
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    // Validation longueur chaîne
    public static boolean isValidLength(String str, int minLength, int maxLength) {
        if (str == null) return false;
        int length = str.trim().length();
        return length >= minLength && length <= maxLength;
    }

    // Validation numéro PV (format: PV-YYYY-NNNN)
    public static boolean isValidPVNumber(String numero) {
        return numero != null &&
                Pattern.matches("^PV-\\d{4}-\\d{4}$", numero);
    }

    // Validation surface (positive et raisonnable)
    public static boolean isValidSurface(Double surface) {
        return surface != null && surface > 0 && surface <= 10000; // Max 10000 m²
    }

    // Validation préfecture/commune (caractères alphanumériques et espaces)
    public static boolean isValidLocationName(String name) {
        return name != null &&
                Pattern.matches("^[a-zA-ZÀ-ÿ\\s-]{2,50}$", name);
    }

    // Validation URL
    public static boolean isValidURL(String url) {
        try {
            new java.net.URL(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Messages d'erreur standardisés
    public static class ErrorMessages {
        public static final String INVALID_EMAIL = "Format d'email invalide";
        public static final String INVALID_PASSWORD = "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule et un chiffre";
        public static final String INVALID_PHONE = "Format de téléphone marocain invalide";
        public static final String REQUIRED_FIELD = "Ce champ est obligatoire";
        public static final String INVALID_COORDINATES = "Coordonnées géographiques invalides";
        public static final String INVALID_PV_NUMBER = "Format de numéro PV invalide (PV-YYYY-NNNN)";
        public static final String INVALID_SURFACE = "Surface doit être positive et inférieure à 10000 m²";
        public static final String INVALID_LOCATION = "Nom de localisation invalide";
    }
}