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

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/**
 * This file provides basic Telop driving for a Pushbot robot.
 * The code is structured as an Iterative OpMode
 *
 * This OpMode uses the common Pushbot hardware class to define the devices on the robot.
 * All device access is managed through the HardwarePushbot class.
 *
 * This particular OpMode executes a basic Tank Drive Teleop for a PushBot
 * It raises and lowers the claw using the Gampad Y and A buttons respectively.
 * It also opens and closes the claws slowly using the left and right Bumper buttons.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

//12-9-16 Modified by Nisha Patel
@TeleOp(name="Warrior Teleop", group="Warrior")
//@Disabled
public class WarriorTeleOp extends OpMode{

    /* Declare OpMode members. */
    HardwareWarrior robot       = new HardwareWarrior();  // use the class created to define a Warrior's hardware
    // could also use HardwarePushbotMatrix class.
    double          buttonOffset      = -0.1 ;
    final double    BUTTON_SPEED      = 0.02 ;             // sets rate to move servo
    double          triggerOffset     = 0.0;
    final double    TRIGGER_REST      = 0.1;

    private ElapsedTime runtime = new ElapsedTime();

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);

        robot.pushButtonL.setPosition(0.1);
        // Send telemetry message to signify robot waiting;
        telemetry.addData("Say", "Hello RoboTech Warriors");
        updateTelemetry(telemetry);
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {

        //Use gamepad1 to control basic drive functions

        // this is code that makes robot move with gamepad 1
        // throttle:  left_stick_y ranges from -2 to 2, where -2 is full up,  and 1 is full down
        // direction: left_stick_x ranges from -2 to 2, where -2 is full left and 1 is full right
        float throttle = -gamepad1.left_stick_y;
        float direction = gamepad1.left_stick_x;
        double right = throttle + direction;
        double left = throttle - direction;


        // clip the right/left values so that the values never exceed +/- 1
        right = (float) Range.clip(right, -0.65, 0.65);
        left = (float) Range.clip(left, -0.65, 0.65);

        // scale the joystick value to make it easier to control
        // the robot more precisely at slower speeds.
        right = (float)scaleInput(right);
        left  = (float)scaleInput(left);

        // write the values to the motors
        robot.rightMotor.setPower(right);
        robot.leftMotor.setPower(left);

        // Use gamepad2 left and right Bumpers to move the snorfler clockwise and anti-clockwise
        if (gamepad2.right_bumper)
            robot.snorfMotor.setPower(robot.SNORF_LEFT_POWER);
        else if (gamepad2.left_bumper)
            robot.snorfMotor.setPower(robot.SNORF_RIGHT_POWER);
        else
            robot.snorfMotor.setPower(0.0);

        //Use gamepad2 y and a to turn the launcher
        if (gamepad2.y) {
            robot.launchMotor.setPower(robot.LAUNCH_RIGHT_POWER);
            runtime.reset();
            while ((runtime.seconds() < 1.1)) {
                telemetry.addData("Path", "Leg 1: %2.5f S Elapsed", runtime.seconds());
                telemetry.update();
            }

            //Stop launch motor
            robot.launchMotor.setPower(0);
        }
        else if (gamepad2.a)
            robot.launchMotor.setPower(robot.LAUNCH_RIGHT_POWER);
        else
            robot.launchMotor.setPower(0.0);

        // Use gamepad2 dpad up and dpad down to move the arm to press beacon buttons
        if (gamepad2.dpad_up)
            buttonOffset += BUTTON_SPEED;
        else if (gamepad2.dpad_down)
            buttonOffset -= BUTTON_SPEED;

        //Use gamepad2 x activates the particle release trigger
        if (gamepad2.x)
            robot.trigger.setPosition(0.3);
        else
            robot.trigger.setPosition(robot.TRIGGER_START_POSITION);

        //Use gamepad2 dpad left and dpad right to move side button pusher
        if (gamepad2.dpad_right)
            robot.pushMotor.setPower(robot.PUSH_RIGHT_POWER);
        else if (gamepad2.dpad_left)
            robot.pushMotor.setPower(robot.PUSH_LEFT_POWER);
        else
            robot.pushMotor.setPower(0);

        // Move both servos to new position.  Assume servos are mirror image of each other.
        buttonOffset = Range.clip(buttonOffset, -0.5, 1.0);
        robot.pushButtonL.setPosition(buttonOffset);
        triggerOffset = Range.clip(triggerOffset, -0.5, 0.5);
        // Send telemetry message to signify robot running;
        telemetry.addData("push",  "Offset = %.2f", buttonOffset);
        telemetry.addData("trigger", "Offset = %.2f", triggerOffset);
        telemetry.addData("left",  "%.2f", left);
        telemetry.addData("right", "%.2f", right);
        updateTelemetry(telemetry);
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }
    /*
     * This method scales the joystick input so for low joystick values, the
     * scaled value is less than linear.  This is to make it easier to drive
     * the robot more precisely at slower speeds.
     */
    double scaleInput(double dVal)  {
        double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24, 0.30, 0.36, 0.43, 0.50, 0.55, 0.6, 0.85, 1.00, 1.00 };

        // get the corresponding index for the scaleInput array.
        int index = (int) (dVal * 16.0);

        // index should be positive.
        if (index < 0) {
            index = -index;
        }

        // index cannot exceed size of array minus 1.
        if (index > 16) {
            index = 16;
        }

        // get value from the array.
        double dScale = 0.0;
        if (dVal < 0) {
            dScale = -scaleArray[index];
        } else {
            dScale = scaleArray[index];
        }

        // return scaled value.
        return dScale;
    }

}
