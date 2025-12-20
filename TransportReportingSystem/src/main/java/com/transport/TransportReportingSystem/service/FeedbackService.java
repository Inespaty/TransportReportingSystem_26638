package com.transport.TransportReportingSystem.service;

import com.transport.TransportReportingSystem.dto.FeedbackDTO;
import com.transport.TransportReportingSystem.entity.Feedback;
import com.transport.TransportReportingSystem.entity.User;
import com.transport.TransportReportingSystem.entity.Route;

import com.transport.TransportReportingSystem.entity.Location;
import com.transport.TransportReportingSystem.entity.Company;
import com.transport.TransportReportingSystem.enums.FeedbackStatus;
import com.transport.TransportReportingSystem.enums.IssueCategory;
import com.transport.TransportReportingSystem.repository.FeedbackRepository;
import com.transport.TransportReportingSystem.repository.UserRepository;
import com.transport.TransportReportingSystem.repository.RouteRepository;

import com.transport.TransportReportingSystem.repository.LocationRepository;
import com.transport.TransportReportingSystem.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FeedbackService {
    
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final RouteRepository routeRepository;

    private final LocationRepository locationRepository;
    private final ActivityService activityService;
    private final EmailService emailService;
    
    
    public FeedbackDTO createFeedback(FeedbackDTO feedbackDTO) {
        Feedback feedback = new Feedback();
        feedback.setTitle(feedbackDTO.getTitle());
        feedback.setDescription(feedbackDTO.getDescription());
        feedback.setImageUrl(feedbackDTO.getImageUrl());
        feedback.setStatus(FeedbackStatus.PENDING);
        feedback.setIssueCategory(IssueCategory.valueOf(feedbackDTO.getIssueCategory()));
        
        Long userId = feedbackDTO.getUserId();
        if (userId != null) {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            feedback.setUser(user);
        }
        
        Long routeId = feedbackDTO.getRouteId();
        if (routeId != null) {
            Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));
            feedback.setRoute(route);
        }
        
        Long assignedUserId = feedbackDTO.getAssignedUserId();
        if (assignedUserId != null) {
            User assignedUser = userRepository.findById(assignedUserId)
                .orElseThrow(() -> new RuntimeException("Assigned User not found"));
            feedback.setAssignedUser(assignedUser);
        }
        
        Long incidentLocationId = feedbackDTO.getIncidentLocationId();
        if (incidentLocationId != null) {
            Location location = locationRepository.findById(incidentLocationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));
            feedback.setIncidentLocation(location);
        }
        
        Feedback savedFeedback = feedbackRepository.save(feedback);
        activityService.logInfo("Feedback Received", "A new report regarding '" + savedFeedback.getTitle() + "' has been submitted.", savedFeedback.getUser());
        
        // Notify user via email
        if (savedFeedback.getUser() != null && savedFeedback.getUser().getEmail() != null) {
            String subject = "Feedback Submitted: " + savedFeedback.getTitle();
            String body = "<h3>Hello " + savedFeedback.getUser().getName() + ",</h3>" +
                    "<p>We have received your feedback regarding <b>" + savedFeedback.getTitle() + "</b>.</p>" +
                    "<p>Our team is reviewing it and will get back to you soon.</p>" +
                    "<p>Status: <b>PENDING</b></p>" +
                    "<br><p>Thank you for using our service.</p>";
            emailService.sendEmail(savedFeedback.getUser().getEmail(), subject, body);
        }

        return convertToDTO(savedFeedback);
    }
    
    
    public FeedbackDTO getFeedbackById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Feedback ID cannot be null");
        }
        Feedback feedback = feedbackRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Feedback not found"));
        return convertToDTO(feedback);
    }
    
    
    public List<FeedbackDTO> getAllFeedbacks() {
        return feedbackRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    
    public List<FeedbackDTO> getFeedbacksForUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return feedbackRepository.findByUser(user).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    
    public List<FeedbackDTO> getFeedbacksForCompanyAdmin(Long adminUserId) {
        if (adminUserId == null) {
            throw new IllegalArgumentException("Admin User ID cannot be null");
        }
        User user = userRepository.findById(adminUserId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getCompany() == null) {
            throw new RuntimeException("Company not found for user");
        }
        Company company = user.getCompany();
        return feedbackRepository.findByRouteCompany(company).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
 
    public Page<FeedbackDTO> getAllFeedbacksPaginated(String search, Pageable pageable, java.security.Principal principal) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean hasSearch = search != null && !search.trim().isEmpty();

        // 1. SUPER_ADMIN: Can see everything
        if ("SUPER_ADMIN".equals(user.getRole().name())) {
            if (hasSearch) {
                return feedbackRepository.searchFeedbacks(search, pageable).map(this::convertToDTO);
            }
            return feedbackRepository.findAll(pageable).map(this::convertToDTO);
        }

        // 2. COMPANY_ADMIN: Can only see feedback for their company's routes
        if ("COMPANY_ADMIN".equals(user.getRole().name())) {
            Long companyId = (user.getCompany() != null) ? user.getCompany().getCompanyId() : null;
            if (companyId == null) {
                 return Page.empty(pageable);
            }
            if (hasSearch) {
                return feedbackRepository.searchFeedbacksByCompanyRole(companyId, search, pageable)
                    .map(this::convertToDTO);
            }
            return feedbackRepository.findByRouteCompanyCompanyId(companyId, pageable)
                .map(this::convertToDTO);
        }

        // 3. USER: Can only see their own feedback
        if (hasSearch) {
            return feedbackRepository.searchFeedbacksByUserRole(user.getUserId(), search, pageable)
                .map(this::convertToDTO);
        }
        return feedbackRepository.findByUser(user, pageable)
            .map(this::convertToDTO);
    }
    
   
    public FeedbackDTO updateFeedback(Long id, FeedbackDTO feedbackDTO) {
        if (id == null) {
            throw new IllegalArgumentException("Feedback ID cannot be null");
        }
        Feedback feedback = feedbackRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Feedback not found"));
        
        feedback.setTitle(feedbackDTO.getTitle());
        feedback.setDescription(feedbackDTO.getDescription());
        feedback.setImageUrl(feedbackDTO.getImageUrl());
        feedback.setStatus(FeedbackStatus.valueOf(feedbackDTO.getStatus()));
        feedback.setIssueCategory(IssueCategory.valueOf(feedbackDTO.getIssueCategory()));
        
        Long userId = feedbackDTO.getUserId();
        if (userId != null) {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            feedback.setUser(user);
        }
        
        Long routeId = feedbackDTO.getRouteId();
        if (routeId != null) {
            Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));
            feedback.setRoute(route);
        }
        
        Long assignedUserId = feedbackDTO.getAssignedUserId();
        if (assignedUserId != null) {
            User assignedUser = userRepository.findById(assignedUserId)
                .orElseThrow(() -> new RuntimeException("Assigned User not found"));
            feedback.setAssignedUser(assignedUser);
        }
        
        Long incidentLocationId = feedbackDTO.getIncidentLocationId();
        if (incidentLocationId != null) {
            Location location = locationRepository.findById(incidentLocationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));
            feedback.setIncidentLocation(location);
        }
        
        Feedback updatedFeedback = feedbackRepository.save(feedback);
        return convertToDTO(updatedFeedback);
    }
    
    
    public void deleteFeedback(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Feedback ID cannot be null");
        }
        if (!feedbackRepository.existsById(id)) {
            throw new RuntimeException("Feedback not found");
        }
        feedbackRepository.deleteById(id);
    }
    
   
    public List<FeedbackDTO> getFeedbacksByRoute(Long routeId) {
        if (routeId == null) {
            throw new IllegalArgumentException("Route ID cannot be null");
        }
        Route route = routeRepository.findById(routeId)
            .orElseThrow(() -> new RuntimeException("Route not found"));
        
        return feedbackRepository.findByRoute(route).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    
    public List<FeedbackDTO> getFeedbacksByStatus(String status) {
        return feedbackRepository.findByStatus(FeedbackStatus.valueOf(status)).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
   
    private FeedbackDTO convertToDTO(Feedback feedback) {
        FeedbackDTO dto = new FeedbackDTO();
        dto.setFeedbackId(feedback.getFeedbackId());
        dto.setTitle(feedback.getTitle());
        dto.setDescription(feedback.getDescription());
        dto.setImageUrl(feedback.getImageUrl());
        dto.setStatus(feedback.getStatus().toString());
        dto.setIssueCategory(feedback.getIssueCategory().toString());
        
        if (feedback.getUser() != null) {
            dto.setUserId(feedback.getUser().getUserId());
            dto.setUserName(feedback.getUser().getName());
        }
        
        if (feedback.getRoute() != null) {
            Route route = feedback.getRoute();
            dto.setRouteId(route.getRouteId());
            dto.setRouteName(route.getRouteName());
            if (route.getCompany() != null) {
                dto.setCompanyId(route.getCompany().getCompanyId());
                dto.setCompanyName(route.getCompany().getCompanyName());
            } else {
                dto.setCompanyName("Unassigned");
            }
        }
        
        if (feedback.getAssignedUser() != null) {
            dto.setAssignedUserId(feedback.getAssignedUser().getUserId());
            dto.setAssignedUserName(feedback.getAssignedUser().getName());
        }
        
        if (feedback.getIncidentLocation() != null) {
            dto.setIncidentLocationId(feedback.getIncidentLocation().getLocationId());
        dto.setIncidentLocationName(feedback.getIncidentLocation().getLocationName());
        }
        
        if (feedback.getCreatedAt() != null) {
            dto.setCreatedAt(feedback.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        }
        
        dto.setAdminResponse(feedback.getAdminResponse()); // Map response

        return dto;
    }

    public FeedbackDTO resolveFeedback(Long id, String response) {
        if (id == null) {
            throw new IllegalArgumentException("Feedback ID cannot be null");
        }
        Feedback feedback = feedbackRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Feedback not found"));
        
        feedback.setAdminResponse(response);
        feedback.setStatus(FeedbackStatus.RESOLVED);
        
        Feedback savedFeedback = feedbackRepository.save(feedback);
        activityService.logSuccess("Case Resolved", "The feedback for '" + savedFeedback.getTitle() + "' was successfully resolved.", null);
        
        // Notify user via email
        if (savedFeedback.getUser() != null && savedFeedback.getUser().getEmail() != null) {
            String subject = "Feedback Resolved: " + savedFeedback.getTitle();
            String body = "<h3>Hello " + savedFeedback.getUser().getName() + ",</h3>" +
                    "<p>Your feedback regarding <b>" + savedFeedback.getTitle() + "</b> has been resolved.</p>" +
                    "<p><b>Admin Response:</b> " + response + "</p>" +
                    "<p>Status: <b>RESOLVED</b></p>" +
                    "<br><p>Thank you for your patience.</p>";
            emailService.sendEmail(savedFeedback.getUser().getEmail(), subject, body);
        }

        return convertToDTO(savedFeedback);
    }
    public FeedbackDTO updateStatus(Long id, String status, String response) {
        if (id == null) {
            throw new IllegalArgumentException("Feedback ID cannot be null");
        }
        Feedback feedback = feedbackRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Feedback not found"));
        
        feedback.setStatus(FeedbackStatus.valueOf(status));
        if (response != null && !response.isEmpty()) {
            feedback.setAdminResponse(response);
        }
        
        Feedback savedFeedback = feedbackRepository.save(feedback);
        
        String activityTitle = "Status Update";
        String activityMessage = "Feedback '" + savedFeedback.getTitle() + "' status changed to " + status;
        activityService.logInfo(activityTitle, activityMessage, null);

        // Notify user via email
        if (savedFeedback.getUser() != null && savedFeedback.getUser().getEmail() != null) {
            String subject = "Feedback Status Updated: " + savedFeedback.getTitle();
            String body = "<h3>Hello " + savedFeedback.getUser().getName() + ",</h3>" +
                    "<p>Your feedback regarding <b>" + savedFeedback.getTitle() + "</b> has been updated.</p>" +
                    "<p><b>New Status:</b> " + status + "</p>" +
                    (response != null && !response.isEmpty() ? "<p><b>Admin Comment:</b> " + response + "</p>" : "") +
                    "<br><p>Thank you for your patience.</p>";
            emailService.sendEmail(savedFeedback.getUser().getEmail(), subject, body);
        }

        return convertToDTO(savedFeedback);
    }
}
