/*
 * 版权所有 2017 Tweea。
 * 保留所有权利。
 */
package cn.tweea.filecontentlogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;

import com.google.common.base.MoreObjects;

public final class Main {
    public static void main(String[] args)
        throws IOException {
        if (args.length < 3) {
            throw new IllegalArgumentException("缺少参数");
        }

        String inputResourceName = args[0];
        String fileBaseName = args[1];
        String outputResourceName = args[2];
        boolean isDeleteDuplicate;
        if (args.length >= 4) {
            isDeleteDuplicate = BooleanUtils.toBoolean(args[3]);
        } else {
            isDeleteDuplicate = false;
        }

        Resource inputResource;
        if ("-".equals(inputResourceName)) {
            inputResource = null;
        } else {
            inputResource = new FileSystemResource(inputResourceName);
            if (!inputResource.exists()) {
                throw new IllegalArgumentException("输入文件不存在");
            }
        }
        File fileBase = new File(fileBaseName);
        if (!fileBase.exists()) {
            throw new IllegalArgumentException("数据目录/文件不存在");
        }
        WritableResource outputResource = new FileSystemResource(outputResourceName);
        if (outputResource.exists()) {
            if (outputResource.equals(inputResource)) {
                throw new IllegalArgumentException("输入文件与输出文件不能相同");
            }

            System.out.print("输出文件已存在，是否覆盖？");
            char answer = (char) System.in.read();
            if (answer != 'Y' && answer != 'y') {
                return;
            }
        }
        WritableResource duplicateResource = new FileSystemResource(outputResourceName + ".duplicate");

        Map<String, FileContent> fileContents;
        if (inputResource == null) {
            fileContents = new TreeMap<>();
        } else {
            fileContents = DataFiles.readFileContentLog(inputResource);
        }
        Map<String, List<String>> duplicateFiles = new TreeMap<>();
        loadFileContent(fileBase, fileContents, duplicateFiles, isDeleteDuplicate);
        if (!fileContents.isEmpty()) {
            DataFiles.writeFileContentLog(fileContents, outputResource);
        }
        if (!duplicateFiles.isEmpty()) {
            DataFiles.writeDuplicateFiles(duplicateFiles, duplicateResource);
        }
    }

    private static void loadFileContent(File fileBase, Map<String, FileContent> fileContents, Map<String, List<String>> duplicateFiles,
        boolean isDeleteDuplicate)
        throws IOException {
        if (fileBase.isFile()) {
            File file = fileBase.getCanonicalFile();
            System.out.println(file);

            String hash = hashFileContent(file);
            long length = file.length();
            String name = file.getName();
            String path = file.getPath();

            FileContent fileContent = fileContents.get(hash);
            if (fileContent == null) {
                fileContent = new FileContent();
                fileContent.setHash(hash);
                fileContent.setLength(length);
                fileContent.setName(name);
                fileContent.setPath(path);
                fileContents.put(hash, fileContent);
            } else {
                List<String> duplicateFilenames = duplicateFiles.get(hash);
                if (duplicateFilenames == null) {
                    duplicateFilenames = new ArrayList<>();
                    duplicateFilenames.add(MoreObjects.firstNonNull(fileContent.getPath(), fileContent.getName()));
                    duplicateFiles.put(hash, duplicateFilenames);
                }
                fileContent.setLength(length);
                fileContent.setName(name);
                fileContent.setPath(path);
                duplicateFilenames.add(path);

                if (isDeleteDuplicate) {
                    FileUtils.forceDelete(file);
                }
            }
        } else if (fileBase.isDirectory()) {
            File[] files = fileBase.listFiles();
            for (File file : files) {
                loadFileContent(file, fileContents, duplicateFiles, isDeleteDuplicate);
            }
        }
    }

    private static String hashFileContent(File file)
        throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            return DigestUtils.sha512Hex(is);
        }
    }
}
