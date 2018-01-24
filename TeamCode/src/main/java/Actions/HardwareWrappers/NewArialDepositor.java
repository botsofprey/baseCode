package Actions.HardwareWrappers;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import Actions.ActionHandler;
import Autonomous.REVColorDistanceSensorController;

/**
 * Created by robotics on 12/2/17.
 */

/*
    A class to set up two motors to use our extendotron and belt for our glyph system
 */
public class NewArialDepositor implements ActionHandler {
    NewSpoolMotor liftMotor;
    NewSpoolMotor belt;
    REVColorDistanceSensorController glyphSensor;
    TouchSensor extendLimit;
    HardwareMap hardwareMap;
    long liftPositionOffsetTicks = 0;
    private final static double FAST_RETRACT_SPEED = 10.0;
    private final static double FAST_EXTEND_SPEED = 15.0;
    private final static double SLOW_RETRACT_SPEED = 1.0;
    private final static double SLOW_EXTEND_SPEED = 5.0;

    public int TICKS_PER_REV;
    public double EXTENDOTRON_SPOOL_DIAMETER_INCHES;

    public enum GLYPH_PLACEMENT_LEVEL{GROUND,ROW1,ROW2,ROW3,ROW4,ROW1_AND_2,ROW3_AND_4};
    public final static double GROUND_LEVEL_PLACEMENT_HEIGHT = 0;
    public final static double ROW1_PLACEMENT_HEIGHT = 6;
    public final static double ROW2_PLACEMENT_HEIGHT = 12;
    public final static double ROW3_PLACEMENT_HEIGHT = 18;
    public final static double ROW4_PLACEMENT_HEIGHT = 24;
    public final static double ROW1_AND_2_PLACEMENT_HEIGHT = 14.5;
    public final static double ROW3_AND_4_PLACEMENT_HEIGHT = 27;

    public NewArialDepositor(HardwareMap hw) throws Exception{
        hardwareMap = hw;
        liftMotor = new NewSpoolMotor("liftMotor","MotorConfig/FunctionMotors/AerialLiftSpool.json", FAST_EXTEND_SPEED, FAST_RETRACT_SPEED, hardwareMap);
        liftMotor.setMotorRunMode(DcMotor.RunMode.RUN_USING_ENCODER);
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        belt = new NewSpoolMotor("belt", "MotorConfig/FunctionMotors/BeltMotor.json", 10, 10, hardwareMap);
        belt.setMotorRunMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        belt.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        glyphSensor = new REVColorDistanceSensorController(REVColorDistanceSensorController.type.GLYPH_STACK_O_TRON, "glyphSensor", hardwareMap);
        extendLimit = hardwareMap.touchSensor.get("extendLimit");
        TICKS_PER_REV = liftMotor.getTicksPerRevolution();
        EXTENDOTRON_SPOOL_DIAMETER_INCHES = liftMotor.getWheelDiameterInInches();
//        zeroBedHeight();
    }

    public void extend(){
        liftMotor.extend();
    }

    public void retract(){
        liftMotor.retract();
    }

    public void slowRetract(){
        liftMotor.retract(.5);
    }

    public void slowExtend(){
        liftMotor.extend(.5);
    }

    public void stopLift(){
        liftMotor.holdPosition();
    }

    public void goToLiftPosition(double positionInInches){
        liftMotor.setMotorRunMode(DcMotor.RunMode.RUN_TO_POSITION);
        //double liftPositionOffsetInches = (double)liftPositionOffsetTicks/TICKS_PER_REV*Math.PI*EXTENDOTRON_DIAMETER_INCHES;
        //long tick = (long)(positionInInches/(Math.PI*EXTENDOTRON_DIAMETER_INCHES)*TICKS_PER_REV);
//        Log.d("Offset inch", Double.toString(liftPositionOffsetInches));
//        Log.d("Offset tick", Long.toString(liftPositionOffsetTicks));
//        Log.d("Desired inch", Double.toString(positionInInches));
//        Log.d("Desired tick", Long.toString(tick));
        liftMotor.setPositionInches(positionInInches);
        liftMotor.setMotorPower(1);
    }

    public double getExtendotronHeight(){
        return liftMotor.getInchesFromStart();
    }

    public void goToGlyphLevel(GLYPH_PLACEMENT_LEVEL level){
        switch(level){
            case GROUND:
                goToLiftPosition(GROUND_LEVEL_PLACEMENT_HEIGHT);
                break;
            case ROW1:
                goToLiftPosition(ROW1_PLACEMENT_HEIGHT);
                break;
            case ROW2:
                goToLiftPosition(ROW2_PLACEMENT_HEIGHT);
                break;
            case ROW3:
                goToLiftPosition(ROW3_PLACEMENT_HEIGHT);
                break;
            case ROW4:
                goToLiftPosition(ROW4_PLACEMENT_HEIGHT);
                break;
            case ROW1_AND_2:
                goToLiftPosition(ROW1_AND_2_PLACEMENT_HEIGHT);
                break;
            case ROW3_AND_4:
                goToLiftPosition(ROW3_AND_4_PLACEMENT_HEIGHT);
                break;
        }
    }

//    public void setLiftPositionOffsetTicks(long offset){
//        liftPositionOffsetTicks = offset;
//    }

    public void setLiftPower(double power){
        liftMotor.setMotorPower(power);
    }

    public void setLiftDirection(DcMotor.Direction dir){
        liftMotor.setMotorDirection(dir);
    }

    public void setBeltDirection(DcMotor.Direction dir){
        belt.setMotorDirection(dir);
    }

//    public void setBeltPower(double power){
//        belt.se(power);
//    }

    public long getLiftPositionOffsetTicks() {
        return liftPositionOffsetTicks;
    }

    public long getLiftMotorPosition(){
        return liftMotor.getCurrentTick();
    }

    public void startBelt(){
        belt.extend();
    }

    public void stopBelt(){
        belt.pause();
    }

    public void retractBelt(){
        belt.retract();
    }

    public REVColorDistanceSensorController.color getColor() {
        return glyphSensor.getColor();
    }

    public double getDistance(DistanceUnit unit){
        return glyphSensor.getDistance(unit);
    }

    public boolean isPressed(){
        return extendLimit.isPressed();
    }

    public double getRPS(){
        return liftMotor.getCurrentRPS();
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
    public void kill() {
        liftMotor.brake();
        liftMotor.killMotorController();
    }

}