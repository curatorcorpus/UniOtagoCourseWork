using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class OctreeController : MonoBehaviour
{
    private static int MAX_VERTS = 65534;

    [Header("Voxel Space Settings")]
    [SerializeField] private float voxelSpaceLength = 5.0f;
    [SerializeField] private int octreeMaxDepth = 2;

    [Header("Models")]
    [SerializeField] private GameObject meshModel;

    [Header("Debug Tools")]
    [SerializeField] private bool debugOctree = false;
    [SerializeField] private bool useBoundedVoxelization = false;
    [SerializeField] private bool useBasicVoxelization = false;
    [SerializeField] private bool debugMeshes = false;

    private bool initDebug = true;
    private bool updated = false;

    private Octree<int> tree;
    private Material voxelMat;

    private List<Mesh> meshes;
    private List<Vector3> verts;
    private List<Color32> clrs;

    private List<int> indices;

    // GIZMOS DEBUGGER
    void OnDrawGizmos()
    {
        if (debugOctree)
        {
            // throw error if debugger is turned on without starting program.
            if(tree == null)
            {
                //throw new System.Exception("[DEBUG::CONTROL::GIZMO] Null Reference to tree for debugging!");
            }
            else if(initDebug) // draw debugger once.
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

    //==================== MAIN ===========================
    void Start ()
    {
        prepare();
        Debug.Log("Number of points: " + tree.getAllPoints().Count);
        initMeshes(tree.getAllPoints().Count);    // add to mesh
        initIndices();                // initialize indices to use
        voxelDrawNode();             // inital draw
    }

    void Update()
    { 
        if(Input.GetKey(KeyCode.RightArrow))
        {
            tree = new Octree<int>(this.transform.position, voxelSpaceLength, octreeMaxDepth);

            Quaternion rot = Quaternion.Euler(0, 10.0f, 0);

            Matrix4x4 m = Matrix4x4.TRS(Vector3.zero,
                                        rot,
                                        new Vector3(1,1,1));

            for (int i = 0; i < verts.Count; i++)
            {
                tree.add(m.MultiplyPoint3x4(verts[i]), clrs[i]);
            }

            updated = true;
        }

        if (Input.GetKey(KeyCode.LeftArrow))
        {
            tree = new Octree<int>(this.transform.position, voxelSpaceLength, octreeMaxDepth);

            Quaternion rot = Quaternion.Euler(0, -10.0f, 0);

            Matrix4x4 m = Matrix4x4.TRS(Vector3.zero,
                                        rot,
                                        new Vector3(1, 1, 1));

            for (int i = 0; i < verts.Count; i++)
            {
                tree.add(m.MultiplyPoint3x4(verts[i]), clrs[i]);
            }

            updated = true;
        }

        if (updated)
        {
            voxelDrawNode();
            updated = false;
        }
    }

    //======================================================

    // PRIVATE METHODS
    private void initMeshes(int voxelCount)
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

    private void initIndices()
    {
        indices = new List<int>(MAX_VERTS);

        for (int i = 0; i < MAX_VERTS; i++)
        {
            indices.Add(i);
        }
    }

    /**
     * Method prepares data structure and voxelizes models. 
     */
    private void prepare()
    {
        voxelMat = Resources.Load("Materials/VoxelMat") as Material;
        if (voxelMat == null)
            throw new System.Exception("Material File wasn't loaded!"); // check that materials were loaded successfully

        // initialize data structure.
        tree = new Octree<int>(this.transform.position, voxelSpaceLength, octreeMaxDepth);

        voxelMat.SetFloat("voxel_size", tree.getVoxelSize());

        List<GameObject> gameObjects = new List<GameObject>();
        List<Transform> childTransforms = new List<Transform>();

        for (int i = 0; i < meshModel.transform.childCount; i++)
        {
            gameObjects.Add(meshModel.gameObject.transform.GetChild(i).gameObject);
            childTransforms.Add(meshModel.gameObject.transform.GetChild(i).transform);
        }

        //meshModel.hideFlags = HideFlags.HideInInspector;
        //meshModel.hideFlags = HideFlags.NotEditable;
        //meshModel.hideFlags = HideFlags.HideInHierarchy;
        meshModel.SetActive(false);

        // voxelizes and adds voxels to data structure.
        for (int i = 0; i < gameObjects.Count; i++)
        {
            Material mat = gameObjects[i].GetComponent<MeshRenderer>().material;
            MeshFilter meshFilter = gameObjects[i].GetComponent<MeshFilter>();
            Transform modelTransform = childTransforms[i];

            Matrix4x4 localToWorldMatrix = modelTransform.localToWorldMatrix;

            if (useBasicVoxelization)
            {
                Vector3[] verts = meshFilter.mesh.vertices;

                for (int j = 0; j < verts.Length; j++)
                {
                    tree.add(localToWorldMatrix.MultiplyPoint3x4(verts[j]) * 50, mat.color);
                }
            }
            else if(useBoundedVoxelization)
            {
                Vector3[] verts = meshFilter.mesh.vertices;
                Color32 clr = mat.color;

                tree.addWithCheck(ref verts, ref localToWorldMatrix, ref clr);
            }
            else
            {
                List<Voxelizer.Voxel> voxels = Voxelizer.Voxelize(meshFilter.mesh, 50);

                for (int j = 0; j < voxels.Count; j++)
                {
                    tree.add(localToWorldMatrix.MultiplyPoint3x4(voxels[j].position) * 50, mat.color);
                }
            }
        }
    }

    // RENDER METHODS
    private void voxelDrawNode()
    {
        verts = tree.getAllPoints();
        clrs = tree.getAllColors();

        //Debug.Log("Final poss" + verts.Count);
        //Debug.Log("Final clrss" + clrs.Count);

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
