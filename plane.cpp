/**
*   @Author Jung-Woo (Noel) Park.
*/

#include "plane.hpp"

/**
*	Method for ...
* 	Plane equation: ax + by + cz + d = 0
*/
Vector4d Plane::compute_plane(Vector3d a, Vector3d b, Vector3d c) {

	Vector4d plane;

  	// Find the vectors from the reference point a. 
  	// Computes vector a->b, and a->c.
  	Vector3d ab = b-a;
  	Vector3d ac = c-a;
  	
  	// Apply cross product of the vectors ab, ac and find perpendicular vector.
  	Vector3d normal = ab.cross(ac);
  	
  	// Compute d value of plane equation.
  	plane[3] = -(normal.dot(a));

  	// assign normal scalar values to plane.
  	plane[0] = normal[0];
  	plane[1] = normal[1];
  	plane[2] = normal[2];

  	return plane;
}