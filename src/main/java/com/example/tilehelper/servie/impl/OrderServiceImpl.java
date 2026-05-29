package com.example.tilehelper.servie.impl;

import com.example.tilehelper.entity.OrderEntity;
import com.example.tilehelper.entity.TileEntity;
import com.example.tilehelper.entity.TransportEntity;
import com.example.tilehelper.mapper.OrderMapper;
import com.example.tilehelper.servie.OrderService;
import com.example.tilehelper.servie.TileService;
import com.example.tilehelper.servie.TransportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final TileService tileService;
    private final TransportService transportService;

    public OrderServiceImpl(OrderMapper orderMapper, TileService tileService, TransportService transportService) {
        this.orderMapper = orderMapper;
        this.tileService = tileService;
        this.transportService = transportService;
    }

    @Override
    public List<OrderEntity> list() {
        return orderMapper.list();
    }

    @Override
    @Transactional
    public boolean add(OrderEntity orderEntity) {
        calcPackage(orderEntity);
        if (orderMapper.add(orderEntity) <= 0) {
            return false;
        }
        transportService.add(buildTransport(orderEntity));
        return true;
    }

    @Override
    @Transactional
    public boolean updateStatus(OrderEntity orderEntity) {
        if (orderMapper.updateStatus(orderEntity) <= 0) {
            return false;
        }
        transportService.updateStatusByOrderId(orderEntity.getId(), orderEntity.getStatus());
        return true;
    }

    @Override
    @Transactional
    public boolean update(OrderEntity orderEntity) {
        calcPackage(orderEntity);
        if (orderMapper.update(orderEntity) <= 0) {
            return false;
        }
        transportService.updateByOrderId(buildTransport(orderEntity));
        return true;
    }

    @Override
    @Transactional
    public boolean delete(List<Integer> ids) {
        if (orderMapper.delete(ids) <= 0) {
            return false;
        }
        transportService.deleteByOrderIds(ids);
        return true;
    }

    private void calcPackage(OrderEntity orderEntity) {
        String tileName = orderEntity.getTileName();
        Integer tileQuantity = orderEntity.getTileQuantity();
        if (tileName == null || tileName.isEmpty() || tileQuantity == null || tileQuantity <= 0) {
            return;
        }
        TileEntity tile = tileService.getByTileName(tileName);
        if (tile != null && tile.getUnitQuantity() != null && tile.getUnitQuantity() > 0) {
            int unitQty = tile.getUnitQuantity();
            orderEntity.setPackageCount(tileQuantity / unitQty);
            orderEntity.setExtraPieces(tileQuantity % unitQty);
        }
    }

    private TransportEntity buildTransport(OrderEntity order) {
        TransportEntity transport = new TransportEntity();
        transport.setOrderId(order.getId());
        transport.setCustomerName(order.getCustomerName());
        transport.setTotalPieces(order.getTileQuantity());
        transport.setPackageCount(order.getPackageCount());
        transport.setExtraPieces(order.getExtraPieces());
        transport.setNote(order.getRemark());
        transport.setDeliveryAddress(order.getDeliveryAddress());
        transport.setDeliveryTime(order.getDeliveryTime());
        transport.setStatus(order.getStatus());
        transport.setTileName(order.getTileName());
        transport.setCreatedAt(order.getCreatedAt() != null ? order.getCreatedAt() : new Date());

        String tileName = order.getTileName();
        if (tileName != null && !tileName.isEmpty()) {
            TileEntity tile = tileService.getByTileName(tileName);
            if (tile != null) {
                transport.setTileId(tile.getId());
            }
        }
        return transport;
    }
}
