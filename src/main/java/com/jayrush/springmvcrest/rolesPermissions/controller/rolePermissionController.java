package com.jayrush.springmvcrest.rolesPermissions.controller;

import com.jayrush.springmvcrest.domain.Response;
import com.jayrush.springmvcrest.rolesPermissions.dtos.permissionDto;
import com.jayrush.springmvcrest.rolesPermissions.dtos.rolesDto;
import com.jayrush.springmvcrest.rolesPermissions.models.Permissions;
import com.jayrush.springmvcrest.rolesPermissions.models.Roles;
import com.jayrush.springmvcrest.rolesPermissions.services.permissionService;
import com.jayrush.springmvcrest.rolesPermissions.services.roleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author JoshuaO
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(rolePermissionController.BASE_URL)
public class rolePermissionController {
    private static Logger logger = LoggerFactory.getLogger(rolePermissionController.class);

    public static final String BASE_URL = "/api/v1/rolePermissions";
    private static final String SUCCESS = "Success";
    private static final String SUCCESS_CODE = "00";
    private static final String FAILED = "Failed";
    private static final String FAILED_CODE = "96";

    @Autowired
    roleService roleService;
    @Autowired
    permissionService permissionService;

    @GetMapping()
    public ResponseEntity<?> getAllRoles(){
        try {
            Response response = new Response();
            List<Roles> roles = roleService.getAllRoles();
            response.setRespCode(SUCCESS_CODE);
            response.setRespDescription(SUCCESS);
            response.setRespBody(roles);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            Response response = new Response();
            response.setRespCode(FAILED_CODE);
            response.setRespDescription(FAILED);
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }

    @PostMapping()
    public ResponseEntity<?> getAllRolesByInstitutionID(String token){
        try {
            Response response = new Response();
            List<Roles> roles = roleService.getAllRolesByInstitutionID(token);
            response.setRespCode(SUCCESS_CODE);
            response.setRespDescription(SUCCESS);
            response.setRespBody(roles);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            Response response = new Response();
            response.setRespCode(FAILED_CODE);
            response.setRespDescription(FAILED);
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }

    @PostMapping("/addRoles")
    public ResponseEntity<?> addRoles(@RequestBody rolesDto rolesDto){
        try {
            Response response = new Response();
            Roles roles = roleService.addRoles(rolesDto);
            response.setRespCode(SUCCESS_CODE);
            response.setRespDescription(SUCCESS);
            response.setRespBody(roles);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            Response response = new Response();
            response.setRespCode(FAILED_CODE);
            response.setRespDescription(FAILED);
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }

    @PostMapping("/editRoles")
    public ResponseEntity<?> editRoles(@RequestBody Roles rolesDto){
        try {
            Response response = new Response();
            Roles roles = roleService.editRoles(rolesDto);
            response.setRespCode(SUCCESS_CODE);
            response.setRespDescription(SUCCESS);
            response.setRespBody(roles);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            Response response = new Response();
            response.setRespCode(FAILED_CODE);
            response.setRespDescription(FAILED);
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }
    @PostMapping("/deleteRoles")
    public ResponseEntity<?> deleteRoles(@RequestBody Roles rolesDto){
        try {
            Response response = new Response();
            String roles = roleService.deleteRoles(rolesDto);
            response.setRespCode(SUCCESS_CODE);
            response.setRespDescription(SUCCESS);
            response.setRespBody(roles);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            Response response = new Response();
            response.setRespCode(FAILED_CODE);
            response.setRespDescription(FAILED);
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }


    @GetMapping("/getAllPermissions")
    public ResponseEntity<?> getAllPermissions(){
        try {
            Response response = new Response();
            List<Permissions> permissions = permissionService.getAllPermissions();
            response.setRespCode(SUCCESS_CODE);
            response.setRespDescription(SUCCESS);
            response.setRespBody(permissions);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            Response response = new Response();
            response.setRespCode(FAILED_CODE);
            response.setRespDescription(FAILED);
            response.setRespBody(null);
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
    }

    @RequestMapping(
            value = "/**",
            method = RequestMethod.OPTIONS
    )
    public ResponseEntity handle() {
        return new ResponseEntity(HttpStatus.OK);
    }

}
