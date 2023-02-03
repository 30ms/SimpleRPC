package site.zhenbin.simplerpc;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Liuzhenbin
 * @date 2023/2/2 17:19
 **/
public class RpcResponse {
    @JsonProperty
    String requestId;

    @JsonProperty
    Object result;

    @JsonProperty
    String error;
}
