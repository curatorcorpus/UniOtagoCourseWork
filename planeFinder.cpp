/**
*   @Author Jung-Woo (Noel) Park.
*/
#include <CGAL/Exact_predicates_inexact_constructions_kernel.h>
//#include <CGAL/Triangulation_3.h>
#include <CGAL/IO/Geomview_stream.h>
#include <CGAL/IO/Triangulation_geomview_ostream_3.h>
#include <CGAL/IO/Color.h>
#include <CGAL/Delaunay_triangulation_3.h>
#include <CGAL/Triangulation_vertex_base_with_info_3.h>
#include <CGAL/IO/Color.h>

#include <iostream>
#include <fstream>
#include <cassert>
#include <list>
#include <vector>
#include <regex>

typedef CGAL::Exact_predicates_inexact_constructions_kernel K;
typedef CGAL::Triangulation_vertex_base_with_info_3<CGAL::Color, K> Vb;
typedef CGAL::Triangulation_data_structure_3<Vb>                    Tds;
typedef CGAL::Delaunay_triangulation_3<K, Tds>                      Delaunay;
typedef Delaunay::Point         Point;

#include "cmd_parser.hpp"
#include "plane.hpp"
#include "ransac.hpp"

#include <chrono>
#include <Eigen/Geometry>

using namespace std;
using namespace Eigen;

/**
*   Main method.
*/
int main (int argc, char *argv[]) 
{
    bool   run_raw         = false;
    bool   filter_outliers = false;
    bool   triangulate     = false;
    bool   wireframe       = false;
    double success_rate    = 0.9;
    double thresh_prob     = 0.21;
    double plane_percent   = 0.1;
    double threshold;
    int    no_planes;
    int    n_trials;

    std::string input, output;

    CMDParser p("<input file> <output file>");
    p.addOpt("f", -1, "fout", "[Filters out outliers in write out file].");
    p.addOpt("p", 1, "prob", "[Success Probability] - Default: 0.9");
    p.addOpt("r", 3, "raw", "[Raw RANSAC Method] Usage: planeFinder <number of planes> <point-plane threshold> <number of RANSAC trials>");
    p.addOpt("t", 1, "tpercent","[Estimate percentage of points that defines the biggest plane] - Default: 0.21");
    p.addOpt("tr",-1, "tri", "[Triangulates Planes Points]");
    p.addOpt("w", -1, "wireframe", "[Show triangulation with wireframe, must have triangulation active!]");
    p.addOpt("pp", 1, "planepercent", "[Sets the plane search termination criteria for RANSAC] - Default 0.1");
    p.init(argc, argv);
    
    regex e(".ply");

    // obtain input and output file names.
    input = argv[1];
    output = argv[2];

    if (!regex_search(input,e) || !regex_search(output,e)) {
        cerr << "Incorrect input or output file!" << endl;
        return 0;
    }

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
            cerr << "Invalid Threshold Percentage!" << endl;
            return 0;
        }
    }
    if(p.isOptSet("pp")) 
    {
        plane_percent = atof(p.getOptsString("pp")[0].c_str());
        if(plane_percent >= 1.0 || plane_percent <= 0)
        {
            cerr << "Invalid Plane Percentage!" << endl;
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
    if(p.isOptSet("tr"))
    {
        triangulate = true;
        if(p.isOptSet("w"))
        {
            wireframe = true;
        }
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
    int total_size = point_cloud->size();
    // Search for planes using RANSAC.
    std::vector<std::vector<PlyPoint>> results;

    if(run_raw) 
    {
        results = Ransac::search(point_cloud, no_planes, threshold, n_trials);
    } else 
    {
        results = Ransac::auto_param_search(point_cloud, success_rate, thresh_prob, plane_percent);
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
        int b = 0;
        for(int i = 4; i < no_planes; i++) 
        {
            colours.push_back(Eigen::Vector3i(r,g,b));
        }
    }

    int total_inlier_pts = 0;

    Delaunay meshes[no_planes]; // for triangulating results.
    SimplePly new_ply;
    Vector3d min, max;
    int min_sum = 0, max_sum = numeric_limits<int>::max();

    cout << endl;
    if(triangulate) 
        cout << "Preparing Ply file & Triangulating!" << endl;
    else
        cout << "Preparing Ply file!" << endl;

    int neglect_value;
    if(triangulate) 
    {
        if(total_size > 100000)
        {
            neglect_value = total_size * 0.001;
        }
    }

    // Add all inliers and assign plane colours.
    for(int p = 0; p < no_planes; p++) 
    {
        Delaunay tris;
        Vector3i col = colours[p];
        vector<PlyPoint> plane_pc = results[p];
        
        int size = plane_pc.size();
        total_inlier_pts += size;
        for(int i = 0; i < size; i++) 
        {
            Vector3d pt = plane_pc[i].location;
            plane_pc[i].colour = col;
            new_ply.add_point_cloud(plane_pc[i]);
            if(triangulate) 
            {   
                // insert point for triangulation.
                if(total_size > 100000)
                {
                    if(i % neglect_value == 0) 
                    {
                        tris.insert(Point(pt(0),pt(1),pt(2)));
                    }
                }
                else
                {
                    tris.insert(Point(pt(0),pt(1),pt(2)));
                }

                // sum point values and determine min and max bounding box.
                int sum = pt(0)+pt(1)+pt(2);
                if(sum < min_sum)
                {
                    min_sum = sum;
                    min = pt;
                }else if(sum > max_sum)
                {
                    max_sum = sum;
                    max = pt;
                };
            }
        }
        if(triangulate) 
        {
            meshes[p] = tris;
            cout << "Plane " << (p+1) << " triangulated!" << endl;
        }
        cout << "Plane " << (p+1) << " coloured!" << endl;
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

    cout << endl;
    cout << "Total Inlier Points:  " << total_inlier_pts << endl;
    cout << "Total Outlier Points: " << (ply.size() - total_inlier_pts) << endl;
    cout << "Total Planes Found:   " << no_planes << endl << endl;

    // Write the resulting (re-coloured) point cloud to a PLY file.
    std::cout << "Writing PLY data to " << output << std::endl << endl;
    if (!new_ply.write(output))
    {
        std::cout << "Could not write PLY data to file " << output << std::endl;
        return -2;
    }

    if(triangulate) 
    {   
        cout << "Rendering CGAL Delaunay Triangulation Planes!" << endl;

        // increase initial bounding box.
        double minx = min(0)*2.0;
        double miny = min(1)*2.0;
        double minz = min(2)*2.0;
        double maxx = max(0)*2.0;
        double maxy = max(1)*2.0;
        double maxz = max(2)*2.0;

        // checks if values are out of bounds.
        if(minx < 1e-2 || miny < 1e-2 || minz < 1e-2)
        {
            minx = -3;
            miny = -3;
            minz = -3;
        }
        if(maxx < 1e-2 || maxy < 1e-2 || maxz < 1e-2) 
        {
            maxx = 3;
            maxy = 3;
            maxz = 3;
        }

        // draw triangulated points.
        CGAL::Geomview_stream gv(CGAL::Bbox_3(minx,miny,minz,maxx,maxy,maxz));
        gv.set_bg_color(CGAL::Color(255, 255, 255));
        if(wireframe)
        {
            gv.set_wired(true);
        }
        gv.clear();
        for(int i = 0; i < no_planes; i++) 
        {   
            gv << meshes[i];
            cout << "Rendering!" << endl;
        }
        cout << "Finished Rendering Triangles!" << endl;
        std::cout << "Enter any key to finish" << std::endl;
        char ch;
        std::cin >> ch;
        gv.clear();
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