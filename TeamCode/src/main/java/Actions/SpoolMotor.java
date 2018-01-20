package Actions;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import MotorControllers.MotorController;

/**
 * Created by robotics on 11/7/17.
 */

/*
    A class to set up a motor that has a spool of string attached to it
    ex: our extendotron lift motor
 */
public class SpoolMotor implements ActionHandler{
    MotorController motor;
    HardwareMap hardwareMap;
    private double extendSpeedInPerSecond = 0;
    private double retractSpeedInPerSecond = 0;
    private double extendPower = 0.5;
    private long startTickLocation = 0;
    private double maxExtendLoc;

    public SpoolMotor(MotorController m, double extendInPerSec, double retractInPerSec, double maxEtendInches, HardwareMap h){
        hardwareMap = h;
        motor = m;
        m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        retractSpeedInPerSecond = retractInPerSec;
        extendSpeedInPerSecond = extendInPerSec;
        maxExtendLoc = maxEtendInches;
        startTickLocation = m.getCurrentTick();
    }


    public void setDirection(DcMotor.Direction direction){
        motor.setMotorDirection(direction);
    }

    public void extend(){
        motor.setInchesPerSecondVelocity(extendSpeedInPerSecond);
    }

    public DcMotor.RunMode getMotorControllerMode() {
        return motor.getMotorRunMode();
    }

    public void retract()
    {
        motor.setInchesPerSecondVelocity(-retractSpeedInPerSecond);
    }

    public void pause()
    {
//        if(motor.getMotorControllerMode() != MotorController.MotorControllerMode.SPEED_CONTROLLER) motor.setMotorControllerMode(MotorController.MotorControllerMode.SPEED_CONTROLLER);
        motor.setInchesPerSecondVelocity(0);
    }

    public void extendWithPower(){
        motor.setMotorPower(extendPower);
    }

    public void retractWithPower(){
        motor.setMotorPower(-extendPower);
    }

    public void setPower(double power){
        motor.setMotorPower(power);
    }

    public void setExtendSpeed(double speed){
        extendSpeedInPerSecond = speed;
    }

    public void setRetractSpeed(double speed){
        extendSpeedInPerSecond = speed;
    }

    public void setExtendPower(double power){
        extendPower = power;
    }

    public void resetCurrentPositionToZero(){
        //todo implement
    }

    public void setPostitionInches(double positionInInches){
        motor.setPositionInches(positionInInches);
    }

    public void setPostitionTicks(int ticks){
        motor.setPositionTicks(ticks);
    }

    public long getPosition(){
        return motor.getCurrentTick();
    }

    public void setMode(DcMotor.RunMode mode){
        motor.setMotorRunMode(mode);
    }

    public void holdPosition(){
        motor.setMotorRunMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPositionTicks((int)motor.getCurrentTick());
        motor.setMotorPower(1);
    }

    @Override
    public boolean doAction(String action, long maxTimeAllowed) {
        return false;
    }

    @Override
    public boolean stopAction(String action) {
        return false;
    }

    @Override
    public boolean startDoingAction(String action) {
        return false;
    }

    @Override
    public void stop() {
        motor.setInchesPerSecondVelocity(0);
        motor.killMotorController();
    }
}
