using System;
using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using System.Threading;

public class OctreeController : MonoBehaviour 
{
    private static int MAX_VERTS = 65534;
    private static int MM_FACTOR = 1000;
    
    [Header("Voxel Settings")]
    [SerializeField] private int voxelSpaceSize = 2048;
    [SerializeField] private int voxelSize = 1;

    [Header("Voxelizer Features")]
    [SerializeField] private bool useBasicVoxelization = false;
    [SerializeField] private bool useGridVoxelization = false;
    [SerializeField] private bool useFillSpace = false;
    [SerializeField] private bool saveVoxelModel = false;
    [SerializeField] private bool loadVoxelModel = false;
    [SerializeField] private UnityEngine.Object filePath;
    
    [Header("Debug Tools")]
    [SerializeField] private bool debugMeshes = false;
    [SerializeField] private bool debugOctree = false;

    private bool updated = false;

    private Octree<int> tree;
    private Material voxelMat;

    private List<Mesh> meshes;
    private List<Vector3> verts;
    private List<Color32> clrs;

    private List<int> indices;

    private List<GameObject> models = new List<GameObject>();

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
            Gizmos.color = Color.blue;
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
        renderVoxels();             // inital draw
    }

    private void Update()
    { 
        if (updated)
        {
            renderVoxels();
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
    /**
     * Method for checking that user voxel settings are power of two.
     */
    private void CheckVoxelSettings()
    { 
        /*
         * Performs a bitwise logical operation to determine if voxel space size and
         * voxel size are powers of two. This happens using the AND logical operator. 
         * 
         * For example if voxel size if 10, in binary it would be 1 0 1 0. The difference of 1 
         * would be 9 - 1 0 0 1. If we perform the AND operator. (1 0 1 0 AND 1 0 0 1) the 
         * resulting binary value will be non-zero. If the value was a power of 2. For example, 
         * 8 - 1 0 0 0. Then we would be checking 1 0 0 0 AND 0 1 1 1. Because any power of 
         * 2 number will have the form 1 * 0 (how many zeros in binary), the AND operation will 
         * always be zero. 
         */
        if ((voxelSpaceSize & (voxelSpaceSize - 1 )) != 0)
        {
            int closestVSS = MathUtils.ClosetPow2(voxelSpaceSize);

            Debug.Log("Voxel Settings not powers of 2.\nVoxel space size rounded from " + voxelSpaceSize + " to " + closestVSS);

            voxelSpaceSize = closestVSS;
        }
        if((voxelSize & (voxelSize - 1)) != 0)
        {
            int closestVS = MathUtils.ClosetPow2(voxelSize);

            Debug.Log("Voxel Settings not powers of 2.\nVoxel Size rounded from " + voxelSize + " to " + closestVS + ".");

            voxelSize = MathUtils.ClosetPow2(voxelSize);
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
        tree = new Octree<int>(this.transform.position, (float) voxelSpaceSize / MM_FACTOR,
            (float) voxelSize / MM_FACTOR);

        // set voxel size to shader.
        voxelMat.SetFloat("voxel_size", (float) voxelSize / MM_FACTOR);

        //  List<GameObject> gameObjects = new List<GameObject>();   // obtain the objects to voxelize.
        // List<Transform> childTransforms = new List<Transform>(); // 
        List<VoxelizerThread> threads = new List<VoxelizerThread>();
        List<Color32> meshColors = new List<Color32>();

        // Add objects to our model list
        for (int i = 0; i < this.transform.childCount; i++)
            if (this.transform.GetChild(i).gameObject.activeSelf)
                models.Add(this.transform.GetChild(i).gameObject);
        
        if (!loadVoxelModel)
        {
            for (int m = 0; m < models.Count; m++)
            {
                GameObject currModel = models[m];
                int noOfMeshModelChildren = currModel.transform.childCount;

                for (int i = 0; i < noOfMeshModelChildren; i++)
                {
                    // obtain mesh properties.
                    GameObject meshObject = currModel.gameObject.transform.GetChild(i).gameObject;

                    // obtains local to world transformation matrix.
                    Transform meshTransform = currModel.gameObject.transform.GetChild(i).transform;
                    Matrix4x4 localToWorldMatrix = meshTransform.localToWorldMatrix;

                    // obtain mesh and mesh colour.
                    Material material = meshObject.GetComponent<MeshRenderer>().material;
                    MeshFilter meshFilter = meshObject.GetComponent<MeshFilter>();
                    Color32 matColor = material.color;

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
                        threads.Add(new VoxelizerThread(ref verts, ref tris, localToWorldMatrix,
                            (float) voxelSize / MM_FACTOR));
                        threads[i].Start(); // start voxelizing.
                    }
                    else if (useFillSpace)
                    {
                        tree.AddFill();
                    }
                }

                VoxelData voxelData = new VoxelData(threads.Count);

                // main worker thread for adding thread voxels to octree.
                int idx = 0;
                int voxelCount = 0;
                while (idx < threads.Count)
                {
                    VoxelizerThread currThread = threads[idx];

                    // start extracting voxels once thread is finished.
                    if (currThread.Finished)
                    {
                        Color32 color = meshColors[idx];
                        voxelCount += currThread.VoxelsToAdd.Count;

                        if (saveVoxelModel)
                        {
                            voxelData.AddToColorList(idx, color);
                            voxelData.AddToColorSwitch(idx, voxelCount);
                        }

                        while(currThread.VoxelsToAdd.Count != 0)
                        {
                            Vector3 voxelPos = currThread.VoxelsToAdd.Pop();

                            tree.Add(voxelPos, color);

                            if (saveVoxelModel)
                                voxelData.AddToVoxelList(voxelPos);
                        }
                        // only jump to next thread once we finished extracting all voxels.
                        ++idx;
                    }
                }

                // destroy all thread class objects.
                threads.Clear();

                // Clear colours array
                meshColors.Clear();

                // hide mesh model.
                currModel.hideFlags = HideFlags.HideInInspector;
                currModel.hideFlags = HideFlags.NotEditable;
                currModel.hideFlags = HideFlags.HideInHierarchy;
                currModel.SetActive(false);

                if (saveVoxelModel)
                {
                    VoxelSerializer.saveModel(currModel.name, voxelData);
                }
            }
        }
        else
        {
            VoxelData voxelData = VoxelSerializer.loadModel(this.filePath.name+".arnold");

            List<SerializableVector3> voxels = voxelData.GetVoxelList();
            SerializableColor32[] colors = voxelData.GetColorList();
            int[] colorSwitches = voxelData.GetColorSwitches();

            int voxelCount = voxelData.GetListSize();
            int colorIdx = 0;
            int colorCount = colorSwitches[colorIdx];

            Color32 color = colors[colorIdx];

            for (int i = 0; i < voxelCount; i++)
            {
                if(i == colorCount)
                {
                    colorCount = colorSwitches[++colorIdx];
                    color = colors[colorIdx];
                }
                tree.Add(voxels[i], color);
            }

            // hide all active models.
            for (int i = 0; i < models.Count; i++)
            {
                GameObject currModel = models[i];
                
                currModel.hideFlags = HideFlags.HideInInspector;
                currModel.hideFlags = HideFlags.NotEditable;
                currModel.hideFlags = HideFlags.HideInHierarchy;
                currModel.SetActive(false);
            }
        }
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
    private void renderVoxels()
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
