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

    private bool newPosExists = false;
	private float subspaceSize;

	private Vector3 center;
	private OctreeNode<TType>[] children;

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
	public bool isLeaf()
	{
		return children == null;
	}

    // PUBLIC METHODS
	public void add(Vector3 newPos, int depth = 0)
    {
		if(depth == 0)
			return;

        if(children == null)
            splitSubSpace();

        int bestSSIdx = findBestSubspace(newPos); // find best subspace idx.

        this.children[bestSSIdx].newPosExists = true;
        this.children[bestSSIdx].add(newPos, depth - 1);

        // now that we found best sub space, remove other children
        //this.remove();
    }

    public List<Vector3> getPositions()
    {
        List<Vector3> pos = new List<Vector3>();
        
        if(children == null)
        {
            if(newPosExists)
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

    public void remove()
    {
        int bestIdx = findBestSubspace(this.center);

        for (int i = 0; i < children.Length; i++)
        {
            if (newPosExists)
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
            Vector3 newPos = center;
            if ((i & 4) == 4) newPos.y += quarter;
            else              newPos.y -= quarter;

            if ((i & 2) == 2) newPos.x += quarter;
            else              newPos.x -= quarter;

            if ((i & 1) == 1) newPos.z += quarter;
            else              newPos.z -= quarter;

            children[i] = new OctreeNode<TType>(newPos, subspaceSize * 0.5f);
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
    private int findBestSubspace(Vector3 newPos)
    {

        Debug.Log(newPos.x + " x: " + center.x + " y: " + newPos.y + " " + center.y + " z: " + newPos.z + " " + center.z);
        Debug.Log((newPos.x >= center.x ? 0 : 1) +
               (newPos.y >= center.y ? 0 : 2) +
               (newPos.z >= center.z ? 0 : 4));

        return (newPos.x >= center.x ? 0 : 1) + 
               (newPos.y >= center.y ? 0 : 2) +
               (newPos.z >= center.z ? 0 : 4);
    }

    void splitSubSpace()
    {
        float quarter = subspaceSize * 0.25f;
        float newPos = subspaceSize * 0.5f;

        setupChildren(); // allocates memory to children array.

        children[TRF] = new OctreeNode<TType>(this.center + new Vector3(quarter, quarter, quarter), newPos);
        children[TLF] = new OctreeNode<TType>(this.center + new Vector3(-quarter, quarter, quarter), newPos);

        children[BRF] = new OctreeNode<TType>(this.center + new Vector3(quarter, -quarter, quarter), newPos);
        children[BLF] = new OctreeNode<TType>(this.center + new Vector3(-quarter, -quarter, quarter), newPos);

        children[TRB] = new OctreeNode<TType>(this.center + new Vector3(quarter, quarter, -quarter), newPos);
        children[TLB] = new OctreeNode<TType>(this.center + new Vector3(-quarter, quarter, -quarter), newPos);

        children[BRB] = new OctreeNode<TType>(this.center + new Vector3(quarter, -quarter, -quarter), newPos);
        children[BLB] = new OctreeNode<TType>(this.center + new Vector3(-quarter, -quarter, -quarter), newPos);
}

    private void setupChildren()
    {
        children = new OctreeNode<TType>[DEGREE];
    }
}
