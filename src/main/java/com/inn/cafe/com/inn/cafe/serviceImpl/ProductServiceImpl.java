package com.inn.cafe.com.inn.cafe.serviceImpl;

import com.inn.cafe.com.inn.cafe.JWT.JwtAuthFilter;
import com.inn.cafe.com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.com.inn.cafe.dao.ProductDAO;
import com.inn.cafe.com.inn.cafe.model.Category;
import com.inn.cafe.com.inn.cafe.model.Product;
import com.inn.cafe.com.inn.cafe.service.ProductService;
import com.inn.cafe.com.inn.cafe.utils.CafeUtils;
import com.inn.cafe.com.inn.cafe.wrapper.ProductWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductDAO productDAO;

    private final JwtAuthFilter jwtAuthFilter;
    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try {
            //check admin
            if(jwtAuthFilter.isAdmin()) {
                //validate product
                if(validateProduct(requestMap)){
                    productDAO.save(getProductFromMap(requestMap));
                    return CafeUtils.getResponseEntity("Product added successfully", HttpStatus.OK);

                } else {
                    return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
                }

            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProduct() {
        try {
            return new ResponseEntity<>(productDAO.getAllProduct(), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try {
            //check if admin
            if(jwtAuthFilter.isAdmin()){
                //check validate
                if(validateProduct(requestMap)){
                    Optional<Product> productOptional = productDAO.findById(Integer.parseInt(requestMap.get("id")));
                    log.info(requestMap.get("id"));
                    log.info(String.valueOf(productOptional.isPresent()));
                    //check empty
                    if(productOptional.isPresent()){
                        productOptional.get().setName(requestMap.get("name"));
                        productOptional.get().setDescription(requestMap.get("description"));
                        productOptional.get().setPrice(Integer.valueOf(requestMap.get("price")));
                        Category category = new Category();
                        category.setId(Integer.parseInt(requestMap.get("categoryId")));
                        productOptional.get().setCategory(category);
                        productDAO.save(productOptional.get());
                        return CafeUtils.getResponseEntity("Update successfully",HttpStatus.OK);
                    } else {
                        return CafeUtils.getResponseEntity("Product not found",HttpStatus.OK);
                    }


                } else {
                    return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteProduct(Integer id) {
        try {
            //check if admin
            if(jwtAuthFilter.isAdmin()){
                productDAO.deleteById(id);
                return CafeUtils.getResponseEntity("Product is deleted",HttpStatus.OK);
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProductStatus(Map<String, String> requestMap) {
        try {
            //check if admin
            if(jwtAuthFilter.isAdmin()){
                //check validate
                    Optional<Product> productOptional = productDAO.findById(Integer.parseInt(requestMap.get("id")));
                    log.info(requestMap.get("id"));
                    log.info(String.valueOf(productOptional.isPresent()));
                    //check empty
                    if(productOptional.isPresent()){
                        productOptional.get().setStatus(Boolean.parseBoolean(requestMap.get("status")));
                        productDAO.save(productOptional.get());
                        return CafeUtils.getResponseEntity("Product status update successfully",HttpStatus.OK);
                    } else {
                        return CafeUtils.getResponseEntity("Product not found",HttpStatus.OK);
                    }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Product>> getByCategory(Integer categoryId) {
            try {
                return new ResponseEntity<>(productDAO.getByCategory(categoryId),HttpStatus.OK);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return new ResponseEntity<>(new ArrayList<Product>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    @Override
    public ResponseEntity<Product> getById(Integer id) {
        try {
            return new ResponseEntity<>(productDAO.findById(id).get(),HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new Product(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Product getProductFromMap(Map<String, String> requestMap) {
        Category category = new Category();
        category.setId(Integer.parseInt(requestMap.get("categoryId")));
        Product product = Product.builder()
                .name(requestMap.get("name"))
                .category(category)
                .description(requestMap.get("description"))
                .price(Integer.parseInt(requestMap.get("price")))
                .status(true)
                .build();
        return product;
    }

    private boolean validateProduct(Map<String, String> requestMap) {
        Optional<Product> product = productDAO.findByName(String.valueOf(requestMap.get("name")));
        //validate
        if(requestMap.containsKey("name")){
            //check dup
            if(product.isEmpty()){
                return true;
            }
        }
        return false;
    }
}
