package android.pakaco.openglsphere;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Administrator on 2017/7/4.
 */
public class SphereRender implements GLSurfaceView.Renderer {

    private static final String TAG="SphereRender";
    private Context mContext;
    private Sphere mSphere;
    private final float[] mCamera = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewProjectionMatrix = new float[16];

    private float CAMERA_Z = 0.5f;
    private float[] mView = new float[16];
    private float mMMtx[] = new float[16];
    private int[] mResourceId = {R.drawable.photo_sphere_1, R.drawable.photo_sphere_2, R.drawable.photo_sphere_3,
            R.drawable.photo_sphere_4};


    private float mAngleX= 0.0f;
    private float mAngleY= 0.0f;
    private float mAngleZ= 0.0f;
    private float mZoom= 1.0f;

    /**
     * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
     * of being located at the center of the universe) to world space.
     */
    private float[] mModelMatrix = new float[16];

    public SphereRender(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated");
        GLES20.glClearColor(1f, 0f, 0f, 1f);// Dark background so text shows up well.

        /** Creating the Sphere for Rendering images inside the sphere **/
        mSphere = new Sphere(mContext, 50, 5f);
        mSphere.loadTexture(mContext, mResourceId[0]);
        checkGLError("onSurfaceCreated");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        /**Setting the view port to the width and height of the device **/
        GLES20.glViewport(0, 0, width, height);
        /** Setting the projection Matrix for the view **/
        MatrixHelper.perspectiveM(mProjectionMatrix, 90, (float) width
                / (float) height, 1f, 10f);
       //Matrix.frustumM(mProjectionMatrix,0,left,right,bottom,top,3,9);
      // Matrix.perspectiveM(mProjectionMatrix,0,45,width/height, 0.01F, 10F);
        /** Setting the camera in the center **/
        Matrix.setLookAtM(mCamera, 0, 0.0f, 0.0f, 4, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
       // float fov = 90F + 15F * 0.015F;
       // Matrix.perspectiveM(mProjectionMatrix, 0, (float)Math.min(90D, fov), width/height, 0.01F, 10F);
        //Matrix.setLookAtM(mCamera, 0, 0.0F, 0.0F, (float)Math.max(0.0D, (double)fov - 90D) / 10F, 0.0F, 0.0F, -1F, 0.0F, -1F, 0.0F);
        checkGLError("onReadyToDraw");
        Log.i(TAG, "onSurfaceChanged");
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // float[] scratch = new float[16];
        //   long time = SystemClock.uptimeMillis() % 4000L;
        //   float angle = 0.090f * ((int) time);
        // Matrix.setRotateM(mRotationMatrix, 0, angle, 0, 1, 0.0f);

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        /** Camera should move based on the user movement **/

        // Draw some cubes.
        Matrix.setIdentityM(mModelMatrix, 0);
        //Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -5.0f);
        //Matrix.rotateM(mModelMatrix, 0, (float) Math.toDegrees(-mSensorFusion.fusedOrientation[1]), 1.0f, 0.0f, 0.0f); // pitch
        ///Matrix.rotateM(mModelMatrix, 0, (float) Math.toDegrees(mSensorFusion.fusedOrientation[2]), 0.0f, 1.0f, 0.0f); // roll
        //Matrix.rotateM(mModelMatrix, 0, (float) Math.toDegrees(mSensorFusion.fusedOrientation[0]), 0.0f, 0.0f, 1.0f); // yaw
        Matrix.rotateM(mModelMatrix, 0, mAngleX, 1.0f, 0.0f, 0.0f); // pitch
        Matrix.rotateM(mModelMatrix, 0, mAngleY, 0.0f, 1.0f, 0.0f); // roll
        Matrix.rotateM(mModelMatrix, 0, mAngleZ, 0.0f, 0.0f, 1.0f); // yaw
        Matrix.scaleM(mModelMatrix,0,0.5f,0.5f,0.5f);


        /** setting the view projection matrix **/
        Matrix.multiplyMM(mViewProjectionMatrix, 0, mCamera, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mViewProjectionMatrix, 0, mProjectionMatrix, 0, mViewProjectionMatrix, 0);

        //  multiplyMM(mViewProjectionMatrix, 0, mProjectionMatrix, 0, mCamera, 0);
        //  multiplyMM(scratch, 0, mViewProjectionMatrix, 0, mRotationMatrix, 0);

        /** Drawing the sphere  and apply the projection to it**/
        mSphere.draw(mViewProjectionMatrix);

        checkGLError("onDrawEye");

            //resetTexture();



    }

    /**
     * Reload the texture
     */
    private void resetTexture() {
        mSphere.deleteCurrentTexture();
        checkGLError("after deleting texture");
        mSphere.loadTexture(mContext, mResourceId[0]);
        checkGLError("loading texture");
    }
    /**
     * Checks if we've had an error inside of OpenGL ES, and if so what that error is.
     *
     * @param label Label to report in case of error.
     */
    private static void checkGLError(String label) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, label + ": glError " + error);
            throw new RuntimeException(label + ": glError " + error);
        }
    }

    public void setAngle(float x,float y,float z){
        mAngleX = x;
        mAngleY = y;
        mAngleZ = z;
    }
    /**
     * The zoom factor for the touch to zoom.
     *
     * @param mult the zoom factor.
     */
    public final void zoom(float mult)
    {
         mZoom= mult;
    }
}
