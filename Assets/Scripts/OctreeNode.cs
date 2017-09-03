using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class OctreeNode<TType> {

    // subspace indices
	/*private enum SSIndices
	{
        BLF = 0, //000,
        BLB = 1, //001,
        BRF = 2, //010,
	    BRB = 3, //011,


	    TLF = 4, //100,
        TLB = 5, //101,
        TRF = 6, //110,
	    TRB = 7, //111,
	}*/

	private static int DEGREE = 8;

    private bool newPosExists = false;
	private float size;

	private Vector3 position;

	private OctreeNode<TType>[] children;

    // CONSTRUCTORS
	public OctreeNode(Vector3 position, float size) 
	{
		this.position = position;
		this.size 	  = size;
	}

    // GETTERS AND SETTERS
	public Vector3 Position
	{
		get { return position; }
		set { this.position = value; }
	}
	public float Size
	{
		get { return size; }
		set { this.size = value; }
	}
	public bool isLeaf()
	{
		return children == null;
	}

    // PUBLIC METHODS
	public void add(Vector3 otherPos, int depth = 0)
    {
		if(depth < 0)
			return;

        if(children == null)
        {
            splitSubSpace();
        }

        int bestSSIdx = findBestSubspace(otherPos); // find best subspace idx
        this.children[bestSSIdx].newPosExists = true;
        this.children[bestSSIdx].add(otherPos, depth - 1);

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
                pos.Add(position);
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
        int bestIdx = findBestSubspace(this.position);

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
        float quarter = size * 0.25f;

        setupChildren();
        for (int i = 0; i < children.Length; i++)
        {
            Vector3 newPos = position;
            if ((i & 4) == 4) newPos.y += quarter;
            else              newPos.y -= quarter;

            if ((i & 2) == 2) newPos.x += quarter;
            else              newPos.x -= quarter;

            if ((i & 1) == 1) newPos.z += quarter;
            else              newPos.z -= quarter;

            children[i] = new OctreeNode<TType>(newPos, size * 0.5f);
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
    private int findBestSubspace(Vector3 otherPos)
    {
        return (otherPos.x <= position.x ? 0 : 1) + 
               (otherPos.y >= position.y ? 0 : 4) +
               (otherPos.z <= position.z ? 0 : 2);
    }

    void splitSubSpace()
    {
        float quarter = size * 0.25f;
        float newPos = size * 0.5f;

        setupChildren(); // allocates memory to children array.

        children[0] = new OctreeNode<TType>(this.position + new Vector3(-quarter, quarter, -quarter), newPos);
        children[1] = new OctreeNode<TType>(this.position + new Vector3(quarter, quarter, -quarter), newPos);
        children[2] = new OctreeNode<TType>(this.position + new Vector3(-quarter, quarter, quarter), newPos);
        children[3] = new OctreeNode<TType>(this.position + new Vector3(quarter, quarter, quarter), newPos);
        children[4] = new OctreeNode<TType>(this.position + new Vector3(-quarter, -quarter, -quarter), newPos);
        children[5] = new OctreeNode<TType>(this.position + new Vector3(quarter, -quarter, -quarter), newPos);
        children[6] = new OctreeNode<TType>(this.position + new Vector3(-quarter, -quarter, quarter), newPos);
        children[7] = new OctreeNode<TType>(this.position + new Vector3(quarter, -quarter, quarter), newPos);
    }

    private void setupChildren()
    {
        children = new OctreeNode<TType>[DEGREE];
    }
}
