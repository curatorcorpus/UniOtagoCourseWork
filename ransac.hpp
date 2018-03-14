#ifndef RANSAC_HPP_
#define RANSAC_HPP_

#include <iostream>

#include <Eigen/Core>
#include <Eigen/Geometry>

using namespace Eigen;

class Ransac 
{

    private:

        static double distance_to_plane(Vector4d plane, Vector3d point);
        
    public:
        
        static void search();
};

#endif