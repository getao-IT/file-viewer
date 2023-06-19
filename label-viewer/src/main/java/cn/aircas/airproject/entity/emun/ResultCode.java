package cn.aircas.airproject.entity.emun;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ResultCode {

    SUCCESS(0, "success"),
    FAIL(190000, "操作失败！"),
    FAIL_GETFILECONTENT(190001, "获取文件内容失败！"),
    FAIL_BINARY(190002, "不支持对二进制文件的编辑！"),
    FAIL_NO_SUCH_FILE(190003, "路径不存在！"),
    FAIL_PERMISSION_DENIED(190004, "没有操作权限！");

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
