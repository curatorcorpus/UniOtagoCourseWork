//using System;

using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using UnityEngine;
using UnityEngine.EventSystems;
using UnityEngine.VR.WSA;

public class Octree<TType>
{
    private OctreeNode<TType> root;

    private int count;
	
    private float voxelSpaceSize;
    private float voxelSize;
    private float maxPoint;

    // GETTERS AND SETTERS
    public int Count
    {
        get { return count; }
        //set { this.count = value;  }
    }

    public OctreeNode<TType> Root
    {
        get { return root; }
    }

    // CONSTRUCTORS
    public Octree(Vector3 position, float voxelSpaceSize, float voxelSize)
    {
        // initialize the root node
        this.root = new OctreeNode<TType>(position, voxelSpaceSize);

	    this.voxelSize = voxelSize;
        this.voxelSpaceSize = voxelSpaceSize;
	    
        this.maxPoint = voxelSpaceSize / 2;
        this.count = 0;
    }

    // PUBLIC METHODS
    /*
     * Method for adding a new node to data structure.
     */
    public void Add(Vector3 pos, Color32 color)
    {
        if (pos.x <= maxPoint && pos.x >= -maxPoint &&
            pos.y <= maxPoint && pos.y >= -maxPoint &&
            pos.z <= maxPoint && pos.z >= -maxPoint)
        {
            root.add(pos, color, voxelSize);
            count++;
        }

        // any position outside mentioned boundary is just inserted at the boundary points.
        else
        {
            UnityEngine.Debug.Log("position " + pos + " wasn't inserted because the max size is " + voxelSpaceSize);
            UnityEngine.Debug.Log("The maximum boundaries are: ");
            PrintBoundaries();
        }
    }

    public void AddFill()
    {
        root.AddFill(voxelSize);
    }
    
    /*
     * Method for traversing entire Octree data structure.
     */
    public List<Vector3> GetAllPoints()
    {
        if(root == null)
        {
            return null;
        }

        return root.getPositions();
    }

    public List<Color32> GetAllColors()
    {
        if (root == null)
        {
            return null;
        }

        return root.getColors();
    }

    public void PrintBoundaries()
    {
        UnityEngine.Debug.Log(new Vector3(maxPoint, maxPoint, maxPoint));
        UnityEngine.Debug.Log(new Vector3(-maxPoint, maxPoint, maxPoint));
        UnityEngine.Debug.Log(new Vector3(maxPoint, -maxPoint, maxPoint));
        UnityEngine.Debug.Log(new Vector3(maxPoint, maxPoint, -maxPoint));
        UnityEngine.Debug.Log(new Vector3(-maxPoint, -maxPoint, maxPoint));
        UnityEngine.Debug.Log(new Vector3(-maxPoint, maxPoint, -maxPoint));
        UnityEngine.Debug.Log(new Vector3(maxPoint, -maxPoint, -maxPoint));
        UnityEngine.Debug.Log(new Vector3(-maxPoint, -maxPoint, -maxPoint));
    }
}