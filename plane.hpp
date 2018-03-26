/**
*   @Author Jung-Woo (Noel) Park.
*/

#ifndef PLANE_HPP_
#define PLANE_HPP_

#include <Eigen/Core>
#include <Eigen/Geometry>

using namespace Eigen;

class Plane 
{

private: 


public:

    static double pt_to_pl_dist(Vector4d plane, Vector3d point);
	static Vector4d compute_plane(Vector3d a, Vector3d b, Vector3d c);
};

#endif