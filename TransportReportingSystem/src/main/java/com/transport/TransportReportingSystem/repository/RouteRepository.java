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

    List<Route> findByCompanyCompanyId(Long companyId);
    
    long countByCompanyCompanyId(Long companyId);
    
    Page<Route> findByCompanyCompanyId(Long companyId, Pageable pageable);
    
    
    List<Route> findByCompanyCompanyName(String companyName);
    
    
    List<Route> findByStartPointContaining(String keyword);
    
    
    List<Route> findByEndPointContaining(String keyword);
    
    // Pagination 
    @NonNull
    Page<Route> findAll(@NonNull Pageable pageable);
    
    @org.springframework.data.jpa.repository.Query("SELECT r FROM Route r WHERE LOWER(r.routeNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.routeName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.startPoint) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.endPoint) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.company.companyName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Route> searchRoutes(@org.springframework.data.repository.query.Param("keyword") String keyword, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT r FROM Route r WHERE r.company.companyId = :companyId AND (LOWER(r.routeNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.routeName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.startPoint) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.endPoint) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Route> searchRoutesByCompanyRole(@org.springframework.data.repository.query.Param("companyId") Long companyId, @org.springframework.data.repository.query.Param("keyword") String keyword, Pageable pageable);

    @NonNull
    Page<Route> findByCompany(@NonNull Company company, @NonNull Pageable pageable);
}
