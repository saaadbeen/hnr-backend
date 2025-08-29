package com.example.HNR.Util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileUtils {

    private static final String UPLOAD_DIR = "uploads/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    // Valider le type de fichier
    public static boolean isValidFileType(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        return contentType != null && (
                contentType.startsWith("image/") ||
                        contentType.equals("application/pdf") ||
                        contentType.equals("application/msword") ||
                        contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
        );
    }

    // Valider la taille du fichier
    public static boolean isValidFileSize(MultipartFile file) {
        return file != null && file.getSize() <= MAX_FILE_SIZE;
    }

    // Générer nom unique pour le fichier
    public static String generateUniqueFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        return UUID.randomUUID().toString() + "." + extension;
    }

    // Obtenir l'extension du fichier
    public static String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    // Sauvegarder fichier
    public static String saveFile(MultipartFile file, String subDirectory) throws IOException {
        if (!isValidFileType(file) || !isValidFileSize(file)) {
            throw new IllegalArgumentException("Type de fichier non valide ou taille trop importante");
        }

        // Créer le répertoire s'il n'existe pas
        Path uploadPath = Paths.get(UPLOAD_DIR + subDirectory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Générer nom unique
        String uniqueFileName = generateUniqueFileName(file.getOriginalFilename());
        Path filePath = uploadPath.resolve(uniqueFileName);

        // Sauvegarder le fichier
        Files.copy(file.getInputStream(), filePath);

        return subDirectory + "/" + uniqueFileName;
    }

    // Supprimer fichier
    public static boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(UPLOAD_DIR + filePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            return false;
        }
    }

    // Vérifier si le fichier existe
    public static boolean fileExists(String filePath) {
        Path path = Paths.get(UPLOAD_DIR + filePath);
        return Files.exists(path);
    }

    // Obtenir la taille formatée
    public static String getFormattedFileSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024.0));
        return String.format("%.1f GB", size / (1024.0 * 1024.0 * 1024.0));
    }

    // Valider si c'est une image
    public static boolean isImageFile(String contentType) {
        return contentType != null && contentType.startsWith("image/");
    }

    // Valider si c'est un PDF
    public static boolean isPDFFile(String contentType) {
        return "application/pdf".equals(contentType);
    }
}