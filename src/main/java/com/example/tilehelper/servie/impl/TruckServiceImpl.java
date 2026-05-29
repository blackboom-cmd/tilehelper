package com.example.tilehelper.servie.impl;

import com.example.tilehelper.entity.Truck;
import com.example.tilehelper.mapper.TruckMapper;
import com.example.tilehelper.servie.TruckService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TruckServiceImpl implements TruckService {

    private final TruckMapper truckMapper;

    public TruckServiceImpl(TruckMapper truckMapper) {
        this.truckMapper = truckMapper;
    }

    @Override
    public List<Truck> list() {
        return truckMapper.list();
    }

    @Override
    public boolean add(Truck truck) {
        return truckMapper.add(truck) > 0;
    }

    @Override
    public boolean update(Truck truck) {
        return truckMapper.update(truck) > 0;
    }

    @Override
    public boolean delete(List<Long> ids) {
        return truckMapper.delete(ids) > 0;
    }
}
