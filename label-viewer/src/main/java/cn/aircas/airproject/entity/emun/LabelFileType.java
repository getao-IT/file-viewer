package cn.aircas.airproject.entity.emun;


/**
 * 针对打开标注文件中对应的文件类型XML，图片，SHP三种类型
 */

public enum LabelFileType {

    XML(0), IMG(1), SHP(2);

    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    LabelFileType(int value) {
        this.value = value;
    }
}
