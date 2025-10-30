package com.transport.TransportReportingSystem.repository;

import com.transport.TransportReportingSystem.entity.Route;
import com.transport.TransportReportingSystem.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    Optional<Route> findByRouteNumber(String routeNumber);
    
    
    boolean existsByRouteNumber(String routeNumber);
    

    List<Route> findByCompany(Company company);
    
    
    List<Route> findByCompanyCompanyName(String companyName);
    
    
    List<Route> findByStartPointContaining(String keyword);
    
    
    List<Route> findByEndPointContaining(String keyword);
    
    // Pagination 
    @NonNull
    Page<Route> findAll(@NonNull Pageable pageable);
    
    @NonNull
    Page<Route> findByCompany(@NonNull Company company, @NonNull Pageable pageable);
}
