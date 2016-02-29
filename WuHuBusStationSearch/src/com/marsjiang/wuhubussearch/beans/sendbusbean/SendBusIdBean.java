package com.marsjiang.wuhubussearch.beans.sendbusbean;

public class SendBusIdBean {

	public String cmd;
	public Params params;

	public class Params {

		public String lineName;
		public String stationId;
		public int type;
	}
}
