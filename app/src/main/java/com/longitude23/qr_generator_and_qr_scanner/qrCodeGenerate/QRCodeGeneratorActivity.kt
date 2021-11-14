package com.longitude23.qr_generator_and_qr_scanner.qrCodeGenerate

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.longitude23.qr_generator_and_qr_scanner.R
import com.longitude23.qr_generator_and_qr_scanner.databinding.ActivityQrcodeGeneratorBinding
import java.io.File
import java.io.FileOutputStream
import com.google.zxing.WriterException
import android.graphics.Point
import android.util.Log
import android.view.Display
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import android.view.WindowManager


class QRCodeGeneratorActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityQrcodeGeneratorBinding
    private val EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1
    var bitmap: Bitmap? = null
    var qrgEncoder: QRGEncoder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQrcodeGeneratorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (!checkPermissionForExternalStorage()) {
            requestPermissionForExternalStorage()
        }

        initView()
    }


    private fun initView() {

        binding.linkCardViewId.setOnClickListener(this)
        binding.vCardViewId.setOnClickListener(this)
        binding.generateQRSaveBtnId.setOnClickListener(this)
        binding.generateQRBtnId.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.linkCardViewId -> {
                binding.vCardLayoutId.visibility = View.GONE
                binding.linkTiId.visibility = View.VISIBLE
                binding.generateQRBtnId.visibility = View.VISIBLE
            }

            R.id.vCardViewId -> {
                binding.linkTiId.visibility = View.GONE
                binding.vCardLayoutId.visibility = View.VISIBLE
                binding.generateQRBtnId.visibility = View.VISIBLE
            }

            R.id.generateQRBtnId -> {
                if (binding.linkTiId.visibility == View.VISIBLE) {

                    if (binding.linkTvId.text.toString().isNotEmpty()) {
                        generateQRCode()
                    } else {
                        binding.linkTvId.error = "This field is required"
                    }

                } else if (binding.vCardLayoutId.visibility == View.VISIBLE) {
                    if (binding.nameTvId.text.toString().isEmpty() && binding.phoneTvId.text.toString().isEmpty()
                        && binding.emailTvId.text.toString().isEmpty() && binding.linkTvId.text.toString().isEmpty()
                        && binding.addressTvId.text.toString().isEmpty()) {
                        Toast.makeText(applicationContext, "All fields cannot be empty", Toast.LENGTH_SHORT).show()
                    } else {
                        generateQRCode()
                    }
                }
            }
            R.id.generateQRSaveBtnId -> {
                if (!checkPermissionForExternalStorage()) {
                    Toast.makeText(
                        this,
                        "External Storage permission needed. Please allow in App Settings for additional functionality.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    if (bitmap != null) {
                        saveImage(bitmap!!)
                    }
                }
            }
        }
    }

    //function for requesting storage access
    private fun requestPermissionForExternalStorage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            Toast.makeText(
                this,
                "External Storage permission needed. Please allow in App Settings for additional functionality.",
                Toast.LENGTH_LONG
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
            )
        }
    }

    //function for checking storage permission
    private fun checkPermissionForExternalStorage(): Boolean {

        val result =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    //function for saving image into gallery
    private fun saveImage(image: Bitmap): String {
        var savedImagePath: String? = null

        val imageFileName = "QR" + getTimeStamp() + ".jpg"
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "/QRGenerator"
        )
        var success = true
        if (!storageDir.exists()) {
            success = storageDir.mkdirs()
        }
        if (success) {
            val imageFile = File(storageDir, imageFileName)
            savedImagePath = imageFile.getAbsolutePath()
            try {
                var fOut = FileOutputStream(imageFile)
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.close()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

            // Add the image to the system gallery
            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val f = File(savedImagePath)
            val contentUri = Uri.fromFile(f)
            mediaScanIntent.data = contentUri
            sendBroadcast(mediaScanIntent)
            Toast.makeText(
                this,
                "QR Image saved into folder: QRGenerator in Gallery",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(this, "ERROR SAVING IMAGE", Toast.LENGTH_SHORT).show()
        }
        return savedImagePath!!
    }


    //Function for Generating QR code

    private fun generateQRCode() {

        val manager = getSystemService(WINDOW_SERVICE) as WindowManager
        val display: Display = manager.defaultDisplay
        val point = Point()
        display.getSize(point)
        val width: Int = point.x
        val height: Int = point.y
        var dimen = if (width < height) width else height
        dimen = dimen * 3 / 4

        if (binding.linkTiId.visibility == View.VISIBLE) {
            qrgEncoder = QRGEncoder(binding.linkTvId.text.toString(), null, QRGContents.Type.EMAIL, dimen)

        } else if (binding.vCardLayoutId.visibility == View.VISIBLE) {
            qrgEncoder = QRGEncoder(binding.nameTvId.text.toString(), null, QRGContents.Type.TEXT, dimen)
            qrgEncoder = QRGEncoder(binding.phoneTvId.text.toString(), null, QRGContents.Type.PHONE, dimen)
            qrgEncoder = QRGEncoder(binding.emailTvId.text.toString(), null, QRGContents.Type.EMAIL, dimen)
            qrgEncoder = QRGEncoder(binding.linkTvId.text.toString(), null, QRGContents.Type.TEXT, dimen)
            qrgEncoder = QRGEncoder(binding.addressTvId.text.toString(), null, QRGContents.Type.TEXT, dimen)

        }

        try {
            bitmap = qrgEncoder!!.encodeAsBitmap()
            if (bitmap!=null){
                binding.qrCodeImgId.setImageBitmap(bitmap)
                binding.generateQRSaveBtnId.visibility = View.VISIBLE
            }



        } catch (e: WriterException) {
            Log.e("Tag", e.toString())
        }


    }

/*    private fun generateQRCode() {
        if(binding.vCardLayoutId.visibility == View.VISIBLE)
        {


            val vCard = VCard(binding.nameTvId.text.toString())
                .setEmail(binding.emailTvId.text.toString())
                .setAddress(binding.addressTvId.text.toString())
                .setPhoneNumber(binding.phoneTvId.text.toString())
                .setWebsite(binding.webTvId.text.toString())
            qrImage = net.glxn.qrgen.android.QRCode.from(vCard).bitmap()
            if(qrImage != null)
            {
                binding.qrCodeImgId.setImageBitmap(qrImage)
                binding.generateQRSaveBtnId.visibility = View.VISIBLE
            }
        }
        else if(binding.linkTvId.visibility == View.VISIBLE)
        {
            qrImage = net.glxn.qrgen.android.QRCode.from(binding.linkTvId.text.toString()).bitmap()
            if(qrImage != null)
            {
                binding.qrCodeImgId.setImageBitmap(qrImage)
               binding.generateQRSaveBtnId.visibility = View.VISIBLE
            }
        }
    }*/

    private fun getTimeStamp(): String? {
        val tsLong = System.currentTimeMillis() / 1000
        val ts = tsLong.toString()

        return ts
    }
}


