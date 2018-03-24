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
        std::cout << "Plane Equation: " << best_plane[0] << "x + " << best_plane[1] << "y + " 
                                        << best_plane[2] << "z + " << best_plane[3] << " = 0" << std::endl;
        std::cout << "Remain points: " << new_pc.size() << std::endl << std::endl;
    }
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
std::vector<std::vector<PlyPoint>> Ransac::auto_param_search(std::vector<PlyPoint>* point_cloud, double success_rate)
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

    double threshold = compute_threshold(point_cloud);

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
            trials = 1942806191;
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
                    trials = 1942806191;
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
        std::cout << "Plane Equation: " << best_plane[0] << "x + " << best_plane[1] << "y + " 
                                        << best_plane[2] << "z + " << best_plane[3] << " = 0" << std::endl;
        std::cout << "Remain points: " << new_pc.size() << std::endl << std::endl;
    }

    no_planes = p;

    std::cout << "finished" << std::endl;
    return results;
}

int Ransac::estimate_trials(double success_rate, double no_inliers, int sample_size, double total_size)
{
    double neumerator  = log(1-success_rate);
    double demoninator = log(1-pow((no_inliers/total_size), sample_size));

    return neumerator / demoninator;
}

/**
*   Method for computing the distance from plane to a point.
*   Formula for distance between point and plane:
*       P = (x,y,z)
*       Plane = (a,b,c,d)
*       D = (|ax+by+cz+d|/sqrt(pow(a,2)+pow(b,2)+pow(c,2)))
*/
double Ransac::distance_to_plane(Vector4d plane, Vector3d point)
{
    double nominator   = std::abs(plane[0]*point[0]+plane[1]*point[1]+plane[2]*point[2]+plane[3]);
    double denominator = std::sqrt(plane[0]*plane[0]+plane[1]*plane[1]+plane[2]*plane[2]);

    return nominator / denominator;
}

double Ransac::compute_threshold(std::vector<PlyPoint>* point_cloud) 
{
    double best_thresh = std::numeric_limits<float>::max();

    std::vector<PlyPoint> pc_cpy = (*point_cloud);

    for(int i = 0; i < 40; i++) 
    {
        int pi1 = std::rand() % pc_cpy.size();
        int pi2 = std::rand() % pc_cpy.size();
        int pi3 = std::rand() % pc_cpy.size();

        Vector4d plane = Plane::compute_plane(pc_cpy[pi1].location, pc_cpy[pi2].location, pc_cpy[pi3].location);
        double possible_thresh = estimate_trials_thresh_distance(point_cloud, plane);

        if(possible_thresh < best_thresh) 
        {
            best_thresh = possible_thresh;
        }
        std::cout << "Sampling Local Groups " << i+1 << " Best Threshold: " << best_thresh << std::endl;
    }
    return best_thresh;
}

double Ransac::estimate_trials_thresh_distance(std::vector<PlyPoint>* point_cloud, Vector4d plane)
{

    std::vector<PlyPoint> pc_cpy = (*point_cloud);
    std::vector<double> buckets;

    double max_dist = max_distance(point_cloud, plane);

    int num_jumps = 10000;
    int point_count = 0;
    int  place_hold = 0;
    int req_points = std::ceil(pc_cpy.size()*0.225);

    double jump_size = max_dist / (num_jumps-1);

    for(int i = 0; i < num_jumps; i++) 
    {
        buckets.push_back(0);
    }

    for(int i = 0; i < pc_cpy.size(); i++) 
    {
        double dist = distance_to_plane(plane, pc_cpy[i].location);
        buckets[std::floor(dist / jump_size)]++;
    }

    while(point_count < req_points) 
    {
        point_count += buckets[place_hold];
        place_hold++;
    }

    double threshold = (place_hold+1)*jump_size;

    return threshold;
}

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

/*
double Ransac::estimate_trials_thresh_distance(std::vector<PlyPoint>* point_cloud)
{
    // make deep copy
    std::vector<PlyPoint> pc_cpy = (*point_cloud);

    double max = 0.0;
    int subset = 0.01*pc_cpy.size();
double wtf = 0.0;
    for(int i = 1; i <= 10; i++) 
    {
        int lower = subset*(i-1), upper = subset * i;
        Vector4d plane = Plane::compute_plane(pc_cpy[rand()%pc_cpy.size()].location,
                                              pc_cpy[rand()%pc_cpy.size()].location, 
                                              pc_cpy[rand()%pc_cpy.size()].location);

        double max = 0;
        for(int idx = lower; idx < upper; idx++) 
        {
            double dist = distance_to_plane(plane, pc_cpy[idx].location);
            //if(dist > max) {
              //  max = dist;
            //}
            max+=dist;
            //std::cout << (int)dist << std::endl; 
        }
wtf += max/upper;
        std::cout << max/upper << std::endl; 
    }
            std::cout << wtf/10 << std::endl; 
    return wtf/10-0.068;
}*/