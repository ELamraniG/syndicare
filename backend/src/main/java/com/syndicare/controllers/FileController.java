package com.syndicare.controllers;

import com.syndicare.services.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @GetMapping("/**")
    public ResponseEntity<Resource> filesGetController(@RequestParam(value = "path", required = false) String path,
            org.springframework.web.context.request.WebRequest request) {

        String requestUri = ((org.springframework.web.context.request.ServletWebRequest) request)
                .getRequest().getRequestURI();

        int index = requestUri.indexOf("/files/");
        String relative = index >= 0 ? requestUri.substring(index + 7) : (path == null ? "" : path);
        Resource resource = fileStorageService.load(relative);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CACHE_CONTROL, "max-age=3600");
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(resource);
    }
}
