package com.transport.TransportReportingSystem.controller;

import com.transport.TransportReportingSystem.dto.SearchResultDTO;
import com.transport.TransportReportingSystem.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<List<SearchResultDTO>> search(@RequestParam String query, java.security.Principal principal) {
        return ResponseEntity.ok(searchService.search(query, principal));
    }
}
