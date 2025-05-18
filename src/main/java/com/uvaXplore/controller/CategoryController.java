package com.uvaXplore.controller;

import com.uvaXplore.entity.Category;
import com.uvaXplore.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/getAll")
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/getOne/{id}")
    public Optional<Category> getCategoryById(@PathVariable Integer id) {
        return categoryService.getCategoryById(id);
    }

    @PostMapping("/createCategory")
    public Category addCategory(@RequestBody Category category) {
        return categoryService.saveCategory(category);
    }

    @PutMapping("/update/{id}")
    public Category updateCategory(@PathVariable Integer id, @RequestBody Category category) {
        category.setCategoryId(id);
        return categoryService.saveCategory(category);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
    }
}
