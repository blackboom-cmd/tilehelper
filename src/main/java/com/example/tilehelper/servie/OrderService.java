package com.example.tilehelper.servie;

import com.example.tilehelper.entity.OrderEntity;

import java.util.List;

public interface OrderService {

    List<OrderEntity> list();

    boolean add(OrderEntity orderEntity);

    boolean updateStatus(OrderEntity orderEntity);

    boolean update(OrderEntity orderEntity);

    boolean delete(List<Integer> ids);
}
