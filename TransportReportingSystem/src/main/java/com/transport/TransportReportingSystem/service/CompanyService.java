package com.transport.TransportReportingSystem.service;

import com.transport.TransportReportingSystem.dto.CompanyDTO;
import com.transport.TransportReportingSystem.entity.Company;
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
public class CompanyService {
    
    private final CompanyRepository companyRepository;
    
    
    public CompanyDTO createCompany(CompanyDTO companyDTO) {
        Company company = new Company();
        company.setCompanyName(companyDTO.getCompanyName());
        company.setDescription(companyDTO.getDescription());
        company.setContactInfo(companyDTO.getContactInfo());
        
        Company savedCompany = companyRepository.save(company);
        return convertToDTO(savedCompany);
    }
    
   
    public CompanyDTO getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Company not found"));
        return convertToDTO(company);
    }
    
    
    public List<CompanyDTO> getAllCompanies() {
        return companyRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    
    public Page<CompanyDTO> getAllCompaniesPaginated(Pageable pageable) {
        return companyRepository.findAll(pageable)
            .map(this::convertToDTO);
    }
    
    
    public CompanyDTO updateCompany(Long id, CompanyDTO companyDTO) {
        Company company = companyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Company not found"));
        
        company.setCompanyName(companyDTO.getCompanyName());
        company.setDescription(companyDTO.getDescription());
        company.setContactInfo(companyDTO.getContactInfo());
        
        
        Company updatedCompany = companyRepository.save(company);
        return convertToDTO(updatedCompany);
    }
    
    
    public void deleteCompany(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new RuntimeException("Company not found");
        }
        companyRepository.deleteById(id);
    }
    
    
    public CompanyDTO getCompanyByName(String companyName) {
        Company company = companyRepository.findByCompanyName(companyName)
            .orElseThrow(() -> new RuntimeException("Company not found"));
        return convertToDTO(company);
    }
    
    
    private CompanyDTO convertToDTO(Company company) {
        CompanyDTO dto = new CompanyDTO();
        dto.setCompanyId(company.getCompanyId());
        dto.setCompanyName(company.getCompanyName());
        dto.setDescription(company.getDescription());
        dto.setContactInfo(company.getContactInfo());
        
        return dto;
    }
}
