package com.ifan112.demo.mybatis.mapper;

import com.ifan112.demo.mybatis.entity.User;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;

public interface UserMapper {

    @Insert("INSERT INTO user(`id`, `name`, `age`) VALUE(#{user.id}, #{user.name}, #{user.age})")
    int save(@Param("user") User user);

    User selectOne(long id);

    @Select("SELECT id, name, age, status, last_changed FROM user WHERE status != -1")
    List<User> selectAll();

    @DeleteProvider(type = SqlProvider.class, method = "deleteSql")
    int delete(@Param("id") long id);

    @DeleteProvider(type = SqlProvider.class, method = "batchDeleteSql")
    int batchDelete(@Param("ids") List<Long> ids);

    class SqlProvider {

        public String deleteSql(@Param("id") long id) {
            return new SQL() {{
                DELETE_FROM("user");
                WHERE("id = #{id}");
            }}.toString();
        }

        public String batchDeleteSql(@Param("ids") List<Long> ids) {
            StringBuilder s = new StringBuilder();
            ids.forEach(id -> s.append(id).append(","));

            return new SQL(){{
                DELETE_FROM("user");
                WHERE("id IN(" + s.substring(0, s.length() - 1) + ")");
            }}.toString();
        }
    }

}
