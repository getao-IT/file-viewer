package cn.aircas.airproject.entity.emun;

/**
 * @ClassName: LabelType
 * @Description 标注文件的分类：文件形式或者流形式
 * @Author yzhan
 * @Date 2024/5/27 16:48
 * @Version 1.0
 */


public enum LabelCategory {

    LABEL_FILE(0,"文件形式的标注信息"),
    LABEL_STREAM(1, "流形式的标注信息");


    private int code;
    private String desc;

    private LabelCategory(int code, String desc){
        this.code = code;
        this.desc = desc;
    }
}
