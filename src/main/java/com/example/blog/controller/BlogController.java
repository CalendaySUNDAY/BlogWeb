package com.example.blog.controller;

import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

}
