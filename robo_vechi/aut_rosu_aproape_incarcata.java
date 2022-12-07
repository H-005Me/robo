package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous(name = "aut_rosu_aproape_incarcataa", group = "")
public class aut_rosu_aproape_incarcata extends LinearOpMode {

    private DcMotor DreaptaFata;
    private DcMotor StangaFata;
    private DcMotor DreaptaSpate;
    private DcMotor StangaSpate;
    private DcMotor clapa;
    private DcMotor rata;
    private DcMotor brat;

    private int[][] dirs = { { 1, 1, 1, 1 }, { -1, -1, -1, -1 }, { -1, 1, -1, 1 }, { 1, -1, 1, -1 } };

    private enum DIRS_ENUM {
        SPATE, FATA, ROT_STANGA, ROT_DREAPTA
    };

    private enum rata_loc {
        FATA, STANGA, DREAPTA
    };

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
        clapa = hardwareMap.dcMotor.get("clapa");
        rata = hardwareMap.dcMotor.get("rata");
        brat = hardwareMap.dcMotor.get("brat");
        // Put initialization blocks here.
        DreaptaSpate.setDirection(DcMotorSimple.Direction.REVERSE);
        DreaptaFata.setDirection(DcMotorSimple.Direction.REVERSE);
        waitForStart();

        if (opModeIsActive()) {
            miscare(DIRS_ENUM.FATA, 0.5);
            sleep(600);
            miscare(DIRS_ENUM.ROT_DREAPTA, 0.5);
            sleep(850);
            miscare(DIRS_ENUM.SPATE, 0.5);
            sleep(950);
            miscare(DIRS_ENUM.SPATE, 0);
            rata.setPower(0.5);
            sleep(4400);
            rata.setPower(0);
            miscare(DIRS_ENUM.FATA, 0.5);
            sleep(500);
            miscare(DIRS_ENUM.ROT_DREAPTA, 0.5);
            sleep(250);
            miscare(DIRS_ENUM.FATA, 1);
            sleep(800);
            brat.setPower(1);
            sleep(300);
            miscare(DIRS_ENUM.ROT_STANGA, 0.5);
            sleep(1000);
            miscare(DIRS_ENUM.FATA, 0.5);
            sleep(430);
            miscare(DIRS_ENUM.FATA, 0);
            brat.setPower(0);
            clapa.setPower(0.35);
            sleep(2000);
            clapa.setPower(0);
            miscare(DIRS_ENUM.SPATE, 0.5);
            sleep(100);
            miscare(DIRS_ENUM.ROT_DREAPTA, 0.5);
            sleep(1000);
            miscare(DIRS_ENUM.FATA, 1);
            sleep(1700);
        }
    }

    private void miscare(DIRS_ENUM dir_e, double power) {
        int dir = 1;
        switch (dir_e) {
            case SPATE:
                dir = 0;
                break;
            case FATA:
                dir = 1;
                break;
            case ROT_STANGA:
                dir = 2;
                break;
            case ROT_DREAPTA:
                dir = 3;
                break;
            default:
                break;
        }

        DreaptaFata.setPower(dirs[dir][0] * power);
        StangaFata.setPower(dirs[dir][1] * power);
        DreaptaSpate.setPower(dirs[dir][2] * power);
        StangaSpate.setPower(dirs[dir][3] * power);
    }

    /**
     * Deschide clapa
     */
    private void clapa2(long miliseconds) {
        clapa.setDirection(DcMotorSimple.Direction.REVERSE);
        clapa.setPower(1);
        sleep(miliseconds);
    }

    private void clapa1(long miliseconds) {
        clapa.setDirection(DcMotorSimple.Direction.REVERSE);
        clapa.setPower(-1);
        sleep(miliseconds);
    }

    /**
     * Merge in fata
     */
    private void fata(long miliseconds, double power, long pause) {
        DreaptaFata.setPower(-1 * power);
        StangaFata.setPower(-1 * power);
        DreaptaSpate.setPower(-1 * power);
        StangaSpate.setPower(-1 * power);
        sleep(miliseconds);
        stop(pause);
    }

    /**
     * Merge in spate
     */
    private void spate(long miliseconds, double power, long pause) {
        DreaptaFata.setPower(1 * power);
        StangaFata.setPower(1 * power);
        DreaptaSpate.setPower(1 * power);
        StangaSpate.setPower(1 * power);
        sleep(miliseconds);
        stop(pause);
    }

    /**
     * Se roteste la stanga
     */
    private void rotire_stanga(long miliseconds, double power, long pause) {
        DreaptaFata.setPower(-1 * power);
        StangaFata.setPower(1 * power);
        DreaptaSpate.setPower(-1 * power);
        StangaSpate.setPower(1 * power);
        sleep(miliseconds);
        stop(pause);
    }

    /**
     * Se roteste la dreapta
     */
    private void rotire_dreapta(long miliseconds, double power, long pause) {
        DreaptaFata.setPower(1 * power);
        StangaFata.setPower(-1 * power);
        DreaptaSpate.setPower(1 * power);
        StangaSpate.setPower(-1 * power);
        sleep(miliseconds);
        stop(pause);
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
     * Incercam sa mearga la stanga
     */
    private void stanga(long miliseconds, double power, long pause) {
        DreaptaFata.setPower(-1 * power);
        StangaFata.setPower(1 * power);
        DreaptaSpate.setPower(1 * power);
        StangaSpate.setPower(-1 * power);
        sleep(miliseconds);
        stop(pause);
    }

    /**
     * Incercam sa faca sa mearga la dreapta
     */
    private void dreapta(long miliseconds, double power, long pause) {
        DreaptaFata.setPower(1 * power);
        StangaFata.setPower(-1 * power);
        DreaptaSpate.setPower(-1 * power);
        StangaSpate.setPower(1 * power);
        sleep(miliseconds);
        stop(pause);
    }

}