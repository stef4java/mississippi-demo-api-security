package com.codergogogo.apisecurity.controller;

import com.codergogogo.apisecurity.model.dto.R;
import com.codergogogo.apisecurity.model.entity.Employee;
import com.codergogogo.apisecurity.utils.sign.Sign;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/employees")
public class SignController {

    @Sign
    @GetMapping(value = "/pathVariable/{id}/{name}/{age}/{deptName}")
    public R<Employee> pathVariable(@PathVariable @NotNull Long id, @PathVariable @NotBlank String name, @PathVariable @NotNull Integer age, @PathVariable @NotBlank String deptName) {
        Employee employee = new Employee(id, name, age, deptName);
        return R.ok(employee);
    }


    @Sign
    @GetMapping(value = "/requestParam")
    public R<Employee> requestParam(@RequestParam @NotNull Long id, @RequestParam @NotBlank String name, @RequestParam @NotNull Integer age, @RequestParam @NotBlank String deptName) {
        Employee employee = new Employee(id, name, age, deptName);
        return R.ok(employee);
    }


    @Sign
    @PostMapping(value = "/requestBody")
    public R<Employee>  requestBody( @RequestBody Employee employee) {
        return R.ok(employee);
    }


    @Sign
    @PostMapping(value = "/all/{id}")
    public R<Employee> all(@PathVariable @NotNull Long id, @RequestParam @NotBlank String name, @RequestBody Employee employee) {
        Employee e = new Employee();
        e.setId(id);
        e.setName(name);
        e.setAge(employee.getAge());
        e.setDeptName(employee.getDeptName());
        return R.ok(e);
    }


}