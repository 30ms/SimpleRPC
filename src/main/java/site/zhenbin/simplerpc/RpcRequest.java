package site.zhenbin.simplerpc;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Liuzhenbin
 * @date 2023/2/2 17:18
 **/
public class RpcRequest {
    @JsonProperty
    String requestId;

    @JsonProperty
    String className;

    @JsonProperty
    String methodName;

    @JsonProperty
    String[] parameterClassNames;

    @JsonProperty
    Object[] parameters;

}
