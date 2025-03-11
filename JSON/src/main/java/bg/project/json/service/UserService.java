package bg.project.json.service;

import bg.project.json.service.dtos.export.UserAndProductDto;
import bg.project.json.service.dtos.export.UserSoldProductsDto;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.List;

@Service
public interface UserService {

    void seedUsers() throws FileNotFoundException;

    List<UserSoldProductsDto> getAllUsersAndSoldItems();

    void printAllUsersAndSoldItems();

    UserAndProductDto getUserAndProductDto();

    void printGetUserAndProductDto();

}
