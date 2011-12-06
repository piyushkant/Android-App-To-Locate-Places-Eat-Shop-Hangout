package com.playcez.bean;

import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class MyGestureDetector extends SimpleOnGestureListener {
	
	ResultBean resultBeanObj;
	com.playcez.Result result;
	
    public com.playcez.Result getResult() {
		return result;
	}


	public void setResult(com.playcez.Result result) {
		this.result = result;
	}


	public ResultBean getResultBeanObj() {
		return resultBeanObj;
	}


	public void setResultBeanObj(ResultBean resultBeanObj) {
		this.resultBeanObj = resultBeanObj;
	}


	@Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        try {
            if (Math.abs(e1.getY() - e2.getY()) > resultBeanObj.getSwipeMaxOffPath())
                return false;
            // right to left swipe
            if(e1.getX() - e2.getX() > resultBeanObj.getSwipeMinDistance() && Math.abs(velocityX) > resultBeanObj.getSwipeThresholdVelocity()) {
                resultBeanObj.getFlipper().setInAnimation(result.inFromRightAnimation());
                resultBeanObj.getFlipper().setOutAnimation(result.outToLeftAnimation());
                resultBeanObj.getFlipper().showNext();  
            }  else if (e2.getX() - e1.getX() > resultBeanObj.getSwipeMinDistance() && Math.abs(velocityX) > resultBeanObj.getSwipeThresholdVelocity()) {
                resultBeanObj.getFlipper().setInAnimation(result.inFromLeftAnimation());
                resultBeanObj.getFlipper().setOutAnimation(result.outToRightAnimation());
                resultBeanObj.getFlipper().showPrevious();
            }
        } catch (Exception e) {
            // nothing
        	e.printStackTrace();
        }
        return false;
    }

}
