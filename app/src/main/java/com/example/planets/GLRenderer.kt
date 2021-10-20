package com.example.planets

import android.content.Context
import android.opengl.GLSurfaceView
import android.opengl.GLU
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

internal class GLRenderer(private val context: Context) : GLSurfaceView.Renderer {

	private val mEarth: Sphere = Sphere(2, 1f)
	private val mSun: Sphere = Sphere(3, 2f)
	private val mMoon: Sphere = Sphere(2, 1f)

	private var p1 = 0f
	private var p2 = 0f
	private var p3 = 0f

	companion object {

		private const val AXIAL_TILT_DEGREES = 30
		private const val CLEAR_RED = 0.0f
		private const val CLEAR_GREEN = 0.0f
		private const val CLEAR_BLUE = 0.0f
		private const val CLEAR_ALPHA = 0.5f
		private const val OBJECT_DISTANCE = -10.0f
	}

	override fun onDrawFrame(gl: GL10) {
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f)
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)
		gl.glLoadIdentity()
		gl.glTranslatef(0.0f, 0.0f, OBJECT_DISTANCE)
		gl.glRotatef(AXIAL_TILT_DEGREES.toFloat(), 1f, 0f, 0f)
		p1 = if (p1 > 360) 0f else p1 + 0.5f
		gl.glRotatef(p1, 0f, 1f, 0f)
		gl.glTranslatef(10f, 0.0f, 0.0f)
		mSun.draw(gl)
		p2 = if (p2 > 360) 0f else p2 + 1f
		gl.glRotatef(p2, 0f, 1f, 0f)
		gl.glTranslatef(4.5f, 0.0f, 0.0f)
		mEarth.draw(gl)
		p3 = if (p3 > 360) 0f else p3 + 2f
		gl.glRotatef(p3, 0f, 2f, 1f)
		gl.glScalef(0.5f, 0.5f, 0.5f)
		gl.glTranslatef(2.0f, -1.5f, 2.0f)
		mMoon.draw(gl)
	}

	override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
		gl.glViewport(0, 0, width, height)
		gl.glMatrixMode(GL10.GL_PROJECTION)
		gl.glLoadIdentity()
		GLU.gluPerspective(gl, 90.0f, width.toFloat() / height, 1f, 30.0f)
		gl.glMatrixMode(GL10.GL_MODELVIEW)
		gl.glLoadIdentity()
	}

	override fun onSurfaceCreated(gl: GL10, config: EGLConfig?) {
		mSun.loadGLTexture(gl, context, R.drawable.shrek)
		mEarth.loadGLTexture(gl, context, R.drawable.osel)
		mMoon.loadGLTexture(gl, context, R.drawable.kot)
		gl.glEnable(GL10.GL_TEXTURE_2D)
		gl.glShadeModel(GL10.GL_SMOOTH)
		gl.glClearColor(CLEAR_RED, CLEAR_GREEN, CLEAR_BLUE, CLEAR_ALPHA)
		gl.glClearDepthf(1.0f)
		gl.glEnable(GL10.GL_DEPTH_TEST)
		gl.glDepthFunc(GL10.GL_LEQUAL)
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST)
	}
}