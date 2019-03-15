package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * This is NOT an opmode.
 *
 * This class can be used to define all the specific hardware for a single robot.
 * In this case that robot is a Warrior.
 * See PushbotTeleopTank_Iterative and others classes starting with "Saber" for usage examples.
 *
 * This hardware class assumes the following device names have been configured on the robot:
 * Note:  All names are lower case and some have single spaces between words.
 *
 * Motor channel:  Left  drive motor:           "left motor"
 * Motor channel:  Right drive motor:           "right motor"
 * Motor channel:  Snorfler drive motor:        "snorf motor"
 * Servo channel:  Servo to push beacon button: "push button"
 * Servo channel:  Servo to launch catapult:    "trigger particles"
 */

//12-9-16 Modified by Nisha Patel

public class HardwareWarrior
{
    /* Public OpMode members. */
    public DcMotor    leftMotor     = null;
    public DcMotor    rightMotor    = null;
    public DcMotor    snorfMotor    = null;
    public DcMotor    launchMotor   = null;
    public DcMotor    pushMotor     = null;

    public Servo      pushButtonL   = null;
    //public Servo      pushButtonR   = null;
    public Servo      trigger       = null;

    public static final double MID_SERVO               =  0.5;
    public static final double START_LBUTTON_SERVO     =  0.2;
    //public static final double START_RBUTTON_SERVO     =  0.0;
    public static final double TRIGGER_START_POSITION  =  0.1;
    public static final double TRIGGER_RELEASE_POSTION =  0.3;

    public static final double SNORF_LEFT_POWER        = -1.0;
    public static final double SNORF_RIGHT_POWER       =  1.0;
    //public static final double LAUNCH_LEFT_POWER       = -0.45;
    public static final double LAUNCH_RIGHT_POWER      =  0.45;
    public static final double PUSH_LEFT_POWER         = -0.2;
    public static final double PUSH_RIGHT_POWER        =  0.2;

    /* local OpMode members. */
    HardwareMap hwMap           =  null;
    private ElapsedTime period  = new ElapsedTime();

    /* Constructor */
    public HardwareWarrior(){

    }

    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap) {
        // Save reference to Hardware map
        hwMap = ahwMap;

        // Define and Initialize Motors
        leftMotor   = hwMap.dcMotor.get("left motor");
        rightMotor  = hwMap.dcMotor.get("right motor");
        snorfMotor  = hwMap.dcMotor.get("snorf motor");
        launchMotor = hwMap.dcMotor.get("launch motor");
        pushMotor   = hwMap.dcMotor.get("push motor");
        leftMotor.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors
        rightMotor.setDirection(DcMotor.Direction.FORWARD);// Set to FORWARD if using AndyMark motors

        // Set all motors to zero power
        leftMotor.setPower(0);
        rightMotor.setPower(0);
        snorfMotor.setPower(0);
        launchMotor.setPower(0);
        pushMotor.setPower(0);

        // Set all motors to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        leftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        snorfMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        launchMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        pushMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Define and initialize ALL installed servos.);
        pushButtonL = hwMap.servo.get("push left");
        //pushButtonR = hwMap.servo.get("push right");
        trigger = hwMap.servo.get("trigger particles");
        pushButtonL.setPosition(START_LBUTTON_SERVO);
        //pushButtonR.setPosition(START_RBUTTON_SERVO);
        trigger.setPosition(TRIGGER_START_POSITION);
    }

    /***
     *
     * waitForTick implements a periodic delay. However, this acts like a metronome with a regular
     * periodic tick.  This is used to compensate for varying processing times for each cycle.
     * The function looks at the elapsed cycle time, and sleeps for the remaining time interval.
     *
     * @param periodMs  Length of wait cycle in mSec.
     * @throws InterruptedException
     */
    public void waitForTick(long periodMs) throws InterruptedException {

        long  remaining = periodMs - (long)period.milliseconds();

        // sleep for the remaining portion of the regular cycle period.
        if (remaining > 0)
            Thread.sleep(remaining);

        // Reset the cycle clock for the next pass.
        period.reset();
    }
}

