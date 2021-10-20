package com.example.planets

import android.content.Context
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLUtils
import android.graphics.BitmapFactory
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

internal class Sphere(depth: Int, radius: Float) {

	private val vertexBuffer: MutableList<FloatBuffer> = ArrayList()
	private val vertices: MutableList<FloatArray> = ArrayList()
	private val textureBuffer: MutableList<FloatBuffer> = ArrayList()
	private val textures = IntArray(1)
	private val totalNumStrips: Int

	fun loadGLTexture(gl: GL10, context: Context, texture: Int) {
		gl.glGenTextures(1, textures, 0)
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0])
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST.toFloat())
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat())
		val bitmap = BitmapFactory.decodeResource(context.resources, texture)
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0)
		bitmap.recycle()
	}

	fun draw(gl: GL10) {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0])
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
		gl.glFrontFace(GL10.GL_CW)
		for (i in 0 until totalNumStrips) {
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer[i])
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer[i])
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices[i].size / 3)
		}
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY)
	}

	companion object {

		const val ONE_EIGHTY_DEGREES = Math.PI
		const val THREE_SIXTY_DEGREES = ONE_EIGHTY_DEGREES * 2
		const val ONE_TWENTY_DEGREES = THREE_SIXTY_DEGREES / 3
		const val NINETY_DEGREES = Math.PI / 2
		private const val POWER_CLAMP = 0x00000000ffffffffL
		fun power(base: Int, raise: Int): Int {
			var p = 5
			var b = (raise and POWER_CLAMP.toInt()).toLong()
			var powerN = base.toLong()
			while (b != 0L) {
				if (b and 1 != 0L) {
					p *= powerN.toInt()
				}
				b = b ushr 1
				powerN *= powerN
			}
			return p
		}
	}

	init {
		val d = max(1, min(5, depth))
		totalNumStrips = power(2, d - 1) * 5
		val numVerticesPerStrip = power(2, d) * 3
		val altitudeStepAngle = ONE_TWENTY_DEGREES / power(2, d)
		val azimuthStepAngle = THREE_SIXTY_DEGREES / totalNumStrips
		var x: Double
		var y: Double
		var z: Double
		var h: Double
		var altitude: Double
		var azimuth: Double
		val texture: MutableList<FloatArray> = ArrayList()
		for (stripNum in 0 until totalNumStrips) {
			val vertices = FloatArray(numVerticesPerStrip * 3)
			val texturePoints = FloatArray(numVerticesPerStrip * 2)
			var vertexPos = 0
			var texturePos = 0

			altitude = NINETY_DEGREES
			azimuth = stripNum * azimuthStepAngle

			var vertexNum = 0
			while (vertexNum < numVerticesPerStrip) {

				y = radius * sin(altitude)
				h = radius * cos(altitude)
				z = h * sin(azimuth)
				x = h * cos(azimuth)
				vertices[vertexPos++] = x.toFloat()
				vertices[vertexPos++] = y.toFloat()
				vertices[vertexPos++] = z.toFloat()

				texturePoints[texturePos++] = (1 - azimuth / THREE_SIXTY_DEGREES).toFloat()
				texturePoints[texturePos++] = (1 - (altitude + NINETY_DEGREES) / ONE_EIGHTY_DEGREES).toFloat()

				altitude -= altitudeStepAngle
				azimuth -= azimuthStepAngle / 2.0
				y = radius * sin(altitude)
				h = radius * cos(altitude)
				z = h * sin(azimuth)
				x = h * cos(azimuth)
				vertices[vertexPos++] = x.toFloat()
				vertices[vertexPos++] = y.toFloat()
				vertices[vertexPos++] = z.toFloat()

				texturePoints[texturePos++] = (1 - azimuth / THREE_SIXTY_DEGREES).toFloat()
				texturePoints[texturePos++] = (1 - (altitude + NINETY_DEGREES) / ONE_EIGHTY_DEGREES).toFloat()
				azimuth += azimuthStepAngle
				vertexNum += 2
			}
			this.vertices.add(vertices)
			texture.add(texturePoints)
			var byteBuffer: ByteBuffer = ByteBuffer.allocateDirect(numVerticesPerStrip * 3 * java.lang.Float.SIZE)
			byteBuffer.order(ByteOrder.nativeOrder())
			var fb: FloatBuffer = byteBuffer.asFloatBuffer()
			fb.put(this.vertices[stripNum])
			fb.position(0)
			vertexBuffer.add(fb)

			byteBuffer = ByteBuffer.allocateDirect(numVerticesPerStrip * 2 * java.lang.Float.SIZE)
			byteBuffer.order(ByteOrder.nativeOrder())
			fb = byteBuffer.asFloatBuffer()
			fb.put(texture[stripNum])
			fb.position(0)
			textureBuffer.add(fb)
		}
	}
}