package bg.project.json.service;

import bg.project.json.service.dtos.export.CategoryByProductsDto;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface CategoryService {

    void seedCategories() throws IOException;

    List<CategoryByProductsDto> getAllCategoriesByProducts();

    void printAllCategoriesByProducts();

}
