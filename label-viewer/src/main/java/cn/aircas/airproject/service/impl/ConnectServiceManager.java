package cn.aircas.airproject.service.impl;

import cn.aircas.airproject.entity.common.CommonResult;
import cn.aircas.airproject.entity.emun.ResultCode;
import org.springframework.stereotype.Service;

import java.io.Serializable;


@Service
public class ConnectServiceManager implements Serializable {

    private static final long serialVersionUID = 5460992569020924838L;

    public static int serviceId;

    /**
     * 用于连接到服务
     * @param service_id
     * @return
     */
    public CommonResult<String> getService(int service_id) {
        String yamlContent = "swagger: '2.0'\n" +
                "info:\n" +
                "  description: 文件管理接口文档\n" +
                "  version: v0.0.1\n" +
                "  title: 文件管理接口文档\n" +
                "host: 'localhost:8003'\n" +
                "basePath: /file-process\n" +
                "tags:\n" +
                "  - name: label-project-controller\n" +
                "    description: 本地文件管理\n" +
                "  - name: remote-label-project-controller\n" +
                "    description: 远程文件管理\n" +
                "paths:\n" +
                "  /labelProject/copyFileAndFolder:\n" +
                "    post:\n" +
                "      tags:\n" +
                "        - label-project-controller\n" +
                "      summary: 复制文件\n" +
                "      operationId: copyFileAndFolderUsingPOST\n" +
                "      consumes:\n" +
                "        - application/json\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - name: destPath\n" +
                "          in: query\n" +
                "          description: 复制后的根路径后的路径\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: srcPath\n" +
                "          in: query\n" +
                "          description: 要复制的根路径后的路径\n" +
                "          required: true\n" +
                "          type: string\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "        '201':\n" +
                "          description: Created\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "        '404':\n" +
                "          description: Not Found\n" +
                "      deprecated: false\n" +
                "  /labelProject/createFile:\n" +
                "    post:\n" +
                "      tags:\n" +
                "        - label-project-controller\n" +
                "      summary: 创建文件\n" +
                "      operationId: createFileUsingPOST\n" +
                "      consumes:\n" +
                "        - application/json\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - name: path\n" +
                "          in: query\n" +
                "          description: 创建文件路径\n" +
                "          required: true\n" +
                "          type: string\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "        '201':\n" +
                "          description: Created\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "        '404':\n" +
                "          description: Not Found\n" +
                "      deprecated: false\n" +
                "  /labelProject/createFolder:\n" +
                "    post:\n" +
                "      tags:\n" +
                "        - label-project-controller\n" +
                "      summary: 创建文件夹\n" +
                "      operationId: createFolderUsingPOST\n" +
                "      consumes:\n" +
                "        - application/json\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - name: path\n" +
                "          in: query\n" +
                "          description: 创建文件路径\n" +
                "          required: true\n" +
                "          type: string\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "        '201':\n" +
                "          description: Created\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "        '404':\n" +
                "          description: Not Found\n" +
                "      deprecated: false\n" +
                "  /labelProject/deleteFileOrFolder:\n" +
                "    delete:\n" +
                "      tags:\n" +
                "        - label-project-controller\n" +
                "      summary: 删除文件或文件夹\n" +
                "      operationId: deleteFileOrFolderUsingDELETE\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - name: srcPath\n" +
                "          in: query\n" +
                "          description: 根路径后的目标路径\n" +
                "          required: true\n" +
                "          type: string\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "        '204':\n" +
                "          description: No Content\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "      deprecated: false\n" +
                "  /labelProject/file:\n" +
                "    get:\n" +
                "      tags:\n" +
                "        - label-project-controller\n" +
                "      summary: 获取文件夹下的文件\n" +
                "      operationId: getFileUsingGET\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - name: path\n" +
                "          in: query\n" +
                "          description: 路径\n" +
                "          required: true\n" +
                "          type: string\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "        '404':\n" +
                "          description: Not Found\n" +
                "      deprecated: false\n" +
                "  /labelProject/fileAndFolder:\n" +
                "    get:\n" +
                "      tags:\n" +
                "        - label-project-controller\n" +
                "      summary: 获取文件夹下的文件和子文件夹\n" +
                "      operationId: getFileAndFoderListUsingGET\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - name: path\n" +
                "          in: query\n" +
                "          description: 路径\n" +
                "          required: true\n" +
                "          type: string\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "        '404':\n" +
                "          description: Not Found\n" +
                "      deprecated: false\n" +
                "  /labelProject/fileContent:\n" +
                "    get:\n" +
                "      tags:\n" +
                "        - label-project-controller\n" +
                "      summary: 获取文件内容\n" +
                "      operationId: getFileContentUsingGET\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - name: path\n" +
                "          in: query\n" +
                "          description: 文件路径\n" +
                "          required: true\n" +
                "          type: string\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "        '404':\n" +
                "          description: Not Found\n" +
                "      deprecated: false\n" +
                "  /labelProject/fileInfo:\n" +
                "    get:\n" +
                "      tags:\n" +
                "        - label-project-controller\n" +
                "      summary: 获取属性信息\n" +
                "      operationId: getFileInfoUsingGET\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - name: path\n" +
                "          in: query\n" +
                "          description: 跟路径后的目标文件路径\n" +
                "          required: true\n" +
                "          type: string\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "        '404':\n" +
                "          description: Not Found\n" +
                "      deprecated: false\n" +
                "  /labelProject/fileRename:\n" +
                "    put:\n" +
                "      tags:\n" +
                "        - label-project-controller\n" +
                "      summary: 重命名文件或文件夹\n" +
                "      operationId: fileRenameUsingPUT\n" +
                "      consumes:\n" +
                "        - application/json\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - name: newName\n" +
                "          in: query\n" +
                "          description: 根路径后的新目标路径\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: oldName\n" +
                "          in: query\n" +
                "          description: 根路径后的原目标路径\n" +
                "          required: true\n" +
                "          type: string\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "        '201':\n" +
                "          description: Created\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "        '404':\n" +
                "          description: Not Found\n" +
                "      deprecated: false\n" +
                "  /labelProject/folder:\n" +
                "    get:\n" +
                "      tags:\n" +
                "        - label-project-controller\n" +
                "      summary: 获取文件夹下的子文件夹\n" +
                "      operationId: getFolderListUsingGET\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - name: path\n" +
                "          in: query\n" +
                "          description: 路径\n" +
                "          required: true\n" +
                "          type: string\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "        '404':\n" +
                "          description: Not Found\n" +
                "      deprecated: false\n" +
                "  /labelProject/imageInfo:\n" +
                "    get:\n" +
                "      tags:\n" +
                "        - label-project-controller\n" +
                "      summary: 解析图片\n" +
                "      operationId: getImageInfoUsingGET\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - name: path\n" +
                "          in: query\n" +
                "          description: 路径\n" +
                "          required: true\n" +
                "          type: string\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "        '404':\n" +
                "          description: Not Found\n" +
                "      deprecated: false\n" +
                "  /labelProject/importTag:\n" +
                "    get:\n" +
                "      tags:\n" +
                "        - label-project-controller\n" +
                "      summary: importTag\n" +
                "      operationId: importTagUsingGET\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - name: tagFilePath\n" +
                "          in: query\n" +
                "          description: 路径\n" +
                "          required: true\n" +
                "          type: string\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "        '404':\n" +
                "          description: Not Found\n" +
                "      deprecated: false\n" +
                "  /labelProject/saveLabel:\n" +
                "    post:\n" +
                "      tags:\n" +
                "        - label-project-controller\n" +
                "      summary: 保存Label\n" +
                "      operationId: saveLabelUsingPOST\n" +
                "      consumes:\n" +
                "        - application/json\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - in: body\n" +
                "          name: saveLabelRequest\n" +
                "          required: true\n" +
                "          schema:\n" +
                "            $ref: '#/definitions/SaveLabelRequest'\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "        '201':\n" +
                "          description: Created\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "        '404':\n" +
                "          description: Not Found\n" +
                "      deprecated: false\n" +
                "  /labelProject/toJson:\n" +
                "    post:\n" +
                "      tags:\n" +
                "        - label-project-controller\n" +
                "      summary: Xml转换为Json\n" +
                "      operationId: xmlToJsonUsingPOST\n" +
                "      consumes:\n" +
                "        - multipart/form-data\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - name: multipartFile\n" +
                "          in: query\n" +
                "          required: true\n" +
                "          type: string\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "          schema:\n" +
                "            type: string\n" +
                "        '201':\n" +
                "          description: Created\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "        '404':\n" +
                "          description: Not Found\n" +
                "      deprecated: false\n" +
                "  /labelProject/uploadFile:\n" +
                "    post:\n" +
                "      tags:\n" +
                "        - label-project-controller\n" +
                "      summary: 上传文件\n" +
                "      operationId: uploadFileUsingPOST\n" +
                "      consumes:\n" +
                "        - multipart/form-data\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - name: file\n" +
                "          in: query\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: imagePath\n" +
                "          in: query\n" +
                "          description: 路径\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: labelPointType\n" +
                "          in: query\n" +
                "          required: true\n" +
                "          type: string\n" +
                "          enum:\n" +
                "            - GEODEGREE\n" +
                "            - PIXEL\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "        '201':\n" +
                "          description: Created\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "        '404':\n" +
                "          description: Not Found\n" +
                "      deprecated: false\n" +
                "  /labelProject/viwXmlFile:\n" +
                "    post:\n" +
                "      tags:\n" +
                "        - label-project-controller\n" +
                "      summary: 从服务器打开xml文件\n" +
                "      operationId: viewXmlFileUsingPOST\n" +
                "      consumes:\n" +
                "        - application/json\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - name: imagePath\n" +
                "          in: query\n" +
                "          description: iamge路径\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: labelPointType\n" +
                "          in: query\n" +
                "          required: true\n" +
                "          type: string\n" +
                "          enum:\n" +
                "            - GEODEGREE\n" +
                "            - PIXEL\n" +
                "        - name: xmlPath\n" +
                "          in: query\n" +
                "          description: xml路径\n" +
                "          required: true\n" +
                "          type: string\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "        '201':\n" +
                "          description: Created\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "        '404':\n" +
                "          description: Not Found\n" +
                "      deprecated: false\n" +
                "  /labelProject/writeFile:\n" +
                "    put:\n" +
                "      tags:\n" +
                "        - label-project-controller\n" +
                "      summary: 将内容写入文件\n" +
                "      operationId: writeFileUsingPUT\n" +
                "      consumes:\n" +
                "        - application/json\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - in: body\n" +
                "          name: fileInfo\n" +
                "          description: 根路径后的目标文件路径\\写入内容\\是否追加\n" +
                "          required: true\n" +
                "          schema:\n" +
                "            $ref: '#/definitions/FileInfo'\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "        '201':\n" +
                "          description: Created\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "        '404':\n" +
                "          description: Not Found\n" +
                "      deprecated: false\n" +
                "  /labelProject/download:\n" +
                "        post:\n" +
                "          tags:\n" +
                "            - label-project-controller\n" +
                "          summary: 下载文件\n" +
                "          operationId: downLoadUsingPOST\n" +
                "          consumes:\n" +
                "            - application/json\n" +
                "          produces:\n" +
                "            - '*/*'\n" +
                "          parameters:\n" +
                "            - name: src_file_path\n" +
                "              in: query\n" +
                "              description: 下载目标路径\n" +
                "              required: true\n" +
                "              type: string\n" +
                "          responses:\n" +
                "            '200':\n" +
                "              description: OK\n" +
                "            '201':\n" +
                "              description: Created\n" +
                "            '401':\n" +
                "              description: Unauthorized\n" +
                "            '403':\n" +
                "              description: Forbidden\n" +
                "            '404':\n" +
                "              description: Not Found\n" +
                "          deprecated: false\n" +
                "  /labelProject/upload:\n" +
                "    post:\n" +
                "      tags:\n" +
                "        - label-project-controller\n" +
                "      summary: 上传文件到服务器\n" +
                "      operationId: uploadUsingPOST\n" +
                "      consumes:\n" +
                "        - multipart/form-data\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - name: destPath\n" +
                "          in: query\n" +
                "          description: 服务器中的路径\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: srcFile\n" +
                "          in: query\n" +
                "          description: 源文件\n" +
                "          required: true\n" +
                "          type: string\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "        '201':\n" +
                "          description: Created\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "        '404':\n" +
                "          description: Not Found\n" +
                "      deprecated: false\n" +
                "  /rmtlabelProject/copyFileAndFolder:\n" +
                "    post:\n" +
                "      tags:\n" +
                "        - remote-label-project-controller\n" +
                "      summary: 复制文件或文件夹\n" +
                "      operationId: copyFileAndFolderRUsingPOST\n" +
                "      consumes:\n" +
                "        - multipart/form-data\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - name: srcPath\n" +
                "          in: formData\n" +
                "          description: 要复制的文件或文件夹路径 + 文件名 / 文件夹名\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: destPath\n" +
                "          in: formData\n" +
                "          description: 复制到的目标路径（一定要是已存在的路径）\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: host\n" +
                "          in: formData\n" +
                "          description: 目标主机IP\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: port\n" +
                "          in: formData\n" +
                "          description: 端口号（22即可）\n" +
                "          required: true\n" +
                "          type: integer\n" +
                "          format: int32\n" +
                "        - name: userName\n" +
                "          in: formData\n" +
                "          description: 用户名\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: passWord\n" +
                "          in: formData\n" +
                "          description: 密码\n" +
                "          required: true\n" +
                "          type: string\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "          schema:\n" +
                "            $ref: '#/definitions/CommonResult-List-FileAndFolder'\n" +
                "        '201':\n" +
                "          description: Created\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "        '404':\n" +
                "          description: Not Found\n" +
                "      deprecated: false\n" +
                "  /rmtlabelProject/createFile:\n" +
                "    post:\n" +
                "      tags:\n" +
                "        - remote-label-project-controller\n" +
                "      summary: 创建文件\n" +
                "      operationId: createFileUsingPOST_1\n" +
                "      consumes:\n" +
                "        - multipart/form-data\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - name: path\n" +
                "          in: formData\n" +
                "          description: 创建文件路径 + 文件名\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: host\n" +
                "          in: formData\n" +
                "          description: 目标主机IP\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: port\n" +
                "          in: formData\n" +
                "          description: 端口号（22即可）\n" +
                "          required: true\n" +
                "          type: integer\n" +
                "          format: int32\n" +
                "        - name: userName\n" +
                "          in: formData\n" +
                "          description: 用户名\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: passWord\n" +
                "          in: formData\n" +
                "          description: 密码\n" +
                "          required: true\n" +
                "          type: string\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "          schema:\n" +
                "            $ref: '#/definitions/CommonResult-boolean'\n" +
                "        '201':\n" +
                "          description: Created\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "        '404':\n" +
                "          description: Not Found\n" +
                "      deprecated: false\n" +
                "  /rmtlabelProject/createFolder:\n" +
                "    post:\n" +
                "      tags:\n" +
                "        - remote-label-project-controller\n" +
                "      summary: 创建文件夹\n" +
                "      operationId: createFolderRUsingPOST\n" +
                "      consumes:\n" +
                "        - multipart/form-data\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - name: path\n" +
                "          in: formData\n" +
                "          description: 创建文件夹路径 + 文件夹名称\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: host\n" +
                "          in: formData\n" +
                "          description: 目标主机IP\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: port\n" +
                "          in: formData\n" +
                "          description: 端口号（22即可）\n" +
                "          required: true\n" +
                "          type: integer\n" +
                "          format: int32\n" +
                "        - name: userName\n" +
                "          in: formData\n" +
                "          description: 用户名\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: passWord\n" +
                "          in: formData\n" +
                "          description: 密码\n" +
                "          required: true\n" +
                "          type: string\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "          schema:\n" +
                "            $ref: '#/definitions/CommonResult-boolean'\n" +
                "        '201':\n" +
                "          description: Created\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "        '404':\n" +
                "          description: Not Found\n" +
                "      deprecated: false\n" +
                "  /rmtlabelProject/deleteFile:\n" +
                "    delete:\n" +
                "      tags:\n" +
                "        - remote-label-project-controller\n" +
                "      summary: 删除文件\n" +
                "      operationId: deleteFileUsingDELETE\n" +
                "      consumes:\n" +
                "        - multipart/form-data\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - name: srcPath\n" +
                "          in: formData\n" +
                "          description: 要删除的路径 + 文件名\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: host\n" +
                "          in: formData\n" +
                "          description: 目标主机IP\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: port\n" +
                "          in: formData\n" +
                "          description: 端口号（22即可）\n" +
                "          required: true\n" +
                "          type: integer\n" +
                "          format: int32\n" +
                "        - name: userName\n" +
                "          in: formData\n" +
                "          description: 用户名\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: passWord\n" +
                "          in: formData\n" +
                "          description: 密码\n" +
                "          required: true\n" +
                "          type: string\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "          schema:\n" +
                "            $ref: '#/definitions/CommonResult-boolean'\n" +
                "        '204':\n" +
                "          description: No Content\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "      deprecated: false\n" +
                "  /rmtlabelProject/deleteFolder:\n" +
                "    delete:\n" +
                "      tags:\n" +
                "        - remote-label-project-controller\n" +
                "      summary: 删除文件夹\n" +
                "      operationId: deleteFolderUsingDELETE\n" +
                "      consumes:\n" +
                "        - multipart/form-data\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - name: srcPath\n" +
                "          in: formData\n" +
                "          description: 要删除的路径 + 文件夹名\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: host\n" +
                "          in: formData\n" +
                "          description: 目标主机IP\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: port\n" +
                "          in: formData\n" +
                "          description: 端口号（22即可）\n" +
                "          required: true\n" +
                "          type: integer\n" +
                "          format: int32\n" +
                "        - name: userName\n" +
                "          in: formData\n" +
                "          description: 用户名\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: passWord\n" +
                "          in: formData\n" +
                "          description: 密码\n" +
                "          required: true\n" +
                "          type: string\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "          schema:\n" +
                "            $ref: '#/definitions/CommonResult-boolean'\n" +
                "        '204':\n" +
                "          description: No Content\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "      deprecated: false\n" +
                "  /rmtlabelProject/download:\n" +
                "    post:\n" +
                "      tags:\n" +
                "        - remote-label-project-controller\n" +
                "      summary: 下载文件\n" +
                "      operationId: downLoadRUsingPOST\n" +
                "      consumes:\n" +
                "        - multipart/form-data\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - name: src_file_path\n" +
                "          in: formData\n" +
                "          description: 下载目标路径\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: host\n" +
                "          in: formData\n" +
                "          description: 目标主机IP\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: port\n" +
                "          in: formData\n" +
                "          description: 端口号（22即可）\n" +
                "          required: true\n" +
                "          type: integer\n" +
                "          format: int32\n" +
                "        - name: userName\n" +
                "          in: formData\n" +
                "          description: 用户名\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: passWord\n" +
                "          in: formData\n" +
                "          description: 密码\n" +
                "          required: true\n" +
                "          type: string\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "          schema:\n" +
                "            $ref: '#/definitions/CommonResult-string'\n" +
                "        '201':\n" +
                "          description: Created\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "        '404':\n" +
                "          description: Not Found\n" +
                "      deprecated: false\n" +
                "  /rmtlabelProject/fileAndFolder:\n" +
                "    get:\n" +
                "      tags:\n" +
                "        - remote-label-project-controller\n" +
                "      summary: 获取文件夹以及子文件\n" +
                "      operationId: getFileAndFoderListFromRUsingGET\n" +
                "      consumes:\n" +
                "        - multipart/form-data\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - name: path\n" +
                "          in: formData\n" +
                "          description: 要获取文件目录的路径\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: host\n" +
                "          in: formData\n" +
                "          description: 目标主机IP\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: port\n" +
                "          in: formData\n" +
                "          description: 端口号（22即可）\n" +
                "          required: true\n" +
                "          type: integer\n" +
                "          format: int32\n" +
                "        - name: userName\n" +
                "          in: formData\n" +
                "          description: 用户名\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: passWord\n" +
                "          in: formData\n" +
                "          description: 密码\n" +
                "          required: true\n" +
                "          type: string\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "          schema:\n" +
                "            $ref: '#/definitions/CommonResult-List-FileAndFolder'\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "        '404':\n" +
                "          description: Not Found\n" +
                "      deprecated: false\n" +
                "  /rmtlabelProject/fileContent:\n" +
                "    get:\n" +
                "      tags:\n" +
                "        - remote-label-project-controller\n" +
                "      summary: 获取文件内容\n" +
                "      operationId: getFileContentRUsingGET\n" +
                "      consumes:\n" +
                "        - multipart/form-data\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - name: path\n" +
                "          in: formData\n" +
                "          description: 文件路径\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: host\n" +
                "          in: formData\n" +
                "          description: 目标主机IP\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: port\n" +
                "          in: formData\n" +
                "          description: 端口号（22即可）\n" +
                "          required: true\n" +
                "          type: integer\n" +
                "          format: int32\n" +
                "        - name: userName\n" +
                "          in: formData\n" +
                "          description: 用户名\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: passWord\n" +
                "          in: formData\n" +
                "          description: 密码\n" +
                "          required: true\n" +
                "          type: string\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "          schema:\n" +
                "            $ref: '#/definitions/CommonResult-string'\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "        '404':\n" +
                "          description: Not Found\n" +
                "      deprecated: false\n" +
                "  /rmtlabelProject/fileInfo:\n" +
                "    get:\n" +
                "      tags:\n" +
                "        - remote-label-project-controller\n" +
                "      summary: 获取属性信息\n" +
                "      operationId: getFileInfoUsingGET_1\n" +
                "      consumes:\n" +
                "        - multipart/form-data\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - name: path\n" +
                "          in: formData\n" +
                "          description: 目标文件或者文件夹路径\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: host\n" +
                "          in: formData\n" +
                "          description: 目标主机IP\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: port\n" +
                "          in: formData\n" +
                "          description: 端口号（22即可）\n" +
                "          required: true\n" +
                "          type: integer\n" +
                "          format: int32\n" +
                "        - name: userName\n" +
                "          in: formData\n" +
                "          description: 用户名\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: passWord\n" +
                "          in: formData\n" +
                "          description: 密码\n" +
                "          required: true\n" +
                "          type: string\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "          schema:\n" +
                "            $ref: '#/definitions/CommonResult-FileInfo'\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "        '404':\n" +
                "          description: Not Found\n" +
                "      deprecated: false\n" +
                "  /rmtlabelProject/fileRename:\n" +
                "    put:\n" +
                "      tags:\n" +
                "        - remote-label-project-controller\n" +
                "      summary: 重命名文件或文件夹\n" +
                "      operationId: fileRenameUsingPUT_1\n" +
                "      consumes:\n" +
                "        - application/json\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - name: newName\n" +
                "          in: query\n" +
                "          description: 路径 + 更改后的名称，放在body中\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: oldName\n" +
                "          in: query\n" +
                "          description: 路径 + 原名称，放在body中\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: params\n" +
                "          in: query\n" +
                "          description: 远程连接信息，放在body中\n" +
                "          required: true\n" +
                "          type: string\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "          schema:\n" +
                "            $ref: '#/definitions/CommonResult-string'\n" +
                "        '201':\n" +
                "          description: Created\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "        '404':\n" +
                "          description: Not Found\n" +
                "      deprecated: false\n" +
                "  /rmtlabelProject/getUploadProgress:\n" +
                "    get:\n" +
                "      tags:\n" +
                "        - remote-label-project-controller\n" +
                "      summary: 获取文件上传进度\n" +
                "      operationId: getUploadProgressUsingGET\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "          schema:\n" +
                "            $ref: '#/definitions/CommonResult-ProgressSingleTon'\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "        '404':\n" +
                "          description: Not Found\n" +
                "      deprecated: false\n" +
                "  /rmtlabelProject/upload:\n" +
                "    post:\n" +
                "      tags:\n" +
                "        - remote-label-project-controller\n" +
                "      summary: 上传文件到服务器，并监听上传进度\n" +
                "      operationId: uploadRUsingPOST\n" +
                "      consumes:\n" +
                "        - multipart/form-data\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - name: srcFile\n" +
                "          in: formData\n" +
                "          description: 上传的文件\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: destPath\n" +
                "          in: query\n" +
                "          description: 服务器中的路径，包含上传文件名\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: host\n" +
                "          in: formData\n" +
                "          description: 目标主机IP\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: port\n" +
                "          in: formData\n" +
                "          description: 端口号（22即可）\n" +
                "          required: true\n" +
                "          type: integer\n" +
                "          format: int32\n" +
                "        - name: userName\n" +
                "          in: formData\n" +
                "          description: 用户名\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: passWord\n" +
                "          in: formData\n" +
                "          description: 密码\n" +
                "          required: true\n" +
                "          type: string\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "          schema:\n" +
                "            $ref: '#/definitions/CommonResult-ProgressInfo'\n" +
                "        '201':\n" +
                "          description: Created\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "        '404':\n" +
                "          description: Not Found\n" +
                "      deprecated: false\n" +
                "  /rmtlabelProject/writeFile:\n" +
                "    put:\n" +
                "      tags:\n" +
                "        - remote-label-project-controller\n" +
                "      summary: 将内容写入文件\n" +
                "      operationId: writeFileRUsingPUT\n" +
                "      consumes:\n" +
                "        - application/json\n" +
                "        - multipart/form-data\n" +
                "      produces:\n" +
                "        - '*/*'\n" +
                "      parameters:\n" +
                "        - name: fileInfo\n" +
                "          in: formData\n" +
                "          description: 根路径后的目标文件路径\\写入内容\\是否追加，body中(\"path：路径+文件名 content：内容 append：真\")\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: host\n" +
                "          in: formData\n" +
                "          description: 目标主机IP\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: port\n" +
                "          in: formData\n" +
                "          description: 端口号（22即可）\n" +
                "          required: true\n" +
                "          type: integer\n" +
                "          format: int32\n" +
                "        - name: userName\n" +
                "          in: formData\n" +
                "          description: 用户名\n" +
                "          required: true\n" +
                "          type: string\n" +
                "        - name: passWord\n" +
                "          in: formData\n" +
                "          description: 密码\n" +
                "          required: true\n" +
                "          type: string\n" +
                "      responses:\n" +
                "        '200':\n" +
                "          description: OK\n" +
                "          schema:\n" +
                "            $ref: '#/definitions/CommonResult-string'\n" +
                "        '201':\n" +
                "          description: Created\n" +
                "        '401':\n" +
                "          description: Unauthorized\n" +
                "        '403':\n" +
                "          description: Forbidden\n" +
                "        '404':\n" +
                "          description: Not Found\n" +
                "      deprecated: false\n" +
                "definitions:\n" +
                "  CommonResult-FileInfo:\n" +
                "    type: object\n" +
                "    properties:\n" +
                "      code:\n" +
                "        type: string\n" +
                "        enum:\n" +
                "          - 0\n" +
                "          - 100 CONTINUE\n" +
                "          - 101 SWITCHING_PROTOCOLS\n" +
                "          - 102 PROCESSING\n" +
                "          - 103 CHECKPOINT\n" +
                "          - 200 OK\n" +
                "          - 201 CREATED\n" +
                "          - 202 ACCEPTED\n" +
                "          - 203 NON_AUTHORITATIVE_INFORMATION\n" +
                "          - 204 NO_CONTENT\n" +
                "          - 205 RESET_CONTENT\n" +
                "          - 206 PARTIAL_CONTENT\n" +
                "          - 207 MULTI_STATUS\n" +
                "          - 208 ALREADY_REPORTED\n" +
                "          - 226 IM_USED\n" +
                "          - 300 MULTIPLE_CHOICES\n" +
                "          - 301 MOVED_PERMANENTLY\n" +
                "          - 302 FOUND\n" +
                "          - 302 MOVED_TEMPORARILY\n" +
                "          - 303 SEE_OTHER\n" +
                "          - 304 NOT_MODIFIED\n" +
                "          - 305 USE_PROXY\n" +
                "          - 307 TEMPORARY_REDIRECT\n" +
                "          - 308 PERMANENT_REDIRECT\n" +
                "          - 400 BAD_REQUEST\n" +
                "          - 401 UNAUTHORIZED\n" +
                "          - 402 PAYMENT_REQUIRED\n" +
                "          - 403 FORBIDDEN\n" +
                "          - 404 NOT_FOUND\n" +
                "          - 405 METHOD_NOT_ALLOWED\n" +
                "          - 406 NOT_ACCEPTABLE\n" +
                "          - 407 PROXY_AUTHENTICATION_REQUIRED\n" +
                "          - 408 REQUEST_TIMEOUT\n" +
                "          - 409 CONFLICT\n" +
                "          - 410 GONE\n" +
                "          - 411 LENGTH_REQUIRED\n" +
                "          - 412 PRECONDITION_FAILED\n" +
                "          - 413 PAYLOAD_TOO_LARGE\n" +
                "          - 413 REQUEST_ENTITY_TOO_LARGE\n" +
                "          - 414 URI_TOO_LONG\n" +
                "          - 414 REQUEST_URI_TOO_LONG\n" +
                "          - 415 UNSUPPORTED_MEDIA_TYPE\n" +
                "          - 416 REQUESTED_RANGE_NOT_SATISFIABLE\n" +
                "          - 417 EXPECTATION_FAILED\n" +
                "          - 418 I_AM_A_TEAPOT\n" +
                "          - 419 INSUFFICIENT_SPACE_ON_RESOURCE\n" +
                "          - 420 METHOD_FAILURE\n" +
                "          - 421 DESTINATION_LOCKED\n" +
                "          - 422 UNPROCESSABLE_ENTITY\n" +
                "          - 423 LOCKED\n" +
                "          - 424 FAILED_DEPENDENCY\n" +
                "          - 426 UPGRADE_REQUIRED\n" +
                "          - 428 PRECONDITION_REQUIRED\n" +
                "          - 429 TOO_MANY_REQUESTS\n" +
                "          - 431 REQUEST_HEADER_FIELDS_TOO_LARGE\n" +
                "          - 451 UNAVAILABLE_FOR_LEGAL_REASONS\n" +
                "          - 500 INTERNAL_SERVER_ERROR\n" +
                "          - 501 NOT_IMPLEMENTED\n" +
                "          - 502 BAD_GATEWAY\n" +
                "          - 503 SERVICE_UNAVAILABLE\n" +
                "          - 504 GATEWAY_TIMEOUT\n" +
                "          - 505 HTTP_VERSION_NOT_SUPPORTED\n" +
                "          - 506 VARIANT_ALSO_NEGOTIATES\n" +
                "          - 507 INSUFFICIENT_STORAGE\n" +
                "          - 508 LOOP_DETECTED\n" +
                "          - 509 BANDWIDTH_LIMIT_EXCEEDED\n" +
                "          - 510 NOT_EXTENDED\n" +
                "          - 511 NETWORK_AUTHENTICATION_REQUIRED\n" +
                "      data:\n" +
                "        $ref: '#/definitions/FileInfo'\n" +
                "      message:\n" +
                "        type: string\n" +
                "    title: CommonResult«FileInfo»\n" +
                "  CommonResult-ProgressSingleTon:\n" +
                "    type: object\n" +
                "    properties:\n" +
                "      code:\n" +
                "        type: string\n" +
                "        enum:\n" +
                "          - 0\n" +
                "          - 200 OK\n" +
                "          - 300 MULTIPLE_CHOICES\n" +
                "          - 400 BAD_REQUEST\n" +
                "          - 401 UNAUTHORIZED\n" +
                "          - 402 PAYMENT_REQUIRED\n" +
                "          - 403 FORBIDDEN\n" +
                "          - 404 NOT_FOUND\n" +
                "          - 405 METHOD_NOT_ALLOWED\n" +
                "          - 500 INTERNAL_SERVER_ERROR\n" +
                "      data:\n" +
                "        $ref: '#/definitions/ProgressSingleTon'\n" +
                "      message:\n" +
                "        type: string\n" +
                "    title: CommonResult«ProgressSingleTon»\n" +
                "  CommonResult-ProgressInfo:\n" +
                "    type: object\n" +
                "    properties:\n" +
                "      code:\n" +
                "        type: string\n" +
                "        enum:\n" +
                "          - 0\n" +
                "          - 200 OK\n" +
                "          - 300 MULTIPLE_CHOICES\n" +
                "          - 400 BAD_REQUEST\n" +
                "          - 401 UNAUTHORIZED\n" +
                "          - 402 PAYMENT_REQUIRED\n" +
                "          - 403 FORBIDDEN\n" +
                "          - 404 NOT_FOUND\n" +
                "          - 405 METHOD_NOT_ALLOWED\n" +
                "          - 500 INTERNAL_SERVER_ERROR\n" +
                "      data:\n" +
                "        $ref: '#/definitions/ProgressInfo'\n" +
                "      message:\n" +
                "        type: string\n" +
                "        example: \"上传文件成功\"\n" +
                "    title: CommonResult«ProgressInfo»\n" +
                "  CommonResult«ImageInfo»:\n" +
                "    type: object\n" +
                "    properties:\n" +
                "      code:\n" +
                "        type: string\n" +
                "        enum:\n" +
                "          - 0\n" +
                "          - 200 OK\n" +
                "          - 300 MULTIPLE_CHOICES\n" +
                "          - 400 BAD_REQUEST\n" +
                "          - 401 UNAUTHORIZED\n" +
                "          - 402 PAYMENT_REQUIRED\n" +
                "          - 403 FORBIDDEN\n" +
                "          - 404 NOT_FOUND\n" +
                "          - 405 METHOD_NOT_ALLOWED\n" +
                "          - 500 INTERNAL_SERVER_ERROR\n" +
                "      data:\n" +
                "        $ref: '#/definitions/ImageInfo'\n" +
                "      message:\n" +
                "        type: string\n" +
                "    title: CommonResult«ImageInfo»\n" +
                "  CommonResult-List-FileAndFolder:\n" +
                "    type: object\n" +
                "    properties:\n" +
                "      code:\n" +
                "        type: string\n" +
                "        enum:\n" +
                "          - 0\n" +
                "          - 200 OK\n" +
                "          - 300 MULTIPLE_CHOICES\n" +
                "          - 400 BAD_REQUEST\n" +
                "          - 401 UNAUTHORIZED\n" +
                "          - 402 PAYMENT_REQUIRED\n" +
                "          - 403 FORBIDDEN\n" +
                "          - 404 NOT_FOUND\n" +
                "          - 405 METHOD_NOT_ALLOWED\n" +
                "          - 500 INTERNAL_SERVER_ERROR\n" +
                "      data:\n" +
                "        type: array\n" +
                "        items:\n" +
                "          $ref: '#/definitions/FileAndFolder'\n" +
                "      message:\n" +
                "        type: string\n" +
                "    title: CommonResult«List«FileAndFolder»»\n" +
                "  CommonResult«List«FilePac»»:\n" +
                "    type: object\n" +
                "    properties:\n" +
                "      code:\n" +
                "        type: string\n" +
                "        enum:\n" +
                "          - 0\n" +
                "          - 200 OK\n" +
                "          - 300 MULTIPLE_CHOICES\n" +
                "          - 400 BAD_REQUEST\n" +
                "          - 401 UNAUTHORIZED\n" +
                "          - 402 PAYMENT_REQUIRED\n" +
                "          - 403 FORBIDDEN\n" +
                "          - 404 NOT_FOUND\n" +
                "          - 405 METHOD_NOT_ALLOWED\n" +
                "          - 500 INTERNAL_SERVER_ERROR\n" +
                "      data:\n" +
                "        type: array\n" +
                "        items:\n" +
                "          $ref: '#/definitions/FilePac'\n" +
                "      message:\n" +
                "        type: string\n" +
                "    title: CommonResult«List«FilePac»»\n" +
                "  CommonResult«List«FolderPac»»:\n" +
                "    type: object\n" +
                "    properties:\n" +
                "      code:\n" +
                "        type: string\n" +
                "        enum:\n" +
                "          - 0\n" +
                "          - 200 OK\n" +
                "          - 300 MULTIPLE_CHOICES\n" +
                "          - 400 BAD_REQUEST\n" +
                "          - 401 UNAUTHORIZED\n" +
                "          - 402 PAYMENT_REQUIRED\n" +
                "          - 403 FORBIDDEN\n" +
                "          - 404 NOT_FOUND\n" +
                "          - 405 METHOD_NOT_ALLOWED\n" +
                "          - 500 INTERNAL_SERVER_ERROR\n" +
                "      data:\n" +
                "        type: array\n" +
                "        items:\n" +
                "          $ref: '#/definitions/FolderPac'\n" +
                "      message:\n" +
                "        type: string\n" +
                "    title: CommonResult«List«FolderPac»»\n" +
                "  CommonResult«List«string»»:\n" +
                "    type: object\n" +
                "    properties:\n" +
                "      code:\n" +
                "        type: string\n" +
                "        enum:\n" +
                "          - 0\n" +
                "          - 200 OK\n" +
                "          - 300 MULTIPLE_CHOICES\n" +
                "          - 400 BAD_REQUEST\n" +
                "          - 401 UNAUTHORIZED\n" +
                "          - 402 PAYMENT_REQUIRED\n" +
                "          - 403 FORBIDDEN\n" +
                "          - 404 NOT_FOUND\n" +
                "          - 405 METHOD_NOT_ALLOWED\n" +
                "          - 500 INTERNAL_SERVER_ERROR\n" +
                "      data:\n" +
                "        type: array\n" +
                "        items:\n" +
                "          type: string\n" +
                "      message:\n" +
                "        type: string\n" +
                "    title: CommonResult«List«string»»\n" +
                "  CommonResult-boolean:\n" +
                "    type: object\n" +
                "    properties:\n" +
                "      code:\n" +
                "        type: string\n" +
                "        enum:\n" +
                "          - 0\n" +
                "          - 200 OK\n" +
                "          - 300 MULTIPLE_CHOICES\n" +
                "          - 400 BAD_REQUEST\n" +
                "          - 401 UNAUTHORIZED\n" +
                "          - 402 PAYMENT_REQUIRED\n" +
                "          - 403 FORBIDDEN\n" +
                "          - 404 NOT_FOUND\n" +
                "          - 405 METHOD_NOT_ALLOWED\n" +
                "          - 500 INTERNAL_SERVER_ERROR\n" +
                "      data:\n" +
                "        type: boolean\n" +
                "      message:\n" +
                "        type: string\n" +
                "        example: success\n" +
                "    title: CommonResult«boolean»\n" +
                "  CommonResult-string:\n" +
                "    type: object\n" +
                "    properties:\n" +
                "      code:\n" +
                "        type: string\n" +
                "        enum:\n" +
                "          - 0\n" +
                "          - 200 OK\n" +
                "          - 300 MULTIPLE_CHOICES\n" +
                "          - 400 BAD_REQUEST\n" +
                "          - 401 UNAUTHORIZED\n" +
                "          - 402 PAYMENT_REQUIRED\n" +
                "          - 403 FORBIDDEN\n" +
                "          - 404 NOT_FOUND\n" +
                "          - 405 METHOD_NOT_ALLOWED\n" +
                "          - 500 INTERNAL_SERVER_ERROR\n" +
                "      data:\n" +
                "        type: string\n" +
                "      message:\n" +
                "        type: string\n" +
                "    title: CommonResult«string»\n" +
                "  FileAndFolder:\n" +
                "    type: object\n" +
                "    properties:\n" +
                "      extension:\n" +
                "        type: string\n" +
                "      isFile:\n" +
                "        type: boolean\n" +
                "      lastModified:\n" +
                "        type: string\n" +
                "        format: date-time\n" +
                "      name:\n" +
                "        type: string\n" +
                "      path:\n" +
                "        type: string\n" +
                "    title: FileAndFolder\n" +
                "  FileInfo:\n" +
                "    type: object\n" +
                "    required:\n" +
                "      - name\n" +
                "    properties:\n" +
                "      append:\n" +
                "        type: boolean\n" +
                "        description: 为TRUE时，表示追加内容到文件，反之，覆盖源文件内容\n" +
                "      content:\n" +
                "        type: string\n" +
                "        description: 要修改的文件内容\n" +
                "      fileSize:\n" +
                "        type: integer\n" +
                "        format: int64\n" +
                "        description: 文件大小\n" +
                "      fileType:\n" +
                "        type: string\n" +
                "        description: 文件类型\n" +
                "      inFileNum:\n" +
                "        type: integer\n" +
                "        format: int32\n" +
                "        description: 包含的文件个数\n" +
                "      inFolderNum:\n" +
                "        type: integer\n" +
                "        format: int32\n" +
                "        description: 包含的文件夹个数\n" +
                "      location:\n" +
                "        type: string\n" +
                "        description: 文件位置\n" +
                "      modifyTime:\n" +
                "        type: string\n" +
                "        format: date-time\n" +
                "        description: 最后修改时间\n" +
                "      name:\n" +
                "        type: string\n" +
                "        description: 文件名称\n" +
                "      path:\n" +
                "        type: string\n" +
                "        description: 文件路径\n" +
                "    title: FileInfo\n" +
                "  FilePac:\n" +
                "    type: object\n" +
                "    properties:\n" +
                "      extension:\n" +
                "        type: string\n" +
                "      lastModified:\n" +
                "        type: string\n" +
                "        format: date-time\n" +
                "      name:\n" +
                "        type: string\n" +
                "      path:\n" +
                "        type: string\n" +
                "    title: FilePac\n" +
                "  FolderPac:\n" +
                "    type: object\n" +
                "    properties:\n" +
                "      lastModified:\n" +
                "        type: string\n" +
                "        format: date-time\n" +
                "      name:\n" +
                "        type: string\n" +
                "    title: FolderPac\n" +
                "  ImageInfo:\n" +
                "    type: object\n" +
                "    properties:\n" +
                "      bands:\n" +
                "        type: integer\n" +
                "        format: int32\n" +
                "      bit:\n" +
                "        type: string\n" +
                "      coordinateSystemType:\n" +
                "        type: string\n" +
                "        enum:\n" +
                "          - PIXELCS\n" +
                "          - GEOGCS\n" +
                "          - PROJCS\n" +
                "      height:\n" +
                "        type: integer\n" +
                "        format: int32\n" +
                "      maxLat:\n" +
                "        type: number\n" +
                "        format: double\n" +
                "      maxLon:\n" +
                "        type: number\n" +
                "        format: double\n" +
                "      maxProjectionX:\n" +
                "        type: number\n" +
                "        format: double\n" +
                "      maxProjectionY:\n" +
                "        type: number\n" +
                "        format: double\n" +
                "      minLat:\n" +
                "        type: number\n" +
                "        format: double\n" +
                "      minLon:\n" +
                "        type: number\n" +
                "        format: double\n" +
                "      minProjectionX:\n" +
                "        type: number\n" +
                "        format: double\n" +
                "      minProjectionY:\n" +
                "        type: number\n" +
                "        format: double\n" +
                "      projection:\n" +
                "        type: string\n" +
                "      resolution:\n" +
                "        type: number\n" +
                "        format: double\n" +
                "      width:\n" +
                "        type: integer\n" +
                "        format: int32\n" +
                "    title: ImageInfo\n" +
                "  SaveLabelRequest:\n" +
                "    type: object\n" +
                "    required:\n" +
                "      - imagePath\n" +
                "      - label\n" +
                "      - savePath\n" +
                "    properties:\n" +
                "      imagePath:\n" +
                "        type: string\n" +
                "        description: image路径\n" +
                "      label:\n" +
                "        type: string\n" +
                "        description: 保存的label\n" +
                "      savePath:\n" +
                "        type: string\n" +
                "        description: 保存的路径\n" +
                "    title: SaveLabelRequest\n" +
                "  FileManagerParams:\n" +
                "    type: object\n" +
                "    required:\n" +
                "      - newName\n" +
                "      - oldName\n" +
                "    properties:\n" +
                "      host:\n" +
                "        type: string\n" +
                "      newName:\n" +
                "        type: string\n" +
                "        description: 新文件名\n" +
                "      oldName:\n" +
                "        type: string\n" +
                "        description: 旧文件名\n" +
                "      passWord:\n" +
                "        type: string\n" +
                "      port:\n" +
                "        type: integer\n" +
                "        format: int32\n" +
                "      userName:\n" +
                "        type: string\n" +
                "    title: FileManagerParams\n" +
                "  ProgressSingleTon:\n" +
                "    type: object\n" +
                "    required:\n" +
                "      - fileSize\n" +
                "      - plan\n" +
                "      - transLength\n" +
                "    properties:\n" +
                "      fileSize:\n" +
                "        type: integer\n" +
                "        format: int64\n" +
                "        description: 文件大小\n" +
                "      plan:\n" +
                "        type: string\n" +
                "        description: 传输进度\n" +
                "      transLength:\n" +
                "        type: integer\n" +
                "        format: int64\n" +
                "        description: 已传输大小\n" +
                "    title: ProgressSingleTon\n" +
                "  ProgressInfo:\n" +
                "    type: object\n" +
                "    properties:\n" +
                "      progressId:\n" +
                "        type: string\n" +
                "        description: 文件传输进程ID，通过它可获取传输进度\n" +
                "      fileSize:\n" +
                "        type: number\n" +
                "        description: 文件大小\n" +
                "      transLength:\n" +
                "        type: number\n" +
                "        description: 已传输大小\n" +
                "      plan:\n" +
                "        type: string\n" +
                "        description: 传输进度百分比\n" +
                "      transVelocity:\n" +
                "        type: string\n" +
                "        description: 传输速度\n" +
                "      consumTime:\n" +
                "        type: string\n" +
                "        description: 传输耗时\n" +
                "      remainTime:\n" +
                "        type: string\n" +
                "        description: 预计剩余时间\n" +
                "      second:\n" +
                "        type: integer\n" +
                "        format: int64\n" +
                "        description: 已用传输时间\n" +
                "      isNormal:\n" +
                "        type: boolean\n" +
                "        description: 该传输进程是否正常\n" +
                "    title: ProgressInfo";
        serviceId = service_id;
        return new CommonResult<String>().success(ResultCode.SUCCESS).data("yaml_content:" + yamlContent);
    }
}
