using System.Collections;
using System.Collections.Generic;
using System.Diagnostics;
using UnityEngine;

public class BruteForceController : MonoBehaviour
{

    private static int MAX_VERTS = 65534;

    private bool updated = false;
    private int voxelSpaceHalf;

    private List<Mesh> meshes;

    private List<Vector3> verts;
    private List<Color32> clrs;
    private List<int> indices;

    private VoxelSpace voxelSpace;
    private Mesh meshToVoxelize;

    private Material voxelMat;

    public float scale = 5.5f;

    // Use this for initialization
    void Start()
    {
       // long start = Stopwatch.GetTimestamp();
        voxelMat = Resources.Load("Materials/VoxelMat") as Material;
       // long end = Stopwatch.GetTimestamp();

        //UnityEngine.Debug.Log("Took " + ((float)(end-start)/10000) + " ms to setup voxel space");

        voxelSpace = new VoxelSpace();
        voxelSpaceHalf = voxelSpace.VoxelSpaceHalf;
        meshToVoxelize = GetComponent<MeshFilter>().mesh;

        // check that materials were loaded successfully
        if (voxelMat == null)
            throw new System.Exception("Material File wasn't loaded!");

        // check that model to voxel exists
        if (meshToVoxelize.vertexCount == 0)
            throw new System.Exception("Mesh to voxelize doesn't exist!");

        // add models
        //int voxelCount = voxelSpace.addMeshToVoxelSpace(meshToVoxelize, scale);

        int voxelCount = voxelSpace.VoxelVolume;
        // add to mesh
        initMesh(voxelCount);

        // initialize indices to use
        initArrays();

       // UnityEngine.Debug.Log(meshes.Count + " voxel meshes used");

        // initial draw
       // start = Stopwatch.GetTimestamp();
        drawVoxels();
        //end = Stopwatch.GetTimestamp();

        // UnityEngine.Debug.Log("Took " + ((float)(end - start) / 10000) + " ms to traverse voxel space");
        UnityEngine.Debug.Log(OVRManager.profile.ipd);
    }

    // Update is called once per frame
    void Update()
    {
        if (updated)
            drawVoxels();
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

            gObj.hideFlags = HideFlags.HideInInspector;
            gObj.hideFlags = HideFlags.NotEditable;
            gObj.hideFlags = HideFlags.HideInHierarchy;
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

    private void drawVoxels()
    {
        int idx = 0;
        int currVert = 0;

        if (verts != null)
        {
            verts.Clear();
            clrs.Clear();
        }

        Mesh mesh = meshes[0];

        for (int i = -voxelSpaceHalf; i <= voxelSpaceHalf; i++)
        {
            for (int j = -voxelSpaceHalf; j <= voxelSpaceHalf; j++)
            {
                for (int k = -voxelSpaceHalf; k <= voxelSpaceHalf; k++)
                {
                    if (currVert == MAX_VERTS)
                    {
                        mesh.Clear();
                        mesh.SetVertices(verts);
                        mesh.SetColors(clrs);
                        mesh.SetIndices(indices.GetRange(0, MAX_VERTS).ToArray(), MeshTopology.Points, 0);

                        verts.Clear();
                        clrs.Clear();

                        mesh = meshes[++idx];

                        currVert = 0;
                    }

                    Voxel voxel = voxelSpace.getVoxel(i, j, k);

                    if (voxel.DataExists)
                    {
                        verts.Add(voxelSpace.getPosition(i, j, k));
                        clrs.Add(voxel.Colour);

                        currVert++;
                    }
                }
            }
        }

        mesh.SetVertices(verts);
        mesh.SetColors(clrs);
        mesh.SetIndices(indices.GetRange(0, currVert).ToArray(), MeshTopology.Points, 0);
    }
}
