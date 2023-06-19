package cn.aircas.airproject.entity.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sf.json.JSONObject;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Annotation {
    private JSONObject objects;
    private JSONObject source;
}
