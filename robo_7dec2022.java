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
    private VuforiaLocalizer vuforiaV    = null;
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
            {  1,  1, -1, -1 },
            { -1, -1,  1,  1 }
    };
    public final int MV_FWD = 0, MV_BWD = 1, ROT_LEFT = 2, ROT_RIGHT = 3, MV_LEFT = -1, MV_RIGHT = -1;

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
        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);

        drspa.setDirection(DcMotorSimple.Direction.REVERSE);
        drft.setDirection(DcMotorSimple.Direction.REVERSE);

        // start camera stuff
        webcamName = hardwareMap.get(WebcamName.class, "Webcam 1");

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);

        parameters.vuforiaLicenseKey = VUFORIA_KEY;

        parameters.cameraName = webcamName;

        vuforiaV = ClassFactory.getInstance().createVuforia(parameters);

        waitForStart();

        Bitmap b = take_picture();
        int avg_hue = get_avg_hue(b, 125, 200, 10);
        Dir dir = get_dir(avg_hue);

        telemetry.addData("dir", dir);
        telemetry.update();
        // end camera stuff

        if(opModeIsActive()) {
            move(MV_FWD, steps_per_rot, 1.0);
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
        // h /= 6;
        if (h < 0)
            h += 360;
        return (int)h;
    }

    public Bitmap take_picture() {
        vuforiaV.setFrameQueueCapacity(1);
        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true);
        Camera cam = vuforiaV.getCamera();
        VuforiaLocalizer.CloseableFrame frame = null;
        Image img = null;
        while (frame == null && !isStopRequested()) {
            try {
                frame = vuforiaV.getFrameQueue().take();
            } catch (Exception e) {
                continue;
            }
            for (int i = 0; i < frame.getNumImages(); ++ i) {
                telemetry.addData("a " + i, frame.getImage(i).getFormat());
                if (frame.getImage(i).getFormat() == PIXEL_FORMAT.RGB565) {
                    img = frame.getImage(i);
                    break;
                }
            }
            telemetry.update();
            if (img == null) {
                frame = null;
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
        for (int i = 0; i < b.getHeight(); ++ i) {
            for (int j = 0; j < b.getWidth(); ++ j) {
                int col = b.getPixel(j, i);
                int r = (col >> 16) & 0xff;
                int g = (col >> 8) & 0xff;
                int blue = col & 0xff;

                int h = rgb_to_h((double)r, (double)g, (double)blue);
                if (Math.abs(i - height) <= error && Math.abs(j - width) <= error && get_dir(h) != Dir.NONE) {
                    hue += h;
                    ++ count;
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

    public void move (final int pdir, final int steps, final double power)
    {
        // I'm assuming this doesn't work, only works for arraylist and similar stuff
        // motors.forEach(motor -> motor.setTargetPosition(steps_per_rot));

        setTargetPosition(steps);
        setMotorMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        for (int i = 0; i < 4; i++) {
            motors[i].setPower(dir[pdir][i] * power);
        }
        while(stspa.isBusy() || drspa.isBusy() || stft.isBusy() || drft.isBusy());

        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    // what type is the mode?
    public void setMotorMode (final int mode)
    {
        for (DcMotor motor : motors)
            motor.setMode(mode);
    }

    public void setTargetPosition (final int pos)
    {
        for (DcMotor motor : motors)
            motor.setTargetPosition(pos);
    }
}

// @TeleOp(name = "cam", group = "")
// public class Abcd extends LinearOpMode {


//     @Override public void runOpMode() {

//         File f = new File("/storage/emulated/0/Music", "img.txt");
//         f.setWritable(true);

//         FileOutputStream fout = null;
//         try {
//             fout = new FileOutputStream(f);
//         } catch (FileNotFoundException e) {
//             e.printStackTrace();
//         }


//         // telemetry.addData("")
//         telemetry.addData("color", hue);



//         telemetry.addData("color", (guess == 0 ? "green" : (guess == 1 ? "orange" : (guess == 2 ? "pink" : "nimici"))));
//         telemetry.update();

//         while (!isStopRequested()) {

//         }
//     }
// }
