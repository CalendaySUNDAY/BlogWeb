package com.example.blog.controller;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.core.io.ResourceLoader;
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
    public String getBlogPost(@PathVariable String id) {
        System.out.printf("Loading blog post %s\n", id);
        try {
            // Assume the Markdown file is in 'src/main/resources/blogs'
            String markdownPath = "classpath:/blogs/" + id + ".md";
            String markdown = new String(Files.readAllBytes(Paths.get(resourceLoader.getResource(markdownPath).getURI())));

            // Parse Markdown to HTML
            Parser parser = Parser.builder().build();
            Node document = parser.parse(markdown);
            HtmlRenderer renderer = HtmlRenderer.builder().build();
            String htmlContent = renderer.render(document);

            // Your HTML template structure
            String htmlTemplate = " <section class=\"u-align-center u-clearfix u-section-1\" id=\"sec-6f66\">\n" +
                    "      <div class=\"u-clearfix u-sheet u-valign-middle-md u-valign-middle-sm u-valign-middle-xs u-sheet-1\"><!--post_details--><!--post_details_options_json--><!--{\"source\":\"\"}--><!--/post_details_options_json--><!--blog_post-->\n" +
                    "        <div class=\"u-container-style u-expanded-width u-post-details u-post-details-1\">\n" +
                    "          <div class=\"u-container-layout u-valign-middle u-container-layout-1\"><!--blog_post_image-->\n" +
                    "            <img alt=\"\" class=\"u-blog-control u-expanded-width u-image u-image-default u-image-1\" src=\"../images/68f64b9d.jpeg\"><!--/blog_post_image--><!--blog_post_header-->\n" +
                    "            <h2 class=\"u-blog-control u-text u-text-1\">Post 2 Headline</h2><!--/blog_post_header--><!--blog_post_metadata-->\n" +
                    "            <div class=\"u-blog-control u-metadata u-metadata-1\"><!--blog_post_metadata_date-->\n" +
                    "              <span class=\"u-meta-date u-meta-icon\">Nov 21, 2023</span><!--/blog_post_metadata_date--><!--blog_post_metadata_category-->\n" +
                    "              <!--/blog_post_metadata_category--><!--blog_post_metadata_comments-->\n" +
                    "              <!--/blog_post_metadata_comments-->\n" +
                    "            </div><!--/blog_post_metadata--><!--blog_post_content-->\n" +
                    "            <div class=\"u-align-justify u-blog-control u-post-content u-text u-text-2 fr-view\">\n" +
                    "  Sample small text. Lorem ipsum dolor sit amet.\n" +
                    "  </div><!--/blog_post_content-->\n" +
                    "          </div>\n" +
                    "        </div><!--/blog_post--><!--/post_details-->\n" +
                    "      </div>\n" +
                    "    </section>"; // Simplified for brevity

            // Insert the HTML content into the template
            String finalHtml = htmlTemplate.replace("Sample small text. Lorem ipsum dolor sit amet.", htmlContent);

            return finalHtml;
        } catch (IOException e) {
            e.printStackTrace();
            return "Error loading blog post";
        }
    }
}
