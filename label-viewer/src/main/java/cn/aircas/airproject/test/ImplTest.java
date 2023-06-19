package cn.aircas.airproject.test;

import cn.aircas.airproject.utils.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.Base64Utils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class ImplTest {
    public static void main(String[] args) throws IOException {
        //testBase64Convert();
    }
    private static HttpClient httpClient = new DefaultHttpClient();
    private static HttpHost httpHost;
    private static HttpPost httpPost;
    private static HttpResponse response;
    private static HttpEntity httpEntity;
    private static String postResult = "";
    private static String url = "http://localhost:8003/file-process/rmtlabelProject/upload";

    //http://192.168.9.64:9101/file-process/rmtlabelProject/upload

    private void testUpload() {

    }

    public static void testBase64Convert() throws IOException {
        File file = new File("C:/Users/dell/Desktop/image/1815.jpg");
        byte[] bytesByFile = FileUtils.getBytesByFile(file);
        String s = Base64Utils.encodeToString(bytesByFile);
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decode = decoder.decode(s);
        FileOutputStream outputStream1 = new FileOutputStream("C:/Users/dell/Desktop/image/1815111.jpg");
        outputStream1.write(decode);
        System.out.println("文件转换成功");
    }

    private static void test() throws IOException {

        StringJoiner stringJoiner = new StringJoiner(" ", " ", " ");
        stringJoiner.add("a");
        stringJoiner.add("c");
        stringJoiner.add("b");
        System.out.println(stringJoiner);
        stringJoiner.setEmptyValue("");
        System.out.println("sjfdsklfjslkf:" + stringJoiner);


        Path path = Paths.get("C:\\Users\\dell\\Desktop\\docker-compose.txt");
        File file = new File(path.toUri());
        FileInputStream inputStream = new FileInputStream(file);
        MockMultipartFile multipartFile = new MockMultipartFile(file.getName(), inputStream);

        httpPost = new HttpPost(url);

        /*// UrlEncodeFromEntity方式
        List<NameValuePair> paramsList = new ArrayList<>();
        paramsList.add(new BasicNameValuePair("host", "192.168.9.64"));
        paramsList.add(new BasicNameValuePair("port", "22"));
        paramsList.add(new BasicNameValuePair("userName","iecas"));
        paramsList.add(new BasicNameValuePair("passWord","123456"));
        paramsList.add(new BasicNameValuePair("destPath","/mnt/sdb/home/iecas/airproject/label-viewer"));
        paramsList.add(new BasicNameValuePair("srcFile", multipartFile.getResource().toString()));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramsList);
        httpPost.setEntity(entity);*/

        // Json方式
        JSONObject params = new JSONObject();
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("host", "192.168.9.64");
        paramsMap.put("port", "22");
        paramsMap.put("userName","iecas");
        paramsMap.put("passWord","123456");
        params.put("params", paramsMap);
        params.put("destPath","/mnt/sdb/home/iecas/airproject/label-viewer");
        params.put("srcFile", multipartFile.getResource());
        StringEntity entity = new StringEntity(params.toString());
        entity.setContentType("application/json");
        httpPost.setEntity(entity);

        response = httpClient.execute(httpPost);
        String s = EntityUtils.toString(response.getEntity());
        System.out.println(s);

        httpPost.releaseConnection();

    }
}
