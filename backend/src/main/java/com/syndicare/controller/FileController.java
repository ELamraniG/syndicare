package com.syndicare.controller;

import com.syndicare.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    /**
     * Serve uploaded files (ticket photos, etc.). The path is everything after /files/.
     * Public to keep image previews simple — file names are UUID-prefixed so listing is non-trivial.
     */
    @GetMapping("/**")
    public ResponseEntity<Resource> serve(@RequestParam(value = "path", required = false) String path,
                                          org.springframework.web.context.request.WebRequest request) {
        // Extract path after /files/
        String requestUri = ((org.springframework.web.context.request.ServletWebRequest) request)
                .getRequest().getRequestURI();
        // requestUri starts with context-path /api/files/
        int idx = requestUri.indexOf("/files/");
        String relative = idx >= 0 ? requestUri.substring(idx + 7) : (path == null ? "" : path);
        Resource resource = fileStorageService.load(relative);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CACHE_CONTROL, "max-age=3600");
        return ResponseEntity.ok().headers(headers).body(resource);
    }
}
