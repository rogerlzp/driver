package com.abc.driver.utility;

import com.abc.driver.R;

public class CellSiteConstants {

	public static final String CELLSITE_CONFIG = "cellsite_config";
	public static final String USER_NAME = "username";
	public static final String NAME = "name";
	public static final String PASSWORD = "password";
	public static final String DEVICE_TOKEN = "device_token";

	public static final int DRIVER_ROLE_ID = 1;
	public static final int HUOZHU_ROLE_ID = 2;

	public static final String USER = "user";
	public static final String PROFILE = "profile";
	public static final String MOBILE = "mobile";
	public static final String USER_ID = "user_id";
	public static final String ROLE_ID = "role_id";
	public static final String USER_PORTARIT = "portrait";
	public static final String PROFILE_IMAGE_URL = "profile_image_url";
	public static final String DRIVER_LICENSE_URL = "driver_license_image_url";
	public static final String IDENTITY_CARD_IMAGE_URL = "identity_card_image_url";

	// public static final String DRIVER_LICENSE_URL =
	// "driver_license_image_url";

	public static final String NORMAL_USER_MODE = "regUser";
	public static final String VERIFY_CODE = "verify_code";

	public static final String CARGO_TYPE = "cargo_type";
	public static final String CARGO_WEIGHT = "cargo_weight";
	public static final String CARGO_VOLUME = "cargo_volume";

	//
	public static final String ID = "id";
	public static final String TRUCK = "truck";
	public static final String TRUCK_ID = "truck_id";
	public static final String TRUCK_TYPE = "truck_type";
	public static final String TRUCK_LENGTH = "tlength_id";
	public static final String TRUCK_WEIGHT = "tweight_id";
	public static final String TRUCK_STATUS = "tstatus_id";
	public static final String TRUCK_AUDIT_STATUS = "taudit_status_id";
	public static final String TRUCK_LICENSE_URL = "tl_image_url";
	public static final String TRUCK_LICENSE = "tlicense";
	public static final String TRUCK_PHOTO_URL = "tphoto_image_url";
	public static final String TRUCK_MOBILE_NUM = "tmobile_num";
	public static final String TRUCK_IMAGE_VIEW = "turck_image_type";
	public static final String TRUCK_PLAN_DESCRIPTION = "truck_plan_desc";

	public static final String SHIPPER_ADDRESS_CODE = "sa_code";
	public static final String SHIPPER_DATE = "shipper_date";
	public static final String SHIPPER_USERNAME = "shipper_username";
	public static final String CONSIGNEE_ADDRESS_CODE = "ca_code";
	public static final String HORDER_DESCRIPTION = "horder_desc";

	public static final String SHIPPER_ADDRESS_NAME = "shipper_address_name";
	public static final String SHIPPER_ADDRESS_CODE_IN = "shipper_address_code";
	public static final String CONSIGNEE_ADDRESS_CODE2 = "consignee_address_code";
	public static final String CONSIGNEE_ADDRESS_NAME = "consignee_address_name";

	// horder
	public static final String HORDER_ID = "horder_id";
	public static final String STATUS = "status";
	// RESULT
	public final static String LOGIN_RESULT = "login_result";
	public final static String FORGET_PASSWORD_RESULT = "forget_password_result";

	// RESULT
	// login
	public final static int REGISTER_USER_EXISTS = 10001; // bad username
	public final static int REGISTER_INTEGRATION_ERROR = 10002; // bad username
	public final static int LOGIN_FAILED = 10003; // login succeed
	public final static int LOGIN_OTH_ERROR = 10004; // bad username

	public static final int UNKNOWN_ERROR = 10050;
	public static final int UNKNOWN_HOST_ERROR = 10051;

	// password
	public final static int CHANGE_PASSWORD_SUCC = 10020;
	public final static int CHANGE_PASSWORD_FAIL = 10021;
	public final static int OLD_PASSWORD_WRONG = 10022;
	public final static int FORGET_PASSWORD_FAIL = 10023;
	public final static int LOGIN_NAME_WRONG = 10024;
	public final static int LOGIN_MAIL_WRONG = 10025;

	public final static int SEND_MAIL_SUC = 10026;
	public final static int SEND_MAIL_FAIL = 10027;
	public final static int NOT_AVAILABLE_MAIL = 10028;

	// truck info
	public final static int TAKE_PICTURE = 1001;
	public final static int PICK_PICTURE = 1002;
	public final static int TAKE_PICTURE2 = 1003;
	public final static int PICK_PICTURE2 = 1004;
	public final static int CROP_PICTURE = 1010;
	public final static int UPDATE_TRUCK_MOBILE_REQUSET = 1011;
	// user info
	public final static int TAKE_IDENTITY = 1012;
	public final static int PICK_IDENTITY = 1013;
	public final static int TAKE_DRIVER_LICENSE = 1014;
	public final static int PICK_DRIVER_LICENSE = 1015;
	public final static int TAKE_USER_PORTRAIT = 1016;
	public final static int PICK_USER_PORTRAIT = 1017;

	// image corp size
	public static final int IMAGE_WIDTH = 180;
	public static final int IMAGE_HEIGHT = 180;

	// identity crop size
	public static final int IDENTITY_IMAGE_WIDTH = 200;
	public static final int IDENTITY_IMAGE_HEIGHT = 360;

	// down horder
	public static final int NORMAL_OPERATION = 0;
	public static final int MORE_OPERATION = 1;
	// horder status
	public static final String HORDER_STATUS = "horder_status";
	public static final int HORDER_WAITING = 0;
	public static final int HORDER_SENT = 1;
	public static final int HORDER_COMPLEMENTED = 2;
	// horder page count
	public static final int PAGE_COUNT = 3;

	// �������� URL
	// public static final String HOST = "http://20120328.welives.sinaapp.com/";
	// public static final String HOST = "http://115.28.175.222:9091/";
	public static final String HOST = "http://192.168.2.106:9003/";
	public static final String LOGIN_URL = HOST + "login/fmobile";
	public static final String CHANGE_PASSWORD_URL = HOST
			+ "changePassword.php";
	public static final String FORGET_PASSWORD_URL = HOST
			+ "forgetPassword.php";

	// image url for user
	public final static String UPDATE_DRIVER_LICENSE_URL = HOST
			+ "updateDriverLicense/fmobile";
	public final static String UPDATE_USER_PORTRAIT_URL = HOST
			+ "updateUserPortrait/fmobile";

	public final static String UPDATE_USERNAME_URL = HOST
			+ "updateUsername/fmobile";

	public final static String UPDATE_USER_IDENTITY_URL = HOST
			+ "updateUserIdentityImage/fmobile";

	// get my horder url
	public static final String GET_MY_HORDER_URL = HOST + "getMyHorder/fmobile";
	public static final String GET_HORDER_URL = HOST + "getHorder/fmobile";
	public static final String GET_NEW_HORDER_URL = HOST
			+ "getNewHorder/fmobile";

	public final static String USER_IMAGE_URL = HOST + "";// TOOD

	public final static String USER_QUERY_URL = HOST + "getUserProfile/fmobile";
	// 用户注册
	public static final String REGISTER_URL = HOST + "register/fmobile";

	// driver     注册
	// public static final String REGISTER_URL = HOST +
	// "registerDriver/fmobile";

	public static final String VERIFY_URL = HOST + "verify/fmobile";
	// public static final String REGISTER_URL =
	// "http://115.28.175.222:9091/test1.php" ;
	// HOST + "test1.php";
	public static final String GET_TRUCK_URL = HOST + "getTruck/fmobile";

	public static final String CREATE_HORDER_URL = HOST
			+ "createHorder/fmobile";
	// public final static String REGISTER_RESULT = "register_result";

	// TRUCK
	public static final String UPDATE_TRUCK_URL = HOST + "updateTruck/fmobile";
	public static final String UPDATE_TRUCK_MOBILE_URL = HOST
			+ "updateTruckMobile/fmobile";

	public static final String UPDATE_TRUCK_LICENSE_URL = HOST
			+ "updateTruckLicenseImage/fmobile";
	public static final String UPDATE_TRUCK_PHOTO_URL = HOST
			+ "updateTruckPhoto/fmobile";

	// truck plan
	public static final String CREATE_TRUCK_PLAN_URL = HOST
			+ "createTruckPlan/fmobile";

	public static final String VERIFY_PHONE_NUMBE_URL = "http://m.youhubst.com/phone_cz.php?phone=";

	public final static String RESULT_CODE = "result_code";
	public final static int RESULT_SUC = 0;
	public final static String RESULT = "result";

	// Truck 类型
	public final static String[] TruckTypes = { "厢式车", "平板车", "高低板车", "高栏车",
			"中栏车", "低栏车", "罐式车", "冷藏车", "保温车", "危险品车", "铁笼车", "集装箱", "自卸货车",
			"其他车型" };

	public final static String[] TruckLengths = { "4.2米", "5.2米", "5.8米",
			"6.2米", "6.5米", "6.8米", "7.2米", "8米", "9.6米", "12米", "13米",
			"13.5米", "15米", "16.5米", "17.5米", "18.5米", "20米", "22米", "24米" };

	public final static String[] CargoTypes = { "设备", "矿产", "建材", "食品",
		"蔬菜", "生鲜", "药品", "化工", "木材", "家畜", "纺织品", "日用品", "电子电器",
		"农副产品", "其它类型" };
}
