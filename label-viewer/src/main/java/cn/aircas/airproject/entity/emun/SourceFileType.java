package cn.aircas.airproject.entity.emun;

import cn.aircas.airproject.service.FileTypeService;
import cn.aircas.airproject.utils.SpringContextUtil;



public enum SourceFileType {

    IMAGE("IMAGE"),VIDEO("VIDEO"),TEXT("TEXT"), AUDIO("AUDIO"),ELEC("ELEC");

    private String value;


    SourceFileType(String value){
        this.value = value;
    }


    public String getValue(){
        return this.value;
    }


    public void setValue(String value){
        this.value = value;
    }


    public FileTypeService getService(){
        String serviceName = String.format("%s-SERVICE",this.value.toUpperCase());
        return (FileTypeService) SpringContextUtil.getBean(serviceName);
    }
}
