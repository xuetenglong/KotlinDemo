package com.cardvlaue.sys.data.source.local;

interface LocalDataContract {

    /**
     * 授信
     */
    interface CreditInfoLocalDataContract {

        String JSON = "CreditInfo_JSON";
    }

    /**
     * 申请
     */
    interface ApplyInfoLocalDataContract {

        String JSON = "ApplyInfo_JSON";
    }

    /**
     * 用户
     */
    interface UserInfoLocalDataContract {

        String JSON = "UserInfo_JSON";
    }

    /**
     * 登录
     */
    interface LoginLocalDataContract {

        String MOBILE_PHONE = "Login_mobilePhone";
        String OBJECT_ID = "Login_objectId";
        String ACCESS_TOKEN = "Login_accessToken";
    }

    /**
     * 设备
     */
    interface DeviceLocalDataContract {

        String GPS_ADDRESS = "Device_GPS_ADDRESS";
        String IP_ADDRESS = "Device_IP_ADDRESS";
    }

    /**
     * 版本
     */
    interface VersionLocalDataContract {

        String VERSION_CHECK_INFO = "Device_VERSION_CHECK_INFO";
    }

    interface HomeImageDataLocalDataContract {

        String HOME_IMAGE_DATA = "Device_HOME_IMAGE_DATA_CHECK_INFO";
    }

}
