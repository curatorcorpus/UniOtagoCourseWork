using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class BruteForceController : MonoBehaviour {

    private static int MAX_VERTS = 65534;

    private bool updated = false;
    private int voxelSpaceHalf;

    private List<Mesh> meshes;

    private VoxelSpace voxelSpace;
    private Mesh meshToVoxelize;

    public float scale = 5.5f;

    // Use this for initialization
    void Start ()
    {
        voxelSpace     = new VoxelSpace();
        voxelSpaceHalf = voxelSpace.VoxelSpaceHalf;
        meshToVoxelize = GetComponent<MeshFilter>().mesh;

        // check that model to voxel exists
        if (meshToVoxelize.vertexCount == 0)
            throw new System.Exception("Mesh to voxelize doesn't exist!");

        // add models
        int voxelCount = voxelSpace.addMeshToVoxelSpace(meshToVoxelize, scale);

        // add to mesh
        initMesh(voxelCount);

        // initial draw
        drawVoxels();
    }

    // Update is called once per frame
    void Update ()
    {
        if (updated)
            drawVoxels();
    }

    // PRIVATE METHODS
    private void initMesh(int voxelCount)
    {
        if(voxelCount > MAX_VERTS)
        {
            int divisor = Mathf.Ceil(voxelCount / MAX_VERTS);

            meshes = new List<Mesh>(divisor);
            
            for(int i = 0; i < meshes.Count; i++)
            {   
                Mesh newMesh = new Mesh();
                newMesh.name = "VoxelMesh_" + i;
                
                meshes.Add(new Mesh());
            }
        }
        else{
            meshes = new List<Mesh>(1);
            meshes.Add(new Mesh());
        }
    }

    private void drawVoxels()
    {
        List<Voxel> voxels;

        for (int i = -voxelSpaceHalf; i <= voxelSpaceHalf; i++)
        {
            for (int j = -voxelSpaceHalf; j <= voxelSpaceHalf; j++)
            {
                for (int k = -voxelSpaceHalf; k <= voxelSpaceHalf; k++)
                {
                    Voxel voxel = voxelSpace.getVoxel(i, j, k);

                    if (voxel.DataExists)
                    {
                        

                        /*
                        GameObject cube = GameObject.CreatePrimitive(PrimitiveType.Cube);
                        MeshRenderer shader = cube.gameObject.GetComponent<MeshRenderer>();

                        cube.transform.parent = gameObject.transform;

                        cube.hideFlags = HideFlags.HideInInspector;
                        cube.hideFlags = HideFlags.NotEditable;
                        cube.hideFlags = HideFlags.HideInHierarchy;

                        shader.material.color = voxel.Colour;

                        cube.transform.localPosition = voxelSpace.getPosition(i, j, k);
                        cube.transform.localScale = new Vector3(voxelSpace.VoxelSize, voxelSpace.VoxelSize, voxelSpace.VoxelSize);

                        cube.SetActive(true);*/
                    }
                }
            }
        }
    }
}
