using System;
using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System.Threading;

public class OctreeController : MonoBehaviour 
{
    private static int MAX_VERTS = 65534;
//    private static int M_FACTOR = 100;
//    private static int MM_FACTOR = 1000;
    
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
        Debug.Log("Number of points: " + tree.GetAllPoints().Count);
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
    
    /**
     * Method prepares data structure and voxelizes models. 
     */
    private void Setup()
    {
        voxelMat = Resources.Load("Materials/VoxelMat") as Material;
        if (voxelMat == null)
            throw new System.Exception("Material File wasn't loaded!"); // check that materials were loaded successfully

        // initialize data structure.
        tree = new Octree<int>(this.transform.position, (float)voxelSpaceSize/1000,(float) voxelSize/1000);

        // set voxel size to shader.
        voxelMat.SetFloat("voxel_size", (float)voxelSize/1000);

        List<GameObject> gameObjects = new List<GameObject>();   // obtain the objects to voxelize.
        List<Transform> childTransforms = new List<Transform>(); // 
        List<Thread> threads = new List<Thread>();

        for (int i = 0; i < meshModel.transform.childCount; i++)
        {
            gameObjects.Add(meshModel.gameObject.transform.GetChild(i).gameObject);
            childTransforms.Add(meshModel.gameObject.transform.GetChild(i).transform);
            
            Material mat = gameObjects[i].GetComponent<MeshRenderer>().material;
            MeshFilter meshFilter = gameObjects[i].GetComponent<MeshFilter>();
            Transform modelTransform = childTransforms[i];
            Matrix4x4 localToWorldMatrix = modelTransform.localToWorldMatrix;
            Color32 matColor = mat.color;
            Mesh mesh = meshFilter.mesh;
            
            threads.Add(new Thread(() => new VoxelizerThread().VoxelizeMesh(ref mesh, matColor, localToWorldMatrix)));
        }

        // hide mesh model.
        meshModel.hideFlags = HideFlags.HideInInspector;
        meshModel.hideFlags = HideFlags.NotEditable;
        meshModel.hideFlags = HideFlags.HideInHierarchy;
        meshModel.SetActive(false);

        // voxelizes and adds voxels to data structure.
        for (int i = 0; i < gameObjects.Count; i++)
        {
            // obtain mesh properties.
            Material mat = gameObjects[i].GetComponent<MeshRenderer>().material;
            MeshFilter meshFilter = gameObjects[i].GetComponent<MeshFilter>();
            Transform modelTransform = childTransforms[i];
            Matrix4x4 localToWorldMatrix = modelTransform.localToWorldMatrix;
            Color32 matColor = mat.color;

            //float scale = 0.5f;

            //localToWorldMatrix.m00 = scale;
            //localToWorldMatrix.m11 = scale;
            //localToWorldMatrix.m22 = scale;

            if (useBasicVoxelization)
            {
                Vector3[] verts = meshFilter.mesh.vertices;

                for (int j = 0; j < verts.Length; j++)
                {
                    tree.Add(localToWorldMatrix.MultiplyPoint3x4(verts[j]), matColor);
                }
            }
            else if(useGridVoxelization)
            {
//                Mesh mesh = meshFilter.mesh;

                //tree.VoxelizeMesh(ref mesh, matColor, localToWorldMatrix);
                threads[i].Start();
            }
            else if (useFillSpace)
            {
                tree.AddFill();
            }
        }
        
        throw new Exception();
    }

    private void InitMeshes(int voxelCount)
    {
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
