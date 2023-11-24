package com.example.blog.controller;

import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {

    private final ResourceLoader resourceLoader;

    // Constructor with ResourceLoader injected
    public BlogController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }


    @GetMapping("/{id}")
    public ResponseEntity<String> getBlogPost(@PathVariable String id) {
        try {
            String markdownPath = "classpath:/blogs/" + id + ".md";
            // 直接读取文件内容
            String markdown = new String(Files.readAllBytes(Paths.get(resourceLoader.getResource(markdownPath).getURI())));
            return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(markdown);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error loading blog post");
        }
    }

    @GetMapping("/blog_general")
    public ResponseEntity<String> getBlogNum() {
        File folder = new File("src/main/resources/blogs/");
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            // 使用Lambda表达式创建一个Comparator来比较文件名
            Arrays.sort(listOfFiles, (f1, f2) -> f1.getName().compareTo(f2.getName()));
        }
            List<Map<String, String>> filesData = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            if (listOfFiles != null) {
                for (File file : listOfFiles) {
                    if (file.isFile() && file.getName().endsWith(".md")){
                        Map<String, String> fileData = new HashMap<>();
                        fileData.put("name", file.getName());
                        String content = readFileContent(file);
                        fileData.put("head", content.length() > 20 ? content.substring(0, 20) : content);
                        filesData.add(fileData);
                    }
                }
            }
            // 使用ObjectMapper将filesData转换为JSON字符串
            String json = objectMapper.writeValueAsString(filesData);
            return ResponseEntity.ok(json);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error loading blog posts");
        }
    }

    @PostMapping("/uploadPicture")
    public ResponseEntity<String> getPicture(@RequestParam("file") MultipartFile file) {
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }

            // 获取文件MIME类型并进行检查
            String mimeType = file.getContentType();
            if(mimeType == null || (!mimeType.equals("image/jpeg") && !mimeType.equals("image/png"))) {
                return ResponseEntity.badRequest().body("Invalid image file type");
            }

            // 生成文件名，这里使用了UUID来确保文件名的唯一性
            String filename = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

            // 设置文件存储路径
            Path path = Paths.get("blogs/pic_cache/" + filename);

            // 确保目录存在
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }

            // 保存文件到服务器
            Files.copy(file.getInputStream(), path);

            // 返回pic_id（在这个场景中就是文件名）
            return ResponseEntity.ok(filename);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Could not store the file");
        }
    }

    private String readFileContent(File file) {
        // 使用Java 8的Stream API来读取文件内容
        try (Stream<String> stream = Files.lines(Paths.get(file.getPath()))) {
            return stream.collect(Collectors.joining("\n"));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}


