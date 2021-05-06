package ge.gis.peppergamepad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks
import com.aldebaran.qi.sdk.design.activity.RobotActivity

class MainActivity : RobotActivity(),RobotLifecycleCallbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    /**
     * Called when focus is gained
     *
     * @param qiContext the robot context
     */
    override fun onRobotFocusGained(qiContext: QiContext?) {
        TODO("Not yet implemented")
    }

    /**
     * Called when focus is lost
     */
    override fun onRobotFocusLost() {
        TODO("Not yet implemented")
    }

    /**
     * Called when focus is refused
     *
     * @param reason the reason
     */
    override fun onRobotFocusRefused(reason: String?) {
        TODO("Not yet implemented")
    }
}