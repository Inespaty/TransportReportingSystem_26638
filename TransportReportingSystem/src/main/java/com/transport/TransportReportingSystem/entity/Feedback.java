package com.transport.TransportReportingSystem.entity;

import com.transport.TransportReportingSystem.enums.FeedbackStatus;
import com.transport.TransportReportingSystem.enums.IssueCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long feedbackId;
    
    @Column(name = "title", length = 200)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private FeedbackStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "issue_category")
    private IssueCategory issueCategory;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_admin_id")
    private Admin assignedAdmin;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_location_id")
    private Location incidentLocation;
}
