package cn.aircas.airproject.entity.emun;

public enum LabelPointType {
    GEODEGREE(0),PIXEL(1),PROJECTION(2);
    private int value;
    LabelPointType(int value){
        this.value = value;
    }
    public int getValue(){
        return this.value;
    }

    public void setValue(int value){
        this.value = value;
    }
}
