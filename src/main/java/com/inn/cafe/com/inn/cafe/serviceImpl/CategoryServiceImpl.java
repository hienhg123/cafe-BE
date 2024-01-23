package com.inn.cafe.com.inn.cafe.serviceImpl;

import com.google.common.base.Strings;
import com.inn.cafe.com.inn.cafe.JWT.JwtAuthFilter;
import com.inn.cafe.com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.com.inn.cafe.dao.CategoryDAO;
import com.inn.cafe.com.inn.cafe.model.Category;
import com.inn.cafe.com.inn.cafe.model.Product;
import com.inn.cafe.com.inn.cafe.service.CategoryService;
import com.inn.cafe.com.inn.cafe.utils.CafeUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.xml.transform.sax.SAXResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDAO categoryDAO;

    private final JwtAuthFilter jwtAuthFilter;

    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try{
            //check if user is admin
            if(jwtAuthFilter.isAdmin()){
                //validate the category
                if(validateCategory(requestMap)){
                    Category category = Category.builder()
                            .name(requestMap.get("name"))
                            .build();
                    categoryDAO.save(category);
                    return CafeUtils.getResponseEntity("Category is added successfully", HttpStatus.OK);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
        try{
            //filter value not empty
            if(!Strings.isNullOrEmpty(filterValue)&&filterValue.equalsIgnoreCase("true")){
                log.info("Insite if");
                new ResponseEntity<List<Category>>(categoryDAO.takeCategory(),HttpStatus.OK);
            }
            return new ResponseEntity<>(categoryDAO.findAll(),HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try{
            //check if admin
            if(jwtAuthFilter.isAdmin()){
                //validate category
                if(validateCategory(requestMap)){
                    Category category = categoryDAO.findById(Integer.parseInt(requestMap.get("id"))).get();
                    category.setName(requestMap.get("name"));
                    categoryDAO.save(category);
                    return CafeUtils.getResponseEntity("Update category successfully", HttpStatus.OK);
                } else{
                    return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
                }
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateCategory(Map<String, String> requestMap) {
       Optional<Category> category = categoryDAO.findByName(String.valueOf(requestMap.get("name")));
        //check not null
        if(requestMap.containsKey("name")){
            //check dup
            if(category.isEmpty()){
                return true;
            }
        }
        return false;
    }
}
