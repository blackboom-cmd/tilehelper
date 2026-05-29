package com.example.tilehelper.mapper;

import com.example.tilehelper.entity.TransportEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface TransportMapper {

    @Select({
            "SELECT",
            "id,",
            "order_id,",
            "customer_name,",
            "truck_name,",
            "total_pieces,",
            "package_count,",
            "extra_pieces,",
            "tile_name,",
            "tile_id,",
            "note,",
            "delivery_address,",
            "delivery_time,",
            "status,",
            "created_at",
            "FROM transport"
    })
    @Results(id = "transportResultMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "orderId", column = "order_id"),
            @Result(property = "customerName", column = "customer_name"),
            @Result(property = "truckName", column = "truck_name"),
            @Result(property = "totalPieces", column = "total_pieces"),
            @Result(property = "packageCount", column = "package_count"),
            @Result(property = "extraPieces", column = "extra_pieces"),
            @Result(property = "tileName", column = "tile_name"),
            @Result(property = "tileId", column = "tile_id"),
            @Result(property = "note", column = "note"),
            @Result(property = "deliveryAddress", column = "delivery_address"),
            @Result(property = "deliveryTime", column = "delivery_time"),
            @Result(property = "status", column = "status"),
            @Result(property = "createdAt", column = "created_at")
    })
    List<TransportEntity> list();

    @Insert({
            "INSERT INTO transport (",
            "order_id,",
            "customer_name,",
            "truck_name,",
            "total_pieces,",
            "package_count,",
            "extra_pieces,",
            "tile_name,",
            "tile_id,",
            "note,",
            "delivery_address,",
            "delivery_time,",
            "status,",
            "created_at",
            ") VALUES (",
            "#{orderId},",
            "#{customerName},",
            "#{truckName},",
            "#{totalPieces},",
            "#{packageCount},",
            "#{extraPieces},",
            "#{tileName},",
            "#{tileId},",
            "#{note},",
            "#{deliveryAddress},",
            "#{deliveryTime},",
            "#{status},",
            "#{createdAt}",
            ")"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int add(TransportEntity transportEntity);

    @Update({
            "UPDATE transport",
            "SET status = #{status}",
            "WHERE id = #{id}"
    })
    int updateStatus(TransportEntity transportEntity);

    @Update({
            "UPDATE transport",
            "SET status = #{status}",
            "WHERE order_id = #{orderId}"
    })
    int updateStatusByOrderId(@Param("orderId") Integer orderId, @Param("status") String status);

    @Update({
            "UPDATE transport",
            "SET",
            "order_id = #{orderId},",
            "customer_name = #{customerName},",
            "truck_name = #{truckName},",
            "total_pieces = #{totalPieces},",
            "package_count = #{packageCount},",
            "extra_pieces = #{extraPieces},",
            "tile_name = #{tileName},",
            "tile_id = #{tileId},",
            "note = #{note},",
            "delivery_address = #{deliveryAddress},",
            "delivery_time = #{deliveryTime},",
            "status = #{status},",
            "created_at = #{createdAt}",
            "WHERE id = #{id}"
    })
    int update(TransportEntity transportEntity);

    @Update({
            "UPDATE transport",
            "SET",
            "customer_name = #{customerName},",
            "total_pieces = #{totalPieces},",
            "package_count = #{packageCount},",
            "extra_pieces = #{extraPieces},",
            "tile_name = #{tileName},",
            "tile_id = #{tileId},",
            "note = #{note},",
            "delivery_address = #{deliveryAddress},",
            "delivery_time = #{deliveryTime},",
            "status = #{status},",
            "created_at = #{createdAt}",
            "WHERE order_id = #{orderId}"
    })
    int updateByOrderId(TransportEntity transportEntity);

    @Delete({
            "<script>",
            "DELETE FROM transport WHERE id IN",
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    int delete(@Param("ids") List<Integer> ids);

    @Delete({
            "<script>",
            "DELETE FROM transport WHERE order_id IN",
            "<foreach collection='orderIds' item='orderId' open='(' separator=',' close=')'>",
            "#{orderId}",
            "</foreach>",
            "</script>"
    })
    int deleteByOrderIds(@Param("orderIds") List<Integer> orderIds);
}
