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

/**
*   Method for computing the distance from plane to a point.
*   Formula for distance between point and plane:
*       P = (x,y,z)
*       Plane = (a,b,c,d)
*       D = (|ax+by+cz+d|/sqrt(pow(a,2)+pow(b,2)+pow(c,2)))
*   
*   @param plane is the plane we want to measure the point from. 
*   @param point is the point we want to measure the point to.
*   @return the point-plane distance.
*/
double Plane::pt_to_pl_dist(Vector4d plane, Vector3d point)
{
    double nominator   = std::abs(plane[0]*point[0]+plane[1]*point[1]+plane[2]*point[2]+plane[3]);
    double denominator = std::sqrt(plane[0]*plane[0]+plane[1]*plane[1]+plane[2]*plane[2]);

    return nominator / denominator;
}