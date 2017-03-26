
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
#define DEFAULT_SPD 28           // default ev3 robot speed.
#define OFFSET      23           // offset for revs user defined encoded movements.
#define REV_90      0.5437665781 // exact revolutions for ev3 robot for 90 degrees.
#define REV_360     2.175066313  // exact revolutions for ev3 robot for 360 degrees.
#define SAMPLES     2            // the maximum samples we can obtain.

int smpl_idx = 0;                // index used for sampling and obtaining sample size.
long sample_arr[SAMPLES];

long curr_color = 0;             // obtaining current color.
long thres_l_bl   = 9;           // threshold for identifying black.
long thres_h_bl   = 12;
long thres_bg = 25;
long thres_gw = 40;

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

	//=================== GREY TILE ========================================
	// notify
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
	}

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

task main()
{
	// color_calibrate();
}
