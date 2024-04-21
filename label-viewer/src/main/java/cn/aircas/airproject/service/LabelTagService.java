package cn.aircas.airproject.service;

import cn.aircas.airproject.entity.domain.LabelTagDatabaseInfo;
import java.util.List;


/**
 * 标签管理逻辑服务类
 */
public interface LabelTagService<T> {

    boolean executeSql(String createSql);

    int insert(T object);

    List<Object> queryList(Class<T> clazz, Object params);

    boolean updateById(T object);

    boolean deleteById(int deleteId);

    void setIpAndDriver();

}
