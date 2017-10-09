using System;
using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System.Threading;

public class OctreeController : MonoBehaviour 
{
    private static int MAX_VERTS = 65534;
    private static int MM_FACTOR = 1000;
    
    [Header("Voxel Settings")]
    [SerializeField] private int voxelSpaceSize = 256;
    [SerializeField] private int voxelSize = 1;
    
    [Header("Models")]
    [SerializeField] private GameObject meshModel;

    [Header("Debug Tools")]
    [SerializeField] private bool debugMeshes = false;
    [SerializeField] private bool debugOctree = false;
    [SerializeField] private bool useBasicVoxelization = false;
    [SerializeField] private bool useGridVoxelization = false;
    [SerializeField] private bool useFillSpace = false;

    private bool updated = false;

    private Octree<int> tree;
    private Material voxelMat;

    private List<Mesh> meshes;
    private List<Vector3> verts;
    private List<Color32> clrs;

    private List<int> indices;

    // GIZMOS DEBUGGER
    private void OnDrawGizmos()
    {
        if (debugOctree)
        {
            // throw error if debugger is turned on without starting program.
            if (tree == null)
            {
                throw new System.Exception("[DEBUG::CONTROL::GIZMO] Null Reference to tree for debugging!");
            }
            else
            {
                GizmosDrawNode(tree.Root);                
            }
        }
    }

    private void GizmosDrawNode(OctreeNode<int> node)
    {
        if (node == null)
        {
            return;
        }

        if(node.IsLeafVoxel)
        {
            Gizmos.color = Color.green;
            Gizmos.DrawWireCube(node.Center, Vector3.one * node.SubspaceSize);

            return;
        }

        foreach (var child in node.Children)
        {
            GizmosDrawNode(child);
        }
    }

    //==================== MAIN ===========================
    private void Start ()
    {
        CheckVoxelSettings();
        Setup();
        InitMeshes(tree.GetAllPoints().Count);    // add to mesh
        InitIndices();                // initialize indices to use
        VoxelDrawNode();             // inital draw
    }

    private void Update()
    { 
        if (updated)
        {
            VoxelDrawNode();
            updated = false;
        }
    }

    private void OnApplicationQuit()
    {
        meshes.Clear();
        verts.Clear();
        clrs.Clear();
        indices.Clear();
    }
    //======================================================

    // PRIVATE METHODS
    private void CheckVoxelSettings()
    {
        if (voxelSpaceSize % 2 != 0 || (voxelSize % 2 != 0 && voxelSize != 1))
        {
            int closestVSS = MathUtils.ClosetPow2(voxelSpaceSize);
            int closestVS = MathUtils.ClosetPow2(voxelSize);

            Debug.Log("Voxel Settings not powers of 2.\nVoxel space size rounded from " + voxelSpaceSize + " to " + 
                      closestVSS + ".\nVoxel Size rounded from " + voxelSize + " to " + closestVS + ".");

            voxelSpaceSize = closestVSS;
            voxelSize = closestVS;
        }
    }
    
    /*
     * Method prepares data structure and voxelizes models. 
     */
    private void Setup()
    {
        voxelMat = Resources.Load("Materials/VoxelMat") as Material;
        if (voxelMat == null)
            throw new System.Exception("Material File wasn't loaded!"); // check that materials were loaded successfully

        // initialize data structure.
        tree = new Octree<int>(this.transform.position, (float)voxelSpaceSize/ MM_FACTOR, (float) voxelSize/ MM_FACTOR);

        // set voxel size to shader.
        voxelMat.SetFloat("voxel_size", (float)voxelSize/ MM_FACTOR);

      //  List<GameObject> gameObjects = new List<GameObject>();   // obtain the objects to voxelize.
       // List<Transform> childTransforms = new List<Transform>(); // 
        List<VoxelizerThread> threads = new List<VoxelizerThread>();
        List<Color32> meshColors = new List<Color32>();

        int noOfMeshModelChildren = meshModel.transform.childCount;

        for (int i = 0; i < noOfMeshModelChildren; i++)
        {
            // obtain mesh properties.
            GameObject meshObject = meshModel.gameObject.transform.GetChild(i).gameObject;

            // obtains local to world transformation matrix.
            Transform meshTransform      = meshModel.gameObject.transform.GetChild(i).transform;
            Matrix4x4 localToWorldMatrix = meshTransform.localToWorldMatrix;

            // obtain mesh and mesh colour.
            Material   material   = meshObject.GetComponent<MeshRenderer>().material;
            MeshFilter meshFilter = meshObject.GetComponent<MeshFilter>();
            Color32    matColor   = material.color;

            // remember mesh colour for adding to octree.
            meshColors.Add(matColor);

            if (useBasicVoxelization)
            {
                Vector3[] verts = meshFilter.mesh.vertices;

                for (int j = 0; j < verts.Length; j++)
                {
                    tree.Add(localToWorldMatrix.MultiplyPoint3x4(verts[j]), matColor);
                }
            }
            else if (useGridVoxelization)
            {
                // extract verts and triangles indices. 
                Mesh mesh = meshFilter.mesh;
                Vector3[] verts = mesh.vertices;
                int[] tris = mesh.triangles;

                // setup thread for voxelizing current mesh. 
                threads.Add(new VoxelizerThread(ref verts, ref tris, localToWorldMatrix, (float)voxelSize / MM_FACTOR));
                threads[i].Start(); // start voxelizing.
            }
            else if (useFillSpace)
            {
                tree.AddFill();
            }
        }

        // main worker thread for adding thread voxels to octree.

        int idx = 0;
        while(idx < threads.Count)
        {
            VoxelizerThread currThread = threads[idx];

            // start extracting voxels once thread is finished.
            if (currThread.Finished)
            {
                tree.Add(currThread.VoxelsToAdd.Pop(), meshColors[idx]);

                // only jump to next thread once we finished extracting all voxels.
                if (currThread.VoxelsToAdd.Count == 0)
                    idx++;
            }
        }

        // destroy all thread class objects.
        threads.Clear();

        // hide mesh model.
        meshModel.hideFlags = HideFlags.HideInInspector;
        meshModel.hideFlags = HideFlags.NotEditable;
        meshModel.hideFlags = HideFlags.HideInHierarchy;
        meshModel.SetActive(false);
    }

    private void InitMeshes(int voxelCount)
    {//Debug.Log("Total Voxel Count " + voxelCount);
        if (voxelCount > MAX_VERTS)
        {
            int divisor = Mathf.CeilToInt((float)voxelCount / MAX_VERTS);

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
            gObj.AddComponent<MeshFilter>().mesh = mesh;

            mesh.name = "VoxelMesh";
            gObj.name = "VoxelMesh";

            gObj.transform.parent = gameObject.transform;

            if(!debugMeshes)
            {
                gObj.hideFlags = HideFlags.HideInInspector;
                gObj.hideFlags = HideFlags.NotEditable;
                gObj.hideFlags = HideFlags.HideInHierarchy;
            }
            gObj.SetActive(true);
        });
    }

    private void InitIndices()
    {
        indices = new List<int>(MAX_VERTS);

        for (int i = 0; i < MAX_VERTS; i++)
        {
            indices.Add(i);
        }
    }

    // RENDER METHODS
    private void VoxelDrawNode()
    {
        verts = tree.GetAllPoints();
        clrs = tree.GetAllColors();

        // draw
        int idx = 0;
        int remainingVerts = verts.Count;

        // initial mesh
        Mesh mesh = meshes[0];

        while (remainingVerts > 0)
        {
            mesh.Clear();

            if (remainingVerts < MAX_VERTS)
            {
                mesh.SetVertices(verts.GetRange(0, remainingVerts));
                mesh.SetColors(clrs.GetRange(0, remainingVerts));
                mesh.SetIndices(indices.GetRange(0, remainingVerts).ToArray(), MeshTopology.Points, 0);
            }
            else
            {
                mesh.SetVertices(verts.GetRange(remainingVerts - MAX_VERTS, MAX_VERTS));
                mesh.SetColors(clrs.GetRange(remainingVerts - MAX_VERTS, MAX_VERTS));
                mesh.SetIndices(indices.GetRange(0, MAX_VERTS).ToArray(), MeshTopology.Points, 0);
                mesh = meshes[++idx];
            }
            remainingVerts -= MAX_VERTS;
        }
    }
}
