package moe.shizuku.manager.authorization

import android.os.Bundle
import moe.shizuku.manager.app.AppActivity

/** External permission requests never grant access. Use Application management. */
class RequestPermissionActivity : AppActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finish()
    }
}
