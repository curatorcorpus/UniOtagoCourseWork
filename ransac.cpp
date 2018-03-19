#include "ransac.hpp"

#include <map>
#include <cmath>

int Ransac::no_planes;

/**
*   Method for 
*/
std::vector<std::vector<PlyPoint>> Ransac::search(std::vector<PlyPoint>* point_cloud, int no_planes, double threshold_distance, int no_ransac_trials)
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
                if(distance_to_plane(plane, point) < threshold_distance) 
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

std::vector<std::vector<PlyPoint>> Ransac::auto_param_search(std::vector<PlyPoint>* point_cloud, double success_rate)
{
    std::vector<std::vector<PlyPoint>> results;

    // make deep copy
    std::vector<PlyPoint> pc_cpy = (*point_cloud);
    int size = pc_cpy.size();

    int inliers = 3;
    int p = 0;

    // for each plane count until max number of planes.
    int target_remain_pc = pc_cpy.size()*(1-0.9);

    double threshold_distance = estimate_trials_thresh_distance(point_cloud);

    std::cout << "Estimate Threshold Distance: " << threshold_distance << std::endl;

    while(pc_cpy.size() > target_remain_pc)
    {
        int pc_size = pc_cpy.size();

        Vector4d best_plane;
        std::map<int, bool> best_points;

        int trials = estimate_trials(success_rate, double(inliers), 3, (double)pc_cpy.size());
        if(trials < 0) 
        {
            trials = 1000000;
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
                if(distance_to_plane(plane, point) < threshold_distance) 
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
                    trials = 1000000;
                }
                std::cout << "Estimate Trials: " << trials << std::endl;
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

        std::cout << std::endl;
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
    Vector4d _point;

    _point[0] = point[0]; 
    _point[1] = point[1];
    _point[2] = point[2];
    _point[3] = 1;

    Vector3d normal;

    normal[0] = plane[0];
    normal[1] = plane[1];
    normal[2] = plane[2];

    double nominator   = std::abs(plane.dot(_point));
    double denominator = std::sqrt(normal.dot(normal));

    return nominator / denominator;
}

double Ransac::point_to_point_dist(Vector3d a, Vector3d b) 
{
    double x = b[0]-a[0];
    double y = b[1]-a[1];
    double z = b[2]-a[2];

    double powx = x*x;
    double powy = y*y;
    double powz = z*z;

    return std::sqrt((powx+powy+powz));
}

double Ransac::estimate_trials_thresh_distance(std::vector<PlyPoint>* point_cloud)
{
    // make deep copy
    std::vector<PlyPoint> pc_cpy = (*point_cloud);

    double max = 0;

    int index = rand()%pc_cpy.size();
    Vector4d plane = Plane::compute_plane(pc_cpy[index].location,
                                          pc_cpy[index+1].location, 
                                          pc_cpy[index+2].location);
    // for each point in the point cloud.
    for(int i = index+3; i < index+pc_cpy.size()/5; i++) 
    {
        Vector3d point = pc_cpy[i].location;
        
        //std::cout << distance_to_plane(plane, point) << std::endl;
        // if point distance to plane is less than threshold distance.
        double dist = distance_to_plane(plane, point);
        if(dist > max) {
            max = dist;
        }
    }

    return max;
}