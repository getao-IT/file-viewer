package cn.aircas.airproject.utils;

import cn.aircas.airproject.entity.common.CommonResult;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;



@Slf4j
@Service
public class UserService {

    @Value("${value.api.user-info}")
    private String userInfoApi;

    @Autowired
    private RestTemplate restTemplate;


    /**
     * 根据Token获取用户信息
     * @param token
     * @return
     * @throws ResourceAccessException
     */
    public CommonResult<JSONObject> getUserInfoByToken(String token) throws ResourceAccessException {
        log.info("开始验证token");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("token",token);
        HttpEntity<String> httpEntity = new HttpEntity<>(null,httpHeaders);

        CommonResult result = new CommonResult();

        try {
            JSONObject jsonResult = restTemplate.exchange(userInfoApi, HttpMethod.GET,httpEntity, JSONObject.class).getBody();
            result.setCode(jsonResult.getInteger("code"));
            result.setData(jsonResult.getJSONObject("data"));
            result.setMessage(jsonResult.getString("msg"));
        }catch (ResourceAccessException e){
            log.error("访问用户信息接口：{} 超时",userInfoApi);
            throw new ResourceAccessException("关联服务访问出错");
        }
        return result;
    }


    /**
     * 是否为管理员
     * @param token
     * @return
     */
    public boolean isAdmin(String token) {
        JSONObject userInfo = this.getUserInfoByToken(token).getData();
        return userInfo.getBoolean("is_admin");
    }
}
