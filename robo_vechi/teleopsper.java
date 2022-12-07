package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "teleopsper", group = "")
public class teleopsper extends LinearOpMode {

    private DcMotor DreaptaFata;
    private DcMotor StangaFata;
    private DcMotor DreaptaSpate;
    private DcMotor StangaSpate;
    private CRServo clapa;
    private DcMotor brat;
    private DcMotor rata;

    @Override
    public void runOpMode() {
        DreaptaFata = hardwareMap.dcMotor.get("DreaptaFata");
        StangaFata = hardwareMap.dcMotor.get("StangaFata");
        DreaptaSpate = hardwareMap.dcMotor.get("DreaptaSpate");
        StangaSpate = hardwareMap.dcMotor.get("StangaSpate");
        clapa = hardwareMap.crservo.get("clapa");
        brat = hardwareMap.dcMotor.get("brat");
        rata = hardwareMap.dcMotor.get("rata");

        DreaptaSpate.setDirection(DcMotorSimple.Direction.REVERSE);
        DreaptaFata.setDirection(DcMotorSimple.Direction.REVERSE);
        waitForStart();

        if (opModeIsActive()) {
            while (opModeIsActive()) {
                double dir = gamepad1.right_stick_y;
                double curba = -gamepad1.right_stick_x;
                double stanga = Range.clip(dir + curba, -1.0, 1.0);
                double dreapta = Range.clip(dir - curba, -1.0, 1.0);

                StangaFata.setPower(stanga);
                StangaSpate.setPower(stanga);
                DreaptaFata.setPower(dreapta);
                DreaptaSpate.setPower(dreapta);

                if (gamepad2.x) {
                    clapa.setPower(1.0);
                } else if (gamepad2.b) {
                    clapa.setPower(-1.0);
                } else {
                    clapa.setPower(0.0);
                }

                if (gamepad2.y) {
                    brat.setPower(1);
                } else {
                    brat.setPower(0);
                }

                if (gamepad2.a) {
                    brat.setPower(-1);
                } else {
                    brat.setPower(0);
                }

                if (gamepad2.dpad_left) {
                    rata.setPower(1);
                } else {
                    rata.setPower(0);
                }

                if (gamepad2.dpad_right) {
                    rata.setPower(-1);
                } else {
                    rata.setPower(0);
                }

                telemetry.update();
            }
        }
    }
}