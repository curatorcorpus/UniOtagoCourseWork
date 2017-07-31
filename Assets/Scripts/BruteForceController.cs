using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class BruteForceController : MonoBehaviour {

    private VoxelSpace voxelSpace;
    private int voxelSpaceHalf;

    public float scale = 5.5f;

    // Use this for initialization
    void Start ()
    {
        voxelSpace = new VoxelSpace();
        voxelSpaceHalf = voxelSpace.VoxelSpaceHalf;
        voxelSpace.addMeshToVoxelSpace(GetComponent<MeshFilter>().mesh, scale);

        drawVoxels();
    }
	
	// Update is called once per frame
	//void Update () {}

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
                        //shader.material.color = Random.ColorHSV();
                        shader.material.color = voxel.Colour;
                        cube.SetActive(true);

                        cube.transform.localPosition = voxelSpace.getPosition(i, j, k);
                        cube.transform.localScale = new Vector3(voxelSpace.VoxelSize, voxelSpace.VoxelSize, voxelSpace.VoxelSize);
                    }
                }
            }
        }
    }
}
