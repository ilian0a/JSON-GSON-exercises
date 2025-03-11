package bg.project.json.service.impl;

import bg.project.json.data.entities.User;
import bg.project.json.data.repositories.UserRepository;
import bg.project.json.service.UserService;
import bg.project.json.service.dtos.export.*;
import bg.project.json.service.dtos.imports.UserSeedDto;
import bg.project.json.util.ValidationUtil;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class UserServiceImpl implements UserService {

    private static final String FILE_PATH = "src/main/resources/json/users.json";


    private final UserRepository userRepository;

    private final Gson gson;

    private final ValidationUtil validationUtil;

    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, Gson gson, ValidationUtil validationUtil, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
    }


    @Override
    public void seedUsers() throws FileNotFoundException {
        if (this.userRepository.count() == 0) {
            UserSeedDto[] userSeedDtos = this.gson.fromJson(new FileReader(FILE_PATH), UserSeedDto[].class);
            for (UserSeedDto userSeedDto : userSeedDtos) {
                if (!this.validationUtil.isValid(userSeedDto)) {
                    this.validationUtil.getViolations(userSeedDto)
                            .forEach(v -> System.out.println(v.getMessage()));

                    continue;
                }

                this.userRepository.saveAndFlush(
                        this.modelMapper.map(userSeedDto, User.class));

            }

        }

    }

    @Override
    public List<UserSoldProductsDto> getAllUsersAndSoldItems() {
        return this.userRepository.findAll()
                .stream()
                .filter(u ->
                        u.getSold().stream().anyMatch(p -> p.getBuyer() != null))
                .map(u -> {
                    UserSoldProductsDto userDto = this.modelMapper.map(u, UserSoldProductsDto.class);

                    List<ProductSoldDto> soldProductsDto = u.getSold()
                            .stream()
                            .filter(p -> p.getBuyer() != null)
                            .map(p -> this.modelMapper.map(p, ProductSoldDto.class))
                            .collect(toList());

                    userDto.setSoldProducts(soldProductsDto);
                    return userDto;
                    })
                    .sorted(Comparator.comparing(UserSoldProductsDto::getLastName).thenComparing(UserSoldProductsDto::getFirstName))
                    .toList();
    }

    @Override
    public void printAllUsersAndSoldItems() {

        String json = this.gson.toJson(this.getAllUsersAndSoldItems());
        System.out.println(json);
    }

    @Override
    public UserAndProductDto getUserAndProductDto() {
        UserAndProductDto userAndProductDto = new UserAndProductDto();
        // OUTER CLASS [USERS]:
        // SET USERS & COUNT => lines 122,123.

        List<UserSoldDto> userSoldDtos = this.userRepository.findAll()
                // USER INFO [firstName, lastName, age]
                .stream()
                .filter(u -> !u.getSold().isEmpty()) // CONDITION: USER HAS SOLD PRODUCTS
                .map(u -> {
                    UserSoldDto userSoldDto = this.modelMapper.map(u, UserSoldDto.class);
                    ProductSoldByUserDto productSoldByUserDto = new ProductSoldByUserDto();
                    // INNER CLASS [PRODUCTS SOLD BY USER]
                    // SET PRODUCTS [LIST] AND COUNT => lines 117,118.

                    List<ProductInfoDto> productInfoDtos = u.getSold()
                            // PRODUCT INFO [name, price]
                            .stream()
                            .map(p -> this.modelMapper.map(p, ProductInfoDto.class))
                            .collect(Collectors.toList());



                    productSoldByUserDto.setProducts(productInfoDtos);
                    productSoldByUserDto.setCount(productInfoDtos.size());


                    userSoldDto.setSoldProducts(productSoldByUserDto);
                    return userSoldDto;
                })
                .sorted((a, b) -> {
                    int countA = a.getSoldProducts().getCount();
                    int countB = b.getSoldProducts().getCount();
                    return Integer.compare(countB, countA); // DESC
                })
                .collect(toList());


        userAndProductDto.setUsers(userSoldDtos);
        userAndProductDto.setUsersCount(userSoldDtos.size());
        return userAndProductDto;
    }

    @Override
    public void printGetUserAndProductDto() {
        String json = this.gson.toJson(this.getUserAndProductDto());
        System.out.println(json);
    }
}