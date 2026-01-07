package com.aurumx.service;

import com.aurumx.dto.request.CreateCesUserRequest;
import com.aurumx.entity.CesUser;
import com.aurumx.exception.BusinessRuleViolationException;
import com.aurumx.exception.ResourceNotFoundException;
import com.aurumx.repository.CesUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CesUserService {
    
    private final CesUserRepository cesUserRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public CesUser createCesUser(CreateCesUserRequest request) {
        if (cesUserRepository.existsByUsername(request.getUsername())) {
            throw new BusinessRuleViolationException("Username already exists: " + request.getUsername());
        }
        
        CesUser cesUser = new CesUser();
        cesUser.setUsername(request.getUsername());
        cesUser.setPassword(passwordEncoder.encode(request.getPassword()));
        cesUser.setRole(request.getRole());
        cesUser.setActive(true);
        
        return cesUserRepository.save(cesUser);
    }
    
    public List<CesUser> getAllCesUsers() {
        return cesUserRepository.findAll();
    }
    
    @Transactional
    public void deleteCesUser(Long id) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        
        CesUser userToDelete = cesUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CES User not found with id: " + id));
        
        // Prevent self-deletion
        if (userToDelete.getUsername().equals(currentUsername)) {
            throw new BusinessRuleViolationException("Cannot delete your own account");
        }
        
        cesUserRepository.delete(userToDelete);
    }
}
