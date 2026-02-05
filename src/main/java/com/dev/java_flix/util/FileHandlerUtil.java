package com.dev.java_flix.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

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
     **@param rangeHeader O valor do header 'Range' (ex: "bytes=5000-")
     * @param fileLength O tamanho total do arquivo no disco em bytes.
     * @return Um array de long contendo [início do range, fim do range].
     */
    public static long[] parseRangeHeader(String rangeHeader, long fileLength){

        String[] ranges = rangeHeader.replace("bytes=", "").split("-");
        
        long rangeStart = Long.parseLong(ranges[0]);
        long rangeEnd = ranges.length > 1 && !ranges[1].isEmpty() 
                ? Math.min(Long.parseLong(ranges[1]), fileLength - 1) 
                : fileLength - 1;

        return new long[] {rangeStart, rangeEnd};
    }

    /**
     * Cria um recurso de streaming parcial (Range). 
     * permite que o usuário "salte" para diferentes partes do vídeo.
     **@param filePath Caminho do arquivo no disco.
     * @param rangeStart O byte exato onde a leitura deve começar.
     * @param rangeLength A quantidade de bytes que devem ser lidos (o "pedaço").
     * @return Um InputStreamResource que entrega apenas a fatia solicitada do arquivo.
     */
    public static Resource createRangeResource(Path filePath,  long rangeStart, long rangeLength) throws IOException {
        
        // Abre o arquivo em modo de leitura ("r") permitindo acesso aleatório (saltar para qualquer ponto)
        RandomAccessFile fileReader = new RandomAccessFile(filePath.toFile(), "r");

        // Move o "cursor" de leitura diretamente para o ponto inicial solicitado pelo navegador
        fileReader.seek(rangeStart);

        // Cria um fluxo de entrada customizado que sabe a hora de parar de ler
        InputStream partialContentStream = new InputStream() {
            private long totalBytesRead = 0;

            @Override
            public int read() throws IOException {
                // Se já lemos todo o pedaço solicitado, encerramos o fluxo
                if (totalBytesRead >= rangeLength){
                    fileReader.close();
                    return -1;
                }

                totalBytesRead++;
                return fileReader.read();
            }

            @Override
            public int read(byte[] buffer, int offset, int length) throws IOException {
                if (totalBytesRead >= rangeLength){
                    fileReader.close();
                    return -1;
                }
                // Calcula quanto ainda falta ler para não ultrapassar o limite do Range
                long remainingBytes = rangeLength - totalBytesRead;

                // Garante que não tentaremos ler mais do que o buffer suporta ou o Range permite
                int bytesToRead = (int) Math.min(length, remainingBytes);
                int bytesActuallyRead = fileReader.read(buffer, offset, bytesToRead);

                if (bytesActuallyRead > 0){
                    totalBytesRead += bytesActuallyRead;
                }

                if(totalBytesRead >= rangeLength){
                    fileReader.close();
                }

                return bytesActuallyRead;
            }

            @Override
            public void close() throws IOException {
                fileReader.close();
            } 
        };
        
        // Retorna o recurso informando o tamanho exato do conteúdo parcial
        return new InputStreamResource(partialContentStream){

            @Override
            public long contentLength(){
                return rangeLength;
            }
        };
    }

    /**
     * Cria um recurso para o arquivo completo.
     * Quando o navegador não solicita um Range específico ou para download simples.
     */
    public static Resource createFullResource(Path filePath) throws IOException {
        Resource resource = new UrlResource(filePath.toUri());

        // Garante que o arquivo existe e pode ser lido antes de enviar
        if (!resource.exists() || !resource.isReadable()) {
            throw new IOException("File not found or not readable: "+filePath);
        }

        return resource;
    }
}
