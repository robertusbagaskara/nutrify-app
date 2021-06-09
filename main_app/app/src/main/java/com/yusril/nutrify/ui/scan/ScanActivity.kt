package com.yusril.nutrify.ui.scan

import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yusril.nutrify.databinding.ActivityScanBinding
import com.yusril.nutrify.ui.scan.customview.OverlayView
import com.yusril.nutrify.ui.scan.env.ImageUtils
import com.yusril.nutrify.ui.scan.env.Logger
import com.yusril.nutrify.ui.scan.env.Utils
import com.yusril.nutrify.ui.scan.tflite.Classifier
import com.yusril.nutrify.ui.scan.tflite.Classifier.Recognition
import com.yusril.nutrify.ui.scan.tflite.YoloV4Classifier
import com.yusril.nutrify.ui.scan.tracking.MultiBoxTracker
import java.io.IOException
import java.util.*

@Suppress("DEPRECATION")
class ScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanBinding

        var MINIMUM_CONFIDENCE_TF_OD_API = 0.5f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cameraButton = binding.cameraButton
        val detectButton = binding.detectButton
        val imageView = binding.imageView
        pindah()

//        cameraButton.setOnClickListener(View.OnClickListener { v: View? ->
//            startActivity(
//                Intent(
//                    this,
//                    DetectorActivity::class.java
//                )
//            )
//        })

//        detectButton.setOnClickListener(View.OnClickListener { v: View? ->
//            val handler = Handler()
//            Thread {
//                val results: List<Recognition> =
//                    detector.recognizeImage(cropBitmap)
//                handler.post { cropBitmap?.let { handleResult(it, results) } }
//            }.start()
//        })
//
//        this.sourceBitmap = Utils.getBitmapFromAsset(this, "kite.jpg")
//
//        this.cropBitmap = Utils.processBitmap(sourceBitmap, TF_OD_API_INPUT_SIZE)
//
//        imageView.setImageBitmap(cropBitmap)
//
//        initBox()

    }

    private fun pindah() {
        startActivity(Intent(this, DetectorActivity::class.java))
        finish()
    }


//    private val LOGGER: Logger = Logger()
//
//    val TF_OD_API_INPUT_SIZE = 416
//
//    private val TF_OD_API_IS_QUANTIZED = false
//
//    private val TF_OD_API_MODEL_FILE = "bangkit-fp16"
//
//    private val TF_OD_API_LABELS_FILE = "file:///android_asset/coco.txt"
//
//    // Minimum detection confidence to track a detection.
//    private val MAINTAIN_ASPECT = false
//    private val sensorOrientation = 90
//
//    private lateinit var detector: Classifier
//
//    private var frameToCropTransform: Matrix? = null
//    private var cropToFrameTransform: Matrix? = null
//    private lateinit var tracker: MultiBoxTracker
//    private lateinit var trackingOverlay: OverlayView
//
//    protected var previewWidth = 0
//    protected var previewHeight = 0
//
//    private var sourceBitmap: Bitmap? = null
//    private var cropBitmap: Bitmap? = null
//
//    private val cameraButton: Button? = null
//    private  var detectButton:android.widget.Button? = null
//    private val imageView: ImageView? = null
//
//    private fun initBox() {
//        previewHeight = TF_OD_API_INPUT_SIZE
//        previewWidth = TF_OD_API_INPUT_SIZE
//        frameToCropTransform = ImageUtils.getTransformationMatrix(
//            previewWidth, previewHeight,
//            TF_OD_API_INPUT_SIZE, TF_OD_API_INPUT_SIZE,
//            sensorOrientation, MAINTAIN_ASPECT
//        )
//        cropToFrameTransform = Matrix()
//        frameToCropTransform!!.invert(cropToFrameTransform)
//        tracker = MultiBoxTracker(this)
//        trackingOverlay = binding.trackingOverlay
//        trackingOverlay.addCallback { canvas -> tracker.draw(canvas) }
//        tracker.setFrameConfiguration(TF_OD_API_INPUT_SIZE, TF_OD_API_INPUT_SIZE, sensorOrientation)
//        try {
//            detector = YoloV4Classifier.create(
//                assets,
//                TF_OD_API_MODEL_FILE,
//                TF_OD_API_LABELS_FILE,
//                TF_OD_API_IS_QUANTIZED
//            )
//        } catch (e: IOException) {
//            e.printStackTrace()
//            LOGGER.e(e, "Exception initializing classifier!")
//            val toast = Toast.makeText(
//                applicationContext, "Classifier could not be initialized", Toast.LENGTH_SHORT
//            )
//            toast.show()
//            finish()
//        }
//    }
//
//    private fun handleResult(bitmap: Bitmap, results: List<Recognition>) {
//        val canvas = Canvas(bitmap)
//        val paint = Paint()
//        paint.color = Color.RED
//        paint.style = Paint.Style.STROKE
//        paint.strokeWidth = 2.0f
//        val mappedRecognitions: List<Recognition> = LinkedList()
//        for (result in results) {
//            val location = result.location
//            if (location != null && result.confidence >= MINIMUM_CONFIDENCE_TF_OD_API) {
//                canvas.drawRect(location, paint)
//                //                cropToFrameTransform.mapRect(location);
////
////                result.setLocation(location);
////                mappedRecognitions.add(result);
//            }
//        }
//        //        tracker.trackResults(mappedRecognitions, new Random().nextInt());
////        trackingOverlay.postInvalidate();
//        imageView!!.setImageBitmap(bitmap)
//    }
}