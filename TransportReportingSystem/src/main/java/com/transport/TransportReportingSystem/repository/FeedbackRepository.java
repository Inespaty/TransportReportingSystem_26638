package com.transport.TransportReportingSystem.repository;

import com.transport.TransportReportingSystem.entity.Feedback;
import com.transport.TransportReportingSystem.entity.User;
import com.transport.TransportReportingSystem.entity.Route;
import com.transport.TransportReportingSystem.entity.Admin;
import com.transport.TransportReportingSystem.entity.Location;
import com.transport.TransportReportingSystem.entity.Company;
import com.transport.TransportReportingSystem.enums.FeedbackStatus;
import com.transport.TransportReportingSystem.enums.IssueCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    
    List<Feedback> findByUser(User user);
    
    
    List<Feedback> findByStatus(FeedbackStatus status);

    List<Feedback> findByIssueCategory(IssueCategory category);

    List<Feedback> findByStatusAndIssueCategory(FeedbackStatus status, IssueCategory category);
    
    
    List<Feedback> findByRoute(Route route);
    
    
    List<Feedback> findByAssignedAdmin(Admin admin);
    
    
    List<Feedback> findByIncidentLocation(Location location);
    

    List<Feedback> findByRouteCompany(Company company);
    
    // Pagination 
    @NonNull
    Page<Feedback> findAll(@NonNull Pageable pageable);
    
    @NonNull
    Page<Feedback> findByStatus(@NonNull FeedbackStatus status, @NonNull Pageable pageable);
    
    @NonNull
    Page<Feedback> findByUser(@NonNull User user, @NonNull Pageable pageable);
}
