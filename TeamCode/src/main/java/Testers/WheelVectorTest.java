/*
Copyright (c) 2016 Robert Atkinson

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package Testers;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import Autonomous.HeadingVector;
import DriveEngine.JennyNavigation;

import static DriveEngine.JennyNavigation.BACK_LEFT_HOLONOMIC_DRIVE_MOTOR;
import static DriveEngine.JennyNavigation.BACK_RIGHT_HOLONOMIC_DRIVE_MOTOR;
import static DriveEngine.JennyNavigation.FRONT_LEFT_HOLONOMIC_DRIVE_MOTOR;
import static DriveEngine.JennyNavigation.FRONT_RIGHT_HOLONOMIC_DRIVE_MOTOR;

@TeleOp(name="Wheel vector test", group="Linear Opmode")  // @Autonomous(...) is the other common choice
//@Disabled
public class WheelVectorTest extends LinearOpMode {

    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    JennyNavigation navigation;
    HeadingVector[] vectors;
    //ImuHandler imuHandler;
    @Override
    public void runOpMode() {
        //imuHandler = new ImuHandler("imu", hardwareMap);
        try {
            navigation = new JennyNavigation(hardwareMap,"RobotConfig/JennyV2.json");
        }
        catch (Exception e){
            Log.e("Error!" , "Jenny Navigation: " + e.toString());
            throw new RuntimeException("Navigation Creation Error! " + e.toString());

        }
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();
        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            vectors = navigation.getWheelVectors();

            telemetry.addData("FL Vector", vectors[FRONT_LEFT_HOLONOMIC_DRIVE_MOTOR].x() + ", " + vectors[FRONT_LEFT_HOLONOMIC_DRIVE_MOTOR].y());
            telemetry.addData("FR Vector", vectors[FRONT_RIGHT_HOLONOMIC_DRIVE_MOTOR].x() + ", " + vectors[FRONT_RIGHT_HOLONOMIC_DRIVE_MOTOR].y());
            telemetry.addData("BL Vector", vectors[BACK_LEFT_HOLONOMIC_DRIVE_MOTOR].x() + ", " + vectors[BACK_LEFT_HOLONOMIC_DRIVE_MOTOR].y());
            telemetry.addData("BR Vector", vectors[BACK_RIGHT_HOLONOMIC_DRIVE_MOTOR].x() + ", " + vectors[BACK_RIGHT_HOLONOMIC_DRIVE_MOTOR].y());
            telemetry.update();
        }
        navigation.stopNavigation();
    }
}