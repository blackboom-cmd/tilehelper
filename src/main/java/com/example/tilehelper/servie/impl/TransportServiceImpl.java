package com.example.tilehelper.servie.impl;

import com.example.tilehelper.entity.TransportEntity;
import com.example.tilehelper.mapper.TransportMapper;
import com.example.tilehelper.servie.TransportService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransportServiceImpl implements TransportService {

    private final TransportMapper transportMapper;

    public TransportServiceImpl(TransportMapper transportMapper) {
        this.transportMapper = transportMapper;
    }

    @Override
    public List<TransportEntity> list() {
        return transportMapper.list();
    }

    @Override
    public boolean add(TransportEntity transportEntity) {
        return transportMapper.add(transportEntity) > 0;
    }

    @Override
    public boolean updateStatus(TransportEntity transportEntity) {
        return transportMapper.updateStatus(transportEntity) > 0;
    }

    @Override
    public boolean update(TransportEntity transportEntity) {
        return transportMapper.update(transportEntity) > 0;
    }

    @Override
    public boolean delete(List<Integer> ids) {
        return transportMapper.delete(ids) > 0;
    }

    @Override
    public boolean updateStatusByOrderId(Integer orderId, String status) {
        return transportMapper.updateStatusByOrderId(orderId, status) > 0;
    }

    @Override
    public boolean deleteByOrderIds(List<Integer> orderIds) {
        return transportMapper.deleteByOrderIds(orderIds) > 0;
    }

    @Override
    public boolean updateByOrderId(TransportEntity transportEntity) {
        return transportMapper.updateByOrderId(transportEntity) > 0;
    }
}
