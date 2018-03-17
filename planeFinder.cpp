
#include "plane.hpp"
#include "ransac.hpp"

#include <iostream>
#include <chrono>
#include <Eigen/Geometry>

using namespace std;
using namespace Eigen;

int main (int argc, char *argv[]) {

    // Check the commandline arguments.
    /*if(argc != 6) 
    {
        std::cout << "Usage: planeFinder <input file> <output file> <number of planes> <point-plane threshold> <number of RANSAC trials>" << std::endl;
        return -1;
    }*/
/*
    int n_planes = atoi(argv[3]);
    double threshold = atof(argv[4]);
    int n_trials = atoi(argv[5]);
*//*
    std::cout << "Searching for " << nPlanes << " planes" << std::endl;
    std::cout << "Using a point-plane threshold of " << threshold << " units" << std::endl;
    std::cout << "Applying RANSAC with " << nTrials << " trials" << std::endl;*/
    
    int n_planes = 1;
    double threshold = 3;
    int n_trials = 1000;

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

    int total_filtered_pts = 0;

    SimplePly new_ply;
    for(int p = 0; p < n_planes; p++) 
    {
        Vector3i col = colours[p];
        vector<PlyPoint> plane_pc = results[p];
    
        int size = plane_pc.size();
        total_filtered_pts += size;
        for(int i = 0; i < size; i++) 
        {
            plane_pc[i].colour = col;
            new_ply.add_point_cloud(plane_pc[i]);
        }
    }

    cout << "Total Points Remaining Points: " << total_filtered_pts << endl;
    cout << "Filtered Noise Points: " << (ply.size() - total_filtered_pts) << endl;

    // Write the resulting (re-coloured) point cloud to a PLY file.
    std::cout << "Writing PLY data to " << argv[2] << std::endl;
    if (!new_ply.write(argv[2]))
    {
        std::cout << "Could not write PLY data to file " << argv[2] << std::endl;
        return -2;
    }
    return 0;
}

SimplePly generate_plane_data(int sigma)
{
    SimplePly plane_pc;

    unsigned seed = std::chrono::system_clock::now().time_since_epoch().count();
    std::default_random_engine generator (seed);
    
    std::normal_distribution<double> dist(0.0, sigma);

    for(int x = -100; x < 100; x++) 
    {
        for(int z = -100; z < 100; z++) 
        { 
            PlyPoint point;

            point.location = Vector3d(x,dist(generator),z);
            point.colour = Vector3i(std::rand()%255,std::rand()%255,std::rand()%255);
            plane_pc.add_point_cloud(point);
        }
    }

    return plane_pc;
}