using System;
using System.Collections;
using UnityEngine;

public class Octree<TType>
{
    private OctreeNode<TType> root;

    private int depth = 0;

    public OctreeNode<TType> Root
    {
        get { return root; }
    }

    // CONSTRUCTORS
    public Octree(Vector3 position, float size, int depth)
    {
        this.depth = depth;
        this.root  = new OctreeNode<TType>(position, size);
    }

    // PUBLIC METHODS
    public void add(Vector3 pos)
    {
        root.add(pos, depth);
    }

}
