#pragma config(Sensor, S1,     zzz,            sensorEV3_Color, modeEV3Color_Color)
#pragma config(Sensor, S2,     Touch,          sensorEV3_Touch)
#pragma config(Sensor, S3,     Colour,         sensorEV3_Color)
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
#define DEFAULT_SPD 30           // default ev3 robot speed.
#define OFFSET      23           // offset for revs user defined encoded movements.
#define REV_90      0.5437665781 // exact revolutions for ev3 robot for 90 degrees.
#define REV_360     2.175066313  // exact revolutions for ev3 robot for 360 degrees.
#define SAMPLES     2         // the maximum samples we can obtain.

bool go_left = false; // bool for path correction.

int smpl_idx = 0;     // index used for sampling and obtaining sample size.

long curr_color = 0;  // obtaining current color.
long thres_l_bl   = 9; // threshold for identifying black.
long thres_h_bl   = 12;
//long thres_bg   = 0;  // threshold for idenifying black-grey line.
//long thres_gw   = 0;  // threshold for identifying grey-white line.

long sample_arr[SAMPLES];

long thres_bg = 31;
long thres_gw = 44;

//=======================================================================

//=================== Utility Methods =================================
/*
	Shell sort for sampling array when calibrating. Used to obtain max and min values.
	O(n log n) apparently.
*/
void sample_sort()
{
	for(int m = smpl_idx/2 ; m > 0; m /= 2)
	{
		for(int j = m; j < smpl_idx; j++)
		{
   		for(int i = j - m; i >= 0; i -= m)
   		{
      	if(sample_arr[i + m] >= sample_arr[i]) break;
				else
     		{
        	int mid = sample_arr[i];
        	sample_arr[i] = sample_arr[i + m];
        	sample_arr[i + m] = mid;
				}
			}
		}
	}
}

/*
 Method for obtaining the maximum value of the array.
 Assuming the array is sorted. (Call sample_sort).
*/
int get_max_clr()
{
	return sample_arr[smpl_idx-1];
}

/*
 Method for obtaining the minimum value of the array.
 Assuming the array is sorted. (Call sample_sort).
*/
int get_min_clr()
{
	return sample_arr [0];
}

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
	return SensorValue[Touch];
}
//=======================================================================

//==================== Calibration ======================================
/*
	Method for calibrating the light dark threshold. Used to sample multiple points in
	the environment and then averaged to get optimal threshold values.
*/

task sample()
{
	// compensate for inital colors readings.
	sample_arr[smpl_idx] = getColorReflected(Colour);
	sample_arr[smpl_idx] = getColorReflected(Colour);
	sample_arr[smpl_idx] = getColorReflected(Colour);
	sample_arr[smpl_idx] = getColorReflected(Colour);
	sample_arr[smpl_idx] = getColorReflected(Colour);
	sleep(30);

	while(smpl_idx < SAMPLES)
	{
		sample_arr[smpl_idx++] = getColorReflected(Colour);
		sleep(10);
	}
}

/*
	Method for resetting sample variables + data structures.
*/
void reset_sampler()
{
	smpl_idx = 0;

	// initialize
	for(int i = 0; i < SAMPLES; i++)
	{
		sample_arr[i] = 0;
	}
}

/*
	Method for obtaining n arbutariy points on the tile.
*/
void point_sampling()
{
		// compensate for inital colors readings.
	sample_arr[smpl_idx] = getColorReflected(Colour);
	sample_arr[smpl_idx] = getColorReflected(Colour);
	sample_arr[smpl_idx] = getColorReflected(Colour);
	sample_arr[smpl_idx] = getColorReflected(Colour);
	sample_arr[smpl_idx] = getColorReflected(Colour);
	sleep(30);

	while(smpl_idx < 5)
	{
		displayCenteredTextLine(4, "Point %d, waiting 5 secs", smpl_idx + 1);
		sleep(5000);

		sample_arr[smpl_idx++] = getColorReflected(Colour);
	}
}

/*
	Method for obtaining multiple number of sample points by rotating.
*/
void circular_sampling(float revs, int spd)
{
	// start sample
	startTask(sample);
	turnLeft(revs, rotations, spd);
	stopTask(sample);
}

/*
	Method for obtaining mutliple number of sample points on the grey - black line edge.
*/
void edge_sampling(float revs, int pow)
{
	encoded_mforward(revs, pow);
}

/*
	Method for obtaining averages of sample points.
*/
int get_avg_tile_clr()
{
	// average
	int ctr   = 0;
	int size  = smpl_idx + 1;
	int total = 0;

	while(ctr < size) total += sample_arr[ctr++];

	return ceil(total / size);
}

/*
	Method for obtaining threshold values for path correction.
*/
void color_calibration()
{
	int i = 0;
	int clr_avg = 0;
	int smpl_spd = 10;

	int nw_tiles = 0;
	int ng_tiles = 0;
	int nb_tiles = 0;

	int w_avg = 0;
	int g_avg = 0;
	int b_avg = 0;

	int w_max = 0;
	int g_max = 0;
	int b_max = 0;

	int w_min = 0;
	int g_min = 0;
	int b_min = 0;
/*
	//=================== WHITE TILE ========================================
	// notify
	displayCenteredTextLine(4, "Starting white tile calibration in 7 secs");
	sleep(7000);

	// reset
	reset_sampler();

	// white tile sampling
	point_sampling();

	// notify
	displayCenteredTextLine(4, "Starting circular_sampling 7 secs turning left");
	sleep(7000);
	circular_sampling(REV_90 / 2, smpl_spd);

	// sort samples
	sample_sort();

	// obtain average
	clr_avg = get_avg_tile_clr();

	w_avg = clr_avg;
	w_min = get_min_clr();
	w_max = get_max_clr();

	// results
	displayCenteredTextLine(4, "Color avg for White Tile was %d", w_avg);
	displayCenteredTextLine(6, "Min: %d, Max: %d", w_min, w_max);
	sleep(10000);

	// reset average
	clr_avg = 0;
*/
	//=================== GREY TILE ========================================
	// notify
	/*
	i = 0;
	while(i < 19)
	{
		displayCenteredTextLine(4, "Starting grey tile calibration in 7 secs");
		sleep(7000);

		// reset
		reset_sampler();

		// grey tile sampling
		displayCenteredTextLine(4, "Starting point_sampling 7 secs turning left");
		sleep(7000);
		point_sampling();

		// notify for circular sampling
		displayCenteredTextLine(4, "Starting circular_sampling 7 secs turning left");
		sleep(7000);
		circular_sampling(1, smpl_spd);

		// notify for edge sampling
		displayCenteredTextLine(4, "Starting edge_sampling 7 secs");
		sleep(7000);
		edge_sampling(REV_360 *2 , smpl_spd);

		// sort samples
		sample_sort();

		// obtain average
		clr_avg = get_avg_tile_clr();

		g_avg = clr_avg;
		g_min = get_min_clr();
		g_max = get_max_clr();

		// results
		displayCenteredTextLine(2, "Color avg for Grey Tile");
		displayCenteredTextLine(4, "Avg: %d", g_avg);
		displayCenteredTextLine(6, "Min: %d, Max: %d", g_min, g_max);
		sleep(15000);
		i++;

		eraseDisplay();
	}*/

	// reset average
	clr_avg = 0;

	//=================== BLACK TILE ========================================
	i = 0;
	while(i < 15)
	{
		// notify
		displayCenteredTextLine(4, "Starting black tile calibration in 7 secs");
		sleep(7000);

		// reset
		reset_sampler();

		// grey tile sampling
		point_sampling();

		// notify for circular sampling
		displayCenteredTextLine(4, "Starting circular_sampling 7 secs turning left");
		sleep(7000);
		circular_sampling(REV_90 / 2, smpl_spd);

		// sort samples
		sample_sort();

		// obtain average
		clr_avg = get_avg_tile_clr();

		b_avg = clr_avg;
		b_min = get_min_clr();
		b_max = get_max_clr();

		// results
		displayCenteredTextLine(2, "Color avg for Black Tile");
		displayCenteredTextLine(4, "Avg: %d", b_avg);
		displayCenteredTextLine(6, "Min: %d, Max: %d", b_min, b_max);
		sleep(15000);

		i++;
		eraseDisplay();
	}
		// record thresholds in global varibale manually.
}
//=======================================================================

//==================== Path Correction ==================================
bool path_corrected = true;
/*
	Method for path correction, called linear backoff. If the color is wrong,
	then correct yourself by sampling your surroundings 3 times.
*/
void linear_backoff(bool direction)
{
	bool skip_correction = true;
	float backoff_val    = 0.1;

	int pow       = 15;
	int samples   = 0;
	int n_samples = 3;

	while(samples < n_samples)
	{
		if(!direction) turnLeft( backoff_val, rotations, pow);
		else           turnRight(backoff_val, rotations, pow);

		// sample color.
		curr_color = getColorReflected(Colour);
		samples++;

		if(curr_color < thres_bg || thres_gw < curr_color)
		{
			path_corrected = true;
			skip_correction = false;
			break;
		}
	}

	// return to original position if bad color values with offset.
	if(skip_correction)
	{
		if(!direction) turnRight(backoff_val * (samples + 1), rotations, pow);
		else           turnLeft( backoff_val * (samples + 1), rotations, pow);
	}
}

/*
	Method for path correction between two lines.
*/
void path_correction()
{
	curr_color = getColorReflected(Colour);

	if(thres_bg < curr_color && curr_color < thres_gw)
	{
		path_corrected = false;
		while(path_corrected == false){
		// use simple correction algorithm.
		linear_backoff(go_left);
		go_left = !go_left; // toggle.

	}
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

	encoded_mforward(0.62, 50);// hardwired example.
	//encoded_mforward(0.65, 50);
}
int black;
task black_sensor()
{
	while(true) black = SensorValue[S3];
}
/*
	Method used to move along the black dotted line and count them.
*/
void run_phase1()
{
	int black_count = 0;

	bool on_black = false;
	bool on_dotted_line = true;

	startTask(black_sensor);
curr_color = getColorReflected(Colour);
curr_color = getColorReflected(Colour);
curr_color = getColorReflected(Colour);
curr_color = getColorReflected(Colour);
curr_color = getColorReflected(Colour);

	while (black_count < 15)
	{
		if(black == 1)
		{
			if(!on_black)
			{
				on_black = true;
				displayCenteredBigTextLine(4, "%d", ++black_count);
				playTone(700, 10);
			}
		} else on_black = false;

		path_correction();

		if(path_corrected) setMotorSync(motorB, motorC, 0, DEFAULT_SPD);
		sleep(130);
	}
stopTask(black_sensor);
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

	// Accumulative color Calibration algorithm for ASGN.
	//color_calibration();

	// initial movement from start tile to black line
	initial_step();

	// first right rotation
	// encoded_rpivot(0.55585028, rot_pow);
	//encoded_rpivot(REV_90, rot_pow);
turnRight(REV_360/4,rotations,rot_pow);
	// move along black line and count 15 black dots
	run_phase1();

	// rotate 90 degrees again
	encoded_rpivot(REV_90, DEFAULT_SPD); // TODO: needs to be configured for our environment

	//run_phase2();
}
//=======================================================================
