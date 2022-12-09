package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.robotcore.external.hardware.camera.Camera;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.lang.Math;
import java.util.Arrays;

import android.graphics.Bitmap;

import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Image;
import com.vuforia.Vuforia;

@Autonomous(name="cam", group="")
public class Cam extends LinearOpMode {
    private static final String VUFORIA_KEY =
            "AXa/rK7/////AAABmUJZm5ajXELYhCsjGBSamWkBfIvGYAJ9POhF9puj2Ar+ye+V6FpPxV52S+VanY6Tr6lnIaqAckVdfMJ4ExtVgJgdQDxHigzdZxdVEelLmBvqdbXZ9YIjR6keTV2xF6FgZ1A/VbWAqo6FJvaouisA7b+A6SbM/nNELA3fbC6gcpNgHTsrrYr27wFylwN1nimewVCb75jfb83zrLh+E1GM0pqdAxVEHCWm70S+8sv7b9SAimC/Sh02cNgYxCTqiOIzg+e7+LnR7teg3/2Tv4p+rAvY8zuWmFt1cQOdR2doqXTrZmWiHNULJA2OKJ2A4uiITDouve0YdM7PZ00iLPXduGnj30FoY5cqhDEOoIzKKuH8";

    // Class Members
    private VuforiaLocalizer vuforia    = null;
    private WebcamName webcamName       = null;
    private DcMotor stspa, stft, drspa, drft;
    private DcMotor[] motors = new DcMotor[4];
    private final int steps_per_rot = 720;

    public enum Dir
    {
        NONE, LEFT, CENTER, RIGHT
    }

    /*
     * drft stft drspa stspa
     * dir[0] - fata
     * dir[1] - spate
     * dir[2] - rot_stanga
     * dir[3] - rot_dreapta
     * dir[4] -
     * dir[5] -
     */
    public final int[][] dir = {
            {  1,  1,  1,  1 },
            { -1, -1, -1, -1 },
            { -1,  1, -1,  1 },
            {  1, -1,  1, -1 },
            {  1, -1, -1,  1 },
            { -1,  1,  1, -1 }
    };
    public final int MV_FWD = 0, MV_BWD = 1, ROT_RIGHT = 2, ROT_LEFT = 3, MV_LEFT = 4, MV_RIGHT = 5;

    public final double nPower = 0.4;
    public final int accel_dist = 300;

    @Override public void runOpMode() {
        // init motors
        drft = hardwareMap.dcMotor.get("drft");
        stft = hardwareMap.dcMotor.get("stft");
        drspa = hardwareMap.dcMotor.get("drspa");
        stspa = hardwareMap.dcMotor.get("stspa");
        motors[0] = drft;
        motors[1] = stft;
        motors[2] = drspa;
        motors[3] = stspa;

        // set modes & direction
        setMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setMotorMode(DcMotor.RunMode.RUN_TO_POSITION);

        drspa.setDirection(DcMotorSimple.Direction.REVERSE);
        drft.setDirection(DcMotorSimple.Direction.REVERSE);

        // start camera stuff
        webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");

        // Debugging only
        // int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        // VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = webcamName;

        vuforia = ClassFactory.getInstance().createVuforia(parameters);
        vuforia.setFrameQueueCapacity(1);
        waitForStart();

        Bitmap b = take_picture();
        int avg_hue = get_avg_hue(b, 125, 200, 10);
        Dir dir = get_dir(avg_hue);

        telemetry.addData("dir", dir);
        telemetry.update();
        // end camera stuff

        if(opModeIsActive()) {
            for (int i = 0; i < 10; ++ i) {
                move(MV_FWD, steps_per_rot, nPower);
                move(MV_BWD, steps_per_rot, nPower);
            }

            telemetry.addData("a", stspa.getCurrentPosition() + " " + drspa.getCurrentPosition());
            telemetry.update();
        }
    }

    public static int rgb_to_h(double r, double g, double b) {
        r /= 255;
        g /= 255;
        b /= 255;
        double cmin = Math.min(r, Math.min(g, b));
        double cmax = Math.max(r, Math.max(g, b));
        double delta = cmax - cmin;
        double h = 0;

        if (delta == 0)
            h = 0;
        else if (cmax == r)
            h = (g-b)/delta;
        else if (cmax == g)
            h = (b-r)/delta + 2;
        else
            h = (r-g)/delta + 4;
        h = Math.round(h*60);

        if (h < 0)
            h += 360;
        return (int)h;
    }

    public Bitmap take_picture() {
        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true);

        VuforiaLocalizer.CloseableFrame frame = null;
        Image img = null;
        while (frame == null || img == null) {
            try {
                frame = vuforia.getFrameQueue().take();
            } catch (Exception e) {
                continue;
            }

            img = null;
            for (int i = 0; i < frame.getNumImages(); ++ i) {
                if (frame.getImage(i).getFormat() == PIXEL_FORMAT.RGB565) {
                    img = frame.getImage(i);
                    break;
                }
            }
        }

        Bitmap b = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.RGB_565);
        b.copyPixelsFromBuffer(img.getPixels());
        frame.close();

        return b;
    }

    // 200 125
    public int get_avg_hue(final Bitmap b, final int width, final int height, final int error) {
        int hue = 0;
        int count = 0;
        for (int i = height - error; i < height + error; ++ i) {
            for (int j = width - error; j < width + error; ++ j) {
                int col = b.getPixel(j, i);
                int r = (col >> 16) & 0xff;
                int g = (col >> 8) & 0xff;
                int blue = col & 0xff;
                int h = rgb_to_h((double)r, (double)g, (double)blue);
                if (get_dir(h) != Dir.NONE) {
                    hue += h;
                    ++count;
                }
            }
        }
        hue /= count;
        return hue;
    }

    public Dir get_dir(final int hue) {
        Dir guess = Dir.NONE;
        // green
        if (hue <= 164 && hue >= 75) {
            guess = Dir.CENTER;
        } else if (hue <= 50 && hue >= 3) { // orange
            guess = Dir.RIGHT;
        } else if (hue <= 359 && hue >= 270) { // pink
            guess = Dir.LEFT;
        }
        return guess;
    }

    /* New move() */
    public void move (final int pdir, final int steps, final double power)
    {
        setTargetPosition(steps, pdir);

        final int currentPos = motors[0].getCurrentPosition();
        if (currentPos == 0) /* starting */ {
            Arrays.stream(motors).forEach(x -> x.setPower( /* TODO init power */));
        }

        final int finish_accel = accel_dist; // better name :)
        final int start_deccel = steps-accel_dist;

        while(stspa.isBusy() || drspa.isBusy() || stft.isBusy() || drft.isBusy()) {
            double currentPower;

            if (currentPos < finish_accel) /* hasn't fully accelerated */ {
                final double currentProgress = currentPos / accel_dist;
                currentPower = currentProgress * power;
                telemetry.addData("Accelerating %", currentProgress);
            } else if (currentPos >= start_deccel) /* is stopping */ {
                final double currentProgress = (currentPos - start_deccel) / accel_dist;
                currentPower = (1.0-currentProgress) * power;
                telemetry.addData("Deccelerating %", currentProgress);
            } else  /* is at max power */ {
                currentPower = power;
                telemetry.addData("Full speed");
            }

            Arrays.stream(motors).forEach(x -> x.setPower(currentPower));
            telemetry.addData("Added power", currentPower);
            telemtry.update();
        }

        Arrays.stream(motors).forEach(x -> x.setPower(0));
    }

    /* Old move()
    public void move (final int pdir, final int steps, final double power)
    {
        setTargetPosition(steps, pdir);

        final int start = motors[0].getCurrentPosition();
        int prev_progress = start;

        final double refresh_rate = 10;
        final double accel = 1.0 / accel_dist * refresh_rate;
        double speed = 0;
        // TODO Possible bug: motors are not busy here
        while(stspa.isBusy() || drspa.isBusy() || stft.isBusy() || drft.isBusy()) {
            int progress = Math.abs(motors[0].getCurrentPosition()) - start;
            if (progress - prev_progress >= refresh_rate) {
                if (progress < accel_dist) {
                    speed += accel * ((progress - prev_progress) / refresh_rate);
                } else if (progress > steps - accel_dist) {
                    speed -= accel * ((progress - prev_progress) / refresh_rate);
                }

                Arrays.stream(motors).forEach(x -> x.setPower(power * speed));
                telemetry.addData("a", power * speed);
                telemetry.update();
                prev_progress = progress;
            }
        }

        Arrays.stream(motors).forEach(x -> x.setPower(0));
    }
    */

    // what type is the mode?
    public void setMotorMode (final DcMotor.RunMode mode)
    {
        for (DcMotor motor : motors)
            motor.setMode(mode);
    }

    public void setTargetPosition (final int pos, final int pdir)
    {
        for (int i = 0; i < 4; ++ i)
            motors[i].setTargetPosition(motors[i].getCurrentPosition() + pos * dir[pdir][i]);
    }
}