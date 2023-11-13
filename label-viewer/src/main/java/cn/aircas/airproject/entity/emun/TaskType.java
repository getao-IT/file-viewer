package cn.aircas.airproject.entity.emun;

import lombok.Data;


public enum TaskType {

    OVERVIEWS(1),SLICE(2),CONVERTER(3),GREY(4);

    private int code;

    TaskType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
