package com.example.planets

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import android.opengl.GLSurfaceView




class MainActivity : AppCompatActivity() {

	private var glSurfaceView: GLSurfaceView? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		requestWindowFeature(Window.FEATURE_NO_TITLE)
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
		glSurfaceView = GLSurfaceView(this)
		glSurfaceView?.setEGLConfigChooser(8,8,8,8,16,0)
		glSurfaceView?.setRenderer(GLRenderer(this))
		setContentView(glSurfaceView)
	}

	override fun onResume() {
		super.onResume()
		glSurfaceView?.onResume()
	}

	override fun onPause() {
		super.onPause()
		glSurfaceView?.onPause()
	}
}