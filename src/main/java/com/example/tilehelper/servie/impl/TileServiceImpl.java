package com.example.tilehelper.servie.impl;

import com.example.tilehelper.entity.TileEntity;
import com.example.tilehelper.mapper.TileMapper;
import com.example.tilehelper.servie.TileService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TileServiceImpl implements TileService {

    private final TileMapper tileMapper;

    public TileServiceImpl(TileMapper tileMapper) {
        this.tileMapper = tileMapper;
    }

    @Override
    public List<TileEntity> list() {
        return tileMapper.list();
    }

    @Override
    public boolean add(TileEntity tileEntity) {
        return tileMapper.add(tileEntity) > 0;
    }

    @Override
    public boolean update(TileEntity tileEntity) {
        return tileMapper.update(tileEntity) > 0;
    }

    @Override
    public TileEntity getByTileName(String tileName) {
        return tileMapper.getByTileName(tileName);
    }

    @Override
    public boolean delete(List<Integer> ids) {
        return tileMapper.delete(ids) > 0;
    }
}
