#pragma config(Sensor, S3,     Colour,         sensorEV3_Color, modeEV3Color_Reflected) // We need to set colour Sensor to reflected light
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

#define black_threshold		15
#define white_threshold 	40
#define SMALL_TILE_DISTANCE 0.6 // Need to define. Based on 10cm tile

/* Motor Functions  Method used to tell both wheels to turn certain revs. */
void reset_motors(){
  setMotorSpeed(motorB, 0);
  setMotorSpeed(motorC, 0);
  sleep(10);
}
/* Method used to reset the motor encoder (turn) values. */
void reset_mencoder(){
  resetMotorEncoder(motorB);
  resetMotorEncoder(motorC);
}
/* Method for move forward given n of rotations */
void encoded_mforward(float revs, long pow){
  float revs_to_degs = revs * 360;
  reset_mencoder();
  setMotorSyncEncoder(motorB, motorC, 0, revs_to_degs, pow);
  while(getMotorEncoder(motorB) <= revs_to_degs) {}
}

/* Method for pivot left given a rev and pow. */
void encoded_lpivot(float revs, long pow){
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
void encoded_rpivot(float revs, long pow){
  float revs_to_degs = (revs * 360 - OFFSET);
  displayCenteredBigTextLine(4, "Rotating Right");
  reset_mencoder();
  setMotorSyncEncoder(motorB, motorC, 100, revs_to_degs, pow);
  while(getMotorEncoder(motorB) <= revs_to_degs) {

  }
  eraseDisplay();
}


/*

Does not work

Can be sent whether we are looking for a white or a black tile
Will take incremental rotations in either direction until it
determines the correct color

*/

void pathCorrect(char desiredTile){
	short pivot_increment = 20;
	short left_pivots = 0;
	short right_pivots = 0;
	short max_pivots = 10;

	bool onPath, attempted_left, attempted_right, found_left, found_right = false;

	while(!onPath){
		// Look for color pivoting left
		if(left_pivots <= max_pivots && (!attempted_left)){
			encoded_lpivot(REV_360/pivot_increment, DEFAULT_SPD);
			sleep(50);
	  		short current_colour = SensorValue[Colour];
	  		left_pivots++;
	  		if(desiredTile=='w' && current_colour > white_threshold){ // Testing for White and white Found
	  			onPath = true;
	  			found_left = true;
	  		}
	  		else if(desiredTile=='b' && current_colour < black_threshold){ // testing for Black and black found
	  			onPath = true;
	  			found_left = true;
	  		}
	  		else{
	  			left_pivots++;
	  		}
		}

		// Max Left pivots reached. reset back to normal
		else if(left_pivots == max_pivots && (!attempted_left)){ // Left incremental pivots did not find the color
			encoded_rpivot(REV_360/pivot_increment * max_pivots, DEFAULT_SPD);
			attempted_left = true;
		}

		else if(right_pivots <= max_pivots && (!attempted_right)){
			encoded_rpivot(REV_360/pivot_increment, DEFAULT_SPD);
			sleep(50);
	  		short current_colour = SensorValue[Colour];
	  		right_pivots++;
	  		if(desiredTile=='w' && current_colour > white_threshold){ // Testing for White and white Found
	  			onPath = true;
	  			found_right = true;
	  		}
	  		else if(desiredTile=='b' && current_colour < black_threshold){ // testing for Black and black found
	  			onPath = true;
	  			found_right = true;
	  		}
	  		else{
	  			left_pivots++;
	  		}
		}
		// Max right pivots reached. reset back to normal
		else if(right_pivots == max_pivots && (!attempted_right)){ // Left incremental pivots did not find the color
			encoded_rpivot(REV_360/pivot_increment * max_pivots, DEFAULT_SPD);
			attempted_right = true;
		}
		else{
			displayCenteredBigTextLine(4, "Robot is lost");
		}
	}

	// Path was found at defined left pivots. Move forwards, then reverse pivot by pivoting right
	if(onPath && found_left){
		encoded_mforward(SMALL_TILE_DISTANCE/2, DEFAULT_SPD);
		encoded_rpivot(REV_360/pivot_increment * left_pivots, DEFAULT_SPD);
	}
	// Path was found at defined right pivots. Move forwards, then reverse pivot by pivoting left
	else if(onPath && found_right){
			encoded_mforward(SMALL_TILE_DISTANCE/2, DEFAULT_SPD);
			encoded_lpivot(REV_360/pivot_increment * right_pivots, DEFAULT_SPD);
	}







}




/* Move forward specific rotations, check for black */
void on_track(){
	int black_count = 0;
	int tile_count = 0;

	while(tile_count <= 30){
		encoded_mforward(SMALL_TILE_DISTANCE, DEFAULT_SPD);
		reset_mencoder();

		/* At this stage the robot is sitting on white tile 
		Sleep time depends on speed, we need to ensure it is high enough for full tile transit.
		This could be better handled with a toggle saying on_black */
		sleep(100); 

		short current_colour = SensorValue[Colour];	/* Reflected Colour */

		if( (tile_count % 2 == 0) || (tile_count == 0) ){ // Expecting White
			if(current_colour > 40){ // This tile is white
				tile_count++;
			} else{ // tile is not white
				/*
				This is where we handle path correction
				pathCorrect('w');
				tile_count++;
				*/
		  }
		}
		else{ // Expecting black tile
			if(current_colour < 15){ // This tile is black
				tile_count++;
				black_count++;
				eraseDisplay();
				playTone(200,20);
				displayCenteredBigTextLine(4, "Black");
			} else{ // tile is not black
				/*
				This is where we handle path correction
				pathCorrect('b');
				black_count++;
				tile_count++;
				*/
		  }
		}
	}
}




task main(){
	/* Initial Stage */
	encoded_mforward(0.62, DEFAULT_SPD);
	encoded_rpivot(REV_90, DEFAULT_SPD);

  	/* After Initial Pivot */
  	on_track();

  	/* After second pivot */
	encoded_rpivot(REV_90, DEFAULT_SPD);
  	encoded_mforward(SMALL_TILE_DISTANCE * 4, DEFAULT_SPD);
}
