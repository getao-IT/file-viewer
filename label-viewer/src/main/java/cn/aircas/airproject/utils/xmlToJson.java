package cn.aircas.airproject.utils;

import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/xml")
public class xmlToJson {

    @PostMapping(value = "/toJson")
    public String xmlToJson(MultipartFile multipartFile) throws IOException {
        InputStream inputStream = multipartFile.getInputStream();
        XMLSerializer xmlSerializer = new XMLSerializer();
        JSONObject jsonObject = new JSONObject();
        String jsonStr = "{\"annotation" + "\":"+xmlSerializer.readFromStream(inputStream).toString()+"}";
        jsonObject.put(multipartFile.getName(),jsonStr);
        return jsonStr;
    }


}
