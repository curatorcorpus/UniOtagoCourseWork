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
        static double point_to_point_dist(Vector3d a, Vector3d b);
        static double estimate_trials_thresh_distance(std::vector<PlyPoint>* point_cloud);
        static int estimate_trials(double success_rate, double no_inliers, int sample_size, double total_size);

    public:

        static int no_planes;

        static std::vector<std::vector<PlyPoint>> search(std::vector<PlyPoint>* point_cloud, int no_planes, double threshold_distance, int no_ransac_trials);
        static std::vector<std::vector<PlyPoint>> auto_param_search(std::vector<PlyPoint>* point_cloud, double success_rate);
};

#endif