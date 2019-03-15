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
package org.firstinspires.ftc.teamcode;

import android.app.Activity;
import android.view.View;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * This file illustrates the concept of driving up to a line and then stopping.
 * It uses the common Pushbot hardware class to define the drive on the robot.
 * The code is structured as a LinearOpMode
 * <p>
 * The code shows using two different light sensors:
 * The Primary sensor shown in this code is a legacy NXT Light sensor (called "light sensor")
 * Alternative "commented out" code uses a MR Optical Distance Sensor (called "sensor_ods")
 * instead of the LEGO sensor.  Chose to use one sensor or the other.
 * <p>
 * Setting the correct WHITE_THRESHOLD value is key to stopping correctly.
 * This should be set half way between the light and dark values.
 * These values can be read on the screen once the OpMode has been INIT, but before it is STARTED.
 * Move the senso on asnd off the white line and not the min and max readings.
 * Edit this code to make WHITE_THRESHOLD half way between the min and max.
 * <p>
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@Autonomous(name = "ODS to White Line", group = "Warrior")
@Disabled
public class ODSWhiteLineTest extends LinearOpMode {

    /* Declare OpMode members. */
    HardwareWarrior robot = new HardwareWarrior();   // Use a WarriorPushbot's hardware
    private ElapsedTime runtime = new ElapsedTime();

    OpticalDistanceSensor floorSensor;
    OpticalDistanceSensor wallSensor;
    ColorSensor colorSensor;

    static final double WHITE_THRESHOLD = 0.2;  // spans between 0.1 - 0.5 from dark to light
    static final double BACK_APPROACH_SPEED = -0.5;

    static final double BACK_TURN_SPEED = -0.3;

    @Override
    public void runOpMode() throws InterruptedException {

         /*
         * Initialize the drive system variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        //OPTICAL DISTANCE SENSORS---------------------------------------------------------------
        // get a reference to our Light Sensor object.
        floorSensor = hardwareMap.opticalDistanceSensor.get("floor_ods");

        // turn on LED of light sensor.
        floorSensor.enableLed(true);
        //---------------------------------------------------------------------------------------

        //COLOR SENSOR---------------------------------------------------------------------------
        // hsvValues is an array that will hold the hue, saturation, and value information.
        float hsvValues[] = {0F, 0F, 0F};

        // values is a reference to the hsvValues array.
        final float values[] = hsvValues;

        // get a reference to the RelativeLayout so we can change the background
        // color of the Robot Controller app to match the hue detected by the RGB sensor.
        final View relativeLayout = ((Activity) hardwareMap.appContext).findViewById(com.qualcomm.ftcrobotcontroller.R.id.RelativeLayout);

        // bPrevState and bCurrState represent the previous and current state of the button.
        boolean bPrevState = false;
        boolean bCurrState = false;

        // bLedOn represents the state of the LED.
        boolean bLedOn = true;

        // get a reference to our ColorSensor object.
        colorSensor = hardwareMap.colorSensor.get("color sensor");

        // Set the LED in the beginning
        colorSensor.enableLed(bLedOn);
        //----------------------------------------------------------------------------------------

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Ready to run");    //
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        while (!isStarted()) {

            // Display the light level while we are waiting to start
            telemetry.addData("Light Level", floorSensor.getLightDetected());
            telemetry.update();
            idle();
        }

        while (opModeIsActive() && (runtime.seconds() < 2.0)) {

            if ((floorSensor.getLightDetected() > WHITE_THRESHOLD)) {
                robot.leftMotor.setPower(BACK_APPROACH_SPEED);
                robot.rightMotor.setPower(BACK_TURN_SPEED);
            } else if ((floorSensor.getLightDetected() < WHITE_THRESHOLD)) {
                robot.leftMotor.setPower(BACK_TURN_SPEED);
                robot.rightMotor.setPower(BACK_APPROACH_SPEED);
            } else {
                robot.leftMotor.setPower(BACK_APPROACH_SPEED);
                robot.rightMotor.setPower(BACK_APPROACH_SPEED);
            }

            // Display the light level while we are looking for the line
            telemetry.addData("Light Level Wall", wallSensor.getLightDetected());
            telemetry.update();
            idle(); // Always call idle() at the bottom of your while(opModeIsActive()) loop
        }

        //Step 6: robot will stop in front of the beacon
        robot.leftMotor.setPower(0);
        robot.rightMotor.setPower(0);
    }
}
