package com.hyphenate.easeui.ui;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.hyphenate.easeui.R;
import com.hyphenate.easeui.ui.base.EaseBaseActivity;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.EaseTitleBar;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class EaseBaiduMapActivity extends EaseBaseActivity implements EaseTitleBar.OnBackPressListener,
																		EaseTitleBar.OnRightClickListener{
	private EaseTitleBar titleBarMap;
	private MapView mapView;
	private BaiduMap baiduMap;
	private BDLocation lastLocation;
	protected double latitude;
	protected double longtitude;
	protected String address;
	private BaiduSDKReceiver mBaiduReceiver;
	private LocationClient mLocClient;

	public static void actionStartForResult(Fragment fragment, int requestCode) {
		Intent intent = new Intent(fragment.getContext(), EaseBaiduMapActivity.class);
		fragment.startActivityForResult(intent, requestCode);
	}

	public static void actionStartForResult(Activity activity, int requestCode) {
		Intent intent = new Intent(activity, EaseBaiduMapActivity.class);
		activity.startActivityForResult(intent, requestCode);
	}

	public static void actionStart(Context context, double latitude, double longtitude, String address) {
		Intent intent = new Intent(context, EaseBaiduMapActivity.class);
		intent.putExtra("latitude", latitude);
		intent.putExtra("longtitude", longtitude);
		intent.putExtra("address", address);
		context.startActivity(intent);
	}

	public static String sHA1(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), PackageManager.GET_SIGNATURES);
			byte[] cert = info.signatures[0].toByteArray();
			MessageDigest md = MessageDigest.getInstance("SHA1");
			byte[] publicKey = md.digest(cert);

			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < publicKey.length; i++) {
				String appendString = Integer.toHexString(0xFF & publicKey[i])
						.toUpperCase(Locale.US);
				if (appendString.length() == 1)
					hexString.append("0");
				hexString.append(appendString);
				hexString.append(":");
			}

			String result = hexString.toString();
			result = result.substring(0, result.length() - 1);
			System.out.println("SHA1: " + result);
			return result;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//initialize SDK with context, should call this before setContentView
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.ease_activity_baidumap);

		sHA1(getBaseContext());
		setFitSystemForTheme(false, R.color.transparent, true);
		//ok
		initIntent();
		initView();
		//ok
		initListener();
		initData();
	}

	//ok
	private void initIntent() {
		latitude = getIntent().getDoubleExtra("latitude", 34.148848);
		longtitude = getIntent().getDoubleExtra("longtitude", 108.883752);
		address = getIntent().getStringExtra("address");
		Log.d("message","initIntent初始化完成！");
	}

	//ok
	private void initView() {
		Log.d("message","initView初始化开始！");
		titleBarMap = findViewById(R.id.title_bar_map);
		mapView = findViewById(R.id.bmapView);
		titleBarMap.setRightTitleResource(R.string.button_send);
		double latitude = getIntent().getDoubleExtra("latitude", 34.148848);
		if(latitude != 0) {
			titleBarMap.getRightLayout().setVisibility(View.GONE);
			titleBarMap.getRightLayout().setClickable(true);
		}else {
			titleBarMap.getRightLayout().setVisibility(View.VISIBLE);
			titleBarMap.getRightLayout().setClickable(true);
		}
		ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) titleBarMap.getLayoutParams();
		params.topMargin = (int) EaseCommonUtils.dip2px(this, 24);
		titleBarMap.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent));
		titleBarMap.getRightText().setTextColor(ContextCompat.getColor(this, R.color.white));
		titleBarMap.getRightText().setBackgroundResource(R.drawable.ease_title_bar_right_selector);
		int left = (int) EaseCommonUtils.dip2px(this, 10);
		int top = (int) EaseCommonUtils.dip2px(this, 5);
		titleBarMap.getRightText().setPadding(left, top, left, top);
		ViewGroup.LayoutParams layoutParams = titleBarMap.getRightLayout().getLayoutParams();
		if(layoutParams instanceof ViewGroup.MarginLayoutParams) {
		    ((ViewGroup.MarginLayoutParams) layoutParams).setMargins(0, 0, left, 0);
		}

		baiduMap = mapView.getMap();
		baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15.0f));
		mapView.setLongClickable(true);
		Log.d("message","initView初始化完成！");
	}

	private void initListener() {
		titleBarMap.setOnBackPressListener(this);
		titleBarMap.setOnRightClickListener(this);
		Log.d("message","initListener完成！");
	}

	private void initData() {
		Log.d("message"," initData()初始化数据！");
		if(latitude == 0) {
			mapView = new MapView(this, new BaiduMapOptions());
			baiduMap.setMyLocationConfigeration(
					new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null));
			showMapWithLocationClient();
		}else {
			LatLng lng = new LatLng(latitude, longtitude);
			mapView = new MapView(this,
					new BaiduMapOptions().mapStatus(new MapStatus.Builder().target(lng).build()));
			showMap(latitude, longtitude);
		}
		Log.d("initData()  message","经度"+latitude+"纬度"+longtitude);
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mBaiduReceiver = new BaiduSDKReceiver();
		registerReceiver(mBaiduReceiver, iFilter);
	}

	protected void showMapWithLocationClient() {
		Log.d("message","showMapWithLocationClient获取位置开始！");
		//定位初始化
		mLocClient = new LocationClient(this);
		//注册监听器
		mLocClient.registerLocationListener(new EaseBDLocationListener());
		//选项配置
		LocationClientOption option = new LocationClientOption();
		// open gps
		option.setOpenGps(true);
		// option.setCoorType("bd09ll");
		// Johnson change to use gcj02 coordination. chinese national standard
		// so need to conver to bd09 everytime when draw on baidu map
		option.setCoorType("gcj02");
		option.setScanSpan(30000);
		option.setAddrType("all");
		option.setIgnoreKillProcess(true);
		mLocClient.setLocOption(option);
		if(!mLocClient.isStarted()) {
			mLocClient.start();
			Log.d("message","mLocClient.start()结束！");
		}
		Log.d("message","showMapWithLocationClient获取完成！");
	}

	protected void showMap(double latitude, double longtitude) {
		Log.d("message","经度"+latitude+"纬度"+longtitude);
		LatLng lng = new LatLng(latitude, longtitude);
		CoordinateConverter converter = new CoordinateConverter();
		converter.coord(lng);
		converter.from(CoordinateConverter.CoordType.COMMON);
		LatLng convertLatLng = converter.convert();
		OverlayOptions ooA = new MarkerOptions().position(convertLatLng).icon(BitmapDescriptorFactory
				.fromResource(R.drawable.ease_icon_marka))
				.zIndex(4).draggable(true);
		baiduMap.addOverlay(ooA);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(convertLatLng, 17.0f);
		baiduMap.animateMapStatus(u);
	}

	@Override
	public void onBackPress(View view) {
		onBackPressed();
	}

	@Override
	public void onRightClick(View view) {
		sendLocation();
	}

	public void onReceiveBDLocation(BDLocation bdLocation) {
		if(bdLocation == null) {
			return;
		}
		if (lastLocation != null) {
			if (lastLocation.getLatitude() == bdLocation.getLatitude() && lastLocation.getLongitude() == bdLocation.getLongitude()) {
				Log.d("map", "same location, skip refresh");
				// mMapView.refresh(); //need this refresh?
				return;
			}
		}
		titleBarMap.getRightLayout().setClickable(true);
		lastLocation = bdLocation;
		baiduMap.clear();
		showMap(lastLocation.getLatitude(), lastLocation.getLongitude());
	}

	private void sendLocation() {
		Intent intent = getIntent();
		intent.putExtra("latitude", lastLocation.getLatitude());
		intent.putExtra("longitude", lastLocation.getLongitude());
		intent.putExtra("address", lastLocation.getAddrStr());
		this.setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	protected void onResume() {
		mapView.onResume();
		if (mLocClient != null) {
			if(!mLocClient.isStarted()) {
				mLocClient.start();
			}
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		mapView.onPause();
		if (mLocClient != null) {
			mLocClient.stop();
		}
		super.onPause();
		lastLocation = null;
	}

	@Override
	protected void onDestroy() {
		if (mLocClient != null)
			mLocClient.stop();
		mapView.onDestroy();
		unregisterReceiver(mBaiduReceiver);
		super.onDestroy();
	}

	public class BaiduSDKReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(TextUtils.equals(action, SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				showErrorToast(getResources().getString(R.string.please_check));
			}else if(TextUtils.equals(action, SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				showErrorToast(getResources().getString(R.string.Network_error));
			}
		}
	}

	public class EaseBDLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation bdLocation) {
			onReceiveBDLocation(bdLocation);
		}
	}

	/**
	 * show error message
	 * @param message
	 */
	protected void showErrorToast(String message) {
		Toast.makeText(EaseBaiduMapActivity.this, message, Toast.LENGTH_SHORT).show();
	}
}
