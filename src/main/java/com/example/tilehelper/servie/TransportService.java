package com.example.tilehelper.servie;

import com.example.tilehelper.entity.TransportEntity;

import java.util.List;

public interface TransportService {

    List<TransportEntity> list();

    boolean add(TransportEntity transportEntity);

    boolean updateStatus(TransportEntity transportEntity);

    boolean update(TransportEntity transportEntity);

    boolean delete(List<Integer> ids);

    boolean updateStatusByOrderId(Integer orderId, String status);

    boolean deleteByOrderIds(List<Integer> orderIds);

    boolean updateByOrderId(TransportEntity transportEntity);
}
