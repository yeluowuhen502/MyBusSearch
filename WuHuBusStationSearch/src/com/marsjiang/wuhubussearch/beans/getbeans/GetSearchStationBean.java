package com.marsjiang.wuhubussearch.beans.getbeans;

import java.util.List;

public class GetSearchStationBean {

	public Result result;
	public double lat;
	public double lng;
	
	public class Result{
		public List<String> list;
	}
}
