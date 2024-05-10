package cn.aircas.airproject.entity.emun;


/**
 * 标注文件的保存类型：xml、图片还是shp文件
 */
public enum LabelFileType {

    XML(0), PIC(1),SHP(2);

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
