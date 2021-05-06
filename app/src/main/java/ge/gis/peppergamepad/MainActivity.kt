package ge.gis.peppergamepad

import android.hardware.input.InputManager
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.QiSDK
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.`object`.holder.AutonomousAbilitiesType
import com.aldebaran.qi.sdk.`object`.holder.Holder
import com.aldebaran.qi.sdk.builder.HolderBuilder
import com.aldebaran.qi.sdk.design.activity.RobotActivity
import ge.gis.gamepad.RobotRemoteController

class MainActivity : RobotActivity(), RobotLifecycleCallbacks, InputManager.InputDeviceListener {

    private lateinit var robotRemoteController: RobotRemoteController
    private lateinit var basicAwarenessHolder: Holder
    private var qiContext: QiContext? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        robotRemoteController = RobotRemoteController(this)
        QiSDK.register(this, this)

        robotRemoteController.registerInputManager()
        robotRemoteController.inputManager.registerInputDeviceListener(this, null)
    }

    override fun onRobotFocusGained(qiContext: QiContext) {
        Log.i("TAG", "onRobotFocusGained")
        this.qiContext = qiContext

        checkControllerConnection()

        // Hold Basic Awareness to avoid robot getting distracted
        basicAwarenessHolder = HolderBuilder.with(qiContext)
            .withAutonomousAbilities(AutonomousAbilitiesType.BASIC_AWARENESS)
            .build()
        basicAwarenessHolder.async().hold().thenConsume {
            when {
                it.isSuccess -> Log.i("TAG", "BasicAwareness held with success")
                it.hasError() -> Log.e("TAG", "holdBasicAwareness error: " + it.errorMessage)
                it.isCancelled -> Log.e("TAG", "holdBasicAwareness cancelled")
            }
        }
        robotRemoteController.setRemoteRobotController(qiContext)
        Log.i("TAG", "after RemoteRobotController instantiation")
    }

    override fun onRobotFocusLost() {
        Log.i("TAG", "onRobotFocusLost")
        qiContext = null
    }

    override fun onRobotFocusRefused(reason: String?) {
        Log.e("TAG", "onRobotFocusRefused: $reason")
    }

    override fun onInputDeviceRemoved(deviceId: Int) {
        Log.d("TAG", "onInputDeviceRemoved")
        checkControllerConnection()
    }

    override fun onInputDeviceAdded(deviceId: Int) {
        Log.d("TAG", "onInputDeviceAdded")
        checkControllerConnection()
    }

    override fun onInputDeviceChanged(deviceId: Int) {
        Log.d("TAG", "onInputDeviceChanged")
        checkControllerConnection()
    }

    override fun onPause() {
        super.onPause()
        robotRemoteController.inputManager.unregisterInputDeviceListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        QiSDK.unregister(this, this)
    }

    override fun onGenericMotionEvent(event: MotionEvent?): Boolean {
        return robotRemoteController.onGenericMotionEvent(event!!)
    }

    private fun checkControllerConnection() {
        val connectedControllers = robotRemoteController.getGameControllerIds()
        if (connectedControllers.isEmpty()) {
            runOnUiThread {
                Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show()
            }
        } else {
            runOnUiThread {
                Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
            }
        }
    }
}