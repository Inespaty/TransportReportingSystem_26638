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
    private final com.transport.TransportReportingSystem.repository.UserRepository userRepository;
    private final ActivityService activityService;
    
   
    public RouteDTO createRoute(RouteDTO routeDTO) {
        Route route = new Route();
        if (routeRepository.existsByRouteNumber(routeDTO.getRouteNumber())) {
            throw new RuntimeException("Route with number " + routeDTO.getRouteNumber() + " already exists");
        }
        route.setRouteNumber(routeDTO.getRouteNumber());
        route.setRouteName(routeDTO.getRouteName());
        route.setStartPoint(routeDTO.getStartPoint());
        route.setEndPoint(routeDTO.getEndPoint());
        route.setDirection(routeDTO.getDirection());
        route.setDistrict(routeDTO.getDistrict());
        
        Long companyId = routeDTO.getCompanyId();
        if (companyId != null) {
            Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
            route.setCompany(company);
        }
        
        Route savedRoute = routeRepository.save(route);
        activityService.logSuccess("New Route Established", "The " + savedRoute.getRouteName() + " route (No. " + savedRoute.getRouteNumber() + ") is now active.", null);
        return convertToDTO(savedRoute);
    }
    
  
    public RouteDTO getRouteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Route ID cannot be null");
        }
        Route route = routeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Route not found"));
        return convertToDTO(route);
    }
    
    
    public List<RouteDTO> getAllRoutes() {
        return routeRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    
    public Page<RouteDTO> getAllRoutesPaginated(String search, Pageable pageable, java.security.Principal principal) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        
        com.transport.TransportReportingSystem.entity.User user = userRepository.findByEmail(principal.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));

        boolean hasSearch = search != null && !search.trim().isEmpty();

        if ("COMPANY_ADMIN".equals(user.getRole().name())) {
            if (user.getCompany() == null) {
                 return Page.empty(pageable);
            }
            if (hasSearch) {
                return routeRepository.searchRoutesByCompanyRole(user.getCompany().getCompanyId(), search, pageable)
                    .map(this::convertToDTO);
            }
            return routeRepository.findByCompany(user.getCompany(), pageable)
                .map(this::convertToDTO);
        }

        if (hasSearch) {
            return routeRepository.searchRoutes(search, pageable)
                .map(this::convertToDTO);
        }

        return routeRepository.findAll(pageable)
            .map(this::convertToDTO);
    }
    
    
    public RouteDTO updateRoute(Long id, RouteDTO routeDTO) {
        if (id == null) {
            throw new IllegalArgumentException("Route ID cannot be null");
        }
        Route route = routeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Route not found"));
        
        route.setRouteNumber(routeDTO.getRouteNumber());
        route.setRouteName(routeDTO.getRouteName());
        route.setStartPoint(routeDTO.getStartPoint());
        route.setEndPoint(routeDTO.getEndPoint());
        route.setDirection(routeDTO.getDirection());
        route.setDistrict(routeDTO.getDistrict());
        
        Long companyId = routeDTO.getCompanyId();
        if (companyId != null) {
            Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
            route.setCompany(company);
        }
        
        Route updatedRoute = routeRepository.save(route);
        return convertToDTO(updatedRoute);
    }
    
    
    public void deleteRoute(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Route ID cannot be null");
        }
        Route routeToDelete = routeRepository.findById(id).get();
        String routeName = routeToDelete.getRouteName();
        routeRepository.deleteById(id);
        activityService.logWarning("Route Removed", "The route '" + routeName + "' was deleted from the system.", null);
    }
    
    
    public List<RouteDTO> getRoutesByCompany(Long companyId) {
        if (companyId == null) {
            throw new IllegalArgumentException("Company ID cannot be null");
        }
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
