package cn.aircas.airproject.entity.emun;

/**
 * @ClassName: LabelFileFormat
 * @Description 对应读取任意位置标注文件功能中xml文件类型对应的两种格式：Aircas和VOC
 * @Author yzhan
 * @Date 2024/5/24 8:17
 * @Version 1.0
 */


public enum LabelFileFormat {

    AIRCAS(0), VOC(1), VIF(2);

    private int code;

    private LabelFileFormat(int code){
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
