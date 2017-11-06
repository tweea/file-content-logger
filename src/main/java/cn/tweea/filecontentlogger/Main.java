/*
 * 版权所有 2017 Tweea。
 * 保留所有权利。
 */
package cn.tweea.filecontentlogger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;

public final class Main {
	public static void main(String[] args)
		throws IOException {
		if (args.length < 3) {
			throw new IllegalArgumentException("缺少参数");
		}

		Resource inputResource;
		if ("-".equals(args[0])) {
			inputResource = null;
		} else {
			inputResource = new FileSystemResource(args[0]);
			if (!inputResource.exists()) {
				throw new IllegalArgumentException("输入文件不存在");
			}
		}
		File fileBase = new File(args[1]);
		if (!fileBase.exists()) {
			throw new IllegalArgumentException("数据目录/文件不存在");
		}
		WritableResource outputResource = new FileSystemResource(args[2]);
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
		WritableResource duplicateResource = new FileSystemResource(args[2] + ".duplicate");

		Map<String, FileContent> fileContents;
		if (inputResource == null) {
			fileContents = new TreeMap<>();
		} else {
			fileContents = DataFiles.readFileContentLog(inputResource);
		}
		Map<String, List<String>> duplicateFiles = new TreeMap<>();
		loadFileContent(fileBase, fileContents, duplicateFiles);
		if (!fileContents.isEmpty()) {
			DataFiles.writeFileContentLog(fileContents, outputResource);
		}
		if (!duplicateFiles.isEmpty()) {
			DataFiles.writeDuplicateFiles(duplicateFiles, duplicateResource);
		}
	}

	private static void loadFileContent(File fileBase, Map<String, FileContent> fileContents, Map<String, List<String>> duplicateFiles)
		throws IOException {
		if (fileBase.isFile()) {
			File file = fileBase.getCanonicalFile();
			System.out.println(file);
		} else if (fileBase.isDirectory()) {
			File[] files = fileBase.listFiles();
			for (File file : files) {
				loadFileContent(file, fileContents, duplicateFiles);
			}
		}
	}
}
