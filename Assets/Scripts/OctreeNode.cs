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
    private bool isLeafVoxel = false;
	
    private float subspaceSize;
    
	private OctreeNode<TType>[] children;

    private Color32 clr;
    private Vector3 center;

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
    public bool IsLeafVoxel
    {
        get { return isLeafVoxel; }
    }

    // PUBLIC METHODS
    public List<Vector3> getPositions()
    {
        List<Vector3> pos = new List<Vector3>();

        if (isLeafVoxel)
        {
            pos.Add(center);
        }
        else if (children != null)
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

        if (isLeafVoxel)
        {
            colors.Add(clr);
        }
        else if(children != null)
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

    public void add(Vector3 pos, Color32 color, float minVoxelSize)
    {
        if (subspaceSize <= minVoxelSize)
        {
            //Debug.Log(subspaceSize + " " + minVoxelSize);
            //Debug.Log(subspaceSize == minVoxelSize);
            isLeafVoxel = true;
            clr = color;
            
            return;
        }
        
        int bestSSIdx = findBestSubspace(pos); // find best subspace idx.

        if (children == null)
            setupChildren(); // allocates memory to children array.

        if (children[bestSSIdx] == null)
            spawn(bestSSIdx);
        
        this.children[bestSSIdx].add(pos, color, minVoxelSize);
    }

    // allows foreach children
    public IEnumerable<OctreeNode<TType>> Children
	{
		get {return children; }
	}

    // PRIVATE METHODS
    private int findBestSubspace(Vector3 pos)
    {
        if (debug)
        {
            Debug.Log(pos.x + " x: " + center.x + " y: " + pos.y + " " + center.y + " z: " + pos.z + " " + center.z);
            Debug.Log((pos.x >= center.x ? 0 : 1) +
               (pos.y >= center.y ? 0 : 2) +
               (pos.z >= center.z ? 0 : 4));
            Debug.Log(subspaceSize);
        }

        return (pos.x >= center.x ? 0 : 1) + 
               (pos.y >= center.y ? 0 : 2) +
               (pos.z >= center.z ? 0 : 4);
    }

    private void split()
    {
        float quarter = subspaceSize * 0.25f;
        float pos = subspaceSize * 0.5f;

        setupChildren(); // allocates memory to children array.

        children[TRF] = new OctreeNode<TType>(this.center + new Vector3(quarter, quarter, quarter), pos);
        children[TLF] = new OctreeNode<TType>(this.center + new Vector3(-quarter, quarter, quarter), pos);

        children[BRF] = new OctreeNode<TType>(this.center + new Vector3(quarter, -quarter, quarter), pos);
        children[BLF] = new OctreeNode<TType>(this.center + new Vector3(-quarter, -quarter, quarter), pos);

        children[TRB] = new OctreeNode<TType>(this.center + new Vector3(quarter, quarter, -quarter), pos);
        children[TLB] = new OctreeNode<TType>(this.center + new Vector3(-quarter, quarter, -quarter), pos);

        children[BRB] = new OctreeNode<TType>(this.center + new Vector3(quarter, -quarter, -quarter), pos);
        children[BLB] = new OctreeNode<TType>(this.center + new Vector3(-quarter, -quarter, -quarter), pos);
    }

    private void spawn(int bestIdx)
    {
        float quarter = subspaceSize * 0.25f;
        float voxelSpaceHalf = subspaceSize * 0.5f;

        if (TRF == bestIdx)
            children[TRF] = new OctreeNode<TType>(this.center + new Vector3(quarter, quarter, quarter), voxelSpaceHalf);
        else if (TLF == bestIdx)
            children[TLF] = new OctreeNode<TType>(this.center + new Vector3(-quarter, quarter, quarter), voxelSpaceHalf);
        else if (BRF == bestIdx)
            children[BRF] = new OctreeNode<TType>(this.center + new Vector3(quarter, -quarter, quarter), voxelSpaceHalf);
        else if (BLF == bestIdx)
            children[BLF] = new OctreeNode<TType>(this.center + new Vector3(-quarter, -quarter, quarter), voxelSpaceHalf);
        else if (TRB == bestIdx)
            children[TRB] = new OctreeNode<TType>(this.center + new Vector3(quarter, quarter, -quarter), voxelSpaceHalf);
        else if (TLB == bestIdx)
            children[TLB] = new OctreeNode<TType>(this.center + new Vector3(-quarter, quarter, -quarter), voxelSpaceHalf);
        else if (BRB == bestIdx)
            children[BRB] = new OctreeNode<TType>(this.center + new Vector3(quarter, -quarter, -quarter), voxelSpaceHalf);
        else if (BLB == bestIdx)
            children[BLB] = new OctreeNode<TType>(this.center + new Vector3(-quarter, -quarter, -quarter), voxelSpaceHalf);
    }

    private void setupChildren()
    {
        children = new OctreeNode<TType>[DEGREE];
    }
}
