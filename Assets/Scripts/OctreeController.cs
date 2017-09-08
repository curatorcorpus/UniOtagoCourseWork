using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class OctreeController : MonoBehaviour
{
    private static int MAX_VERTS = 65534;

    [SerializeField] private bool debug = false;
    [SerializeField] private float voxelSpaceLength = 5.0f;
    [SerializeField] private int octreeMaxDepth = 2;
    [SerializeField] private GameObject meshModel;

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
        // check that materials were loaded successfully
        if (voxelMat == null)
            throw new System.Exception("Material File wasn't loaded!");

        tree = new Octree<int>(this.transform.position, voxelSpaceLength, octreeMaxDepth);

        voxelMat.SetFloat("voxel_size", tree.getVoxelSize());

        List<GameObject> gameObjects = new List<GameObject>();
        for(int i = 0; i < meshModel.transform.childCount; i++) 
        {
            gameObjects.Add(meshModel.gameObject.transform.GetChild(i).gameObject); 
        }

        for(int i = 0; i < gameObjects.Count; i++)
        {
            MeshFilter meshfilter = gameObjects[i].GetComponent<MeshFilter>();
            Material mat = gameObjects[i].GetComponent<MeshRenderer>().material;
            Transform modelTransform = gameObjects[i].GetComponent<Transform>();

            Matrix4x4 matrix = Matrix4x4.TRS(modelTransform.localPosition,
                                             modelTransform.localRotation,
                                             modelTransform.localScale);
            Vector3[] verts = meshfilter.mesh.vertices;

            Debug.Log(mat.color);
            Debug.Log(verts.Length);

            for(int j = 0; j < verts.Length; j++)
            {
                tree.add(matrix.MultiplyPoint3x4(verts[j] * 100), mat.color);
                //Debug.Log(matrix.MultiplyPoint3x4(verts[j] * 2));
            }
        }
        /*for(int i = 0; i < GetComponent<MeshFilter>().mesh.vertexCount; i++)
        {
            Debug.Log(GetComponent<MeshFilter>().mesh.vertices[i]);
        }*/
        /*List<Voxelizer.Voxel> voxels = Voxelizer.Voxelize(GetComponent<MeshFilter>().mesh, 100);

        voxels.ForEach(voxel =>
        {
                Vector3 pos = voxel.position;
                Debug.Log(pos);
                tree.add(pos * 110);
        });*/

        initMeshByVoxelCount(tree.getAllPoints().Count);    // add to mesh
        //initMeshByNoMeshes(gameObjects.Count);
        initArrays();                // initialize indices to use
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
    private void initMeshByNoMeshes(int noMeshes)
    {
        meshes = new List<Mesh>(noMeshes);
        for (int i = 0; i < noMeshes; i++)
        {
            GameObject gObj = new GameObject();
            Mesh newMesh = new Mesh();

            meshes.Add(newMesh);

            gObj.AddComponent<MeshRenderer>().material = voxelMat;
            newMesh.name = "VoxelMesh";

            gObj.AddComponent<MeshFilter>().mesh = newMesh;
            gObj.name = "VoxelMesh";

            gObj.transform.parent = gameObject.transform;

            //gObj.hideFlags = HideFlags.HideInInspector;
            //gObj.hideFlags = HideFlags.NotEditable;
            //gObj.hideFlags = HideFlags.HideInHierarchy;
            gObj.SetActive(true);
        }
    }

    private void initMeshByVoxelCount(int voxelCount)
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
        }
    }

    private void voxelDrawNode()
    {
        List<Vector3> test = tree.getAllPoints();
        Debug.Log("Final poss" + test.Count);
        clrs = tree.getAllColors();
        Debug.Log("Final clrss" + clrs.Count);
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
                mesh.SetVertices(test.GetRange(remainingVerts - MAX_VERTS, MAX_VERTS));
                mesh.SetColors(clrs);
                mesh.SetIndices(indices.GetRange(0, MAX_VERTS).ToArray(), MeshTopology.Points, 0);
                mesh = meshes[++idx];
            }

            remainingVerts -= MAX_VERTS;
        }
    }
}
