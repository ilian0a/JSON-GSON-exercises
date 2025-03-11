package bg.project.json.service.impl;

import bg.project.json.data.entities.Category;
import bg.project.json.data.entities.Product;
import bg.project.json.data.repositories.CategoryRepository;
import bg.project.json.service.CategoryService;
import bg.project.json.service.dtos.export.CategoryByProductsDto;
import bg.project.json.service.dtos.imports.CategorySeedDto;
import bg.project.json.util.ValidationUtil;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    // CONSTANTS
    private static final String FILE_PATH = "src/main/resources/json/categories.json";


    // BEANS

    private final CategoryRepository categoryRepository;
    private final Gson gson;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, Gson gson, ValidationUtil validationUtil, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
    }


    @Override
    public void seedCategories() throws IOException {
        if (this.categoryRepository.count() == 0) {
            String jsonContent = new String(Files.readAllBytes(Path.of(FILE_PATH)));

            CategorySeedDto[] categorySeedDtos = this.gson.fromJson(jsonContent, CategorySeedDto[].class);
            for (CategorySeedDto categorySeedDto: categorySeedDtos) {
                if (!this.validationUtil.isValid(categorySeedDto)) {
                    this.validationUtil.getViolations(categorySeedDto)
                            .forEach(v -> System.out.println(v.getMessage()));
                continue;
                }


                Category category = this.modelMapper.map(categorySeedDto, Category.class);
                this.categoryRepository.saveAndFlush(category);

            }

        }



    }

    @Override
    public List<CategoryByProductsDto> getAllCategoriesByProducts() {
         return   this.categoryRepository.findAllCategoriesByProducts()
                    .stream()
                    .map(c -> {
                        CategoryByProductsDto dto = this.modelMapper.map(c, CategoryByProductsDto.class);
                        dto.setProductsCount(c.getProducts().size());
                        BigDecimal sum = c.getProducts().stream()            // .mapToDouble(p -> p.getPrice().doubleValue()).sum() FOR DOUBLE VALUES
                                .map(Product::getPrice)
                                .reduce(BigDecimal::add).get();  // REDUCE: adds every single price(iterates over them, hence reducing the list of prices) to the BigDecimal.

                        dto.setTotalRevenue(sum);
                        dto.setAveragePrice(sum.divide(BigDecimal.valueOf(c.getProducts().size()), MathContext.DECIMAL32));

                        return dto;
                    })
                    .collect(Collectors.toList());

    }

    @Override
    public void printAllCategoriesByProducts() {
        String json = this.gson.toJson(this.getAllCategoriesByProducts());
        System.out.println(json);

    }
}
