package dev.fvames.rpc.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * 传输协议
 *
 * @version 2019/7/3 14:32
 */
@Data
public class InvokerProtocol implements Serializable {

	private String className;
	private String methodName;
	private Class<?>[] parames;
	private Object[] values;

}
