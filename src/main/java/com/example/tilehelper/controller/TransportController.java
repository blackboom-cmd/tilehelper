package com.example.tilehelper.controller;

import com.example.tilehelper.entity.TransportEntity;
import com.example.tilehelper.servie.TransportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/transport")
public class TransportController {

    private final TransportService transportService;

    public TransportController(TransportService transportService) {
        this.transportService = transportService;
    }

    @GetMapping("/list")
    public List<TransportEntity> list() {
        return transportService.list();
    }

    @PostMapping("/add")
    public boolean add(@RequestBody TransportEntity transportEntity) {
        return transportService.add(transportEntity);
    }

    @PostMapping("/updateStatus")
    public boolean updateStatus(@RequestBody TransportEntity transportEntity) {
        return transportService.updateStatus(transportEntity);
    }

    @PutMapping("/update")
    public boolean update(@RequestBody TransportEntity transportEntity) {
        return transportService.update(transportEntity);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody List<Integer> ids) {
        return transportService.delete(ids);
    }
}
