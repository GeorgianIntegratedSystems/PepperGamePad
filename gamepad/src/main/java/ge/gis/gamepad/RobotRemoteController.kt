package ge.gis.gamepad

import android.content.Context
import android.hardware.input.InputManager
import android.util.Log
import android.view.InputDevice
import android.view.MotionEvent
import com.aldebaran.qi.sdk.QiContext
 import kotlin.concurrent.thread
import kotlin.math.abs

class RobotRemoteController(val context: Context) {

    companion object {
        private const val TAG = "RemoteControlSample"
    }

    lateinit var inputManager: InputManager
    lateinit var remoteRobotController: RobotGamePadController


    fun registerInputManager(){
        inputManager = context.getSystemService(Context.INPUT_SERVICE) as InputManager
    }



    fun setRemoteRobotController(qiContext:QiContext){
        remoteRobotController = RobotGamePadController(qiContext)
    }

    fun getGameControllerIds(): List<Int> {
        val gameControllerDeviceIds = mutableListOf<Int>()
        val deviceIds = inputManager.inputDeviceIds
        deviceIds.forEach { deviceId ->
            InputDevice.getDevice(deviceId).apply {

                // Verify that the device has gamepad buttons, control sticks, or both.
                if (sources and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD
                    || sources and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK
                ) {
                    // This device is a game controller. Store its device ID.
                    gameControllerDeviceIds
                        .takeIf { !it.contains(deviceId) }
                        ?.add(deviceId)
                }
            }
        }
        return gameControllerDeviceIds
    }

     fun getCenteredAxis(
        event: MotionEvent,
        device: InputDevice,
        axis: Int
    ): Float {
        val range: InputDevice.MotionRange? = device.getMotionRange(axis, event.source)

        // A joystick at rest does not always report an absolute position of
        // (0,0). Use the getFlat() method to determine the range of values
        // bounding the joystick axis center.
        range?.apply {
            val value = event.getAxisValue(axis)

            // Ignore axis values that are within the 'flat' region of the
            // joystick axis center.
            if (abs(value) > flat) {
                return value
            }
        }
        return 0f
    }
    fun onGenericMotionEvent(event: MotionEvent): Boolean {
        Log.d("dsdsds", "onGenericMotionEvent $event")

        // Add null protection for when the controller disconnects
        val inputDevice = event.device

        // Get left joystick coordinates
        val leftJoystickX = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_X)
        val leftJoystickY = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_Y)

        // Get right joystick coordinates
        val rightJoystickX = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_Z)
        val rightJoystickY = getCenteredAxis(event, inputDevice, MotionEvent.AXIS_RZ)

        if (::remoteRobotController.isInitialized) {
            thread {
                remoteRobotController.updateTarget(
                    leftJoystickX,
                    leftJoystickY,
                    rightJoystickX,
                    rightJoystickY
                )
            }
        } else {
            Log.d("dsdsdsdsds", "@@@@@@@@@ not initialized")
        }

        return true
    }

}