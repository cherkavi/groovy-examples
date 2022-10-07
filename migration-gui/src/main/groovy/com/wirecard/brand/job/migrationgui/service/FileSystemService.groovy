package com.wirecard.brand.job.migrationgui.service

import org.apache.tomcat.util.http.fileupload.IOUtils
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path

@Component
class FileSystemService {

    static Path TEMP_FOLDER
    static{
        try{
            TEMP_FOLDER = Files.createTempDirectory("FileService", null)
        }catch(IOException ex){
            throw new RuntimeException("can't create temp folder ", ex)
        }
    }
    private static final String FILE_PRODUCT = "product.data"
    private static final String FILE_FULFILL = "product.data"

    void clearFiles(String id) {
        removeAllFilesIntoFolder(id)
    }

    void saveFileProduct(String id, MultipartFile multipartFile) {
        saveFileIntoFolder(resolveFile(id, FILE_PRODUCT), multipartFile)
    }

    void saveFulfillment(String id, MultipartFile multipartFile) {
        saveFileIntoFolder(resolveFile(id, FILE_FULFILL), multipartFile)
    }

    void saveFileIntoFolder(Path file, MultipartFile multipartFile) {
        if(Files.exists(file))
            Files.delete(file)
        file.toFile().withWriter { out ->
            IOUtils.copy(multipartFile.getInputStream(), out)
        }
    }

    private Path resolveFile(String id, String fileName) {
        TEMP_FOLDER.resolve(id).resolve(fileName)
    }

    void removeAllFilesIntoFolder(String subFolder) {
        Files.list(TEMP_FOLDER.resolve(subFolder)).each {Files.delete()}
    }

    def getGetFileData(String id) {
        Files.exists(resolveFile(id, FILE_PRODUCT))
    }

    def getFileFulfill(String id) {
        Files.exists(resolveFile(id, FILE_FULFILL))
    }
}
