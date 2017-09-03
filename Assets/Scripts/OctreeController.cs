using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class OctreeController : MonoBehaviour {

    private static int MAX_VERTS = 65534;

    [SerializeField] private bool drawTree = false;
    [SerializeField] private float size = 5.0f;
    [SerializeField] private int depth = 2;

    private bool updated = true;

    private Octree<int> tree;
    private Material voxelMat;

    private List<Mesh> meshes;
    private List<Vector3> verts;
    private List<Color32> clrs;
    private List<int> indices;

    void OnValidate()
    {
        tree = new Octree<int>(this.transform.position, size, depth);
    }

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
        if(depth > 8)
        {
            throw new System.Exception("The maximum depth is 8 to avoid performance issues!");
        }

        voxelMat = Resources.Load("Materials/VoxelMat") as Material;
        // check that materials were loaded successfully
        if (voxelMat == null)
            throw new System.Exception("Material File wasn't loaded!");

        //MeshFilter[] models = GetComponentsInChildren<MeshFilter>();
        Mesh meshToVoxelize = GetComponent<MeshFilter>().mesh;

        // check that model to voxel exists
        if (meshToVoxelize.vertexCount == 0)
           throw new System.Exception("Mesh to voxelize doesn't exist!");

        tree = new Octree<int>(this.transform.position, size, depth);

        List<Voxelizer.Voxel> voxelPos = Voxelizer.Voxelize(meshToVoxelize, 200);
        Debug.Log(voxelPos.Count + "TESt");
        voxelPos.ForEach(voxel => {
            Vector3 pos = voxel.position;

            tree.add(pos);
        });

        /*
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

                voxels = new List<GameObject>(tree.Count);*/
        // add to mesh
        initMesh(voxelPos.Count);

        // initialize indices to use
        initArrays();

        voxelDrawNode();
    }

    // Update is called once per frame
    void Update()
    {
        if (updated)
        {
            //voxelDrawNode(tree.Root, depth);
            updated = false;
        }
    }

    // PRIVATE METHODS
    private void initMesh(int voxelCount)
    {
        if (voxelCount > MAX_VERTS)
        {
            int divisor = Mathf.CeilToInt((float)voxelCount / (float)MAX_VERTS);

            meshes = new List<Mesh>(divisor);

            for (int i = 0; i < divisor; i++)
            {
                Mesh newMesh = new Mesh();
                newMesh.name = "VoxelMesh_" + i;

                meshes.Add(new Mesh());
            }
        }
        else
        {
            meshes = new List<Mesh>(1);
            meshes.Add(new Mesh());
        }

        meshes.ForEach(mesh =>
        {
            GameObject gObj = new GameObject();

            gObj.AddComponent<MeshRenderer>().material = voxelMat;
            mesh.name = "VoxelMesh";

            gObj.AddComponent<MeshFilter>().mesh = mesh;
            gObj.name = "VoxelMesh";

            gObj.transform.parent = gameObject.transform;

            //gObj.hideFlags = HideFlags.HideInInspector;
            //gObj.hideFlags = HideFlags.NotEditable;
            //gObj.hideFlags = HideFlags.HideInHierarchy;
            gObj.SetActive(true);
        });
    }

    private void initArrays()
    {
        verts = new List<Vector3>(MAX_VERTS);
        clrs = new List<Color32>(MAX_VERTS);
        indices = new List<int>(MAX_VERTS);

        for (int i = 0; i < MAX_VERTS; i++)
        {
            indices.Add(i);
            clrs.Add(new Color32(1,1,1,1));
        }
    }

    private void voxelDrawNode()
    {
        List<Vector3> test = tree.get();

        // draw
        int idx = 0;
        int remainingVerts = test.Count;

        if (verts != null)
        {
            verts.Clear();
            clrs.Clear();
        }

        Mesh mesh = meshes[0];

        while (remainingVerts > 0)
        {
            if(remainingVerts < MAX_VERTS)
            {
                mesh.Clear();
                mesh.SetVertices(test.GetRange(0, remainingVerts));
                mesh.SetColors(clrs);
                mesh.SetIndices(indices.GetRange(0, remainingVerts).ToArray(), MeshTopology.Points, 0);
            }
            else
            {
                mesh.Clear();
                mesh.SetVertices(test.GetRange(remainingVerts - MAX_VERTS, remainingVerts));
                mesh.SetColors(clrs);
                mesh.SetIndices(indices.GetRange(remainingVerts - MAX_VERTS, remainingVerts).ToArray(), MeshTopology.Points, 0);
                mesh = meshes[++idx];
            }

            remainingVerts -= MAX_VERTS;
        }
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
