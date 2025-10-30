package com.transport.TransportReportingSystem.repository;

import com.transport.TransportReportingSystem.entity.Admin;
import com.transport.TransportReportingSystem.entity.User;
import com.transport.TransportReportingSystem.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    
    
    Optional<Admin> findByUser(User user);
    
    
    List<Admin> findByCompany(Company company);
    
    
    List<Admin> findByIsSuperAdmin(Boolean isSuperAdmin);
}
