package com.transport.TransportReportingSystem.repository;

import com.transport.TransportReportingSystem.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findTop10ByOrderByTimestampDesc();
    List<ActivityLog> findTop10ByUserUserIdOrderByTimestampDesc(Long userId);
    List<ActivityLog> findTop10ByUserOrderByTimestampDesc(com.transport.TransportReportingSystem.entity.User user);
    List<ActivityLog> findTop10ByUserCompanyCompanyIdOrderByTimestampDesc(Long companyId);
}
