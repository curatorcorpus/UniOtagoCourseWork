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
    [SerializeField] private bool useBasicVoxelization = false;

    private bool initDebug = true;
    private bool updated = true;

    private Octree<int> tree;
    private Material voxelMat;

    private List<Mesh> meshes;
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

    // Use this for initialization
    void Start ()
    {
        voxelMat = Resources.Load("Materials/VoxelMat") as Material;
        if (voxelMat == null)
            throw new System.Exception("Material File wasn't loaded!"); // check that materials were loaded successfully

        tree = new Octree<int>(this.transform.position, voxelSpaceLength, octreeMaxDepth);

        voxelMat.SetFloat("voxel_size", tree.getVoxelSize());

        List<GameObject> gameObjects = new List<GameObject>();
        List<Transform> childTransforms = new List<Transform>();

        for(int i = 0; i < meshModel.transform.childCount; i++) 
        {
            gameObjects.Add(meshModel.gameObject.transform.GetChild(i).gameObject);
            childTransforms.Add(meshModel.gameObject.transform.GetChild(i).transform);
        }

        for (int i = 0; i < gameObjects.Count; i++)
        {
            Material mat = gameObjects[i].GetComponent<MeshRenderer>().material;
            MeshFilter meshFilter = gameObjects[i].GetComponent<MeshFilter>();
            Transform modelTransform = childTransforms[i];

            Matrix4x4 ltW = modelTransform.localToWorldMatrix;

            if(useBasicVoxelization)
            {
                Vector3[] verts = meshFilter.mesh.vertices;

                for (int j = 0; j < verts.Length; j++)
                {
                    tree.add(ltW.MultiplyPoint3x4(verts[j]) * 50, mat.color);
                }
            } else
            {
                List<Voxelizer.Voxel> voxels = Voxelizer.Voxelize(meshFilter.mesh, 50);

                for (int j = 0; j < voxels.Count; j++)
                {
                    tree.add(ltW.MultiplyPoint3x4(voxels[j].position) * 50, mat.color);
                }
            }
        }

        initMeshes(tree.getAllPoints().Count);    // add to mesh
        initIndices();                // initialize indices to use
        voxelDrawNode();             // inital draw
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

            //gObj.hideFlags = HideFlags.HideInInspector;
            //gObj.hideFlags = HideFlags.NotEditable;
            //gObj.hideFlags = HideFlags.HideInHierarchy;
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

    private void voxelDrawNode()
    {
        List<Vector3> test = tree.getAllPoints();
        List<Color32> clrs = tree.getAllColors();

        Debug.Log("Final poss" + test.Count);
        Debug.Log("Final clrss" + clrs.Count);
        // draw
        int idx = 0;
        int remainingVerts = test.Count;

        // initial mesh
        Mesh mesh = meshes[0];

        while (remainingVerts > 0)
        {
            if(remainingVerts < MAX_VERTS)
            {
                mesh.Clear();
                mesh.SetVertices(test.GetRange(0, remainingVerts));
                mesh.SetColors(clrs.GetRange(0, remainingVerts));
                mesh.SetIndices(indices.GetRange(0, remainingVerts).ToArray(), MeshTopology.Points, 0);
            }
            else
            {
                mesh.Clear();
                mesh.SetVertices(test.GetRange(remainingVerts - MAX_VERTS, MAX_VERTS));
                mesh.SetColors(clrs.GetRange(remainingVerts - MAX_VERTS, MAX_VERTS));
                mesh.SetIndices(indices.GetRange(0, MAX_VERTS).ToArray(), MeshTopology.Points, 0);
                mesh = meshes[++idx];
            }

            remainingVerts -= MAX_VERTS;
        }
    }
}
