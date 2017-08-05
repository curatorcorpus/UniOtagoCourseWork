using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class BruteForceController : MonoBehaviour {

    private bool updated = false;
    private int voxelSpaceHalf;

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
        voxelSpace.addMeshToVoxelSpace(meshToVoxelize, scale);

        // initial draw
        drawVoxels();
    }

    // Update is called once per frame
    void Update ()
    {
        if (updated)
            drawVoxels();
    }

    private void drawVoxels()
    {
        for (int i = -voxelSpaceHalf; i <= voxelSpaceHalf; i++)
        {
            for (int j = -voxelSpaceHalf; j <= voxelSpaceHalf; j++)
            {
                for (int k = -voxelSpaceHalf; k <= voxelSpaceHalf; k++)
                {
                    Voxel voxel = voxelSpace.getVoxel(i, j, k);

                    if (voxel.DataExists)
                    {
                        GameObject cube = GameObject.CreatePrimitive(PrimitiveType.Cube);
                        MeshRenderer shader = cube.gameObject.GetComponent<MeshRenderer>();

                        cube.transform.parent = gameObject.transform;

                        cube.hideFlags = HideFlags.HideInInspector;
                        cube.hideFlags = HideFlags.NotEditable;
                        cube.hideFlags = HideFlags.HideInHierarchy;

                        shader.material.color = voxel.Colour;

                        cube.transform.localPosition = voxelSpace.getPosition(i, j, k);
                        cube.transform.localScale = new Vector3(voxelSpace.VoxelSize, voxelSpace.VoxelSize, voxelSpace.VoxelSize);

                        cube.SetActive(true);
                    }
                }
            }
        }
    }
}
