package cn.aircas.airproject.entity.emun;

/**
 * xml格式枚举类
 */
public enum LabelXMLFormat {
    AIRCAS(0), VOC(1), VIF(2);

    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    LabelXMLFormat(int code) {
        this.code = code;
    }
}
