package rikka.shizuku.server;

import moe.shizuku.server.IShizukuApplication;

public class ShizukuClientManager extends ClientManager<ShizukuConfigManager> {

    public ShizukuClientManager(ShizukuConfigManager configManager) {
        super(configManager);
    }

    @Override
    public ClientRecord addClient(int uid, int pid, IShizukuApplication client, String packageName, int apiVersion) {
        ClientRecord record = super.addClient(uid, pid, client, packageName, apiVersion);
        ShizukuConfig.PackageEntry entry = getConfigManager().find(uid);
        // Android can reuse an uninstalled app's UID. Do not transfer its authorization.
        if (record != null && (entry == null || entry.packages == null || !entry.packages.contains(packageName))) {
            record.allowed = false;
        }
        return record;
    }
}
