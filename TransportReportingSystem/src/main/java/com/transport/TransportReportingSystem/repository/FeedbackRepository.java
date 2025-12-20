package com.transport.TransportReportingSystem.repository;

import com.transport.TransportReportingSystem.entity.Feedback;
import com.transport.TransportReportingSystem.entity.User;
import com.transport.TransportReportingSystem.entity.Route;

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
    
    Page<Feedback> findByUser(User user, Pageable pageable);
    
    long countByUser(User user);
    
    long countByUserAndStatus(User user, FeedbackStatus status);
    
    List<Feedback> findByStatus(FeedbackStatus status);

    List<Feedback> findByIssueCategory(IssueCategory category);

    List<Feedback> findByStatusAndIssueCategory(FeedbackStatus status, IssueCategory category);
    
    
    List<Feedback> findByRoute(Route route);
    
    long countByRouteCompanyCompanyId(Long companyId);
    
    Page<Feedback> findByRouteCompanyCompanyId(Long companyId, Pageable pageable);
    
    List<Feedback> findByAssignedUser(User user);
    
    List<Feedback> findByIncidentLocation(Location location);

    List<Feedback> findByRouteCompany(Company company);
    
    // Pagination 
    @NonNull
    Page<Feedback> findAll(@NonNull Pageable pageable);
    
    @NonNull
    Page<Feedback> findByStatus(@NonNull FeedbackStatus status, @NonNull Pageable pageable);

    long countByStatus(FeedbackStatus status);

    long countByRouteCompanyCompanyIdAndStatus(Long companyId, FeedbackStatus status);
    
    @NonNull
    @org.springframework.data.jpa.repository.Query("SELECT f FROM Feedback f WHERE LOWER(f.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(f.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(f.user.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(f.route.routeName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Feedback> searchFeedbacks(@org.springframework.data.repository.query.Param("keyword") String keyword, Pageable pageable);
    
    @org.springframework.data.jpa.repository.Query("SELECT f FROM Feedback f WHERE f.route.company.companyId = :companyId AND (LOWER(f.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(f.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(f.user.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(f.route.routeName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Feedback> searchFeedbacksByCompanyRole(@org.springframework.data.repository.query.Param("companyId") Long companyId, @org.springframework.data.repository.query.Param("keyword") String keyword, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT f FROM Feedback f WHERE f.user.userId = :userId AND (LOWER(f.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(f.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(f.route.routeName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Feedback> searchFeedbacksByUserRole(@org.springframework.data.repository.query.Param("userId") Long userId, @org.springframework.data.repository.query.Param("keyword") String keyword, Pageable pageable);
}
