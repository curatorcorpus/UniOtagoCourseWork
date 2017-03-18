#pragma config(Sensor, S2,     Touch,          sensorEV3_Touch)
#pragma config(Sensor, S3,     Colour,         sensorEV3_Color, modeEV3Color_Color)
#pragma config(Sensor, S4,     Sonar,          sensorEV3_Ultrasonic)
#pragma config(Motor,  motorA,           ,             tmotorEV3_Large, openLoop)
#pragma config(Motor,  motorB,          left,          tmotorEV3_Large, PIDControl, driveLeft, encoder)
#pragma config(Motor,  motorC,          right,         tmotorEV3_Large, PIDControl, driveRight, encoder)
//*!!Code automatically generated by 'ROBOTC' configuration wizard               !!*//

//=================== Basic Mathematics =================================
/*
Specifications of Robot:
Diameter of Rotation circle: 12.3 cm
Diameter of Wheel: 5.655 cm
Circumference of Rotation Circle: 38.64158964 cm
Circumference of Wheel: 17.76570646 cm
Revs in Rotation Circle: 2.175066313 revs (wheels)
Revs in 90 degree rotation: 0.5437665781 revs (wheels)
*/
//=======================================================================

//=================== Global Variables ==================================

// DEFAULLTS
#define DEFAULT_SPD 40
#define OFFSET      23
#define REV_90      0.5437665781
#define REV_360     2.175066313

bool go_left = false;

int dark = 0;
int light = 0;
int grey = 0;

long curr_color = 0;
long thres_bl   = 0
long thres_bg   = 0;
long thres_gw   = 0;

/*
	long thres_bg = 35;
	long thres_gw = 51;
*/
//=======================================================================

//==================== Motor Operations =================================
/*
	Method used to reset motor pow back to zero.
*/
void reset_motors()
{
	setMotorSpeed(motorB, 0);
	setMotorSpeed(motorC, 0);
	sleep(10);
}

/*
	Method used to reset the motor encoder (turn) values.
*/
void reset_mencoder()
{
	resetMotorEncoder(motorB);
	resetMotorEncoder(motorC);
}

/*
	Method used to tell both wheels to turn certain revs.
*/
void encoded_mforward(float revs, long pow)
{
	float revs_to_degs = revs * 360;

	reset_mencoder();

	setMotorSyncEncoder(motorB, motorC, 0, revs_to_degs, pow);
	while(getMotorEncoder(motorB) <= revs_to_degs) {}
}

/*
	Method for pivot left given a rev and pow.
*/
void encoded_lpivot(float revs, long pow)
{
	float revs_to_degs = (revs * 360 - OFFSET);

	displayCenteredBigTextLine(4, "Rotating Left");

	reset_mencoder();
	setMotorSyncEncoder(motorB, motorC, -100, revs_to_degs, pow);

	while(getMotorEncoder(motorC) <= revs_to_degs) {}
	eraseDisplay();
}

/*
	Method for pivoting right given a rev and pow.
*/
void encoded_rpivot(float revs, long pow)
{
	float revs_to_degs = (revs * 360 - OFFSET);

	displayCenteredBigTextLine(4, "Rotating Right");

	reset_mencoder();
	setMotorSyncEncoder(motorB, motorC, 100, revs_to_degs, pow);

	while(getMotorEncoder(motorB) <= revs_to_degs) {}
	eraseDisplay();
}
//=======================================================================

//==================== Sensor Operations ================================
/*
	Thread method used to detect distance from an object anteriorily.
*/
task thread_sonar_locator()
{
	int curr_ant_dis;

	while(true)
	{
		/*
		TODO: distance orientation correction:
		What we can do is, once the robot turns 90 degrees for sure,
		could let it travel according to black squares again. Need to check if
		the grey tiles correspond to them. because the object is 7 squares away.
		During this course, if the current distance starts decreasing, then we have
		detected an object.
		Measure difference between current and last distance reading (measure error).
		If the error is positive, then we are ok, we are heading closer to the object.
		If the error is negative, stop the robot. check both sides left and right by rotating a certain angle.
		Compare the difference between both sides.
		And pick rotation path with lowest error.
		Continue this process untill current distance get really low.
		*/

		/*
			slowly scan an angular range. aka 90 degrees left and right or less.

			record current value
			record prev distance

			compute error.

			compute angle.

			follow angular path with least error.

			unless I can get the robot to always line up with box 90 degrees.
		*/

		curr_ant_dis = SensorValue[Sonar];
		displayCenteredBigTextLine(4, "Dist: %3d cm", curr_ant_dis);
	}
}

/*
task thread_whiskers()
{
while(true)
{
int is_touch = SensorValue[touch];
displayCenteredBigTextLine(4, "Bumper: %d", is_touch);
}
}
*/

/*
	Thread Used for faster sampling. Uses both main thread sampling + lightweight threads.
*/
task poll_color()
{
	while(true)
	{
		curr_color = getColorReflected(Colour);
	}
}

int poll_whiskers()
{
	return SensorValue[touch];
}
//=======================================================================

//==================== Calibration ======================================
/*
	Method for calibrating the light dark threshold. Used to sample multiple points in
	the environment and then averaged to get optimal threshold values.
*/
void light_calibration()
{
	int set_delay     = 7000;
	int calib_delay   = set_delay;
	int calib_verif_d = 3000;

	long curr_color;

	displayCenteredTextLine(4, "Light Value?");
	while(calib_delay > 0)
	{
		curr_color = getColorReflected(Colour);

		light = curr_color;
		displayCenteredTextLine(4, "delay %d ", calib_delay);
		calib_delay--;
	}
	displayCenteredTextLine(4, "Light Value: %d", light);
	sleep(calib_verif_d);

	curr_color = 0;
	calib_delay = set_delay;

	displayCenteredTextLine(4, "Grey Value?");
	while(calib_delay > 0)
	{
		curr_color = getColorReflected(Colour);

		grey = curr_color;
		displayCenteredTextLine(4, "delay %d ", calib_delay);
		calib_delay--;
	}
	displayCenteredTextLine(4, "Grey Value: %d", grey);
	sleep(calib_verif_d);

	curr_color = 0;
	calib_delay = set_delay;

	displayCenteredTextLine(4, "Dark Value?");
	while(calib_delay > 0)
	{
		curr_color = getColorReflected(Colour);

		dark = curr_color;
		displayCenteredTextLine(4, "delay %d ", calib_delay);
		calib_delay--;
	}
	displayCenteredTextLine(4, "Dark Value: %d", dark);
	sleep(calib_verif_d);

	// assumed that you calibration was given correct light, grey and dark values.
	thres_bg = (dark + grey) / 2;
	thres_gw = (grey + light) / 2;

	thres_gw -= 3;
	eraseDisplay();
}
//=======================================================================

//==================== Path Correction ==================================
/*
	Method for path correction, called linear backoff. If the color is wrong,
	then correct yourself by sampling your surroundings 3 times.
*/
void linear_backoff(bool direction)
{
	bool skip_correction = true;
	float backoff_val    = 0.1;

	int pow       = 20;
	int samples   = 0;
	int n_samples = 3;


	while(samples < n_samples)
	{
		if(!direction) turnLeft( backoff_val, rotations, pow);
		else           turnRight(backoff_val, rotations, pow);

		// color sample
		curr_color = getColorReflected(Colour);
		samples++;

		if(curr_color < thres_bg || thres_gw < curr_color)
		{
			skip_correction = false;
			break;
		}
	}

	// return to original position if bad color values.
	if(skip_correction)
	{
		if(!direction) turnRight(backoff_val * samples, rotations, 20);
		else           turnLeft( backoff_val * samples, rotations, 20);
	}
}

/*
	Method for path correction between two lines.
*/
void path_correction()
{
	if(thres_bg < curr_color && curr_color < thres_gw)
	{
		// use simple correction algorithm.
		linear_backoff(go_left);
		go_left = !go_left; // toggle.

		eraseDisplay();
	}
}
//=======================================================================

//==================== Phases ===========================================
/*
	Method used to travel forward from the starting tile 'S'.
*/
void initial_step()
{
	displayCenteredBigTextLine(4, "Starting in 5 secs");
	sleep(5000);

	eraseDisplay();

	//encoded_mforward(0.6, 50);// hardwired example.
	encoded_mforward(0.65, 50);
}

/*
	Method used to move along the black dotted line and count them.
*/
void run_phase1()
{
	int black_count = 0;
	int motor_pow = 40;

	bool on_black = false;
	bool on_dotted_line = true;

	short current_color;

	setMotorSync(motorB, motorC, 0, motor_pow);

	while (black_count < 15)
	{
		setMotorSync(motorB, motorC, 0, motor_pow);
		curr_color = getColorReflected(Colour);
		path_correction();

		if(curr_color < 13)
		{
			if(!on_black)
			{
				on_black = true;
				displayCenteredBigTextLine(4, "%d", ++black_count);
				playTone(700, 10);
			}
		} else on_black = false;

		sleep(100);
	}

	reset_motors();
	eraseDisplay();
}

/*
	Method used to count the 7 grey squares + move to finishing tile 'F'.
*/
void run_phase2()
{
	//startTask(thread_sonar_locator);

	while(true)
	{
		// move forward
		displayCenteredBigTextLine(4, "Bumper: %d", poll_whiskers());
	}

	//stopTask(thread_sonar_locator);
}
//=======================================================================

//==================== MAIN =============================================
/*
	main task.
*/
task main()
{
	short rot_pow = 20;

	// Accumulative Light Calibration algorithm for ASGN.
	//light_calibration();

	// initial movement from start tile to black line
	initial_step();

	// first right rotation
	encoded_rpivot(0.55585028, rot_pow);

	//startTask(poll_color);

	// move along black line and count 15 black dots
	run_phase1();

	//stopTask(poll_color);

	// rotate 90 degrees again
	//encoded_rpivot(REV_90, DEFAULT_SPD); // TODO: needs to be configured for our environment

	//run_phase2();
}
//=======================================================================
