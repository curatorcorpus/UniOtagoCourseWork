#include "cmd_parser.hpp"
#include "plane.hpp"
#include "ransac.hpp"

#include <iostream>
#include <chrono>
#include <Eigen/Geometry>

using namespace std;
using namespace Eigen;

/**
*   Main method.
*/
int main (int argc, char *argv[]) {

    bool run_raw = false;
    double threshold, success_rate;
    int no_planes, n_trials;

    std::string input, output;

    CMDParser p("<input file> <output file> <success rate>");

    p.addOpt("r", 5, "raw", "[Raw RANSAC Method] Usage: planeFinder <input file> <output file> <number of planes> <point-plane threshold> <number of RANSAC trials>");

    p.init(argc, argv);

    if(p.isOptSet("r")) 
    {
        no_planes  = atoi(argv[3]);
        threshold = atof(argv[4]);
        n_trials  = atoi(argv[5]);

        run_raw = true;

        std::cout << "Searching for " << no_planes << " planes" << std::endl;
        std::cout << "Using a point-plane threshold of " << threshold << " units" << std::endl;
        std::cout << "Applying RANSAC with " << n_trials << " trials" << std::endl;
    }

    if(argc != 4) 
    {   
        p.showHelp();
        return 0;
    }

    input = argv[1];
    output = argv[2];
    success_rate = atof(argv[3]);

    /*
    int n_planes = 1;
    double threshold = 3;
    int n_trials = 1000;
*/
    // Storage for the point cloud.ll
    SimplePly ply;

    // Read in the data from a PLY file
    std::cout << "Reading PLY data from " << input << std::endl;
    if(!ply.read(input)) 
    {
        std::cout << "Could not read PLY data from file " << input << std::endl;
        return -1;
    }
    std::cout << "Read " << ply.size() << " points" << std::endl;

    // Recolour points - here we are just doing colour based on index
    vector<PlyPoint>* point_cloud = ply.get_points();

    // Search for planes using RANSAC.
    std::vector<std::vector<PlyPoint>> results;

    if(run_raw) 
    {
        results = Ransac::search(point_cloud, no_planes, threshold, n_trials);
    } else 
    {
        results = Ransac::auto_param_search(point_cloud, success_rate);
    }

    // Generate plane colours
    std::vector<Vector3i> colours;

    colours.push_back(Eigen::Vector3i(255,0,0));
    colours.push_back(Eigen::Vector3i(0,255,0));
    colours.push_back(Eigen::Vector3i(0,0,255));
    colours.push_back(Eigen::Vector3i(255,255,0));
    colours.push_back(Eigen::Vector3i(255,0,255));
    colours.push_back(Eigen::Vector3i(0,255,255));
    colours.push_back(Eigen::Vector3i(255,255,255));

    no_planes = Ransac::no_planes;

    if(no_planes > colours.size())
    {
        int r = rand() % 255 + 1;
        int g = rand() % 255 + 1;
        int b = rand() % 255 + 1;
        for(int i = 4; i < no_planes; i++) 
        {
            colours.push_back(Eigen::Vector3i(r,g,b));
        }
    }

    int total_filtered_pts = 0;

    SimplePly new_ply;
    for(int p = 0; p < no_planes; p++) 
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
    std::cout << "Writing PLY data to " << output << std::endl;
    if (!new_ply.write(output))
    {
        std::cout << "Could not write PLY data to file " << output << std::endl;
        return -2;
    }
    return 0;
}

/**
*   Utility Method for generating plane point cloud data.
*/
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