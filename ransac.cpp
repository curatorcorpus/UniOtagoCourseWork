#include "ransac.hpp"

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

    double nominator   = plane.dot(_point);
    double denominator = std::sqrt(normal.dot(normal));

    return nominator / denominator;
}

/**
*   Method for 
*/
void Ransac::search(std::vector<PlyPoint>* point_cloud, int no_planes, double threshold_distance, int no_ransac_trials)
{   
    int pc_size = (*point_cloud).size();

    // for each plane count until max number of planes.
    for(int p = 0; p < no_planes; p++) 
    {
        // for each ransac trial until max number of ransac trials.
        for(int r = 1; r < no_ransac_trials; r++)
        {
            // generate plane from three random points
            Vector4d plane = Plane::compute_plane((*point_cloud)[rand()%pc_size].location,
                                              (*point_cloud)[rand()%pc_size].location, 
                                              (*point_cloud)[rand()%pc_size].location);


        }
    }
}