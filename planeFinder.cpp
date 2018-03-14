
#include "plane.hpp"
#include "ransac.hpp"

#include <iostream>
#include <Eigen/Geometry>

using namespace std;
using namespace Eigen;

int main (int argc, char *argv[]) {

    // Check the commandline arguments.
    /*if (argc != 6) {
        std::cout << "Usage: planeFinder <input file> <output file> <number of planes> <point-plane threshold> <number of RANSAC trials>" << std::endl;
        return -1;
    }*/
    /*
    int nPlanes = atoi(argv[3]);
    double threshold = atof(argv[4]);
    int nTrials = atoi(argv[5]);

    std::cout << "Searching for " << nPlanes << " planes" << std::endl;
    std::cout << "Using a point-plane threshold of " << threshold << " units" << std::endl;
    std::cout << "Applying RANSAC with " << nTrials << " trials" << std::endl;  
    */

    // Storage for the point cloud.ll
    SimplePly ply;

    // Read in the data from a PLY file
    std::cout << "Reading PLY data from " << argv[1] << std::endl;
    if (!ply.read(argv[1])) {
        std::cout << "Could not read PLY data from file " << argv[1] << std::endl;
        return -1;
    }
    std::cout << "Read " << ply.size() << " points" << std::endl;

    // Recolour points - here we are just doing colour based on index
    std::cout << "Recolouring points" << std::endl;
    std::vector<Vector3i> colours;

    vector<PlyPoint>* point_cloud = ply.get_points();
    /*
    for(PlyPoint point : *points) {
        cout << point.location << endl;
    }*/
Ransac::search(point_cloud, 2, 0.05, 2);
    //Vector4d plane = Plane::get_plane((*points)[0].location, (*points)[1].location, (*points)[2].location);
    //Vector4d plane = Plane::compute_plane(Vector3d(0,0,0), Vector3d(1,2,3), Vector3d(3,2,1));
    /*cout << plane << endl << endl;
    cout << (*points)[0].location << endl << endl;
    double dist = Ransac::distance_to_plane(plane, (*points)[0].location);
cout << dist << endl << endl;*/
    /*colours.push_back(Eigen::Vector3i(255,0,0));
    colours.push_back(Eigen::Vector3i(0,255,0));
    colours.push_back(Eigen::Vector3i(0,0,255));*/
    // Can add more colours as needed
    /*
    size_t planeSize = ply.size()/nPlanes;
    for (size_t ix = 0; ix < ply.size(); ++ix) {
        size_t planeIx = ix / planeSize;
        size_t colourIx = planeIx % colours.size(); // May need to recycle colours
        ply[ix].colour = colours[colourIx];
    }
    *//*
    // Write the resulting (re-coloured) point cloud to a PLY file.
    std::cout << "Writing PLY data to " << argv[2] << std::endl;
    if (!ply.write(argv[2])) {
        std::cout << "Could not write PLY data to file " << argv[2] << std::endl;
        return -2;
    }*/

    return 0;
}
