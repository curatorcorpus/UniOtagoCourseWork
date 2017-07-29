using UnityEngine;
using System.Collections;

public class OctreeController : MonoBehaviour {

    private bool drawTree = false;

    private Octree<int> tree;

	public float size  = 5.0f;
	public int   depth = 2;

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

        drawTree = true;
    }

    // Update is called once per frame
    void Update() {} 

	void OnDrawGizmos()
    {
        if(drawTree)
        {
            DrawNode(tree.Root);
        }
    }

    // PRIVATE METHODS

    private void DrawNode(OctreeNode<int> node, int nodeDepth = 0)
    {
        if(node == null)
        {
            return;
        }

        if (!node.isLeaf())
        {
            foreach (var child in node.Children)
            {
                DrawNode(child, nodeDepth + 1);
            }
        }
        Gizmos.color = Color.green;
        Gizmos.DrawWireCube(node.Position, Vector3.one * node.Size);
    }
}
