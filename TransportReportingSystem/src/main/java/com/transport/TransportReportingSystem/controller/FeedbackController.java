package com.transport.TransportReportingSystem.controller;

import com.transport.TransportReportingSystem.dto.FeedbackDTO;
import com.transport.TransportReportingSystem.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {
    
    private final FeedbackService feedbackService;
    
   
    @PostMapping
    public ResponseEntity<FeedbackDTO> createFeedback(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) String imageUrl,
            @RequestParam String status,
            @RequestParam String issueCategory,
            @RequestParam Long userId,
            @RequestParam Long routeId,
            @RequestParam(required = false) Long assignedUserId,
            @RequestParam Long incidentLocationId) {
        FeedbackDTO feedbackDTO = new FeedbackDTO();
        feedbackDTO.setTitle(title);
        feedbackDTO.setDescription(description);
        feedbackDTO.setImageUrl(imageUrl);
        feedbackDTO.setStatus(status);
        feedbackDTO.setIssueCategory(issueCategory);
        feedbackDTO.setUserId(userId);
        feedbackDTO.setRouteId(routeId);
        feedbackDTO.setAssignedUserId(assignedUserId);
        feedbackDTO.setIncidentLocationId(incidentLocationId);
        FeedbackDTO createdFeedback = feedbackService.createFeedback(feedbackDTO);
        return new ResponseEntity<>(createdFeedback, HttpStatus.CREATED);
    }
    
  
    @GetMapping("/{id}")
    public ResponseEntity<FeedbackDTO> getFeedbackById(@PathVariable Long id) {
        FeedbackDTO feedback = feedbackService.getFeedbackById(id);
        return ResponseEntity.ok(feedback);
    }
    
    
    @GetMapping
    public ResponseEntity<List<FeedbackDTO>> getAllFeedbacks() {
        List<FeedbackDTO> feedbacks = feedbackService.getAllFeedbacks();
        return ResponseEntity.ok(feedbacks);
    }

    // Role-based 
  
    @GetMapping("/mine")
    public ResponseEntity<List<FeedbackDTO>> getMyFeedback(@RequestParam Long userId) {
        return ResponseEntity.ok(feedbackService.getFeedbacksForUser(userId));
    }

  
    @GetMapping("/company")
    public ResponseEntity<List<FeedbackDTO>> getCompanyFeedback(@RequestParam Long adminUserId) {
        return ResponseEntity.ok(feedbackService.getFeedbacksForCompanyAdmin(adminUserId));
    }

    
    @GetMapping("/all")
    public ResponseEntity<List<FeedbackDTO>> getAllFeedbackForSuperAdmin() {
        return ResponseEntity.ok(feedbackService.getAllFeedbacks());
    }
    
    
    @GetMapping("/paginated")
    public ResponseEntity<Page<FeedbackDTO>> getAllFeedbacksPaginated(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "feedbackId", direction = Sort.Direction.DESC) Pageable pageable,
            java.security.Principal principal) {
        Page<FeedbackDTO> feedbacks = feedbackService.getAllFeedbacksPaginated(search, pageable, principal);
        return ResponseEntity.ok(feedbacks);
    }
    
    
    @PutMapping("/{id}")
    public ResponseEntity<FeedbackDTO> updateFeedback(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false) String imageUrl,
            @RequestParam String status,
            @RequestParam String issueCategory,
            @RequestParam Long userId,
            @RequestParam Long routeId,
            @RequestParam(required = false) Long assignedUserId,
            @RequestParam Long incidentLocationId) {
        FeedbackDTO feedbackDTO = new FeedbackDTO();
        feedbackDTO.setTitle(title);
        feedbackDTO.setDescription(description);
        feedbackDTO.setImageUrl(imageUrl);
        feedbackDTO.setStatus(status);
        feedbackDTO.setIssueCategory(issueCategory);
        feedbackDTO.setUserId(userId);
        feedbackDTO.setRouteId(routeId);
        feedbackDTO.setAssignedUserId(assignedUserId);
        feedbackDTO.setIncidentLocationId(incidentLocationId);
        FeedbackDTO updatedFeedback = feedbackService.updateFeedback(id, feedbackDTO);
        return ResponseEntity.ok(updatedFeedback);
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<FeedbackDTO> resolveFeedback(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        String response = body.get("response");
        return ResponseEntity.ok(feedbackService.resolveFeedback(id, response));
    }
    
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }
    
    
    @GetMapping("/by-route/{routeId}")
    public ResponseEntity<List<FeedbackDTO>> getFeedbacksByRoute(@PathVariable Long routeId) {
        List<FeedbackDTO> feedbacks = feedbackService.getFeedbacksByRoute(routeId);
        return ResponseEntity.ok(feedbacks);
    }
    
    
    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<FeedbackDTO>> getFeedbacksByStatus(@PathVariable String status) {
        List<FeedbackDTO> feedbacks = feedbackService.getFeedbacksByStatus(status);
        return ResponseEntity.ok(feedbacks);
    }
    @PutMapping("/{id}/status")
    public ResponseEntity<FeedbackDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String response) {
        return ResponseEntity.ok(feedbackService.updateStatus(id, status, response));
    }
}
