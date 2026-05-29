package com.example.tilehelper.mapper;

import com.example.tilehelper.entity.Truck;
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
public interface TruckMapper {

    @Select({
            "SELECT",
            "id,",
            "name,",
            "capacity",
            "FROM truck"
    })
    @Results(id = "truckResultMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "name"),
            @Result(property = "capacity", column = "capacity")
    })
    List<Truck> list();

    @Insert({
            "INSERT INTO truck (",
            "name,",
            "capacity",
            ") VALUES (",
            "#{name},",
            "#{capacity}",
            ")"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int add(Truck truck);

    @Update({
            "UPDATE truck",
            "SET",
            "name = #{name},",
            "capacity = #{capacity}",
            "WHERE id = #{id}"
    })
    int update(Truck truck);

    @Delete({
            "<script>",
            "DELETE FROM truck WHERE id IN",
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    int delete(@Param("ids") List<Long> ids);
}
