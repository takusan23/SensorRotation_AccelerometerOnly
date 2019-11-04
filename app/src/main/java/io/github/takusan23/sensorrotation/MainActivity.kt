package io.github.takusan23.sensorrotation

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {

    lateinit var sensorManager: SensorManager
    lateinit var sensorEventListener: SensorEventListener

    //加速度の値。配列になっている
    var accelerometerList = floatArrayOf()
    //磁気の値。こちらも配列になっている
    var magneticList = floatArrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        //加速度
        val accelerometer = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER)
        //磁気
        val magnetic = sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD)
        //受け取る
        sensorEventListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                //つかわん
            }

            override fun onSensorChanged(event: SensorEvent?) {
                //値はここで受けとる
                when (event?.sensor?.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        //加速度
                        accelerometerList = event.values.clone()
                        //端末を目の前に持って横向きに倒すときの値。
                        val yokoTate = if (accelerometerList[0] >= 8) {
                            "よこ"
                        } else if (accelerometerList[0] <= -8) {
                            "よこ"
                        } else {
                            "たて"
                        }
                        //画面回転
                        if (accelerometerList[0] >= 8) {
                            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        } else if (accelerometerList[0] <= -8) {
                            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        } else {
                            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        }
                        textview.text = """
                        X：${accelerometerList[0]}
                        Y：${accelerometerList[1]}
                        Z：${accelerometerList[2]}
                        端末の向きは：$yokoTate
                        """.trimIndent()
                    }
                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        //地磁気
                        magneticList = event.values.clone()
                    }
                }
            }
        }
        //加速度センサー登録
        sensorManager.registerListener(
            sensorEventListener,
            accelerometer[0],  //配列のいっこめ。
            SensorManager.SENSOR_DELAY_NORMAL  //更新頻度
        )
/*
        //磁気センサー登録
        sensorManager.registerListener(
            sensorEventListener,
            magnetic[0],  //配列のいっこめ。
            SensorManager.SENSOR_DELAY_NORMAL  //更新頻度
        )
*/
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(sensorEventListener)
    }

}
