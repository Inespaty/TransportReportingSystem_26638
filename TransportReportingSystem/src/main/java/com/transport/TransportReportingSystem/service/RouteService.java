package com.transport.TransportReportingSystem.service;

import com.transport.TransportReportingSystem.dto.RouteDTO;
import com.transport.TransportReportingSystem.entity.Route;
import com.transport.TransportReportingSystem.entity.Company;
import com.transport.TransportReportingSystem.repository.RouteRepository;
import com.transport.TransportReportingSystem.repository.CompanyRepository;
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
public class RouteService {
    
    private final RouteRepository routeRepository;
    private final CompanyRepository companyRepository;
    
   
    public RouteDTO createRoute(RouteDTO routeDTO) {
        Route route = new Route();
        route.setRouteNumber(routeDTO.getRouteNumber());
        route.setRouteName(routeDTO.getRouteName());
        route.setStartPoint(routeDTO.getStartPoint());
        route.setEndPoint(routeDTO.getEndPoint());
        route.setDirection(routeDTO.getDirection());
        route.setDistrict(routeDTO.getDistrict());
        
        if (routeDTO.getCompanyId() != null) {
            Company company = companyRepository.findById(routeDTO.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));
            route.setCompany(company);
        }
        
        Route savedRoute = routeRepository.save(route);
        return convertToDTO(savedRoute);
    }
    
  
    public RouteDTO getRouteById(Long id) {
        Route route = routeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Route not found"));
        return convertToDTO(route);
    }
    
    
    public List<RouteDTO> getAllRoutes() {
        return routeRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    
    public Page<RouteDTO> getAllRoutesPaginated(Pageable pageable) {
        return routeRepository.findAll(pageable)
            .map(this::convertToDTO);
    }
    
    
    public RouteDTO updateRoute(Long id, RouteDTO routeDTO) {
        Route route = routeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Route not found"));
        
        route.setRouteNumber(routeDTO.getRouteNumber());
        route.setRouteName(routeDTO.getRouteName());
        route.setStartPoint(routeDTO.getStartPoint());
        route.setEndPoint(routeDTO.getEndPoint());
        route.setDirection(routeDTO.getDirection());
        route.setDistrict(routeDTO.getDistrict());
        
        if (routeDTO.getCompanyId() != null) {
            Company company = companyRepository.findById(routeDTO.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));
            route.setCompany(company);
        }
        
        Route updatedRoute = routeRepository.save(route);
        return convertToDTO(updatedRoute);
    }
    
    
    public void deleteRoute(Long id) {
        if (!routeRepository.existsById(id)) {
            throw new RuntimeException("Route not found");
        }
        routeRepository.deleteById(id);
    }
    
    
    public List<RouteDTO> getRoutesByCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new RuntimeException("Company not found"));
        
        return routeRepository.findByCompany(company).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    

    private RouteDTO convertToDTO(Route route) {
        RouteDTO dto = new RouteDTO();
        dto.setRouteId(route.getRouteId());
        dto.setRouteNumber(route.getRouteNumber());
        dto.setRouteName(route.getRouteName());
        dto.setStartPoint(route.getStartPoint());
        dto.setEndPoint(route.getEndPoint());
        dto.setDirection(route.getDirection());
        dto.setDistrict(route.getDistrict());
        
        if (route.getCompany() != null) {
            dto.setCompanyId(route.getCompany().getCompanyId());
            dto.setCompanyName(route.getCompany().getCompanyName());
        }
        
        return dto;
    }
}
