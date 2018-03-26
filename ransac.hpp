/**
*   @Author Jung-Woo (Noel) Park.
*/

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

        static double sample_thresh_distance(std::vector<PlyPoint>* point_cloud, Vector4d plane, double thresh_prob);
        static int estimate_trials(double success_rate, double no_inliers, int sample_size, double total_size);
        static double compute_threshold(std::vector<PlyPoint>* point_cloud, double thresh_prob);
        static double max_distance(std::vector<PlyPoint>* point_cloud, Vector4d plane);

    public:

        static int no_planes;

        static std::vector<std::vector<PlyPoint>> search(std::vector<PlyPoint>* point_cloud, int no_planes, double threshold_distance, int no_ransac_trials);
        static std::vector<std::vector<PlyPoint>> auto_param_search(std::vector<PlyPoint>* point_cloud, double success_rate, double thresh_prob);
};

#endif