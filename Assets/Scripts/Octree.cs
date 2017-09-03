using System;
using System.Collections.Generic;
using UnityEngine;

public class Octree<TType>
{
    private OctreeNode<TType> root;

    private int count;
    private int depth = 0;
    private float maxSize;
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
    public Octree(Vector3 position, float size, int depth)
    {
        this.depth = depth;
        this.root  = new OctreeNode<TType>(position, size);
        this.maxSize  = size;
        this.maxPoint = maxSize / 2;
        this.count = 0;
    }

    // PUBLIC METHODS
    public void add(Vector3 pos)
    {
        if (pos.x <= maxPoint && pos.x >= -maxPoint &&
            pos.y <= maxPoint && pos.y >= -maxPoint &&
            pos.z <= maxPoint && pos.z >= -maxPoint)
        {
            root.add(pos, depth);
            count++;
        }

        // any position outside mentioned boundary is just inserted at the boundary points.
        else
        {
            Debug.Log("position " + pos + " wasn't inserted because the max size is " + maxSize);
            Debug.Log("The maximum boundaries are: ");
            printBoundaries();
        }
    }

    public List<Vector3> get()
    {
        if(root == null)
        {
            return null;
        }

        return root.getPositions();
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
}