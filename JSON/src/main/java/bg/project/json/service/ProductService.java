package bg.project.json.service;

import bg.project.json.service.dtos.export.ProductInRangeDto;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.List;

@Service
public interface ProductService {

    void seedProducts() throws FileNotFoundException;

    List<ProductInRangeDto> getAllProductsInRange(BigDecimal from, BigDecimal to);

    void printAllProductsInRange(BigDecimal from, BigDecimal to);

}
