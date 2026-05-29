package com.example.tilehelper.servie;

import com.example.tilehelper.entity.Truck;

import java.util.List;

public interface TruckService {

    List<Truck> list();

    boolean add(Truck truck);

    boolean update(Truck truck);

    boolean delete(List<Long> ids);
}
