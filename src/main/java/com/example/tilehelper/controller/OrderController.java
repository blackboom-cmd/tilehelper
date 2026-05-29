package com.example.tilehelper.controller;

import com.example.tilehelper.entity.OrderEntity;
import com.example.tilehelper.servie.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/list")
    public List<OrderEntity> list() {
        return orderService.list();
    }

    @PostMapping("/add")
    public boolean add(@RequestBody OrderEntity orderEntity) {
        return orderService.add(orderEntity);
    }

    @PostMapping("/updateStatus")
    public boolean updateStatus(@RequestBody OrderEntity orderEntity) {
        return orderService.updateStatus(orderEntity);
    }

    @PutMapping("/update")
    public boolean update(@RequestBody OrderEntity orderEntity) {
        return orderService.update(orderEntity);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody List<Integer> ids) {
        return orderService.delete(ids);
    }
}
