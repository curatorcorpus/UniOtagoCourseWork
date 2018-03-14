#include "ransac.hpp"
#include <map>
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
    _point[3] = plane[3];

    Vector3d normal;

    normal[0] = plane[0];
    normal[1] = plane[1];
    normal[2] = plane[2];

    double nominator   = std::abs(plane.dot(_point));
    double denominator = std::sqrt(normal.dot(normal));

    return nominator / denominator;
}

/**
*   Method for 
*/
void Ransac::search(std::vector<PlyPoint>* point_cloud, int no_planes, double threshold_distance, int no_ransac_trials)
{   
    // make deep copy
    std::vector<PlyPoint> pc_cpy = (*point_cloud);
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

        std::vector<PlyPoint> new_pc;
        // remove best pc from point cloud copy.
        for(int i = 0; i < pc_cpy.size(); i++) 
        {   
            if(!best_points[i])
                new_pc.push_back(pc_cpy[i]);

        }
        pc_cpy = new_pc;
        std::cout << "Remain points: " << new_pc.size() << std::endl;
    }
}