package com.gome.test.mock.dubbo;

/**
 * 
 */
public class DubboHessianResponse {
	private DubboHeader dubboHeader;
	private Object result;

	public DubboHessianResponse() {
	    dubboHeader = new DubboHeader();
	}
	

	public DubboHeader getDubboHeader() {
		return dubboHeader;
	}

	public void setDubboHeader(DubboHeader dubboHeader) {
		this.dubboHeader = dubboHeader;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

}
