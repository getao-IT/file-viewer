package cn.aircas.airproject.entity.emun;


/**
 * 针对打开标注文件中对应的文件类型XML，图片，SHP三种类型
 */

public enum LabelFileType {

    XML(0), IMG(1), SHP(2);

    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    LabelFileType(int code) {
        this.code = code;
    }
}
