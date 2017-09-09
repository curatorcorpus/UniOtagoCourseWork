using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class OctreeNode<TType> {

    private static int DEGREE = 8; // branching factor
    private static int TRF = 0;
    private static int TLF = 1;
    private static int BRF = 2;
    private static int BLF = 3;
    private static int TRB = 4;
    private static int TLB = 5;
    private static int BRB = 6;
    private static int BLB = 7;

    private bool debug = false;
    private bool voxelExists = false;
	private float subspaceSize;

	private Vector3 center;
	private OctreeNode<TType>[] children;
    private Color32 clr;

    // CONSTRUCTORS
	public OctreeNode(Vector3 center, float subspaceSize) 
	{
		this.center = center;
		this.subspaceSize = subspaceSize;
	}

    // GETTERS AND SETTERS
	public Vector3 Center
	{
		get { return center; }
		set { this.center = value; }
	}
	public float SubspaceSize
	{
		get { return subspaceSize; }
		set { this.subspaceSize = value; }
	}
    public Color32 Color
    {
        get { return clr; }
        set { this.clr = value; }
    }
	public bool isLeaf()
	{
		return children == null;
	}

    // PUBLIC METHODS
    public List<Vector3> getPositions()
    {
        List<Vector3> pos = new List<Vector3>();
        
        if(children == null)
        {
            if(voxelExists)
            {
                pos.Add(center);
            }
        }
        else
        {
            foreach (OctreeNode<TType> child in children)
            {
                if (child != null)
                {
                    pos.AddRange(child.getPositions());
                }
            }
        }
        return pos;
    }

    public List<Color32> getColors()
    {
        List<Color32> colors = new List<Color32>();

        if (children == null)
        {
            if (voxelExists)
            {
                colors.Add(clr);
            }
        }
        else
        {
            foreach (OctreeNode<TType> child in children)
            {
                if (child != null)
                {
                    colors.AddRange(child.getColors());
                }
            }
        }

        return colors;
    }

    public void add(Vector3 newSize, Color32 color, int depth = 0)
    {
        int bestSSIdx = findBestSubspace(newSize); // find best subspace idx.

        if (depth == 0)
        {
            this.clr = color;
            return;
        }

        if (children == null)
        {
            setupChildren(); // allocates memory to children array.
        }

        if (children[bestSSIdx] == null)
        {
            spawn(bestSSIdx);
        }

        this.children[bestSSIdx].voxelExists = true;
        this.children[bestSSIdx].add(newSize, color, depth - 1);

        // now that we found best sub space, remove other children
        //this.remove(newSize);
    }

    public void remove(Vector3 newSize)
    {
        int bestIdx = findBestSubspace(newSize);

        for (int i = 0; i < children.Length; i++)
        {
            if (bestIdx != i && voxelExists)
            {
                this.children[i] = null;
            }
            else
            {
                Debug.Log("working");
            }
        }
    }

    // allows foreach children
    public IEnumerable<OctreeNode<TType>> Children
	{
		get {return children; }
	}

    // PRIVATE METHODS
    private int findBestSubspace(Vector3 newSize)
    {

        if (debug)
        {
            Debug.Log(newSize.x + " x: " + center.x + " y: " + newSize.y + " " + center.y + " z: " + newSize.z + " " + center.z);
            Debug.Log((newSize.x >= center.x ? 0 : 1) +
               (newSize.y >= center.y ? 0 : 2) +
               (newSize.z >= center.z ? 0 : 4));
            Debug.Log(subspaceSize);
        }

        return (newSize.x >= center.x ? 0 : 1) + 
               (newSize.y >= center.y ? 0 : 2) +
               (newSize.z >= center.z ? 0 : 4);
    }

    private void split()
    {
        float quarter = subspaceSize * 0.25f;
        float newSize = subspaceSize * 0.5f;

        setupChildren(); // allocates memory to children array.

        children[TRF] = new OctreeNode<TType>(this.center + new Vector3(quarter, quarter, quarter), newSize);
        children[TLF] = new OctreeNode<TType>(this.center + new Vector3(-quarter, quarter, quarter), newSize);

        children[BRF] = new OctreeNode<TType>(this.center + new Vector3(quarter, -quarter, quarter), newSize);
        children[BLF] = new OctreeNode<TType>(this.center + new Vector3(-quarter, -quarter, quarter), newSize);

        children[TRB] = new OctreeNode<TType>(this.center + new Vector3(quarter, quarter, -quarter), newSize);
        children[TLB] = new OctreeNode<TType>(this.center + new Vector3(-quarter, quarter, -quarter), newSize);

        children[BRB] = new OctreeNode<TType>(this.center + new Vector3(quarter, -quarter, -quarter), newSize);
        children[BLB] = new OctreeNode<TType>(this.center + new Vector3(-quarter, -quarter, -quarter), newSize);
    }

    private void spawn(int bestIdx)
    {
        float quarter = subspaceSize * 0.25f;
        float newSize = subspaceSize * 0.5f;

        if (TRF == bestIdx)
            children[TRF] = new OctreeNode<TType>(this.center + new Vector3(quarter, quarter, quarter), newSize);
        else if (TLF == bestIdx)
            children[TLF] = new OctreeNode<TType>(this.center + new Vector3(-quarter, quarter, quarter), newSize);
        else if (BRF == bestIdx)
            children[BRF] = new OctreeNode<TType>(this.center + new Vector3(quarter, -quarter, quarter), newSize);
        else if (BLF == bestIdx)
            children[BLF] = new OctreeNode<TType>(this.center + new Vector3(-quarter, -quarter, quarter), newSize);
        else if (TRB == bestIdx)
            children[TRB] = new OctreeNode<TType>(this.center + new Vector3(quarter, quarter, -quarter), newSize);
        else if (TLB == bestIdx)
            children[TLB] = new OctreeNode<TType>(this.center + new Vector3(-quarter, quarter, -quarter), newSize);
        else if (BRB == bestIdx)
            children[BRB] = new OctreeNode<TType>(this.center + new Vector3(quarter, -quarter, -quarter), newSize);
        else if (BLB == bestIdx)
            children[BLB] = new OctreeNode<TType>(this.center + new Vector3(-quarter, -quarter, -quarter), newSize);
    }

    private void setupChildren()
    {
        children = new OctreeNode<TType>[DEGREE];
    }
}
