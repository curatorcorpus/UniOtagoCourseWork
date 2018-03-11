#ifndef PLANE_HPP_
#define PLANE_HPP_

#include <Eigen/Core>
#include <Eigen/Geometry>

using namespace Eigen;

class Plane 
{

private: 


public:

	static Vector4d get_plane(Vector3d a, Vector3d b, Vector3d c);
};

#endif