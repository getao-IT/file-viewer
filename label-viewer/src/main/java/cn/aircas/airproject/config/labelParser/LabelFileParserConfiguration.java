package cn.aircas.airproject.config.labelParser;

import cn.aircas.airproject.config.labelParser.parsers.LabelFileParser;
import cn.aircas.airproject.config.labelParser.parsers.LabelFileParserComposite;
import cn.aircas.airproject.config.labelParser.parsers.LabelParserComposite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @ClassName: LabelFileParserConfiguration
 * @Description TODO
 * @Author yzhan
 * @Date 2024/5/24 16:50
 * @Version 1.0
 */


@Configuration
public class LabelFileParserConfiguration {

    @Autowired
    @Bean
    public LabelFileParserComposite labelFileParserComposite(List<LabelFileParser> parses){
        LabelParserComposite labelParserComposite = new LabelParserComposite(parses);
        return labelParserComposite;
    }

}
