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

import Actions.JewelJousterV2;
import Autonomous.REVColorDistanceSensorController;

import static DriveEngine.JennyNavigation.LONG_SLEEP_DELAY_MILLIS;
import static DriveEngine.JennyNavigation.MED_SLEEP_DELAY_MILLIS;

/*
    An opmode to test if all our drive wheels are working correctly
 */
@TeleOp(name="Jewel Joust Tester", group="Linear Opmode")  // @Autonomous(...) is the other common choice
//@Disabled
public class JewelJoustTest extends LinearOpMode {

    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    JewelJousterV2 jewelJoust;

    @Override
    public void runOpMode() {
        try {
            jewelJoust = new JewelJousterV2("jewelJoust", "jewelJoustTurn", this, hardwareMap);
        }
        catch (Exception e){
            Log.e("Error!" , "Glyph Lift: " + e.toString());
            throw new RuntimeException("Glyph Lift Creation Error! " + e.toString());

        }
        jewelJoust.setJoustMode(JewelJousterV2.JEWEL_JOUSTER_POSITIONS.STORE);
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();
        jewelJoust.setJoustMode(JewelJousterV2.JEWEL_JOUSTER_POSITIONS.READ);
        sleep(2*LONG_SLEEP_DELAY_MILLIS);
        REVColorDistanceSensorController.color readColor = jewelJoust.getJewelColor();
        while(readColor == REVColorDistanceSensorController.color.UNKNOWN || readColor == REVColorDistanceSensorController.color.NOT_IN_RANGE && opModeIsActive()){
            sleep(100);
            readColor = jewelJoust.getJewelColor();
            telemetry.addData("Color", readColor);
            telemetry.update();
        }
        if(opModeIsActive()) {
            if (readColor == REVColorDistanceSensorController.color.BLUE) {
                jewelJoust.setJoustMode(JewelJousterV2.JEWEL_JOUSTER_POSITIONS.HIT_RIGHT);
                telemetry.addData("HIT RIGHT", "");
            } else if (readColor == REVColorDistanceSensorController.color.RED) {
                jewelJoust.setJoustMode(JewelJousterV2.JEWEL_JOUSTER_POSITIONS.HIT_LEFT);
                telemetry.addData("HIT LEFT", "");
            }
            telemetry.addData("Color", readColor);
            telemetry.update();
        }
        while (opModeIsActive());
        jewelJoust.kill();
    }
}
