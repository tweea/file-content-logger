/*
 * 版权所有 2017 Tweea。
 * 保留所有权利。
 */
package cn.tweea.filecontentlogger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.yaml.snakeyaml.Yaml;

public class DataFiles {
	private static Yaml YAML = new Yaml();

	public static void writeFileContentLog(Map<String, FileContent> fileContents, WritableResource target)
		throws IOException {
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(target.getOutputStream(), StandardCharsets.UTF_8)))) {
			for (FileContent fileContent : fileContents.values()) {
				writer.println('-');
				writer.print("  hash: '");
				writer.print(fileContent.getHash());
				writer.println('\'');
				writer.print("  length: ");
				writer.println(fileContent.getLength());
				writer.print("  name: '");
				writer.print(fileContent.getName());
				writer.println('\'');
			}
			writer.flush();
		}
	}

	public static Map<String, FileContent> readFileContentLog(Resource source)
		throws IOException {
		Map<String, FileContent> fileContents = new TreeMap<>();

		List<Map<String, Object>> fileContentMapList = YAML.load(source.getInputStream());
		for (Map<String, Object> fileContentMap : fileContentMapList) {
			FileContent fileContent = new FileContent();
			fileContent.setHash((String) fileContentMap.get("hash"));
			fileContent.setLength(((Number) fileContentMap.get("length")).longValue());
			fileContent.setName((String) fileContentMap.get("name"));
			fileContents.put(fileContent.getHash(), fileContent);
		}

		return fileContents;
	}

	public static void writeDuplicateFiles(Map<String, List<String>> duplicateFiles, WritableResource target)
		throws IOException {
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(target.getOutputStream(), StandardCharsets.UTF_8)))) {
			for (Map.Entry<String, List<String>> duplicateFileEntry : duplicateFiles.entrySet()) {
				writer.println(duplicateFileEntry.getKey());
				for (String name : duplicateFileEntry.getValue()) {
					writer.print("  ");
					writer.println(name);
				}
			}
			writer.flush();
		}
	}
}
