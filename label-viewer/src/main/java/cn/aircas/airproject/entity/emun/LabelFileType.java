package cn.aircas.airproject.entity.emun;



public enum LabelFileType {

    XML(0), VIF(1);

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
