package com.dev.java_flix.util;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileHandlerUtil {

    private FileHandlerUtil(){}

    public static String extractFileExtension(String originalFileName){

        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")){
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        return fileExtension;
    }

    public static Path findFileByUuid(Path directory, String uuid) throws Exception{

        return Files.list(directory)
                    .filter(path -> path.getFileName().toString().startsWith(uuid))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("File not found for UUID: "+uuid));
    }

    public static String detectVideoContentType(String fileName){
        if (fileName == null) return "video/mp4";

        if (fileName.endsWith(".webm")) return "video/webm";
        if (fileName.endsWith(".ogg")) return "video/ogg";
        if (fileName.endsWith(".mkv")) return "video/x-matroska";
        if (fileName.endsWith(".avi")) return "video/x-msvideo";
        if (fileName.endsWith(".mov")) return "video/quicktime";
        if (fileName.endsWith(".flv")) return "video/x-flv";
        if (fileName.endsWith(".wmv")) return "video/x-ms-wmv";
        if (fileName.endsWith(".m4v")) return "video/x-m4v";
        if (fileName.endsWith(".3gp")) return "video/3gpp";
        if (fileName.endsWith(".mpg") || fileName.endsWith(".mpeg")) return "video/mpeg";
        
        return "video/mp4";
    }

    public static String detectImageContentType(String fileName){
        if (fileName == null) return "image/jpeg";

        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".gif")) return "image/gif";
        if (fileName.endsWith(".webp")) return "image/webp";

        return "image/jpeg";
    }

    /** "Fatia" o vídeo.
     * Analisa o cabeçalho 'Range' da requisição HTTP para suportar streaming parcial.
     * O navegador envia algo como "bytes=0-1024" para pedir apenas um pedaço do vídeo.
     * * @param rangeHeader O valor do header 'Range' (ex: "bytes=5000-")
     * @param fileLength O tamanho total do arquivo no disco em bytes.
     * @return Um array de long contendo [início do range, fim do range].
     */
    public static long[] parseRangeHeader(String rangeHeader, long fileLenght){

        String[] ranges = rangeHeader.replace("bytes=", "").split("-");
        
        long rangeStart = Long.parseLong(ranges[0]);
        long rangeEnd = ranges.length > 1 && !ranges[1].isEmpty() 
                ? Math.min(Long.parseLong(ranges[1]), fileLenght - 1) 
                : fileLenght - 1;

        return new long[] {rangeStart, rangeEnd};
    }
}
