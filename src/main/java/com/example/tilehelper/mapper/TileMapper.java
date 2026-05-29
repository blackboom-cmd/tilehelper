package com.example.tilehelper.mapper;

import com.example.tilehelper.entity.TileEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface TileMapper {

    @Select({
            "SELECT",
            "id,",
            "tile_name,",
            "model,",
            "specification,",
            "color,",
            "price,",
            "unit_quantity,",
            "stock_quantity,",
            "accessories,",
            "image_url,",
            "location",
            "FROM tile"
    })
    @Results(id = "tileResultMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "tileName", column = "tile_name"),
            @Result(property = "model", column = "model"),
            @Result(property = "specification", column = "specification"),
            @Result(property = "color", column = "color"),
            @Result(property = "price", column = "price"),
            @Result(property = "unitQuantity", column = "unit_quantity"),
            @Result(property = "stockQuantity", column = "stock_quantity"),
            @Result(property = "accessories", column = "accessories"),
            @Result(property = "imageUrl", column = "image_url"),
            @Result(property = "location", column = "location")
    })
    List<TileEntity> list();

    @Insert({
            "INSERT INTO tile (",
            "tile_name,",
            "model,",
            "specification,",
            "color,",
            "price,",
            "unit_quantity,",
            "stock_quantity,",
            "accessories,",
            "image_url,",
            "location",
            ") VALUES (",
            "#{tileName},",
            "#{model},",
            "#{specification},",
            "#{color},",
            "#{price},",
            "#{unitQuantity},",
            "#{stockQuantity},",
            "#{accessories},",
            "#{imageUrl},",
            "#{location}",
            ")"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int add(TileEntity tileEntity);

    @Update({
            "UPDATE tile",
            "SET",
            "tile_name = #{tileName},",
            "model = #{model},",
            "specification = #{specification},",
            "color = #{color},",
            "price = #{price},",
            "unit_quantity = #{unitQuantity},",
            "stock_quantity = #{stockQuantity},",
            "accessories = #{accessories},",
            "image_url = #{imageUrl},",
            "location = #{location}",
            "WHERE id = #{id}"
    })
    int update(TileEntity tileEntity);

    @Select({
            "SELECT",
            "id,",
            "tile_name,",
            "model,",
            "specification,",
            "color,",
            "price,",
            "unit_quantity,",
            "stock_quantity,",
            "accessories,",
            "image_url,",
            "location",
            "FROM tile WHERE tile_name = #{tileName} LIMIT 1"
    })
    @ResultMap("tileResultMap")
    TileEntity getByTileName(@Param("tileName") String tileName);

    @Delete({
            "<script>",
            "DELETE FROM tile WHERE id IN",
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    int delete(@Param("ids") List<Integer> ids);
}
