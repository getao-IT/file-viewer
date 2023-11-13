package cn.aircas.airproject.entity.emun;

public enum TaskStatus {

    WORKING(1), FINISH(2),FAIL(3);

    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    TaskStatus(int code) {
        this.code = code;
    }

    public static void main(String[] args) {
        System.out.println(TaskStatus.FINISH);
    }
}
