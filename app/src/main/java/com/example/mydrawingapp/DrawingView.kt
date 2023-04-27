package com.example.mydrawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context : Context, attrs : AttributeSet) : View(context, attrs){

    private var mDrawPath : CustomPath? = null
    private var mCanvasBitmap : Bitmap? = null
    private var mDrawPaint : Paint? = null
    private var mCanvasPaint : Paint? = null
    private var mBrushSize : Float = 0.toFloat()
    private var color = Color.BLACK
    private var canvas : Canvas? = null
    private var mPaths = ArrayList<CustomPath>()    // tu zapisujemy to co namalowaliśmy

    init{
        setUpDrawing()
    }
    private fun setUpDrawing(){
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color, mBrushSize)
        mDrawPaint!!.color = color
        mDrawPaint!!.style = Paint.Style.STROKE
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        //mBrushSize = 20.toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {//jak się zmieni rozmiar
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888) //265 wartośći dla każdego koloru
        canvas = Canvas(mCanvasBitmap!!)

    }
    // Change Canvas to Canvas? if fails
    override fun onDraw(canvas: Canvas) { //draw on canvas
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!, 0f,0f, mCanvasPaint) // left, top -> pozycja w ktorej zaczynamy

        for(path in mPaths){    //zapamiętuje wsyztskie namalwoena linie
            mDrawPaint!!.strokeWidth = path.brushThickens
            mDrawPaint!!.color = path.color
            canvas.drawPath(path, mDrawPaint!!)
        }

        if(!mDrawPath!!.isEmpty) {
            mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickens
            mDrawPaint!!.color = mDrawPath!!.color
            canvas.drawPath(mDrawPath!!, mDrawPaint!!)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean { //jak się dotknie
        val touchX = event?.x
        val touchY = event?.y

        when(event?.action){                //jak dzieją się jakieś akcje związane z ruchem na ekranie
            MotionEvent.ACTION_DOWN -> {    // jak dotkniemy
                mDrawPath!!.color = color
                mDrawPath!!.brushThickens = mBrushSize

                mDrawPath!!.reset() // clear all the paths
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.moveTo(touchX,touchY)
                    }
                }
            }
            MotionEvent.ACTION_MOVE ->{     //jak przesuwamy
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.lineTo(touchX,touchY)
                    }
                }
            }
            MotionEvent.ACTION_UP ->{
                mPaths.add(mDrawPath!!)
                mDrawPath = CustomPath(color, mBrushSize)
            }
            else -> return false
        }
        invalidate()        //unieważnia widok

        return true
    }
    fun setSizeForBrush(newSize : Float){
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, resources.displayMetrics
            )
        mDrawPaint!!.strokeWidth = mBrushSize
    }
    internal inner class CustomPath(var color: Int,
                                    var brushThickens: Float) : Path(){//dostęp tylko tutaj

    }


}