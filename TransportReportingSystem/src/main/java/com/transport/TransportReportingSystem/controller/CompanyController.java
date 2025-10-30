package com.transport.TransportReportingSystem.controller;

import com.transport.TransportReportingSystem.dto.CompanyDTO;
import com.transport.TransportReportingSystem.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {
    
    private final CompanyService companyService;
    
    
    @PostMapping
    public ResponseEntity<CompanyDTO> createCompany(
            @RequestParam String companyName,
            @RequestParam String description,
            @RequestParam String contactInfo) {
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setCompanyName(companyName);
        companyDTO.setDescription(description);
        companyDTO.setContactInfo(contactInfo);
        
        CompanyDTO createdCompany = companyService.createCompany(companyDTO);
        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }
    
    
    @GetMapping("/{id}")
    public ResponseEntity<CompanyDTO> getCompanyById(@PathVariable Long id) {
        CompanyDTO company = companyService.getCompanyById(id);
        return ResponseEntity.ok(company);
    }
    
    
    @GetMapping
    public ResponseEntity<List<CompanyDTO>> getAllCompanies() {
        List<CompanyDTO> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(companies);
    }
    
    
    @GetMapping("/paginated")
    public ResponseEntity<Page<CompanyDTO>> getAllCompaniesPaginated(Pageable pageable) {
        Page<CompanyDTO> companies = companyService.getAllCompaniesPaginated(pageable);
        return ResponseEntity.ok(companies);
    }
    
    
    @PutMapping("/{id}")
    public ResponseEntity<CompanyDTO> updateCompany(
            @PathVariable Long id,
            @RequestParam String companyName,
            @RequestParam String description,
            @RequestParam String contactInfo){
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setCompanyName(companyName);
        companyDTO.setDescription(description);
        companyDTO.setContactInfo(contactInfo);
        
        CompanyDTO updatedCompany = companyService.updateCompany(id, companyDTO);
        return ResponseEntity.ok(updatedCompany);
    }
    
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }
    
    
    @GetMapping("/by-name/{companyName}")
    public ResponseEntity<CompanyDTO> getCompanyByName(@PathVariable String companyName) {
        CompanyDTO company = companyService.getCompanyByName(companyName);
        return ResponseEntity.ok(company);
    }
}
