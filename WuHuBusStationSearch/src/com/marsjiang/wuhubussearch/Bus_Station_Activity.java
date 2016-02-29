package com.marsjiang.wuhubussearch;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.marsjiang.wuhubussearch.beans.getbeans.GetSearchStationBean;
import com.marsjiang.wuhubussearch.beans.sendbusbean.SendBusStationBean;
import com.marsjiang.wuhubussearch.utils.ToastUtil;

public class Bus_Station_Activity extends Activity {

	private EditText search_00;
	private ListView lv_search;
	
	private String currentStationName = "-1";

	private GetSearchStationBean getsearchbusnumbean;
	
	private SearchBusNumAdapter searchbusnumadapter;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			System.out.println("收到消息");
			searchbusnumadapter.notifyDataSetChanged();
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bus_line_station_show);
		//进行总体的初始化
		initView();
		initData();
		
	}

	// 初始化界面控件
	private void initView() {
		search_00 = (EditText) findViewById(R.id.et_search);
		lv_search = (ListView) findViewById(R.id.lv_search);
	//	currentNum = search_00.getText().toString().trim();
		searchbusnumadapter = new SearchBusNumAdapter();
		lv_search.setAdapter(searchbusnumadapter);
		
		search_00.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//System.out.println("onTextChanged");
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
				//System.out.println("beforeTextChanged");
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				//System.out.println("afterTextChanged");
				getDateFromServer();
				//searchbusnumadapter.notifyDataSetChanged();
			}
		});
		
		lv_search.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				String busstationname = getsearchbusnumbean.result.list.get(arg2);
				Intent intent = new Intent(Bus_Station_Activity.this,BusStation_around_show.class);
				intent.putExtra("stationname", busstationname);
				startActivity(intent);
			}
		});
		
	}

	// 初始化数据
	private void initData() {
		/*
		 * 写入数字发送内容： POST http://220.180.139.42:8980/SmartBusServer/Main
		 * HTTP/1.1 Accept-Encoding: gzip, deflate Content-Length: 46
		 * Content-Type: text/plain; charset=UTF-8 Host: 220.180.139.42:8980
		 * Connection: Keep-Alive
		 * 
		 * {"cmd":"searchLine","params":{"lineName":"3"}}
		 */
		getDateFromServer();
		
		//searchbusnumadapter.notifyDataSetChanged();
	}

	// 从数据库获取联想数据
	private void getDateFromServer() {
		//用来存储发送数据
		if(!"".equals(search_00.getText().toString().trim())){
			currentStationName = search_00.getText().toString().trim();
		}
		
		final SendBusStationBean sendbusnumbean = new SendBusStationBean();
		sendbusnumbean.cmd = "searchStation";
		sendbusnumbean.params = sendbusnumbean.new Params();
		sendbusnumbean.params.stationName=currentStationName;
		//用来解析json数据
		final Gson gson = new Gson();

		//设置传输参数。​
		RequestParams params = new RequestParams("UTF-8");
		
		try{
		/*	params.setBodyEntity(new StringEntity(gson.toJson("要转成json的对象"),"UTF-8"));
​					params.setContentType("applicatin/json");*/
			params.setBodyEntity(new StringEntity(gson.toJson(sendbusnumbean),"UTF-8"));
			System.out.println(gson.toJson(sendbusnumbean));
			params.setContentType("applicatin/json");
		} catch (UnsupportedEncodingException e){
			e.printStackTrace();
		}
		//访问网络获取数据
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.POST, "http://220.180.139.42:8980/SmartBusServer/Main", params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				System.out.println("failed");
				ToastUtil.showToast(getApplicationContext(), "请检查网络连接！");
			}

			@Override
			public void onSuccess(ResponseInfo<String> string) {
				
				if(string!=null){
					System.out.println(string.result);
					getsearchbusnumbean = gson.fromJson(string.result,GetSearchStationBean.class);
					mHandler.sendEmptyMessage(0);
				}else{
					ToastUtil.showToast(getApplicationContext(), "请输入正确格式的数据");
					System.out.println("请输入正确格式的数据");
				}
				
			}
		});
		
	}

	//listView的adapter
	class SearchBusNumAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(getsearchbusnumbean == null||currentStationName == "-1"||getsearchbusnumbean.result == null){
				return 0;
			}else{
				return getsearchbusnumbean.result.list.size();
			}
			
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return getsearchbusnumbean.result.list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			String busNum = getsearchbusnumbean.result.list.get(position);
			if(convertView == null){
				viewHolder = new ViewHolder();
				convertView = View.inflate(getApplicationContext(), R.layout.search_bus_station_show_item, null);
				viewHolder.tv_searchNum = (TextView) convertView.findViewById(R.id.tv_searchbusnum);
				convertView.setTag(viewHolder);
				
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			viewHolder.tv_searchNum.setText(busNum);
			return convertView;
		}
		
	}//Adapter的结尾处
	
	static class ViewHolder{
		TextView tv_searchNum;
	}
	
}
