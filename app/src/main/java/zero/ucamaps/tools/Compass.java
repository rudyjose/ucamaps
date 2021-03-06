package zero.ucamaps.tools;

import com.esri.android.map.MapView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;

import zero.ucamaps.R;

public class Compass extends View implements SensorEventListener {

    float mAngle = 0;
    Paint mPaint;
    Bitmap mBitmap;
    Matrix mMatrix;
    MapView mMapView;

    // Handles the sensors
    public SensorManager sensorManager;

    // Sensors for accelerometer and magnetometer
    public Sensor gsensor;
    public Sensor msensor;
    public Sensor asensor;

    // Used for orientation of the compass
    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];

    // The angle of rotation of the compass
    private float azimuth = 0f;

    // To send and receive notification from the sensors.
    public SensorEventListener sensorEventListener;

    int width;

    public Compass(Context context) {
        super(context);

        mPaint = new Paint();
        mMatrix = new Matrix();

        mBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_compass);

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        asensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gsensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
    }

    public void start() {
        // A copy of instance which is used to restart the sensors
        sensorEventListener = this;

        // Enable the sensors
        sensorManager.registerListener(this, asensor,SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, msensor,SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, gsensor,SensorManager.SENSOR_DELAY_GAME);
    }

    public void stop() {
        // Disable the sensors
        sensorManager.unregisterListener(this);
    }

    /**
     * Updates the angle, in degrees, at which the compass is draw within this
     * view.
     */
    public void setRotationAngle(double angle) {
        // Save the new rotation angle.
        mAngle = (float) angle;

        // Force the compass to re-paint itself.
        postInvalidate();
    }

    /** Draws the compass image at the current angle of rotation on the canvas. */
    @Override
    protected void onDraw(Canvas canvas) {

        // Reset the matrix to default values.
        mMatrix.reset();

        // Pass the current rotation angle to the matrix. The center of rotation
        // is set to be the center of the bitmap.
        mMatrix.postRotate(-this.mAngle, mBitmap.getHeight() / 2, mBitmap.getWidth() / 2);

        // Use the matrix to draw the bitmap image of the compass.
        canvas.drawBitmap(mBitmap, mMatrix, mPaint);

        super.onDraw(canvas);

    }

    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.97f;

        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                mGravity[0] = alpha * mGravity[0] + (1 - alpha)
                        * event.values[0];
                mGravity[1] = alpha * mGravity[1] + (1 - alpha)
                        * event.values[1];
                mGravity[2] = alpha * mGravity[2] + (1 - alpha)
                        * event.values[2];
            }

            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {

                mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha)
                        * event.values[0];
                mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha)
                        * event.values[1];
                mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha)
                        * event.values[2];

            }

            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);

            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimuth = (float) Math.toDegrees(orientation[0]); // orientation
                azimuth = (azimuth + 360) % 360;

                setRotationAngle(-azimuth);
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}