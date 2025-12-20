package com.transport.TransportReportingSystem.service;

import com.transport.TransportReportingSystem.dto.SearchResultDTO;
import com.transport.TransportReportingSystem.entity.Company;
import com.transport.TransportReportingSystem.entity.Location;
import com.transport.TransportReportingSystem.entity.Route;
import com.transport.TransportReportingSystem.entity.User;
import com.transport.TransportReportingSystem.repository.CompanyRepository;
import com.transport.TransportReportingSystem.repository.LocationRepository;
import com.transport.TransportReportingSystem.repository.RouteRepository;
import com.transport.TransportReportingSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class SearchService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RouteRepository routeRepository;
    private final LocationRepository locationRepository;

    public List<SearchResultDTO> search(String query, java.security.Principal principal) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String trimmedQuery = query.trim();
        List<SearchResultDTO> results = new ArrayList<>();
        String role = user.getRole().name();

        // 1. Locations: Always searchable by everyone
        locationRepository.findByLocationNameContaining(trimmedQuery).stream()
             .limit(5)
             .forEach(l -> results.add(mapLocation(l)));

        // 2. Routes: Searchable by everyone, but filtered for COMPANY_ADMIN
        if ("SUPER_ADMIN".equals(role) || "USER".equals(role)) {
            routeRepository.searchRoutes(trimmedQuery, PageRequest.of(0, 5)).getContent().stream()
                .forEach(r -> results.add(mapRoute(r)));
        } else if ("COMPANY_ADMIN".equals(role)) {
            Long companyId = (user.getCompany() != null) ? user.getCompany().getCompanyId() : null;
            if (companyId != null) {
                routeRepository.searchRoutesByCompanyRole(companyId, trimmedQuery, PageRequest.of(0, 5)).getContent().stream()
                    .forEach(r -> results.add(mapRoute(r)));
            }
        }

        // 3. Companies: Only for SUPER_ADMIN (or COMPANY_ADMIN seeing their own)
        if ("SUPER_ADMIN".equals(role)) {
            companyRepository.searchCompanies(trimmedQuery, PageRequest.of(0, 5)).getContent().stream()
                .forEach(c -> results.add(mapCompany(c)));
        } else if ("COMPANY_ADMIN".equals(role)) {
            if (user.getCompany() != null && containsIgnoreCase(user.getCompany().getCompanyName(), trimmedQuery)) {
                results.add(mapCompany(user.getCompany()));
            }
        }

        // 4. Users: SUPER_ADMIN (all), COMPANY_ADMIN (their company)
        if ("SUPER_ADMIN".equals(role)) {
             userRepository.searchUsers(trimmedQuery, PageRequest.of(0, 5)).getContent().stream()
                .forEach(u -> results.add(mapUser(u)));
        } else if ("COMPANY_ADMIN".equals(role)) {
            Long companyId = (user.getCompany() != null) ? user.getCompany().getCompanyId() : null;
            if (companyId != null) {
                userRepository.searchUsersByCompany(companyId, trimmedQuery, PageRequest.of(0, 5)).getContent().stream()
                    .forEach(u -> results.add(mapUser(u)));
            }
        }

        return results;
    }

    private boolean containsIgnoreCase(String source, String target) {
        return source != null && target != null && source.toLowerCase().contains(target.toLowerCase());
    }

    private SearchResultDTO mapUser(User user) {
        return SearchResultDTO.builder()
                .type("User")
                .id(user.getUserId())
                .title(user.getName()) // Use Name
                .description(user.getEmail() + " (" + user.getRole() + ")")
                .link("/dashboard/users/edit/" + user.getUserId())
                .build();
    }

    private SearchResultDTO mapCompany(Company company) {
         return SearchResultDTO.builder()
                .type("Company")
                .id(company.getCompanyId())
                .title(company.getCompanyName())
                .description("Contact: " + company.getContactInfo())
                .link("/dashboard/companies/edit/" + company.getCompanyId())
                .build();
    }

    private SearchResultDTO mapRoute(Route route) {
         return SearchResultDTO.builder()
                .type("Route")
                .id(route.getRouteId())
                .title(route.getRouteNumber())
                .description(route.getStartPoint() + " -> " + route.getEndPoint())
                .link("/dashboard/routes/edit/" + route.getRouteId())
                .build();
    }
    
    private SearchResultDTO mapLocation(Location location) {
         // Location page might not exist yet, pointing to dashboard for now or a placeholder
         return SearchResultDTO.builder()
                .type("Location")
                .id(location.getLocationId())
                .title(location.getLocationName())
                .description(location.getLocationType().toString())
                .link("/dashboard") // No specific page for location edit yet
                .build();
    }
}
