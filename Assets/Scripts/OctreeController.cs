using UnityEngine;
using System.Collections;

public class OctreeController : MonoBehaviour {

	private OctreeNode<int> root;

	public float size  = 5.0f;
	public int   depth = 2;

	// Use this for initialization
	void Start () {
		
createOctree();
	}
	
	// Update is called once per frame
	void Update () {
	
	}

	private void createOctree() 
	{
		root = new OctreeNode<int>(this.transform.position, size);
		root.subdivide(depth);
	}    

	void OnDrawGizmos()
    {
        //createOctree();

        //DrawNode(root);
    }

    private Color minColor = new Color(1, 1, 1, 1f);
    private Color maxColor = new Color(0, 0.5f, 1, 0.25f);

    private void DrawNode(OctreeNode<int> node, int nodeDepth = 0)
    {
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
