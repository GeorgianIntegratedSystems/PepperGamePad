# PepperGamePad
Pepper GamePad Library


1. Add it in your build.gradle (Module:) dependencies:

``` gradle
dependencies {
             ...
	  implementation 'com.github.GeorgianIntegratedSystems:PepperGamePad:0.1.0'
}
  ```
  
2. Add maven in your build.gradle (Project:)

``` gradle
allprojects {
      repositories {
               ...
        maven { url 'https://jitpack.io' }
      }
}
  ```
3. add the following code in onCreate method:
``` kotlin
    robotRemoteController = RobotRemoteController(this)
    QiSDK.register(this, this)
    
    robotRemoteController.registerInputManager()
    robotRemoteController.inputManager.registerInputDeviceListener(this, null)
  ```

4. override InputDeviceListener
``` kotlin
  class MainActivity : RobotActivity(), RobotLifecycleCallbacks, InputManager.InputDeviceListener
  ```
    
5. check controller connection
    
``` kotlin
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
  ```
  
  6. add the following code in onRobotFocusGained:
  ``` kotlin
   override fun onRobotFocusGained(qiContext: QiContext) {
        Log.i("TAG", "onRobotFocusGained")
        this.qiContext = qiContext
        checkControllerConnection()
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
  ```
