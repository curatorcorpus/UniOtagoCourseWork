using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class OctreeNode<TType> {

	public enum OctreeIndex
	{
	    BottomLeftFront = 0, //000,
	    BottomRightFront = 2, //010,
	    BottomRightBack = 3, //011,
	    BottomLeftBack = 1, //001,
	    TopLeftFront = 4, //100,
	    TopRightFront = 6, //110,
	    TopRightBack = 7, //111,
	    TopLeftBack = 5, //101,
	}

	private static int DEGREE = 8;

	private float size;

	private Vector3 position;

	private OctreeNode<TType>[] children;

	public OctreeNode(Vector3 position, float size) 
	{
		this.position = position;
		this.size 	  = size;
	}

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

	public void subdivide(int depth = 0)
	{
		children = new OctreeNode<TType>[DEGREE];
		for (int i = 0; i < children.Length; i++)
        {
            Vector3 newPos = position;
            if ((i & 4) == 4) newPos.y += size * 0.25f;
            else 			  newPos.y -= size * 0.25f;

            if ((i & 2) == 2) newPos.x += size * 0.25f;
            else 			  newPos.x -= size * 0.25f;

            if ((i & 1) == 1) newPos.z += size * 0.25f;
            else 			  newPos.z -= size * 0.25f;

            children[i] = new OctreeNode<TType>(newPos, size * 0.5f);
            if (depth > 0)
            {
                children[i].subdivide(depth - 1);
            }
        }
	}

	// allows foreach children
	public IEnumerable<OctreeNode<TType>> Children
	{
		get {return children; }
	}
}
