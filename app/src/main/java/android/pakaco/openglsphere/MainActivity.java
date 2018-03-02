package android.pakaco.openglsphere;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener{
    private static final String TAG="sphere";

    private GLSurfaceView mGLSurfaceView;
    private SphereRender mSphereRender;

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;
    private float distance = 0;
    private float mAngleX;
    private float mAngleY;
    private float mAngleZ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setOnTouchListener(this);
        setContentView(mGLSurfaceView);
        mSphereRender = new SphereRender(this);
        mGLSurfaceView.setRenderer( mSphereRender);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        float x = event.getX();
        float y = event.getY();
        float newDist = 0;
        Log.d(TAG, "event.getAction()=" + event.getAction());
        switch (event.getAction())
        {
            case MotionEvent.ACTION_MOVE:
                // rotate
                if (event.getPointerCount() == 1)
                {
                    float dx = x - mPreviousX;
                    float dy = y - mPreviousY;
                    float dr = (float) Math.sqrt(dx*dx+dy*dy);
                    mPreviousX =x;
                    mPreviousY =y;
                    Log.d(TAG, "wz delta: " + dx+"  dy:"+dy+"  dr:"+dr);

                    if(mPreviousX>0&&mPreviousY>0&&dr<30) {
                        mAngleX = mAngleX + dx * TOUCH_SCALE_FACTOR;
                        mAngleX = mAngleX % 360;


                        mAngleY = dy * TOUCH_SCALE_FACTOR + mAngleY;
                        mAngleY = mAngleY % 360;
                    }

                    Log.d(TAG,"mAngleY="+mAngleY+",mAngleX="+mAngleX);
                    mSphereRender.setAngle(-mAngleY, -mAngleX,0);

                }

                // pinch to zoom
                if (event.getPointerCount() == 2)
                {
                    if (distance == 0)
                    {
                        distance = fingerDist(event);
                    }
                    newDist = fingerDist(event);
                    float d = distance / newDist;
                    mSphereRender.zoom(d);

                    distance = newDist;
                }
                mGLSurfaceView.requestRender();
        }

        return true;
    }

    protected final float fingerDist(MotionEvent event)
    {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }
}
