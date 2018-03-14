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

void Ransac::search()
{

}