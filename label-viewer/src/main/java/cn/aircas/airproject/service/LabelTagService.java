package cn.aircas.airproject.service;

import cn.aircas.airproject.entity.dto.LabelTagDto;

import java.util.List;


/**
 * 标签管理逻辑服务类
 */
public interface LabelTagService<T> {

    boolean createTable(String createSql);

    boolean insert(T object);

    List<Object> queryList(Class<T> clazz);

    boolean updateById(T object);

    boolean deleteById(int deleteId);

}
