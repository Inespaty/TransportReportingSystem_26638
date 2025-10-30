package com.transport.TransportReportingSystem.service;

import com.transport.TransportReportingSystem.dto.FeedbackDTO;
import com.transport.TransportReportingSystem.entity.Feedback;
import com.transport.TransportReportingSystem.entity.User;
import com.transport.TransportReportingSystem.entity.Route;
import com.transport.TransportReportingSystem.entity.Admin;
import com.transport.TransportReportingSystem.entity.Location;
import com.transport.TransportReportingSystem.entity.Company;
import com.transport.TransportReportingSystem.enums.FeedbackStatus;
import com.transport.TransportReportingSystem.enums.IssueCategory;
import com.transport.TransportReportingSystem.repository.FeedbackRepository;
import com.transport.TransportReportingSystem.repository.UserRepository;
import com.transport.TransportReportingSystem.repository.RouteRepository;
import com.transport.TransportReportingSystem.repository.AdminRepository;
import com.transport.TransportReportingSystem.repository.LocationRepository;
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
    private final AdminRepository adminRepository;
    private final LocationRepository locationRepository;
    
    
    public FeedbackDTO createFeedback(FeedbackDTO feedbackDTO) {
        Feedback feedback = new Feedback();
        feedback.setTitle(feedbackDTO.getTitle());
        feedback.setDescription(feedbackDTO.getDescription());
        feedback.setImageUrl(feedbackDTO.getImageUrl());
        feedback.setStatus(FeedbackStatus.PENDING);
        feedback.setIssueCategory(IssueCategory.valueOf(feedbackDTO.getIssueCategory()));
        
        if (feedbackDTO.getUserId() != null) {
            User user = userRepository.findById(feedbackDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            feedback.setUser(user);
        }
        
        if (feedbackDTO.getRouteId() != null) {
            Route route = routeRepository.findById(feedbackDTO.getRouteId())
                .orElseThrow(() -> new RuntimeException("Route not found"));
            feedback.setRoute(route);
        }
        
        if (feedbackDTO.getAssignedAdminId() != null) {
            Admin admin = adminRepository.findById(feedbackDTO.getAssignedAdminId())
                .orElseThrow(() -> new RuntimeException("Admin not found"));
            feedback.setAssignedAdmin(admin);
        }
        
        if (feedbackDTO.getIncidentLocationId() != null) {
            Location location = locationRepository.findById(feedbackDTO.getIncidentLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found"));
            feedback.setIncidentLocation(location);
        }
        
        Feedback savedFeedback = feedbackRepository.save(feedback);
        return convertToDTO(savedFeedback);
    }
    
    
    public FeedbackDTO getFeedbackById(Long id) {
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
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return feedbackRepository.findByUser(user).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    
    public List<FeedbackDTO> getFeedbacksForCompanyAdmin(Long adminUserId) {
        User user = userRepository.findById(adminUserId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getAdmin() == null || user.getAdmin().getCompany() == null) {
            throw new RuntimeException("Admin or company not found for user");
        }
        Company company = user.getAdmin().getCompany();
        return feedbackRepository.findByRouteCompany(company).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
 
    public Page<FeedbackDTO> getAllFeedbacksPaginated(Pageable pageable) {
        return feedbackRepository.findAll(pageable)
            .map(this::convertToDTO);
    }
    
   
    public FeedbackDTO updateFeedback(Long id, FeedbackDTO feedbackDTO) {
        Feedback feedback = feedbackRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Feedback not found"));
        
        feedback.setTitle(feedbackDTO.getTitle());
        feedback.setDescription(feedbackDTO.getDescription());
        feedback.setImageUrl(feedbackDTO.getImageUrl());
        feedback.setStatus(FeedbackStatus.valueOf(feedbackDTO.getStatus()));
        feedback.setIssueCategory(IssueCategory.valueOf(feedbackDTO.getIssueCategory()));
        
        if (feedbackDTO.getUserId() != null) {
            User user = userRepository.findById(feedbackDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            feedback.setUser(user);
        }
        
        if (feedbackDTO.getRouteId() != null) {
            Route route = routeRepository.findById(feedbackDTO.getRouteId())
                .orElseThrow(() -> new RuntimeException("Route not found"));
            feedback.setRoute(route);
        }
        
        if (feedbackDTO.getAssignedAdminId() != null) {
            Admin admin = adminRepository.findById(feedbackDTO.getAssignedAdminId())
                .orElseThrow(() -> new RuntimeException("Admin not found"));
            feedback.setAssignedAdmin(admin);
        }
        
        if (feedbackDTO.getIncidentLocationId() != null) {
            Location location = locationRepository.findById(feedbackDTO.getIncidentLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found"));
            feedback.setIncidentLocation(location);
        }
        
        Feedback updatedFeedback = feedbackRepository.save(feedback);
        return convertToDTO(updatedFeedback);
    }
    
    
    public void deleteFeedback(Long id) {
        if (!feedbackRepository.existsById(id)) {
            throw new RuntimeException("Feedback not found");
        }
        feedbackRepository.deleteById(id);
    }
    
   
    public List<FeedbackDTO> getFeedbacksByRoute(Long routeId) {
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
            dto.setRouteId(feedback.getRoute().getRouteId());
            dto.setRouteName(feedback.getRoute().getRouteName());
        }
        
        if (feedback.getAssignedAdmin() != null) {
            dto.setAssignedAdminId(feedback.getAssignedAdmin().getAdminId());
            dto.setAssignedAdminName(feedback.getAssignedAdmin().getUser().getName());
        }
        
        if (feedback.getIncidentLocation() != null) {
            dto.setIncidentLocationId(feedback.getIncidentLocation().getLocationId());
            dto.setIncidentLocationName(feedback.getIncidentLocation().getLocationName());
        }
        
        return dto;
    }
}
