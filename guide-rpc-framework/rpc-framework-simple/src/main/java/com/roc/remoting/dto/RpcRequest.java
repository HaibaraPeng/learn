package com.roc.remoting.dto;

import lombok.*;

import java.io.Serializable;

/**
 * @Description RpcRequest
 * @Author dongp
 * @Date 2022/8/8 0008 16:36
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = 4378840018426823369L;

    private String requestId;
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;
    private String version;
    private String group;

    public String getRpcServiceName() {
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }
}
