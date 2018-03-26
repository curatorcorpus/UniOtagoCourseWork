/**
*   @Author Jung-Woo (Noel) Park.
*/

#include "ransac.hpp"

#include <map>
#include <cmath>

int Ransac::no_planes;

/**
*   Method for applying basic RANSAC search for finding points to closest planes. 
*   RANSAC parameters are manually determined by the user.
*
*   @param *point_cloud is the point cloud data. 
*   @param no_planes is the number of planes in the scene.
*   @param threshold is the threshold point-plane distance.
*   @param no_ransac_trials is the number of ransac trials per plane.
*   @return list of points grouped into planes.
*/
std::vector<std::vector<PlyPoint>> Ransac::search(std::vector<PlyPoint>* point_cloud, int no_planes, double threshold, int no_ransac_trials)
{   
    std::vector<std::vector<PlyPoint>> results;

    // make deep copy
    std::vector<PlyPoint> pc_cpy = (*point_cloud);
    int size = pc_cpy.size();

    // for each plane count until max number of planes.
    for(int p = 0; p < no_planes; p++) 
    {
        int pc_size = pc_cpy.size();

        Vector4d best_plane;
        std::map<int, bool> best_points;

        // for each ransac trial until max number of ransac trials.
        for(int r = 0; r < no_ransac_trials; r++)
        {
            // generate plane from three random points
            Vector4d plane = Plane::compute_plane(pc_cpy[rand()%pc_size].location,
                                                  pc_cpy[rand()%pc_size].location, 
                                                  pc_cpy[rand()%pc_size].location);
            std::map<int, bool> curr_pc;
            // for each point in the point cloud.
            for(int i = 0; i < pc_size; i++) 
            {
                Vector3d point = pc_cpy[i].location;
                
                //std::cout << distance_to_plane(plane, point) << std::endl;
                // if point distance to plane is less than threshold distance.
                if(distance_to_plane(plane, point) < threshold) 
                {
                    curr_pc[i] = true;
                }
            }

            if(curr_pc.size() > best_points.size()) 
            {
                best_plane  = plane;
                best_points = curr_pc;
            }
        }

        std::vector<PlyPoint> tmp;
        std::vector<PlyPoint> new_pc;
        // remove best pc from point cloud copy.
        for(int i = 0; i < pc_cpy.size(); i++) 
        {   
            if(!best_points[i])
                new_pc.push_back(pc_cpy[i]);
            else 
                tmp.push_back(pc_cpy[i]);
        }
        results.push_back(tmp);

        pc_cpy = new_pc;

        std::cout << std::endl;
        std::cout << "Plane Equation: (" << best_plane[0] << ")x + (" << best_plane[1] << ")y + (" 
                                        << best_plane[2] << ")z + (" << best_plane[3] << ") = 0" << std::endl;
        std::cout << "Remain points: " << new_pc.size() << std::endl << std::endl;
    }

    // Add remaining Points.
    results.push_back(pc_cpy);

    return results;
}

/**
*   Method for applying auto parameter RANSAC search for finding points to closest planes. 
*   The following RANSAC parameters are determined algorithmically:
*       no_trials - the number of ransac trials is determined using formula: log(1-success_rate) / log(1-pow((no_inliers/total_size), sample_size));
*       threshold - the threshold point-plane distance that indicates the point is part of plane. 
*       no_planes - the number of planes, we continue to apply ransac until majority of point clouds are explained by planes.
*   
*   @param *point_cloud is the point cloud data. 
*   @param success_rate is the probability you will pick all inliers for each sample points [TODO: revise on definition].
*   @return list of points grouped into planes.
*/
std::vector<std::vector<PlyPoint>> Ransac::auto_param_search(std::vector<PlyPoint>* point_cloud, double success_rate, double thresh_prob)
{
    std::vector<std::vector<PlyPoint>> results;

    // dereference point cloud pointer.
    std::vector<PlyPoint> pc_cpy = (*point_cloud);

    int size = pc_cpy.size();
    int inliers = 3;
    int p = 0;

    // for each plane count until max number of planes.
    int target_remain_pc = pc_cpy.size()*0.1;

    //double threshold = estimate_trials_thresh_distance(point_cloud);

    double threshold = compute_threshold(point_cloud, thresh_prob);

    std::cout << "Estimate Threshold Distance: " << threshold << std::endl;

    std::cout << std::endl;
    while(pc_cpy.size() > target_remain_pc)
    {
        int pc_size = pc_cpy.size();

        Vector4d best_plane;
        std::map<int, bool> best_points;

        int trials = estimate_trials(success_rate, double(inliers), 3, (double)pc_cpy.size());
        if(trials < 0) 
        {
            trials = std::numeric_limits<int>::max();
        }

        // for each ransac trial until max number of ransac trials.
        for(int r = 0; r < trials; r++)
        {
            // generate plane from three random points
            Vector4d plane = Plane::compute_plane(pc_cpy[rand()%pc_size].location,
                                                  pc_cpy[rand()%pc_size].location, 
                                                  pc_cpy[rand()%pc_size].location);
            std::map<int, bool> curr_pc;
            // for each point in the point cloud.
            for(int i = 0; i < pc_size; i++) 
            {
                Vector3d point = pc_cpy[i].location;
                
                //std::cout << distance_to_plane(plane, point) << std::endl;
                // if point distance to plane is less than threshold distance.
                if(distance_to_plane(plane, point) < threshold) 
                {
                    curr_pc[i] = true;
                }
            }

            if(curr_pc.size() > best_points.size()) 
            {
                best_plane  = plane;
                best_points = curr_pc;

                inliers = curr_pc.size()+results.size();
                trials = estimate_trials(success_rate, double(inliers), 3, (double)pc_cpy.size());

                if(trials < 0) 
                {
                    trials = std::numeric_limits<int>::max();
                }
                std::cout << "Trials Remaining: " << trials << std::endl;
                r = 0;
            }
        }

        std::vector<PlyPoint> tmp;
        std::vector<PlyPoint> new_pc;
        // remove best pc from point cloud copy.
        for(int i = 0; i < pc_cpy.size(); i++) 
        {   
            if(!best_points[i])
                new_pc.push_back(pc_cpy[i]);
            else 
                tmp.push_back(pc_cpy[i]);
        }
        results.push_back(tmp);

        pc_cpy = new_pc;

        ++p;
        std::cout << "Plane Equation: (" << best_plane[0] << ")x + (" << best_plane[1] << ")y + (" 
                                        << best_plane[2] << ")z + (" << best_plane[3] << ") = 0" << std::endl;
        std::cout << "Remain points: " << new_pc.size() << std::endl << std::endl;
    }

    // Add remaining Points.
    results.push_back(pc_cpy);

    no_planes = p;

    std::cout << "RANSAC Search Finished!" << std::endl;
    return results;
}

/**
*   Method for estimating the number of RANSAC trials. 
*   
*   @param success_rate 
*   @param no_inliers 
*   @param sample_size
*   @param total_size
*   @return the number of trials. (Limited to integer data type max). 
*/
int Ransac::estimate_trials(double success_rate, double no_inliers, int sample_size, double total_size)
{
    double neumerator  = log(1-success_rate);
    double demoninator = log(1-pow((no_inliers/total_size), sample_size));

    return std::ceil(neumerator / demoninator); // ceil used to prevent infinite loops when trial value becomes less than 1.
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
double Ransac::distance_to_plane(Vector4d plane, Vector3d point)
{
    double nominator   = std::abs(plane[0]*point[0]+plane[1]*point[1]+plane[2]*point[2]+plane[3]);
    double denominator = std::sqrt(plane[0]*plane[0]+plane[1]*plane[1]+plane[2]*plane[2]);

    return nominator / denominator;
}

/**
*   Method that guesses the threshold point-plane distance to classify a point is part of a plane. 
*   The method samples 40 random planes, searches 
*/
double Ransac::compute_threshold(std::vector<PlyPoint>* point_cloud, double thresh_prob) 
{
    double best_thresh = std::numeric_limits<double>::max();
    int max_samples = 40;

    std::vector<PlyPoint> pc_cpy = (*point_cloud);

    std::cout << std::endl;
    std::cout << "Sampling " << max_samples << " random planes." << std::endl;

    for(int i = 0; i < max_samples; i++) 
    {
        int pi1 = std::rand() % pc_cpy.size();
        int pi2 = std::rand() % pc_cpy.size();
        int pi3 = std::rand() % pc_cpy.size();

        Vector4d plane = Plane::compute_plane(pc_cpy[pi1].location, pc_cpy[pi2].location, pc_cpy[pi3].location);
        double possible_thresh = sample_thresh_distance(point_cloud, plane, thresh_prob);

        if(possible_thresh < best_thresh) 
        {
            best_thresh = possible_thresh;
        }
        std::cout << "Sampling Random Plane " << i+1 << ", Best Threshold: " << best_thresh << std::endl;
    }
    return best_thresh;
}

/**
*   Method for estimating the threshold distance for points to be classified as part of a plane.
*   The method first finds the maximum distance of all point-plane distance for a random plane. 
*   Then for the given maximum distance, we have 10000 buckets to represent each subdistance of 
*   of the maximum distance: 
*       bucket 0    -> max_distance/10000
*       bucket 9999 -> max_distance
*   
*   The distances of all points are computed and allocated to each bucket group. Now we have count of all 
*   points of the same point-plane distance foreach bucket. The user will determine the percentage of points 
*   that's forms one plane. If the user underestimates percentage of points, the threshold estimates will be small
*   and therefore the RANSAC algorithm will be more pedantic - will over approximate. Else if the user overestimates percentage 
*   of points, the threshold estimates will be large and therefore the RANSAC algorithm will be relaxed. But
*   that means, it will under approximate. 
*   
*   Another way to look at this is if there are lots of points in the earlier buckets, then that indicates 
*   there local group of points that fall below half the maximum distance. If there are lots of points 
*   in the later buckets, then there are local group of points that are above half the maximum distance. 
*   
*   This method assumes for a small region of points it can be approximated to a plane. 
*
*   @param point cloud is the list of points.
*   @param plane is the plane we using to compute point-plane distance of. 
*   @param thresh_prob is the percentage of points required to determine the threshold distance. 
*   @return the threshold distance based on the probability of points to define the threshold distance.
*/
double Ransac::sample_thresh_distance(std::vector<PlyPoint>* point_cloud, Vector4d plane, double thresh_prob)
{

    std::vector<PlyPoint> pc_cpy = (*point_cloud);
    std::vector<double> buckets;

    int no_buckets = 10000;
    int point_count = 0;
    int bucket_count = 0;
    int req_points = std::ceil(pc_cpy.size()*thresh_prob);

    double max_dist = max_distance(point_cloud, plane);
    double bucket_interval = max_dist / (no_buckets-1);

    // Initialize buckets.
    for(int i = 0; i < no_buckets; i++) 
        buckets.push_back(0);

    // Computes point-plane distance and counts number of points in local bucket group.
    for(int i = 0; i < pc_cpy.size(); i++) 
    {
        double dist = distance_to_plane(plane, pc_cpy[i].location);
        buckets[std::floor(dist / bucket_interval)]++;
    }

    // Sums point count in buckets until it exceeds user point count estimate for one plane. 
    while(point_count < req_points) 
        point_count += buckets[bucket_count++];

    double threshold = (bucket_count+1)*bucket_interval;

    return threshold;
}

/**
*   Method for finding the maximum distance from a random plane. 
*   
*   @param point cloud is the list of points.
*   @param plane is the plane we are using to measure the point-plane distance.
*   @return maximum point-plane based on plane parameter.
*/
double Ransac::max_distance(std::vector<PlyPoint>* point_cloud, Vector4d plane)
{
    double max_dist = 0.0, curr_dist = 0.0;

    std::vector<PlyPoint> pc_cpy = (*point_cloud);

    for(int i = 0; i < pc_cpy.size(); i++) 
    {
        if((curr_dist = distance_to_plane(plane, pc_cpy[i].location)) > max_dist)
        {
            max_dist = curr_dist;
        }
    }

    return max_dist;
}