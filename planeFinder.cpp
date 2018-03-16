
#include "plane.hpp"
#include "ransac.hpp"

#include <iostream>
#include <Eigen/Geometry>

using namespace std;
using namespace Eigen;

int main (int argc, char *argv[]) {

    // Check the commandline arguments.
    /*if(argc != 6) 
    {
        std::cout << "Usage: planeFinder <input file> <output file> <number of planes> <point-plane threshold> <number of RANSAC trials>" << std::endl;
        return -1;
    }

    int n_planes = atoi(argv[3]);
    double threshold = atof(argv[4]);
    int n_trials = atoi(argv[5]);

    std::cout << "Searching for " << nPlanes << " planes" << std::endl;
    std::cout << "Using a point-plane threshold of " << threshold << " units" << std::endl;
    std::cout << "Applying RANSAC with " << nTrials << " trials" << std::endl;*/
    int n_planes = 5;
    double threshold = 0.03;
    int n_trials = 50;

    // Storage for the point cloud.ll
    SimplePly ply;

    // Read in the data from a PLY file
    std::cout << "Reading PLY data from " << argv[1] << std::endl;
    if(!ply.read(argv[1])) 
    {
        std::cout << "Could not read PLY data from file " << argv[1] << std::endl;
        return -1;
    }
    std::cout << "Read " << ply.size() << " points" << std::endl;

    // Recolour points - here we are just doing colour based on index
    vector<PlyPoint>* point_cloud = ply.get_points();
    
    cout << endl;

    // Search for planes using RANSAC.
    std::vector<std::vector<PlyPoint>> results = Ransac::search(point_cloud, n_planes, threshold, n_trials);

    // Generate plane colours
    std::vector<Vector3i> colours;

    colours.push_back(Eigen::Vector3i(255,0,0));
    colours.push_back(Eigen::Vector3i(0,255,0));
    colours.push_back(Eigen::Vector3i(0,0,255));
  
    if(n_planes > 3)
    {
        int r = rand() % 255 + 1;
        int g = rand() % 255 + 1;
        int b = rand() % 255 + 1;
        for(int i = 4; i < n_planes; i++) 
        {
            colours.push_back(Eigen::Vector3i(r,g,b));
        }
    }

    SimplePly new_ply;
    for(int p = 0; p < n_planes; p++) 
    {
        Vector3i col = colours[p];
        vector<PlyPoint> plane_pc = results[p];
       /* vector<PlyPoint>* ptr_plane_pc = results[p];
        vector<PlyPoint>& dptr_plane_pc = *ptr_plane_pc; // dereference pointer.
*/
        int size = plane_pc.size();cout<<size<<endl;
        for(int i = 0; i < size; i++) 
        {
            plane_pc[i].colour = col;
            new_ply.add_point_cloud(plane_pc[i]);
        }
    }

    // Write the resulting (re-coloured) point cloud to a PLY file.
    std::cout << "Writing PLY data to " << argv[2] << std::endl;
    if (!new_ply.write(argv[2]))
    {
        std::cout << "Could not write PLY data to file " << argv[2] << std::endl;
        return -2;
    }

    return 0;
}
