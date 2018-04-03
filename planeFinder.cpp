/**
*   @Author Jung-Woo (Noel) Park.
*/

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
int main (int argc, char *argv[]) 
{
    bool   run_raw = false;
    bool   filter_outliers = false;
    double threshold;
    double success_rate = 0.9;
    double thresh_prob = 0.21;
    int    no_planes;
    int    n_trials;

    std::string input, output;

    CMDParser p("<input file> <output file>");
    p.addOpt("f", -1, "fout", "[Filters out outliers in write out file].");
    p.addOpt("p", 1, "prob", "[Success Probability] - Default: 0.9");
    p.addOpt("r", 3, "raw", "[Raw RANSAC Method] Usage: planeFinder <number of planes> <point-plane threshold> <number of RANSAC trials>");
    p.addOpt("t", 1, "tpercent","[Estimate percentage of points that defines the biggest plane] - Default: 0.21");
    p.init(argc, argv);
    
    // obtain input and output file names.
    input = argv[1];
    output = argv[2];

    if(p.isOptSet("f"))
    {
        filter_outliers = true;
    }
    if(p.isOptSet("p"))
    {
        success_rate = atof(p.getOptsString("p")[0].c_str());
        if(success_rate >= 1.0 || success_rate <= 0)
        {
            cerr << "Invalid Success Rate!" << endl;
            return 0;
        }
    }
    if(p.isOptSet("t"))
    {
        thresh_prob = atof(p.getOptsString("t")[0].c_str());
        if(thresh_prob >= 1.0 || thresh_prob <= 0)
        {
            cerr << "Invalid Success Rate!" << endl;
            return 0;
        }
    }
    if(p.isOptSet("r")) 
    {
        no_planes  = atoi(p.getOptsString("r")[0].c_str());
        threshold = atof(p.getOptsString("r")[1].c_str());
        n_trials  = atoi(p.getOptsString("r")[2].c_str());

        if(no_planes <= 0)
        {
            cerr << "Invalid! Negative/zero number of planes." << endl;
            return 0;
        } 
        if(threshold <= 0) 
        {
            cerr << "Invalid! Negative/zero number for threshold distance." << endl;
            return 0;
        }
        if(n_trials <= 0)
        {
            cerr << "Invalid! Negative/zero number of trials." << endl;
            return 0;
        }

        Ransac::no_planes = no_planes;

        run_raw = true;

        std::cout << "Searching for " << no_planes << " planes" << std::endl;
        std::cout << "Using a point-plane threshold of " << threshold << " units" << std::endl;
        std::cout << "Applying RANSAC with " << n_trials << " trials" << std::endl;
    }

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
        results = Ransac::auto_param_search(point_cloud, success_rate, thresh_prob);
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

    int total_inlier_pts = 0;

    SimplePly new_ply;
    for(int p = 0; p < no_planes; p++) 
    {
        Vector3i col = colours[p];
        vector<PlyPoint> plane_pc = results[p];
    
        int size = plane_pc.size();
        total_inlier_pts += size;
        for(int i = 0; i < size; i++) 
        {
            plane_pc[i].colour = col;
            new_ply.add_point_cloud(plane_pc[i]);
        }
    }

    // Add remaining Colors.
    if(!filter_outliers) 
    {
        for(int p = no_planes; p < results.size(); p++) 
        {
            Vector3i col = colours[p];
            vector<PlyPoint> plane_pc = results[p];
        
            int size = plane_pc.size();
            for(int i = 0; i < size; i++) 
            {
                new_ply.add_point_cloud(plane_pc[i]);
            }
        }
    }

    cout << "Total Inlier Points:  " << total_inlier_pts << endl;
    cout << "Total Outlier Points: " << (ply.size() - total_inlier_pts) << endl;
    cout << "Total Planes Found:   " << no_planes << endl << endl;

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
*   Utility Method for generating plane point cloud data - only a single plane.
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