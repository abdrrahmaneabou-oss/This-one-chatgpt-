package moe.shizuku.manager.authorization

import android.app.Activity
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Parcel
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import moe.shizuku.manager.R
import rikka.shizuku.Shizuku
import rikka.shizuku.server.ServerConstants
import java.lang.ref.WeakReference

/** Only a resumed, focused, full-screen manager Activity emits timer heartbeats. */
object GuardLock {
    data class State(val connected: Boolean, val locked: Boolean, val remainingMs: Long, val hidden: Boolean = false)
    val state = MutableLiveData(State(false, true, 60_000))
    private val handler = Handler(Looper.getMainLooper())
    private var foreground: WeakReference<Activity>? = null

    private fun call(action: Int): State {
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        return try {
            data.writeInterfaceToken("moe.shizuku.server.IShizukuService")
            data.writeInt(action)
            val binder = Shizuku.getBinder() ?: error("Service is not running")
            check(binder.transact(ServerConstants.BINDER_TRANSACTION_guardLock, data, reply, 0))
            reply.readException()
            State(true, reply.readInt() != 0, reply.readLong(), reply.readInt() != 0)
        } finally {
            data.recycle()
            reply.recycle()
        }
    }

    private fun eligible(activity: Activity): Boolean =
        activity.hasWindowFocus() && !activity.isFinishing && !activity.isDestroyed &&
            (Build.VERSION.SDK_INT < 24 || !activity.isInMultiWindowMode) &&
            (Build.VERSION.SDK_INT < 26 || !activity.isInPictureInPictureMode)

    private val tick = object : Runnable {
        override fun run() {
            val activity = foreground?.get() ?: return
            if (!eligible(activity)) {
                setForeground(activity, false)
                return
            }
            val old = state.value
            val new = try { call(ServerConstants.FOREGROUND_PULSE) }
                catch (_: Exception) { State(false, true, 60_000) }
            state.value = new
            if (old?.connected == true && old.locked && new.connected && !new.locked) {
                Toast.makeText(activity, R.string.guard_unlocked, Toast.LENGTH_SHORT).show()
            }
            handler.postDelayed(this, 250)
        }
    }

    fun setForeground(activity: Activity, visible: Boolean) {
        if (visible && eligible(activity)) {
            if (foreground?.get() === activity) return
            foreground = WeakReference(activity)
            handler.removeCallbacks(tick)
            handler.post(tick)
        } else if (foreground?.get() === activity) {
            foreground = null
            handler.removeCallbacks(tick)
            state.value = try { call(ServerConstants.FOREGROUND_RESET) }
                catch (_: Exception) { State(false, true, 60_000) }
        }
    }

    fun lock(activity: Activity) {
        try {
            val new = call(ServerConstants.LOCK_NOW)
            state.value = new
            Toast.makeText(activity, if (new.hidden) R.string.guard_locked_hidden
                else R.string.guard_locked_hide_failed, Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(activity, activity.getString(R.string.guard_lock_failed, e.message), Toast.LENGTH_LONG).show()
        }
    }

    fun requireEditable() {
        check(state.value?.connected == true) { "Service is not running" }
        if (state.value?.locked != false) throw SecurityException("Permissions are locked")
    }
}
