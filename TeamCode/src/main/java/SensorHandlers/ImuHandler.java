package SensorHandlers;

import android.os.Environment;
import android.util.Log;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ReadWriteFile;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;
import Autonomous.Location;

import java.io.File;
import java.io.IOException;

/**
 * Created by root on 8/18/17.
 */

/*
    A class to handle the IMU for us - it sets up the gyroscope in the REV modules for us
 */
public class ImuHandler extends Thread{
    private long updateDelay = 200;
    private long lastUpdateStart;
    private BNO055IMU imu;
    private Velocity velocities;
    private Orientation angles;
    private volatile boolean shouldRun;
    private double orientationOffset;
    private HardwareMap map;

    public ImuHandler(String name, double robotOrientationOffset, HardwareMap h){

        initIMU(name, h);
        shouldRun = true;
        orientationOffset = robotOrientationOffset;
        new Thread(new Runnable(){
            public void run(){
                while(shouldRun) {
                    lastUpdateStart = System.currentTimeMillis();
                    try{
                        updateIMU();
                    } catch (Exception e){
                        shouldRun = false;
                        throw new RuntimeException(e);
                    }
                    long timeLeft = updateDelay - (System.currentTimeMillis() - lastUpdateStart);
                    if(timeLeft > 0) safetySleep(timeLeft);
                }
                //imu.close();
            }
        }).start();
    }

    public void setOrientationOffset(double offset){
        orientationOffset = offset + orientationOffset;
    }

    private void safetySleep(long time){
        long start = System.currentTimeMillis();
        while(System.currentTimeMillis() - start < time && shouldRun);
    }

    private void initIMU(String name, HardwareMap hardwareMap){
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        //parameters.calibrationDataFile = "AdafruitIMUCalibration.json"; // see the calibration sample opmode
        File file = new File(Environment.getExternalStorageDirectory() + "/imu1/AdafruitIMUCalibration.json");
        String serialized = null;
        try {
            serialized = ReadWriteFile.readFileOrThrow(file);
        } catch (IOException e) {
            Log.e("File error!",e.toString());
        }
        BNO055IMU.CalibrationData dat = BNO055IMU.CalibrationData.deserialize(serialized);
        parameters.calibrationData = dat;
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        imu = hardwareMap.get(BNO055IMU.class, name);
        imu.initialize(parameters);
        safetySleep(10);
        imu.startAccelerationIntegration(new Position(), new Velocity(), 200);
        //imu.startAccelerationIntegration();
        Log.d("IMU Status", imu.getSystemStatus().toString());
        Log.d("IMU Calibration", imu.getCalibrationStatus().toString());
        updateIMU();
    }

    private void updateIMU(){
        try {
            angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            velocities = imu.getVelocity();
        } catch (Exception e){
            stopIMU();
            throw new RuntimeException(e);

        }
    }

    public double getOrientationOffset(){
        return  orientationOffset;
    }

    public Location getLocation(){
        Position p = imu.getPosition();
        Location l = new Location(p.x,p.y);
        return l; 
    }

    /*
        returns the orientation of the robot, 0 to 359 degrees
     */
    public double getOrientation(){
        double angle = 360 - angles.firstAngle;
        if(angle < 0) angle += 360;
        else if(angle >= 360) angle -= 360;
        angle += orientationOffset;
        angle %= 360;
        return angle;
    }

    public double[] getAngles(){
        double[] newAngles = {getOrientation(), angles.secondAngle, angles.thirdAngle};
        return newAngles;
    }

    public void stopIMU(){
        shouldRun = false;
    }

    /*
        returns the heading of the robot, 0 to 359 degrees
     */
    public double getHeadingFromVelocity(){
        //cannot use z heading as it represents the orientation, not the heading
        //couple of options.... use accelerometers to determine the headingvimu.getAngularVelocity(); //we'll see if this works
        //turn the robot to always face forward
        //use multiple compasses -- one per major axis and use trig to represent actual heading
        double heading = Math.toDegrees(Math.atan2(velocities.yVeloc,velocities.xVeloc));
        //Log.d("IMU XVel",Double.toString(velocities.xVeloc));
        //Log.d("IMU YVel",Double.toString(velocities.yVeloc));
        if(heading > 360); heading -= 360;
        if(heading < 0) heading += 360;
        return heading; // I have absolutely no idea how well this will work... TODO Test this
    }
}
