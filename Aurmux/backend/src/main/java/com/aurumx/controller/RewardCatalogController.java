package com.aurumx.controller;

import com.aurumx.entity.RewardCategory;
import com.aurumx.entity.RewardItem;
import com.aurumx.service.RewardCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class RewardCatalogController {
    
    private final RewardCatalogService catalogService;
    
    @GetMapping("/categories")
    public ResponseEntity<List<RewardCategory>> getAllCategories() {
        List<RewardCategory> categories = catalogService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    @GetMapping("/items")
    public ResponseEntity<List<RewardItem>> getItemsByCategory(@RequestParam(required = false) Long categoryId) {
        List<RewardItem> items;
        if (categoryId != null) {
            items = catalogService.getItemsByCategory(categoryId);
        } else {
            items = catalogService.getAllAvailableItems();
        }
        return ResponseEntity.ok(items);
    }
}
