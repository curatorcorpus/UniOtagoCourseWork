using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class OctreeController : MonoBehaviour {

    private bool drawTree = false;
    private Octree<int> tree;
    private List<GameObject> voxels;

	public float size  = 5.0f;
	public int   depth = 2;

    // used for debugging
    void OnDrawGizmos()
    {
        if (drawTree)
        {
            gizmosDrawNode(tree.Root);
        }
    }

    // Use this for initialization
    void Start ()
    {
        tree = new Octree<int>(this.transform.position, size, depth);

        tree.add(new Vector3(0.25f, 0.25f, 0.25f));
        tree.add(new Vector3(-0.25f, 0.25f, 0.25f));
        tree.add(new Vector3(0.25f, -0.25f, 0.25f));
        tree.add(new Vector3(0.25f, 0.25f, -0.25f));
        tree.add(new Vector3(-0.25f,- 0.25f, 0.25f));
        tree.add(new Vector3(0.25f, -0.25f, -0.25f));
        tree.add(new Vector3(-0.25f, 0.25f, -0.25f));
        tree.add(new Vector3(-2f, -2f, -2f));

        tree.add(new Vector3(5f, 5f, 5f));
        tree.add(new Vector3(-5f, -5f, -5f));
        tree.add(new Vector3(5f, -5f, 5f));
        tree.add(new Vector3(5f, 5f, -5f));
        tree.add(new Vector3(-5f, 5f, 5f));
        tree.add(new Vector3(-5f, 5f, -5f));
        tree.add(new Vector3(-5f, -5f, 5f));
        tree.add(new Vector3(5f, -5f, -5f));

        tree.add(new Vector3(20f, 20f, 20f));

        voxels = new List<GameObject>(tree.Count);

        for(int i = 0; i < tree.Count; i++)
        {
            GameObject voxel = GameObject.CreatePrimitive(PrimitiveType.Cube);


            voxels.Add(voxel);
        }

        drawTree = true;
    }

    // Update is called once per frame
    /*void Update()
    {
        voxelDrawNode(tree.Root, depth);
    } */

    // PRIVATE METHODS
    private void voxelDrawNode(OctreeNode<int> node, int nodeDepth = 0)
    {
        if (node == null)
        {
            return;
        }

        if (!node.isLeaf())
        {
            foreach (var child in node.Children)
            {
                voxelDrawNode(child, nodeDepth + 1);
            }
        }

        // draw
        GameObject voxel = voxels[0];

        voxel.transform.position = node.Position;
    }

    private void gizmosDrawNode(OctreeNode<int> node, int nodeDepth = 0)
    {
        if(node == null)
        {
            return;
        }

        if (!node.isLeaf())
        {
            foreach (var child in node.Children)
            {
                gizmosDrawNode(child, nodeDepth + 1);
            }
        }
        Gizmos.color = Color.green;
        Gizmos.DrawWireCube(node.Position, Vector3.one * node.Size);
    }
}
