package com.example.template.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public final class UtilFile extends org.apache.commons.io.FileUtils {
    public static String getExtension(String filepath) {
        if (filepath.contains(".")) {
            return filepath.substring(filepath.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    public static File compressFiles(String filepathDestination, List<File> filesSource) {
        return compressFiles(new File(filepathDestination), filesSource);
    }

    public static File compressFiles(File fileDestination, List<File> filesSource) {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(
                Files.newOutputStream(UtilFile.getFile(fileDestination).toPath()))) {
            for (File file : filesSource) {
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zipOutputStream.putNextEntry(zipEntry);
                IOUtils.copy(Files.newInputStream(file.toPath()), zipOutputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return getFile(fileDestination);
    }

    public static File compressFile(String filepathDestination, File fileSource) {
        return compressFile(new File(filepathDestination), fileSource);
    }

    public static File compressFile(File fileDestination, File fileSource) {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(
                Files.newOutputStream(fileDestination.toPath()))) {
            ZipEntry zipEntry = new ZipEntry(fileSource.getName());
            zipOutputStream.putNextEntry(zipEntry);
            IOUtils.copy(Files.newInputStream(fileSource.toPath()), zipOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return getFile(fileDestination);
    }
}
