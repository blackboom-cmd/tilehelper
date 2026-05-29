package com.example.tilehelper.mapper;

import com.example.tilehelper.entity.OrderEntity;
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
public interface OrderMapper {

    @Select({
            "SELECT",
            "id,",
            "order_no,",
            "customer_id,",
            "customer_name,",
            "total_amount,",
            "status,",
            "delivery_address,",
            "delivery_time,",
            "remark,",
            "tile_name,",
            "tile_quantity,",
            "package_count,",
            "extra_pieces,",
            "created_at",
            "FROM `order`"
    })
    @Results(id = "orderResultMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "orderNo", column = "order_no"),
            @Result(property = "customerId", column = "customer_id"),
            @Result(property = "customerName", column = "customer_name"),
            @Result(property = "totalAmount", column = "total_amount"),
            @Result(property = "status", column = "status"),
            @Result(property = "deliveryAddress", column = "delivery_address"),
            @Result(property = "deliveryTime", column = "delivery_time"),
            @Result(property = "remark", column = "remark"),
            @Result(property = "tileName", column = "tile_name"),
            @Result(property = "tileQuantity", column = "tile_quantity"),
            @Result(property = "packageCount", column = "package_count"),
            @Result(property = "extraPieces", column = "extra_pieces"),
            @Result(property = "createdAt", column = "created_at")
    })
    List<OrderEntity> list();

    @Insert({
            "INSERT INTO `order` (",
            "order_no,",
            "customer_id,",
            "customer_name,",
            "total_amount,",
            "status,",
            "delivery_address,",
            "delivery_time,",
            "remark,",
            "tile_name,",
            "tile_quantity,",
            "package_count,",
            "extra_pieces,",
            "created_at",
            ") VALUES (",
            "#{orderNo},",
            "#{customerId},",
            "#{customerName},",
            "#{totalAmount},",
            "#{status},",
            "#{deliveryAddress},",
            "#{deliveryTime},",
            "#{remark},",
            "#{tileName},",
            "#{tileQuantity},",
            "#{packageCount},",
            "#{extraPieces},",
            "#{createdAt}",
            ")"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int add(OrderEntity orderEntity);

    @Update({
            "UPDATE `order`",
            "SET status = #{status}",
            "WHERE id = #{id}"
    })
    int updateStatus(OrderEntity orderEntity);

    @Update({
            "UPDATE `order`",
            "SET",
            "order_no = #{orderNo},",
            "customer_id = #{customerId},",
            "customer_name = #{customerName},",
            "total_amount = #{totalAmount},",
            "status = #{status},",
            "delivery_address = #{deliveryAddress},",
            "delivery_time = #{deliveryTime},",
            "remark = #{remark},",
            "tile_name = #{tileName},",
            "tile_quantity = #{tileQuantity},",
            "package_count = #{packageCount},",
            "extra_pieces = #{extraPieces},",
            "created_at = #{createdAt}",
            "WHERE id = #{id}"
    })
    int update(OrderEntity orderEntity);

    @Delete({
            "<script>",
            "DELETE FROM `order` WHERE id IN",
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    int delete(@Param("ids") List<Integer> ids);
}
