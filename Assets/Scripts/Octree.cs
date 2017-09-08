using System;
using System.Collections.Generic;
using UnityEngine;

public class Octree<TType>
{
    private OctreeNode<TType> root;

    private int count;
    private int depth = 0;
    private float vsLength;
    private float voxelSize = -1;
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

    public float getVoxelSize()
    {
        if(voxelSize == -1)
        {
            voxelSize = calculateVoxelSize();
        }
        return voxelSize;
    }

    // CONSTRUCTORS
    public Octree(Vector3 position, float size, int depth)
    {
        // initialize the root node
        this.root = new OctreeNode<TType>(position, size);

        this.depth = depth;
        this.vsLength  = size;
        this.maxPoint = vsLength / 2;
        this.count = 0;
    }

    // PUBLIC METHODS
    /*
     * Method for adding a new node to data structure.
     */
    public void add(Vector3 pos, Color32 color)
    {
        if (pos.x <= maxPoint && pos.x >= -maxPoint &&
            pos.y <= maxPoint && pos.y >= -maxPoint &&
            pos.z <= maxPoint && pos.z >= -maxPoint)
        {
            root.add(pos, color, depth);
            count++;
        }

        // any position outside mentioned boundary is just inserted at the boundary points.
        else
        {
            Debug.Log("position " + pos + " wasn't inserted because the max size is " + vsLength);
            Debug.Log("The maximum boundaries are: ");
            printBoundaries();
        }
    }

    /*
     * Method for traversing entire Octree data structure.
     */
    public List<Vector3> getAllPoints()
    {
        if(root == null)
        {
            return null;
        }

        return root.getPositions();
    }

    public List<Color32> getAllColors()
    {
        if (root == null)
        {
            return null;
        }

        return root.getColors();
    }

    public void printBoundaries()
    {
        Debug.Log(new Vector3(maxPoint, maxPoint, maxPoint));
        Debug.Log(new Vector3(-maxPoint, maxPoint, maxPoint));
        Debug.Log(new Vector3(maxPoint, -maxPoint, maxPoint));
        Debug.Log(new Vector3(maxPoint, maxPoint, -maxPoint));
        Debug.Log(new Vector3(-maxPoint, -maxPoint, maxPoint));
        Debug.Log(new Vector3(-maxPoint, maxPoint, -maxPoint));
        Debug.Log(new Vector3(maxPoint, -maxPoint, -maxPoint));
        Debug.Log(new Vector3(-maxPoint, -maxPoint, -maxPoint));
    }

    // PRIVATE METHODS

    /*
     * Method for calculating the minimum voxel size given voxel space
     * length and depth of octree. 
     */
    private float calculateVoxelSize()
    {
        return vsLength / Mathf.Pow(2, depth);
    }
}