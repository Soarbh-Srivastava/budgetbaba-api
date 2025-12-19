package xyz.whysoarbh.bugetbaba.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.whysoarbh.bugetbaba.dto.CategoryDTO;
import xyz.whysoarbh.bugetbaba.service.CategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController
{
    private final CategoryService categoryService;
    @PostMapping
    public ResponseEntity<CategoryDTO> saveCategory(@RequestBody CategoryDTO categoryDTO)
    {
        CategoryDTO saveCategory = categoryService.saveCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveCategory);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getCategories()
    {
        List<CategoryDTO> categories = categoryService.getCategoriesForCurrentUser();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<CategoryDTO>> getCategoriesByTypeForCurrentUser(@PathVariable String type)
    {
        List<CategoryDTO> list = categoryService.getCategoriesByTypeForCurrentUser(type);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody CategoryDTO dto)
    {
        CategoryDTO updatedCategory = categoryService.updateCategory(categoryId, dto);
        return ResponseEntity.ok(updatedCategory);
    }
}
