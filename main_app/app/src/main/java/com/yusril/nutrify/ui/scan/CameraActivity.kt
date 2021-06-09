package com.yusril.nutrify.ui.scan

import android.Manifest
import android.app.Fragment
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.hardware.Camera
import android.hardware.Camera.PreviewCallback
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.Image.Plane
import android.media.ImageReader
import android.media.ImageReader.OnImageAvailableListener
import android.os.*
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.firebase.auth.FirebaseAuth
import com.yusril.nutrify.R
import com.yusril.nutrify.core.data.Resource
import com.yusril.nutrify.core.domain.model.Food
import com.yusril.nutrify.core.domain.model.ListStatistics
import com.yusril.nutrify.ui.MainActivity
import com.yusril.nutrify.ui.home.HomeViewModel
import com.yusril.nutrify.ui.recommendation.RecommendationViewModel
import com.yusril.nutrify.ui.scan.env.ImageUtils
import com.yusril.nutrify.ui.scan.env.Logger
import com.yusril.nutrify.ui.statistic.StatisticsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.compat.ViewModelCompat.viewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.random.Random

abstract class CameraActivity : AppCompatActivity(), OnImageAvailableListener,
    PreviewCallback, CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private val viewModel: HomeViewModel by viewModel()
    private val viewModelRec: RecommendationViewModel by viewModel()


    public var previewWidth = 0
    public var previewHeight = 0
    protected var inValidButtonColor = "#E9E9E9"
    protected var validButtonColor = "#00796B"
    val isDebug = false
    private var handler: Handler? = null
    private var handlerThread: HandlerThread? = null
    private var useCamera2API = false
    private var isProcessingFrame = false
    private val yuvBytes = arrayOfNulls<ByteArray>(3)
    private var rgbBytes: IntArray? = null
    protected var luminanceStride = 0
        private set
    private var postInferenceCallback: Runnable? = null
    private var imageConverter: Runnable? = null
    private lateinit var bottomSheetLayout: LinearLayout
    private lateinit var gestureLayout: LinearLayout
    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>
    protected var frameValueTextView: TextView? = null
    protected var cropValueTextView: TextView? = null
    protected var inferenceTimeTextView: TextView? = null
    protected var tvResult: TextView? = null
    protected lateinit var bottomSheetArrowImageView: ImageView
    private lateinit var plusImageView: ImageView
    private lateinit var minusImageView: ImageView
    private lateinit var apiSwitchCompat: SwitchCompat
    private var threadsTextView: TextView? = null
    protected var btnSave: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        LOGGER.d("onCreate $this")
        super.onCreate(null)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.tfe_od_activity_camera)
        //Toolbar toolbar = findViewById(R.id.toolbar);
//    setSupportActionBar(toolbar);
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        if (hasPermission()) {
            setFragment()
        } else {
            requestPermission()
        }
        threadsTextView = findViewById(R.id.threads)
        plusImageView = findViewById(R.id.plus)
        minusImageView = findViewById(R.id.minus)
        apiSwitchCompat = findViewById(R.id.api_info_switch)
        bottomSheetLayout = findViewById(R.id.bottom_sheet_layout)
        gestureLayout = findViewById(R.id.gesture_layout)
        sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        bottomSheetArrowImageView = findViewById(R.id.bottom_sheet_arrow)
        val vto = gestureLayout.getViewTreeObserver()
        vto.addOnGlobalLayoutListener(
            object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        gestureLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this)
                    } else {
                        gestureLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this)
                    }
                    //                int width = bottomSheetLayout.getMeasuredWidth();
                    val height = gestureLayout.getMeasuredHeight()
                    sheetBehavior.peekHeight = height
                }
            })
        sheetBehavior.isHideable = false
        sheetBehavior.setBottomSheetCallback(
            object : BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_HIDDEN -> {
                        }
                        BottomSheetBehavior.STATE_EXPANDED -> {
                            bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_down)
                        }
                        BottomSheetBehavior.STATE_COLLAPSED -> {
                            bottomSheetArrowImageView.setImageResource(R.drawable.icn_chevron_up)
                        }
                        BottomSheetBehavior.STATE_DRAGGING -> {
                        }
                        BottomSheetBehavior.STATE_SETTLING -> bottomSheetArrowImageView.setImageResource(
                            R.drawable.icn_chevron_up
                        )
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })

//    frameValueTextView = findViewById(R.id.frame_info);
//    cropValueTextView = findViewById(R.id.crop_info);
//    inferenceTimeTextView = findViewById(R.id.inference_info);
        tvResult = findViewById(R.id.tv_result)
        btnSave = findViewById(R.id.btn_save_result)
        apiSwitchCompat.setOnCheckedChangeListener(this)
        plusImageView.setOnClickListener(this)
        minusImageView.setOnClickListener(this)
    }

    protected fun getRgbBytes(): IntArray? {
        imageConverter!!.run()
        return rgbBytes
    }

    protected val luminance: ByteArray?
        protected get() = yuvBytes[0]

    /** Callback for android.hardware.Camera API  */
    override fun onPreviewFrame(bytes: ByteArray, camera: Camera) {
        if (isProcessingFrame) {
            LOGGER.w("Dropping frame!")
            return
        }
        try {
            // Initialize the storage bitmaps once when the resolution is known.
            if (rgbBytes == null) {
                val previewSize = camera.parameters.previewSize
                previewHeight = previewSize.height
                previewWidth = previewSize.width
                rgbBytes = IntArray(previewWidth * previewHeight)
                onPreviewSizeChosen(Size(previewSize.width, previewSize.height), 90)
            }
        } catch (e: Exception) {
            LOGGER.e(e, "Exception!")
            return
        }
        isProcessingFrame = true
        yuvBytes[0] = bytes
        luminanceStride = previewWidth
        imageConverter = Runnable {
            ImageUtils.convertYUV420SPToARGB8888(
                bytes,
                previewWidth,
                previewHeight,
                rgbBytes
            )
        }
        postInferenceCallback = Runnable {
            camera.addCallbackBuffer(bytes)
            isProcessingFrame = false
        }
        processImage()
    }

    /** Callback for Camera2 API  */
    override fun onImageAvailable(reader: ImageReader) {
        // We need wait until we have some size from onPreviewSizeChosen
        if (previewWidth == 0 || previewHeight == 0) {
            return
        }
        if (rgbBytes == null) {
            rgbBytes = IntArray(previewWidth * previewHeight)
        }
        try {
            val image = reader.acquireLatestImage() ?: return
            if (isProcessingFrame) {
                image.close()
                return
            }
            isProcessingFrame = true
            Trace.beginSection("imageAvailable")
            val planes = image.planes
            fillBytes(planes, yuvBytes)
            luminanceStride = planes[0].rowStride
            val uvRowStride = planes[1].rowStride
            val uvPixelStride = planes[1].pixelStride
            imageConverter = object : Runnable {
                override fun run() {
                    ImageUtils.convertYUV420ToARGB8888(
                        yuvBytes[0],
                        yuvBytes[1],
                        yuvBytes[2],
                        previewWidth,
                        previewHeight,
                        luminanceStride,
                        uvRowStride,
                        uvPixelStride,
                        rgbBytes
                    )
                }
            }
            postInferenceCallback = Runnable {
                image.close()
                isProcessingFrame = false
            }
            processImage()
        } catch (e: Exception) {
            LOGGER.e(e, "Exception!")
            Trace.endSection()
            return
        }
        Trace.endSection()
    }

    @Synchronized
    public override fun onStart() {
        LOGGER.d("onStart $this")
        super.onStart()
    }

    @Synchronized
    public override fun onResume() {
        LOGGER.d("onResume $this")
        super.onResume()
        handlerThread = HandlerThread("inference")
        handlerThread!!.start()
        handler = Handler(handlerThread!!.looper)
    }

    @Synchronized
    public override fun onPause() {
        LOGGER.d("onPause $this")
        handlerThread!!.quitSafely()
        try {
            handlerThread!!.join()
            handlerThread = null
            handler = null
        } catch (e: InterruptedException) {
            LOGGER.e(e, "Exception!")
        }
        super.onPause()
    }

    @Synchronized
    public override fun onStop() {
        LOGGER.d("onStop $this")
        super.onStop()
    }

    @Synchronized
    public override fun onDestroy() {
        LOGGER.d("onDestroy $this")
        super.onDestroy()
    }

    @Synchronized
    protected fun runInBackground(r: Runnable?) {
        if (handler != null) {
            handler!!.post(r!!)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST) {
            if (allPermissionsGranted(grantResults)) {
                setFragment()
            } else {
                requestPermission()
            }
        }
    }

    private fun hasPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(PERMISSION_CAMERA)) {
                Toast.makeText(
                    this@CameraActivity,
                    "Camera permission is required for this demo",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
            requestPermissions(arrayOf(PERMISSION_CAMERA), PERMISSIONS_REQUEST)
        }
    }

    // Returns true if the device supports the required hardware level, or better.
    private fun isHardwareLevelSupported(
        characteristics: CameraCharacteristics, requiredLevel: Int
    ): Boolean {
        val deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)!!
        return if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
            requiredLevel == deviceLevel
        } else requiredLevel <= deviceLevel
        // deviceLevel is not LEGACY, can use numerical sort
    }

    private fun chooseCamera(): String? {
        val manager = getSystemService(CAMERA_SERVICE) as CameraManager
        try {
            for (cameraId in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(cameraId)

                // We don't use a front facing camera in this sample.
                val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue
                }
                val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    ?: continue

                // Fallback to camera1 API for internal cameras that don't have full support.
                // This should help with legacy situations where using the camera2 API causes
                // distorted or otherwise broken previews.
                useCamera2API = (facing == CameraCharacteristics.LENS_FACING_EXTERNAL
                        || isHardwareLevelSupported(
                    characteristics, CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL
                ))
                LOGGER.i("Camera API lv2?: %s", useCamera2API)
                return cameraId
            }
        } catch (e: CameraAccessException) {
            LOGGER.e(e, "Not allowed to access camera")
        }
        return null
    }

    protected fun setFragment() {
        val cameraId = chooseCamera()
        val fragment: Fragment
        if (useCamera2API) {
            val camera2Fragment = CameraConnectionFragment.newInstance(
                { size, rotation ->
                    previewHeight = size.height
                    previewWidth = size.width
                    onPreviewSizeChosen(size, rotation)
                },
                this,
                layoutId,
                desiredPreviewFrameSize
            )
            camera2Fragment.setCamera(cameraId)
            fragment = camera2Fragment
        } else {
            fragment = LegacyCameraConnectionFragment(this, layoutId, desiredPreviewFrameSize)
        }
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    protected fun fillBytes(planes: Array<Plane>, yuvBytes: Array<ByteArray?>) {
        // Because of the variable row stride it's not possible to know in
        // advance the actual necessary dimensions of the yuv planes.
        for (i in planes.indices) {
            val buffer = planes[i].buffer
            if (yuvBytes[i] == null) {
                LOGGER.d("Initializing buffer %d at size %d", i, buffer.capacity())
                yuvBytes[i] = ByteArray(buffer.capacity())
            }
            buffer[yuvBytes[i]]
        }
    }

    protected fun readyForNextImage() {
        if (postInferenceCallback != null) {
            postInferenceCallback!!.run()
        }
    }

    protected val screenOrientation: Int
        protected get() = when (windowManager.defaultDisplay.rotation) {
            Surface.ROTATION_270 -> 270
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_90 -> 90
            else -> 0
        }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        setUseNNAPI(isChecked)
        if (isChecked) apiSwitchCompat!!.text = "NNAPI" else apiSwitchCompat!!.text = "TFLITE"
    }

    override fun onClick(v: View) {
        if (v.id == R.id.plus) {
            val threads = threadsTextView!!.text.toString().trim { it <= ' ' }
            var numThreads = threads.toInt()
            if (numThreads >= 9) return
            numThreads++
            threadsTextView!!.text = numThreads.toString()
            setNumThreads(numThreads)
        } else if (v.id == R.id.minus) {
            val threads = threadsTextView!!.text.toString().trim { it <= ' ' }
            var numThreads = threads.toInt()
            if (numThreads == 1) {
                return
            }
            numThreads--
            threadsTextView!!.text = numThreads.toString()
            setNumThreads(numThreads)
        }
    }

    //  protected void showFrameInfo(String frameInfo) {
    //    frameValueTextView.setText(frameInfo);
    //  }
    protected fun showResult(labelInfo: List<String>) {

        val calendar = Calendar.getInstance()
        val dateLocal = LocalDateTime.now()
        val formatter = DateTimeFormatter.BASIC_ISO_DATE
        val dateLocalFormat = dateLocal.format(formatter)
        val currentTime = calendar.get(Calendar.HOUR_OF_DAY)
        val times = currentTime.toString()

        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        viewModel.id = currentUser!!.uid
        viewModel.date = dateLocalFormat
        viewModel.time = times

        val listSeparated = java.lang.String.join(", ", labelInfo)
        tvResult?.text = listSeparated

        viewModelRec.getFoodDataFromScan(labelInfo).observe(this, {
            if (it != null) {
                when (it) {
                    is Resource.Loading -> {
                        Log.d("getstats", "loading")
                    }
                    is Resource.Success -> {
                        Log.d("resource", it.toString())
                        var totalCalories = 0
                        it.data.map { item ->
                            totalCalories += item.calories
                        }

                        val listFood = ArrayList<Food>()
                        var item: Food
                        for (i in 0 until labelInfo.size-1) {
                            item = Food(
                                labelInfo[i]
                            )
                            listFood.add(item)
                        }

                        val calendar = Calendar.getInstance()
                        val dateLocal = LocalDateTime.now()
                        val formatter = DateTimeFormatter.BASIC_ISO_DATE
                        val dateLocalFormat = dateLocal.format(formatter)
                        val listStats = ListStatistics(
                            foods = listFood,
                            total_calories = totalCalories,
                            time = calendar.get(Calendar.HOUR_OF_DAY).toString(),
                            date = dateLocalFormat.toInt()
                        )

                        GlobalScope.launch(Dispatchers.Default) {
                            viewModel.setStatistic(listStats)
                        }


                        Log.d("getstats", "berhasil")
                    }
                    is Resource.Error -> {
                        Log.d("getstats", "error")
                    }
                }
            }
        })

        if (labelInfo != null) {
            btnSave?.isClickable = true
            btnSave?.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(validButtonColor))
            btnSave?.setOnClickListener {
                viewModelRec.getFoodDataFromScan(labelInfo).observe(this, {
                    if (it != null) {
                        when (it) {
                            is Resource.Loading -> {
                                Log.d("getstats", "loading")
                                btnSave?.text = "Loading"
                            }
                            is Resource.Success -> {
                                Log.d("resource", it.toString())
                                var totalCalories = 0
                                it.data.map { item ->
                                    totalCalories += item.calories
                                }

                                val listFood = ArrayList<Food>()
                                var item: Food
                                for (i in 0 until labelInfo.size-1) {
                                    item = Food(
                                        labelInfo[i]
                                    )
                                    listFood.add(item)
                                }
                                val listStats = ListStatistics(
                                    foods = listFood,
                                    total_calories = totalCalories,
                                    time = calendar.get(Calendar.HOUR_OF_DAY).toString(),
                                    date = dateLocalFormat.toInt()
                                )

                                GlobalScope.launch(Dispatchers.Default) {
                                    viewModel.setStatistic(listStats)
                                }

                                val i = Intent(this, MainActivity::class.java)
                                startActivity(i)
                                finish()


                                Log.d("getstats", "berhasil")
                            }
                            is Resource.Error -> {
                                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                })
                //          viewModel.getValue().setStatistic()
            }
        } else {
            btnSave?.isClickable = false
            btnSave?.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(inValidButtonColor))
        }
    }

    //  protected void showCropInfo(String cropInfo) {
    //    cropValueTextView.setText(cropInfo);
    //  }
    //
    //  protected void showInference(String inferenceTime) {
    //    inferenceTimeTextView.setText(inferenceTime);
    //  }
    protected abstract fun processImage()
    protected abstract fun onPreviewSizeChosen(size: Size?, rotation: Int)
    protected abstract val layoutId: Int
    protected abstract val desiredPreviewFrameSize: Size?

    protected abstract fun setNumThreads(numThreads: Int)
    protected abstract fun setUseNNAPI(isChecked: Boolean)

    companion object {
        private val LOGGER = Logger()
        private const val PERMISSIONS_REQUEST = 1
        private const val PERMISSION_CAMERA = Manifest.permission.CAMERA
        private fun allPermissionsGranted(grantResults: IntArray): Boolean {
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
            return true
        }
    }
}
