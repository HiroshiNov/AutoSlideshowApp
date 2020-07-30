package jp.techacademy.hiroshi.murata.autoslideshowapp

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.os.Handler


class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 1

    private var mTimer: Timer? = null

    private var mHandler = Handler()

    private var resolver: ContentResolver? = null

    private var cursor: Cursor? = null

    private var slideshowToggler = false

    private var imageUri: Uri? = null

    private var fieldIndex: Int? = null

    private var id: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Check user's permission.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Being allowed.
                getContentsInfo()
            } else {
                //Not being allowed.
                //Show the dialog.
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
            }
        } else {
            getContentsInfo()
        }

        playpauseButton.setOnClickListener {
            //After 2sec from you tapped this button, this app display each images in gerallry for 2 sec.
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                slideshowToggler = !slideshowToggler

                Log.d("slideshowToggler : ", slideshowToggler.toString())

                if (slideshowToggler) {
                    playpauseButton.text = "停止"

                    mTimer = Timer()
                    mTimer!!.schedule(object : TimerTask() {
                    override fun run() {
                        Log.d("ANDROID", "URI :" + imageUri.toString())
                        mHandler.post {
                                imageView.setImageURI(imageUri)
                            }
                        if (cursor!!.moveToNext()) {
//                            cursor!!.moveToNext()  <- .moveToNext() had been already excuted. This code cause the error.
                            fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                            id = cursor!!.getLong(fieldIndex!!)
                            imageUri = ContentUris.withAppendedId(
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        id!!)
                        } else {
                            cursor!!.moveToFirst()
                            fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                            id = cursor!!.getLong(fieldIndex!!)
                            imageUri = ContentUris.withAppendedId(
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        id!!)
                            }
                        }
                    }, 2000, 2000)
                } else {
                    if (mTimer != null) {
                        mTimer!!.cancel()
                        mTimer = null
                        playpauseButton.text = "再生"
                    }
                }
            } else {
                // 許可されていない
                    Log.d("ANDROID","You are not allowed.")
            }

        }

        nextButton.setOnClickListener {
            //This button is unable to tap when slideshow is going on.
            //When you tap this button, this app display next image.

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                if (!slideshowToggler) {
                    if (cursor!!.moveToNext()) {
                        fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                        id = cursor!!.getLong(fieldIndex!!)
                        imageUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id!!
                        )
                        Log.d("ANDROID", "URI :" + imageUri.toString())
                        imageView.setImageURI(imageUri)
                    } else {
                        cursor!!.moveToFirst()
                        fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                        id = cursor!!.getLong(fieldIndex!!)
                        imageUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id!!
                        )
                        Log.d("ANDROID", "URI :" + imageUri.toString())
                        imageView.setImageURI(imageUri)
                    }
                }
            } else {
                // 許可されていない
                Log.d("ANDROID","You are not allowed.")
            }
        }

        backButton.setOnClickListener {
            //This button is unable to tap when slideshow is going on.
            //When you tap this button, this app display previous image.

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                if (!slideshowToggler) {
                    if (cursor!!.moveToPrevious()) {
                        fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                        id = cursor!!.getLong(fieldIndex!!)
                        imageUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id!!
                        )
                        Log.d("ANDROID", "URI :" + imageUri.toString())
                        imageView.setImageURI(imageUri)
                    } else {
                        cursor!!.moveToLast()
                        fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                        id = cursor!!.getLong(fieldIndex!!)
                        imageUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id!!
                        )
                        Log.d("ANDROID", "URI :" + imageUri.toString())
                        imageView.setImageURI(imageUri)
                    }
                }
            } else {
                // 許可されていない
                Log.d("ANDROID","You are not allowed.")
            }
        }
    }
    override fun onRequestPermissionsResult(requestsCode: Int, permissions: Array<String>, grantResults: IntArray){
        when (requestsCode){
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d("ANDROID","Request has been denied.")
                    getContentsInfo()
                }else{
                    Log.d("ANDROID","Request has been denied.")
                }
        }
    }

    fun getContentsInfo() {
        resolver = contentResolver
        cursor = resolver!!.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null)
        if (cursor!!.moveToFirst()){
            fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            id = cursor!!.getLong(fieldIndex!!)
            imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id!!)
        }
    }
}