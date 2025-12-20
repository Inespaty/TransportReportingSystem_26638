package com.transport.TransportReportingSystem.repository;

import com.transport.TransportReportingSystem.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    
    
    Optional<Company> findByCompanyName(String companyName);
    
    
    List<Company> findByCompanyNameContaining(String keyword);
    
    
    List<Company> findByContactInfoContaining(String keyword);
    
    
    @org.springframework.data.jpa.repository.Query("SELECT c FROM Company c WHERE LOWER(c.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.contactInfo) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Company> searchCompanies(@org.springframework.data.repository.query.Param("keyword") String keyword, Pageable pageable);
}
