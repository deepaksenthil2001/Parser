package com.smartcode.analyzer.util;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtil {

    // Extract ZIP → Temp Folder → Return list of Java and PHP Files
    public static List<File> extractZipToTemp(InputStream zipStream) throws Exception {

        File tempDir = Files.createTempDirectory("smartcode_zip").toFile();
        List<File> extractedFiles = new ArrayList<>();

        ZipInputStream zis = new ZipInputStream(zipStream);
        ZipEntry entry;

        while ((entry = zis.getNextEntry()) != null) {

            if (!entry.getName().endsWith(".java") && !entry.getName().endsWith(".php")) continue;

            File outFile = new File(tempDir, entry.getName());
            outFile.getParentFile().mkdirs();

            FileOutputStream fos = new FileOutputStream(outFile);

            byte[] buffer = new byte[1024];
            int len;

            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }

            fos.close();
            extractedFiles.add(outFile);
        }

        zis.close();
        return extractedFiles;
    }

    // Convert File → MultipartFile (Spring can read)
    public static MultipartFile convertFileToMultipart(File file) throws Exception {

        FileInputStream fis = new FileInputStream(file);

        MultipartFile multipart = new MockMultipartFile(
                file.getName(),
                file.getName(),
                "text/plain",
                fis
        );

        fis.close();
        return multipart;
    }

    // Delete folder recursively
    public static void deleteFolder(File folder) {

        if (folder == null || !folder.exists()) return;

        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) deleteFolder(f);
                else f.delete();
            }
        }
        folder.delete();
    }
}
