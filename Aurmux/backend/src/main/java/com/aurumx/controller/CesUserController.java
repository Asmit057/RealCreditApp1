package com.aurumx.controller;

import com.aurumx.dto.request.CreateCesUserRequest;
import com.aurumx.entity.CesUser;
import com.aurumx.service.CesUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ces-users")
@RequiredArgsConstructor
public class CesUserController {
    
    private final CesUserService cesUserService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN_CES')")
    public ResponseEntity<CesUser> createCesUser(@Valid @RequestBody CreateCesUserRequest request) {
        CesUser cesUser = cesUserService.createCesUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cesUser);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN_CES')")
    public ResponseEntity<List<CesUser>> getAllCesUsers() {
        List<CesUser> users = cesUserService.getAllCesUsers();
        return ResponseEntity.ok(users);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_CES')")
    public ResponseEntity<Void> deleteCesUser(@PathVariable Long id) {
        cesUserService.deleteCesUser(id);
        return ResponseEntity.noContent().build();
    }
}
