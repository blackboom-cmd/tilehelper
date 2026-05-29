package com.example.tilehelper.controller;

import com.example.tilehelper.entity.TileEntity;
import com.example.tilehelper.servie.TileService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/tile")
public class TileController {

    private final TileService tileService;

    public TileController(TileService tileService) {
        this.tileService = tileService;
    }

    @GetMapping("/list")
    public List<TileEntity> list() {
        return tileService.list();
    }

    @PostMapping("/add")
    public boolean add(@RequestBody TileEntity tileEntity) {
        return tileService.add(tileEntity);
    }

    @PutMapping("/update")
    public boolean update(@RequestBody TileEntity tileEntity) {
        return tileService.update(tileEntity);
    }

    @DeleteMapping("/delete")
    public boolean delete(@RequestBody List<Integer> ids) {
        return tileService.delete(ids);
    }
}
