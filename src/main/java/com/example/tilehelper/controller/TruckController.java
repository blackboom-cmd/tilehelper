package com.example.tilehelper.controller;

import com.example.tilehelper.entity.Truck;
import com.example.tilehelper.servie.TruckService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/truck")
public class TruckController {

    private final TruckService truckService;

    public TruckController(TruckService truckService) {
        this.truckService = truckService;
    }

    @GetMapping("/list")
    public List<Truck> list() {
        return truckService.list();
    }

    @PostMapping("/add")
    public boolean add(@RequestBody Truck truck) {
        return truckService.add(truck);
    }

    @PutMapping("/update")
    public boolean update(@RequestBody Truck truck) {
        return truckService.update(truck);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody List<Long> ids) {
        return truckService.delete(ids);
    }
}
