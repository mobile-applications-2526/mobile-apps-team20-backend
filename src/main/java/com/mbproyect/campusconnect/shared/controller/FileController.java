package com.mbproyect.campusconnect.shared.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/uploads") // Base path for serving uploaded files
public class FileController {

    private final Path eventRootLocation = Paths.get("uploads/events");
    private final Path profileRootLocation = Paths.get("uploads/profiles");

    // Event images (backwards compatible): /api/uploads/{filename}
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveEventFile(@PathVariable String filename) {
        try {
            Path file = eventRootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                // Determine content type (you can improve this detection)
                String contentType = "image/jpeg";
                if (filename.endsWith(".png")) contentType = "image/png";

                System.out.println("Returning event image: " + resource.getFilename());

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Profile images: /api/uploads/profiles/{filename}
    @GetMapping("/profiles/{filename:.+}")
    public ResponseEntity<Resource> serveProfileFile(@PathVariable String filename) {
        try {
            Path file = profileRootLocation.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                String contentType = "image/jpeg";
                if (filename.endsWith(".png")) contentType = "image/png";

                System.out.println("Returning profile image: " + resource.getFilename());

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}