package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous(name = "aut_albastru", group = "")
public class aut_albastru extends LinearOpMode {

    private DcMotor DreaptaFata;
    private DcMotor StangaFata;
    private DcMotor DreaptaSpate;
    private DcMotor StangaSpate;
    private CRServo clapa;

    /**
     * Merge in fata
     */
    private void fata(long miliseconds, double power, long pause) {
        DreaptaFata.setPower(power);
        StangaFata.setPower(power);
        DreaptaSpate.setPower(power);
        StangaSpate.setPower(power);
        sleep(miliseconds);
        stop(pause);
    }

    /**
     * This function is executed when this Op Mode is selected from the Driver
     * Station.
     */
    @Override
    public void runOpMode() {
        DreaptaFata = hardwareMap.dcMotor.get("DreaptaFata");
        StangaFata = hardwareMap.dcMotor.get("StangaFata");
        DreaptaSpate = hardwareMap.dcMotor.get("DreaptaSpate");
        StangaSpate = hardwareMap.dcMotor.get("StangaSpate");
        clapa = hardwareMap.crservo.get("clapa");

        // Put initialization blocks here.
        DreaptaSpate.setDirection(DcMotorSimple.Direction.REVERSE);
        DreaptaFata.setDirection(DcMotorSimple.Direction.REVERSE);
        waitForStart();
        if (opModeIsActive()) {
            // Put run blocks here. yeaa
            fata(900, -1, 1000);
            rotire_dreapta(500);
            stop(1000);
            fata(160, -0.5, 1000);
            clapa2(500);
            stop(1000);
            spate(220);
            rotire_stanga(600);
            stop(13000);
            // Bine pana aici! :*
            spate(500);
            stop(1000);
            rotire_dreapta(550);
            stop(1000);
            fata(2200, -1, 1000);
        }
    }

    /**
     * Deschide clapa
     */
    private void clapa2(long miliseconds) {
        clapa.setDirection(DcMotorSimple.Direction.REVERSE);
        clapa.setPower(1);
        sleep(miliseconds);
    }

    /**
     * Incercam sa mearga la stanga
     */
    private void stanga(long miliseconds) {
        DreaptaFata.setPower(-1);
        StangaFata.setPower(1);
        DreaptaSpate.setPower(1);
        StangaSpate.setPower(-1);
        sleep(miliseconds);
    }

    /**
     * Se opreste
     */
    private void stop(long miliseconds) {
        DreaptaFata.setPower(0);
        StangaFata.setPower(0);
        DreaptaSpate.setPower(0);
        StangaSpate.setPower(0);
        sleep(miliseconds);
    }

    /**
     * Incercam sa faca sa mearga la dreapta
     */
    private void dreapta(long miliseconds) {
        DreaptaFata.setPower(1);
        StangaFata.setPower(-1);
        DreaptaSpate.setPower(-1);
        StangaSpate.setPower(1);
        sleep(miliseconds);
    }

    /**
     * Se roteste la stanga
     */
    private void rotire_stanga(long miliseconds) {
        DreaptaFata.setPower(-1);
        StangaFata.setPower(1);
        DreaptaSpate.setPower(-1);
        StangaSpate.setPower(1);
        sleep(miliseconds);
    }

    /**
     * Merge in spate
     */
    private void spate(long miliseconds) {
        DreaptaFata.setPower(1);
        StangaFata.setPower(1);
        DreaptaSpate.setPower(1);
        StangaSpate.setPower(1);
        sleep(miliseconds);
    }

    /**
     * Se roteste la dreapta
     */
    private void rotire_dreapta(long miliseconds) {
        DreaptaFata.setPower(1);
        StangaFata.setPower(-1);
        DreaptaSpate.setPower(1);
        StangaSpate.setPower(-1);
        sleep(miliseconds);
    }
}