package com.example.monitorcar.TCP;

public class Event {
    public static final int USER_ACCESS_REQ = 0;
    public static final int USER_ACCESS_RSP = 1;
    public static final int USER_REGISTER_REQ = 2;
    public static final int USER_REGISTER_RSP = 3;
    public static final int USER_LOGIN_WITH_OUTSIDE_USER_ID_REQ = 4;
    public static final int USER_LOGIN_WITH_INSIDE_USER_ID_REQ = 5;
    public static final int USER_PERSONAL_REG_REQ = 13;
    public static final int USER_PERSONAL_REG_RSP = 14;
    public static final int PRE_LOGIN_WITH_OUTSIDE_USER_ID_REQ = 15;
    public static final int PRE_LOGIN_WITH_OUTSIDE_USER_ID_RSP = 18;
    public static final int PRE_LOGIN_WITH_INSIDE_USER_ID_REQ = 16;
    public static final int PRE_LOGIN_WITH_INSIDE_USER_ID_RSP = 18;
    public static final int VERIFY_CODE_VER_KEY = 24;
    public static final int VERIFY_CODE_STR_KEY = 25;
    public static final int CHANGE_NEW_VERIFY_CODE_REQ = 19;
    public static final int CHANGE_NEW_VERIFY_CODE_RSP = 20;
    public static final int USER_LOGIN_RSP = 1000;

    public static final int USER_FUNC_ADD_REQ = 20050;
    ;
    public static final int USER_FUNC_ADD_RSP = 20051;
    public static final int USER_FUNC_DEL_REQ = 20052;
    ;
    public static final int USER_FUNC_DEL_RSP = 20053;
    public static final int USER_FUNC_MODIFY_REQ = 20054;
    public static final int USER_FUNC_MODIFY_RSP = 20055;

    public static final int USER_ROLE_ADD_REQ = 20062;
    public static final int USER_ROLE_ADD_RSP = 20063;
    public static final int USER_ROLE_DEL_REQ = 20064;
    public static final int USER_ROLE_DEL_RSP = 20065;
    public static final int USER_ROLE_MODIFY_REQ = 20066;
    public static final int USER_ROLE_MODIFY_RSP = 20067;
    public static final int USER_ROLE_CHANGE_REQ = 20305;
    public static final int USER_ROLE_CHANGE_RSP = 20306;
    public static final int USER_PWD_CHANGE_REQ = 20307;
    public static final int USER_PWD_CHANGE_RSP = 20308;
    public static final int USER_MONITOR_ALARM_CLIENT_REQ = 40031;
    public static final int USER_MONITOR_ALARM_CLIENT_RSP = 40032;
    public static final int USER_OPERATE_CODE_QUERY_USER_HOME_REQ = 20023;
    public static final int USER_OPERATE_CODE_QUERY_USER_HOME_RSP = 20024;
    public static final int USER_ROLE_QUERY_USER_HOME_REQ = 20025;
    public static final int USER_ROLE_QUERY_USER_HOME_RSP = 20026;
    public static final int USER_COMPANYID_QUERY_USER_HOME_REQ = 20027;
    public static final int USER_COMPANYID_QUERY_USER_HOME_RSP = 20028;
    public static final int USER_DEL_WITH_OUTSIDE_USER_ID_REQ = 20011;
    public static final int USER_DEL_WITH_OUTSIDE_USER_ID_RSP = 20012;
    public static final int USER_DEL_WITH_INSIDE_USER_ID_REQ = 20013;
    public static final int USER_DEL_WITH_INSIDE_USER_ID_RSP = 20014;

    public static final int USER_COMPANY_MODIFY_REQ = 20125;
    public static final int USER_COMPANY_MODIFY_RSP = 20126;
    public static final int COMPANY_INFO_DEL_REQ = 20123;
    public static final int COMPANY_INFO_DEL_RSP = 20124;

    public static final int USER_GET_USER_MANAGE_SERVER_ID_REQ = 9;
    public static final int USER_GET_USER_MANAGE_SERVER_ID_RSP = 10;

    public static final int CAR_GET_CAR_MANAGE_SERVER_ID_REQ = 11;
    public static final int CAR_GET_CAR_MANAGE_SERVER_ID_RSP = 12;

    public static final int MONITOR_CAR_NEWEST_POSITION_REQ = 40009;
    public static final int MONITOR_CAR_NEWEST_POSITION_RSP = 40010;

    public static final int MONITOR_CAR_PATH_REQ = 40011;
    public static final int MONITOR_CAR_PATH_RSP = 40012;
    public static final int MONITOR_CAR_REMOTE_CTRL_CMD = 40013;
    public static final int MONITOR_CAR_ROMOTE_CTRL_CMD_RSP = 40014;

    public static final int CAR_PATH_INFO_KEY = 44001;
    public static final int CAR_PATH_QUERY_BEGIN_TIME_KEY = 44002;
    public static final int CAR_PATH_QUERY_INTERVAL_KEY = 44003;

    public static final int COMPANY_ADMIN_MODIFY_REQ = 20127;
    public static final int COMPANY_ADMIN_MODIFY_RSP = 20128;

    public static final int INSIDE_ID_LOGIN_TYPE = 0;
    public static final int OUTSIDE_ID_LOGIN_TYPE = 1;
    public static final int PHONE_NUM_LOGIN_TYPE = 2;

    public static final int INSIDE_USER_ID_KEY = 5;
    public static final int OUTSIDE_USER_ID_KEY = 6;
    public static final int USER_CLIENT_PASSWORD_KEY = 9;
    public static final int USER_COMPANY_ID_KEY = 13;

    public static final int KEY_CAR_ID = INSIDE_USER_ID_KEY;
    public static final int KEY_CAR_SERIALNO = 20251;
    public static final int KEY_CAR_LICENSENO = 20252;
    public static final int KEY_CAR_TYPE = 20253;

    public static final int RSP_KEY = 2;
    public static final int USER_CLIENT_TYPE_KEY = 14;
    public static final int PHONE_NUM_USER_ID_KEY = 7;
    public static final int USER_LOGIN_TYPE_KEY = 9;
    public static final int USER_CLASS_KEY = 9;
    public static final int SERVERIP_KEY = 16;
    public static final int SERVERID_KEY = 15;
    public static final int USER_OPERATE_CODE_KEY = 12;
    public static final int SRC_INSIDE_USER_ID_KEY = 3;
    public static final int KEY_PASSWORD_OLD = 20103;

    public static final int RSP_OK = 0;
    public static final int RSP_NODATA = 40009;
    public static final int RSP_FAIL = 1;
    public static final int RSP_PARA_ERROR = 2;
    public static final int RSP_USER_IN_LOGIN = 3;
    public static final int RSP_RIGSTER_NAME_REPEAT = 4;
    public static final int RSP_lOGIN_PASSWORD_ERROR = 5;
    public static final int RSP_lOGIN_NONEXSIT_USERID = 6;

    public static final int RSP_USERREG_SUCCESS = 20001;
    public static final int RSP_USERNUM_ERROR = 20002;
    public static final int RSP_USERBODY_EMPTY = 20003;
    public static final int RSP_USERNAME_EMPTY = 20004;
    public static final int RSP_PWD_TOO_SIMPLE = 20005;
    public static final int RSP_USERREG_DBERROR = 20006;

    public static final int CLIENT_USER_PROCESS = 1;
    public static final int LOGIN_MAN_PROCESS = 7;
    public static final int USER_MAN_PROCESS = 9;
    public static final int MONITOR_MAN_PROCESS = 52;
    public static final int MONITOR_AGENT_PROCESS = 51;
    public static final int CLIENT_SERVER = 1;
    public static final int LOCAL_SERVER = 0xFFFFFFFF;
    public static final short ACCESS_MAN_PROCESS = 6;

    public static final char PROTO_TCP = 0;
    public static final int PROTO_UDP = 1;
    public static final short COMMON_SERVICE = 0;
    public static final char ROUTE_MASTER = 0;
    public static final int ROUTE_APPOINT = 0;

    public static final int KEY_USER_ID = INSIDE_USER_ID_KEY;
    public static final int KEY_USERNAME = OUTSIDE_USER_ID_KEY;
    public static final int KEY_PASSWORD = USER_CLIENT_PASSWORD_KEY;
    public static final int KEY_USERNAME_LEN = 20103;
    public static final int KEY_PASSWORD_LEN = 20104;
    public static final int KEY_USERALIAS = 20105;
    public static final int KEY_USERALIAS_LEN = 20106;
    public static final int KEY_IDENTIFY_TYPE = 20107;
    public static final int KEY_IDENTIFY_NUM = 20108;
    public static final int KEY_EMAIL = 20109;
    public static final int KEY_EMAIL_LEN = 20110;
    public static final int KEY_ADDRESS_OFFICE = 20111;
    public static final int KEY_ADSOFFICE_LEN = 20112;
    public static final int KEY_ADDRESS_HOME = 20113;
    public static final int KEY_ADSHOME_LEN = 20114;
    public static final int KEY_PHONE1 = 20115;
    public static final int KEY_PHONE2 = 20116;
    public static final int KEY_IMSI = 20117;
    public static final int KEY_ESN = 20118;
    public static final int KEY_REALNAME = 20119;

    public static final int KEY_COMPANY_ID = USER_COMPANY_ID_KEY;
    public static final int KEY_COMPANYNAME = 20151;
    public static final int KEY_COMPANYNAME_LEN = 20152;

    public static final int USER_ROLE_KEY = 11;
    public static final int KEY_ROLE_ID = USER_ROLE_KEY;
    public static final int KEY_ROLE_DESC = 20201;
    public static final int KEY_ROLE_MODIFY_TYPE = 20202;
    public static final int KEY_FUNC_ID = USER_OPERATE_CODE_KEY;
    public static final int KEY_FUNC_DESC = 20204;
    public static final int KEY_FUNCDESC_LEN = 20205;

    public static final int KEY_QUERY_OFFSET = 22160;
    public static final int KEY_QUERY_COUNT = 22161;
    public static final int KEY_USER_INFO = 20300;
    public static final int KEY_ROLE_INFO = 20800;
    public static final int KEY_FUNC_INFO = 20850;
    public static final int KEY_COMPANY_INFO = 21050;
    public static final int KEY_CAR_INFO = 21150;
    public static final int KEY_USER_OWNER = 21650;
    public static final int KEY_CAR_OWNER = 22150;
    public static final int KEY_NUMBER = 20001;
    public static final int KEY_USER_OPERATORTYPE = 20295;
    public static final int KEY_CAR_QUERYTYPE = 20254;

    public static final int MONITOR_OPERATOR_INSIDEID_KEY = 40001;
    public static final int MONITOR_TASK_ID_KEY = 40002;
    public static final int MONITOR_OPERATOR_PHONEID_KEY = 40003;
    public static final int MONITOR_OPERATOR_EMAILID_KEY = 40004;
    public static final int MONITOR_OPERATE_TIME_KEY = 40005;
    public static final int MONITOR_TARGET_INSIDEID_KEY = 40006;
    public static final int MONITOR_TARGET_NAME_KEY = 40007;
    public static final int MONITOR_ALARM_TYPE_KEY = 40008;
    public static final int MONITOR_ALARM_RULE_KEY = 40009;
    public static final int MONITOR_BEGIN_TIME_KEY = 40010;
    public static final int MONITOR_END_TIME_KEY = 40011;
    public static final int MONITOR_USER_INSIDEID_ARRAY_KEY = 40012;
    public static final int MONITOR_USER_PHONEID_ARRAY_KEY = 40013;
    public static final int MONITOR_USER_EMAILID_ARRAY_KEY = 40014;
    public static final int MONITOR_USER_NAME_ARRAY_KEY = 40015;
    public static final int MONITOR_ALARM_SEQUENCE_KEY = 40016;

    public static final int MONTIOR_QUERY_ROW_OFFSET_KEY = 47001;
    public static final int MONITOR_QUERY_ROW_COUNT_KEY = 47002;
    public static final int MONITOR_QUERY_RESULT_TOTAL_NUMBER_KEY = 47003;
    public static final int MONITOR_QUERY_RESULT_SET_KEY = 46001;

    public static final int MONITOR_QUERY_ALARM_REQ = 40023;
    public static final int MONITOR_QUERY_ALARM_RSP = 40024;

    public static final int MONITOR_ALARM_TITLE_KEY = 40017;
    public static final int MONITOR_ALARM_DETAILS_KEY = 40018;
    public static final int MONITOR_ALARM_CODE_KEY = 40019;
    public static final int MONITOR_ALARM_TIME_KEY = 40020;

    public static final int RSP_TASK_STARTING = 40002;
    public static final int RSP_TASK_START_SUCCESS = 40003;
    public static final int RSP_TASK_START_FAIL = 40004;

    public static final int RSP_TASK_STOP_SUCCESS = 40006;
    public static final int RSP_TASK_STOP_FAIL = 40007;
    public static final int RSP_TASK_NO_START = 40001;
    public static final int RSP_TASK_START_TIMEOUT = 40002;
    public static final int RSP_TASK_STOP_TIMEOUT = 40003;
    public static final int RSP_TASK_SAVE_FAIL = 40004;
    public static final int RSP_TASK_DB_ERROR = 40005;
    public static final int RSP_TASK_ALREADY_START = 40006;
    public static final int RSP_TASK_NO_UNIQUE = 40007;
    public static final int RSP_TASK_NO_NORMAL_HANDLE_SERVER = 40008;
    public static final int RSP_NO_DATA = 40009;

    public static final int ALARM_CODE_CAR_STOLEN = 0;
    public static final int ALARM_CODE_CAR_ARRIVE_DELAY = 1;
    public static final int ALARM_CODE_CAR_SHOE_PRESSURE = 2;
    public static final int ALARM_CODE_CAR_BRAKE_SYSTEM = 3;
    public static final int ALARM_CODE_DRIVER_HEARTBEAT = 30000;
    public static final int ALARM_CODE_DRIVER_BLOOD_PRESSURE = 30001;
    public static final int ALARM_CODE_DRIVER_LEAVE_CAR = 30002;

    public static final int ALRM_TYPE_CAR_STEAL_PROTECTION = 0;
    public static final int ALRM_TYPE_CAR_FIXED_POINT_ARRIVE = 1;
    public static final int ALRM_TYPE_CAR_DRIVE_SAFE = 2;

    public static final int USER_ROLE_QUERY_REQ = 20101;
    public static final int USER_ROLE_QUERY_RSP = 20102;
    public static final int USER_ROLEFUNC_QUERY_REQ = 20103;
    public static final int USER_ROLEFUNC_QUERY_RSP = 20104;
    public static final int USER_FUNC_QUERY_REQ = 20105;
    public static final int USER_FUNC_QUERY_RSP = 20106;

    public static final int USER_MYSELF_MODIFY_REQ = 20109;
    public static final int USER_MYSELF_MODIFY_RSP = 20110;
    public static final int USER_MYSELF_QUERY_REQ = 20111;
    public static final int USER_MYSELF_QUERY_RSP = 20112;
    public static final int USER_OWNER_MODIFY_REQ = 20113;
    public static final int USER_OWNER_MODIFY_RSP = 20114;
    public static final int USER_OWNER_QUERY_REQ = 20115;
    public static final int USER_OWNER_QUERY_RSP = 20116;
    public static final int COMPANY_INFO_ADD_REQ = 20117;
    public static final int COMPANY_INFO_ADD_RSP = 20118;
    public static final int COMPANY_INFO_QUERY_REQ = 20119;
    public static final int COMPANY_INFO_QUERY_RSP = 20120;
    public static final int COMPANY_INFO_MODIFY_REQ = 20121;
    public static final int COMPANY_INFO_MODIFY_RSP = 20122;

    public static final int MONITOR_TASK_START_REQ = 40015;
    public static final int MONITOR_TASK_START_RSP = 40016;

    public static final int MONITOR_TASK_STOP_REQ = 40017;
    public static final int MONITOR_TASK_STOP_RSP = 40018;

    public static final int MONITOR_QUERY_CURRENT_TASK_REQ = 40019;
    public static final int MONITOR_QUERY_CURRENT_TASK_RSP = 40020;

    public static final int MONITOR_QUERY_HISTORY_TASK_REQ = 40021;
    public static final int MONITOR_QUERY_HISTORY_TASK_RSP = 40022;

    public static final int CAR_REGISTER_REQ = 20201;
    public static final int CAR_REGISTER_RSP = 20202;
    public static final int CAR_INFO_MODIFY_REQ = 20203;
    public static final int CAR_INFO_MODIFY_RSP = 20204;

    public static final int CAR_DEL_REQ = 20205;
    public static final int CAR_DEL_RSP = 20206;

    public static final int USER_INFO_QUERY_REQ = 20301;
    public static final int USER_INFO_QUERY_RSP = 20302;
    public static final int USER_INFOCHANGE_NOTIFY_REQ = 20303;

    public static final int CAR_INFO_QUERY_REQ = 20207;
    public static final int CAR_INFO_QUERY_RSP = 20208;
    public static final int CAR_INFOCHANGE_NOTIFY_REQ = 20403;
    public static final int CAR_MAN_PROCESS = 11;
    public static final int USER_HOME_PROCESS = 8;
    public static final int USER_COMPANY_ID_OLD_KEY = 18;
    public static final int MONITOR_USER_INSIDEID_KEY = 45001;
    public static final int ALARM_QUERY_BEGIN_TIME_KEY = 45002;
    public static final int ALARM_QUERY_INTERVAL_KEY = 45003;
    public static final int DST_INSIDE_USER_ID_KEY = 4;
    public static final int USER_QUERY_COMPANY_ADMIN_REQ = 20029;
    public static final int USER_QUERY_COMPANY_ADMIN_RSP = 20030;
    public static final int MONITOR_QUERY_HISTORY_ALARM_REQ = 40033;
    public static final int MONITOR_QUERY_HISTORY_ALARM_RSP = 40034;
    public static final int MONITOR_QUERY_CURRENT_ALARM_REQ = 40023;
    public static final int MONITOR_QUERY_CURRENT_ALARM_RSP = 40024;

    public static final int USER_VERSION_CHECK_REQ = 21;
    public static final int USER_VERSION_CHECK_RSP = 22;
    public static final int CLIENT_VERSION_NO_KEY = 26;
    public static final int SERVER_VERSION_NO_KEY = 27;
    public static final int BE_VERSION_UPDATE_KEY = 28;
    public static final int BE_UPDATE_FORCE_KEY = 29;
    public static final int VERSION_DOWNLOAD_TYPE_KEY = 30;
    public static final int VERSION_DOWNLOAD_LINK_KEY = 31;

    public static final int MONITOR_PERSON_DATA_UPLOAD_BATCH = 45002; // 批量上报人员监控数据
    public static final int MONITOR_PERSON_DATA_UPLOAD = 40002; //人员上报的监控数据
    public static final int MONITOR_PERSON_DATA_KEY = 47005;  //
}
