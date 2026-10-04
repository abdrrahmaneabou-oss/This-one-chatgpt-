package rikka.shizuku.server;

public class ServerConstants {

    public static final int MANAGER_APP_NOT_FOUND = 50;

    public static final String PERMISSION = "moe.shizuku.manager.permission.API_V23";
    public static final String MANAGER_APPLICATION_ID = "com.anas.guardbridge";
    public static final String REQUEST_PERMISSION_ACTION = MANAGER_APPLICATION_ID + ".intent.action.REQUEST_PERMISSION";

    public static final int BINDER_TRANSACTION_getApplications = 10001;
    public static final int BINDER_TRANSACTION_guardLock = 10002;
    public static final int LOCK_STATUS = 0;
    public static final int LOCK_NOW = 1;
    public static final int FOREGROUND_PULSE = 2;
    public static final int FOREGROUND_RESET = 3;
}
