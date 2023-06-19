package cn.aircas.airproject.entity.common;

import cn.aircas.airproject.entity.emun.ResultCode;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class CommonResult<T> {
    private T data;

    private int code;

    private String message;

    private static final long serialVersionUID = -4683516289108960739L;

    private void code(int httpStatus){
        this.code = httpStatus;
    }

    public CommonResult<T> message(String message){
        this.message = message;
        return this;
    }

    public CommonResult<T> data(T data){
        this.data = data;
        return this;
    }

    public CommonResult<T> success(ResultCode resultCode){
        code(resultCode.getCode());
        return this;
    }

    public CommonResult<T> fail(ResultCode resultCode){
        code(resultCode.getCode());
        return this;
    }

    public CommonResult<T> setCode(ResultCode resultCode){
        //if (code.toLowerCase().equals("error"))
            code(resultCode.getCode());
        return this;
    }

    public CommonResult<T> setCode(int httpStatus){
        this.code = httpStatus;
        return this;
    }
}
