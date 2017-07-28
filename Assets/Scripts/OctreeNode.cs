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
    public void add(Vector3 otherPos)
    {
        if(children == null)
        {
            setupChildren();
        }

        int bestSSIdx = findBestSubspace(otherPos); // find best subspace idx
        children[bestSSIdx] = new OctreeNode<TType>(otherPos, size);
        children[bestSSIdx].add(otherPos);
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
    
    private void setupChildren()
    {
        children = new OctreeNode<TType>[DEGREE];
    }
}
