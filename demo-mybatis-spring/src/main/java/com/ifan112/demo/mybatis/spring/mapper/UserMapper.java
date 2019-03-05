package com.ifan112.demo.mybatis.spring.mapper;

import com.ifan112.demo.mybatis.spring.entity.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;

// @Mapper
public interface UserMapper {

    @Insert("INSERT INTO user(id, name, age) VALUE(#{id}, #{name}, #{age})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    long insert(User user);

    User selectOne(long id);

    @Select("SELECT id, name, age, status, last_changed FROM user")
    // @Results({
    //         @Result(column = "last_changed", property = "lastChanged")
    // })
    // @ResultMap("commonRM")
    List<User> selectAll();

    @UpdateProvider(type = SqlProvider.class, method = "delete")
    int delete(@Param("id") long id);


    class SqlProvider {

        public String delete(@Param("id") long id) {
            return new SQL() {{
                DELETE_FROM("user");
                WHERE("id = " + id);
            }}.toString();
        }
    }
}
