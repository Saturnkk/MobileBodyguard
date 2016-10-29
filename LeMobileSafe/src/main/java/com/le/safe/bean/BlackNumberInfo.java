package com.le.safe.bean;

public class BlackNumberInfo {

	public String blackNumber;
	public int mode;
	
	public BlackNumberInfo(String blackNumber, int mode) {
		super();
		this.blackNumber = blackNumber;
		this.mode = mode;
	}

	public BlackNumberInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	//输出测试
	@Override
	public String toString() {
		return "BlackNumberInfo [blackNumber=" + blackNumber + ", mode=" + mode
				+ "]";
	}
	
}
