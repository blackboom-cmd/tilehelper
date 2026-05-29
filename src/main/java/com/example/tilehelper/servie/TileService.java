package com.example.tilehelper.servie;

import com.example.tilehelper.entity.TileEntity;

import java.util.List;

public interface TileService {

    List<TileEntity> list();

    boolean add(TileEntity tileEntity);

    boolean update(TileEntity tileEntity);

    TileEntity getByTileName(String tileName);

    boolean delete(List<Integer> ids);
}
