package cn.aircas.airproject.service.impl;

import cn.aircas.airproject.entity.domain.LabelTagChildren;
import cn.aircas.airproject.entity.domain.LabelTagParent;
import cn.aircas.airproject.entity.dto.LabelTagDto;
import cn.aircas.airproject.service.LabelTagService;
import cn.aircas.airproject.utils.SQLiteUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



@Service("LabelTagParent-SERVICE")
public class LabelTagParentServiceImpl implements LabelTagService<LabelTagParent> {

    @Autowired
    private LabelTagChildrenServiceImpl childrenService;


    @Override
    public boolean createTable(String createSql) {
        try {
            SQLiteUtils.createTable(createSql);
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }


    @Override
    public boolean insert(LabelTagParent tagParent) {
        try {
            SQLiteUtils.insert(tagParent, SQLiteUtils.parentTabelName);
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }


    @Override
    public List<Object> queryList(Class clazz) {
        try {
            List<Object> queryList = SQLiteUtils.queryList(clazz, SQLiteUtils.parentTabelName);
            if (queryList != null && queryList.size() != 0) {
                return queryList;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }


    @Override
    public boolean updateById(LabelTagParent tagParent) {
        try {
            SQLiteUtils.updateById(tagParent, SQLiteUtils.parentTabelName);
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }


    @Override
    public boolean deleteById(int deleteId) {
        try {
            SQLiteUtils.deleteById(SQLiteUtils.parentTabelName, deleteId);
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }


    public List<LabelTagDto> listLabelTag() {
        List<LabelTagDto> result = new ArrayList<>();
        try {
            List<Object> tagParents = SQLiteUtils.queryList(LabelTagParent.class, SQLiteUtils.parentTabelName);
            for (Object tagParent : tagParents) {
                LabelTagParent tagp = (LabelTagParent) tagParent;
                String[] cols = new String[]{"parent_id"};
                Object[] values = new Object[]{tagp.getId()};
                List<Object> childrens = SQLiteUtils.queryListByCol(LabelTagChildren.class, SQLiteUtils.childrenTabelName, cols, values);
                LabelTagDto labelTagDto = new LabelTagDto();
                BeanUtils.copyProperties(tagp, labelTagDto);
                labelTagDto.setTagChildrenValues(childrens);
                result.add(labelTagDto);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

}
