#ifndef RANSAC_HPP_
#define RANSAC_HPP_

#include <iostream>
#include <vector>

#include <Eigen/Core>
#include <Eigen/Geometry>

#include "SimplePly.h"
#include "plane.hpp"

using namespace Eigen;

class Ransac 
{
    private:

        static double distance_to_plane(Vector4d plane, Vector3d point);

    public:

        static void search(std::vector<PlyPoint>* point_cloud, int no_planes, double threshold_distance, int no_ransac_trials);
};

#endif