package com.transport.TransportReportingSystem.controller;

import com.transport.TransportReportingSystem.dto.RouteDTO;
import com.transport.TransportReportingSystem.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {
    
    private final RouteService routeService;
    
    
    @PostMapping
    public ResponseEntity<RouteDTO> createRoute(
            @RequestParam String routeNumber,
            @RequestParam String routeName,
            @RequestParam String startPoint,
            @RequestParam String endPoint,
            @RequestParam String direction,
            @RequestParam String district,
            @RequestParam Long companyId) {
        RouteDTO routeDTO = new RouteDTO();
        routeDTO.setRouteNumber(routeNumber);
        routeDTO.setRouteName(routeName);
        routeDTO.setStartPoint(startPoint);
        routeDTO.setEndPoint(endPoint);
        routeDTO.setDirection(direction);
        routeDTO.setDistrict(district);
        routeDTO.setCompanyId(companyId);
        RouteDTO createdRoute = routeService.createRoute(routeDTO);
        return new ResponseEntity<>(createdRoute, HttpStatus.CREATED);
    }
    
  
    @GetMapping("/{id}")
    public ResponseEntity<RouteDTO> getRouteById(@PathVariable Long id) {
        RouteDTO route = routeService.getRouteById(id);
        return ResponseEntity.ok(route);
    }
    
    
    @GetMapping
    public ResponseEntity<List<RouteDTO>> getAllRoutes() {
        List<RouteDTO> routes = routeService.getAllRoutes();
        return ResponseEntity.ok(routes);
    }
    
    
    @GetMapping("/paginated")
    public ResponseEntity<Page<RouteDTO>> getAllRoutesPaginated(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "routeId", direction = Sort.Direction.DESC) Pageable pageable,
            java.security.Principal principal) {
        Page<RouteDTO> routes = routeService.getAllRoutesPaginated(search, pageable, principal);
        return ResponseEntity.ok(routes);
    }
    
    
    @PutMapping("/{id}")
    public ResponseEntity<RouteDTO> updateRoute(
            @PathVariable Long id,
            @RequestParam String routeNumber,
            @RequestParam String routeName,
            @RequestParam String startPoint,
            @RequestParam String endPoint,
            @RequestParam String direction,
            @RequestParam String district,
            @RequestParam Long companyId) {
        RouteDTO routeDTO = new RouteDTO();
        routeDTO.setRouteNumber(routeNumber);
        routeDTO.setRouteName(routeName);
        routeDTO.setStartPoint(startPoint);
        routeDTO.setEndPoint(endPoint);
        routeDTO.setDirection(direction);
        routeDTO.setDistrict(district);
        routeDTO.setCompanyId(companyId);
        RouteDTO updatedRoute = routeService.updateRoute(id, routeDTO);
        return ResponseEntity.ok(updatedRoute);
    }
    
 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }
    
    
    @GetMapping("/by-company/{companyId}")
    public ResponseEntity<List<RouteDTO>> getRoutesByCompany(@PathVariable Long companyId) {
        List<RouteDTO> routes = routeService.getRoutesByCompany(companyId);
        return ResponseEntity.ok(routes);
    }
}
