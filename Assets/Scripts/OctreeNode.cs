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
    private bool newSizeExists = false;
	private float subspaceSize;

	private Vector3 center;
	private OctreeNode<TType>[] children;
    private Color32 clr;

    // CONSTRUCTORS
	public OctreeNode(Vector3 center, float subspaceSize) 
	{
		this.center = center;
		this.subspaceSize 	  = subspaceSize;
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
	public void add(Vector3 newSize, Color32 color, int depth = 0)
    {
		if(depth == 0)
        {
            this.clr = color;
            return;
        }

        if(children == null)
            splitSubSpace();

        int bestSSIdx = findBestSubspace(newSize); // find best subspace idx.

        this.children[bestSSIdx].newSizeExists = true;

       // Debug.Log("WTF " + this.children[bestSSIdx].SubspaceSize);
        this.children[bestSSIdx].add(newSize, color, depth - 1);

        // now that we found best sub space, remove other children
        //this.remove();
    }

    public List<Vector3> getPositions()
    {
        List<Vector3> pos = new List<Vector3>();
        
        if(children == null)
        {
            if(newSizeExists)
            {
                pos.Add(center);
            }
        }
        else
        {
            foreach (OctreeNode<TType> child in children)
            {
                pos.AddRange(child.getPositions());
            }
        }
        return pos;
    }

    public List<Color32> getColors()
    {
        List<Color32> colors = new List<Color32>();

        if (children == null)
        {
            if (newSizeExists)
            {
                colors.Add(clr);
            }
        }
        else
        {
            foreach (OctreeNode<TType> child in children)
            {
                colors.AddRange(child.getColors());
            }
        }
        return colors;
    }

    public void remove()
    {
        int bestIdx = findBestSubspace(this.center);

        for (int i = 0; i < children.Length; i++)
        {
            if (newSizeExists)
            {
                this.children[i] = null;
            }
            else
            {
                Debug.Log("working");
            }
        }
    }

    public void branch(int depth = 0)
    {
        float quarter = subspaceSize * 0.25f;

        setupChildren();
        for (int i = 0; i < children.Length; i++)
        {
            Vector3 newSize = center;
            if ((i & 4) == 4) newSize.y += quarter;
            else              newSize.y -= quarter;

            if ((i & 2) == 2) newSize.x += quarter;
            else              newSize.x -= quarter;

            if ((i & 1) == 1) newSize.z += quarter;
            else              newSize.z -= quarter;

            children[i] = new OctreeNode<TType>(newSize, subspaceSize * 0.5f);
            if (depth > 0)
            {
                children[i].branch(depth - 1);
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

    void splitSubSpace()
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

    private void setupChildren()
    {
        children = new OctreeNode<TType>[DEGREE];
    }
}
