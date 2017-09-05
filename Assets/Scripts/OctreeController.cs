using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class OctreeController : MonoBehaviour {

    private static int MAX_VERTS = 65534;

    [SerializeField] private bool debug = false;
    [SerializeField] private float voxelSpaceLength = 5.0f;
    [SerializeField] private int octreeMaxDepth = 2;

    private bool initDebug = true;
    private bool updated = true;

    private Octree<int> tree;
    private Material voxelMat;

    private List<Mesh> meshes;
    private List<Vector3> verts;
    private List<Color32> clrs;
    private List<int> indices;

    // GIZMOS DEBUGGER
    void OnDrawGizmos()
    {
        if (debug)
        {
            // throw error if debugger is turned on without starting program.
            if(tree == null)
            {
                throw new System.Exception("[DEBUG::CONTROL::GIZMO] Null Reference to tree for debugging!");
            }

            // draw debugger once.
            if(initDebug)
            {
                initDebug = false;
                gizmosDrawNode(tree.Root);
            }

            // re draw if data structure is updated
           // if(updated)
         //   {
                gizmosDrawNode(tree.Root);
           // }
        }
        else
        {
            initDebug = true;
        }
    }

    private void gizmosDrawNode(OctreeNode<int> node, int nodeDepth = 0)
    {
        if (node == null)
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
        Gizmos.DrawWireCube(node.Center, Vector3.one * node.SubspaceSize);
    }

    // Use this for initialization
    void Start ()
    {/*
        if(depth > 8)
        {
            throw new System.Exception("The maximum depth is 8 to avoid performance issues!");
        }*/

        voxelMat = Resources.Load("Materials/VoxelMat") as Material;
        // check that materials were loaded successfully
        if (voxelMat == null)
            throw new System.Exception("Material File wasn't loaded!");

        Mesh meshToVoxelize = GetComponent<MeshFilter>().mesh;

        // check that model to voxel exists
        if (meshToVoxelize.vertexCount == 0)
           throw new System.Exception("Mesh to voxelize doesn't exist!");

        tree = new Octree<int>(this.transform.position, voxelSpaceLength, octreeMaxDepth);
        Debug.Log("voxel size " + tree.getVoxelSize());
        tree.add(new Vector3(-1, -1, -1));
        /*List<Voxelizer.Voxel> voxelPos = Voxelizer.Voxelize(meshToVoxelize, 100);
        voxelPos.ForEach(voxel => 
        {
            Vector3 pos = voxel.position;
            tree.add(pos);
        });*/

        initMesh(1);    // add to mesh
        initArrays();   // initialize indices to use

        voxelDrawNode();    // inital draw
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
        List<Vector3> test = tree.getAllPoints();

        // draw
        int idx = 0;
        int remainingVerts = test.Count;

        if (verts != null)
        {
            verts.Clear();
            clrs.Clear();
        }

        // initial mesh
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
                mesh.SetIndices(indices.GetRange(0, MAX_VERTS).ToArray(), MeshTopology.Points, 0);
                mesh = meshes[++idx];
            }

            remainingVerts -= MAX_VERTS;
        }
    }
}
