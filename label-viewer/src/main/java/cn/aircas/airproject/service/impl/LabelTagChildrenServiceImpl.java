package cn.aircas.airproject.service.impl;

import cn.aircas.airproject.entity.domain.LabelTagChildren;
import cn.aircas.airproject.service.LabelTagService;
import cn.aircas.airproject.utils.SQLiteUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.List;



@Service("LabelTagChildren-SERVICE")
public class LabelTagChildrenServiceImpl implements LabelTagService<LabelTagChildren> {

    @Autowired
    private HttpServletRequest request;


    @Override
    public boolean executeSql(String createSql) {
        try {
            SQLiteUtils.executeSql(createSql, request);
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }


    @Override
    public int insert(LabelTagChildren tagChildren) {
        try {
            return SQLiteUtils.insert(tagChildren, SQLiteUtils.childrenTabelName, request);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return -1;
    }


    @Override
    public List<Object> queryList(Class clazz, Object params) {
        try {
            List<Object> queryList = SQLiteUtils.queryList(clazz, SQLiteUtils.childrenTabelName, params, request);
            if (queryList != null && queryList.size() != 0) {
                return queryList;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }


    @Override
    public boolean updateById(LabelTagChildren tagChildren) {
        try {
            SQLiteUtils.updateById(tagChildren, SQLiteUtils.childrenTabelName, request);
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }


    @Override
    public boolean deleteById(int deleteId) {
        try {
            SQLiteUtils.deleteById(SQLiteUtils.childrenTabelName, deleteId, request);
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }



    /*@Override
    public boolean insert(LabelTagDto tagDto, String tableName) {
        try {
            LabelTagParent tagParent = tagDto.getTagParent();
            LabelTagChildren tagChildren = tagDto.getTagChildren();
            if (tagParent != null && tagChildren != null) {
                String tagChildrens = tagParent.getTag_childrens();
                if (StringUtils.isNotBlank(tagChildrens)) {
                    List<Integer> childrenIds = Arrays.stream(tagChildrens.split(",")).map(Integer::parseInt).collect(Collectors.toList());
                    childrenIds.add(tagChildren.getId());
                    tagParent.setTag_childrens(childrenIds.toString().replace("[", "").replace("]", ""));
                } else {
                    tagParent.setTag_childrens(String.valueOf(tagChildren.getId()));
                }
                // 如果父级标签的ID不为-1，则认为只添加子标签；否则，同时添加父子标签
                if (tagParent.getId() == -1) {
                    SQLiteUtils.insert(tagChildren, SQLiteUtils.parentTabelName);
                    SQLiteUtils.insert(tagChildren, SQLiteUtils.childrenTabelName);
                } else {

                }
                tagChildren.setParentId(tagParent.getId());
            }
            if (tagParent != null && tagChildren == null) {
                tagParent.setTag_childrens();
            }

            SQLiteUtils.insert(object, tableName);
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }*/

}
