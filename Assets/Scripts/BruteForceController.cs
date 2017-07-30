using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class BruteForceController : MonoBehaviour {

    private int[, ,] voxelspace;

    public int maxLength = 3;

    public int voxelLength = 25; // x
    public int voxelHeight = 25; // y
    public int voxelDepth = 25; // z

    private float voxelSize;
    private float voxelSizeHalf;

    // Use this for initialization
    void Start ()
    {
        voxelspace = new int[voxelLength, voxelHeight, voxelDepth];
        voxelSize = ((float)maxLength / (float)voxelLength);
        voxelSizeHalf = voxelSize / 2;

        for (int x = 1; x <= voxelLength; x++)
        {
            for (int y = 1; y <= voxelHeight; y++)
            {
                for (int z = 1; z <= voxelDepth; z++)
                {
                    GameObject voxel = GameObject.CreatePrimitive(PrimitiveType.Cube);
                    MeshRenderer shader = voxel.gameObject.GetComponent<MeshRenderer>();

                    shader.material.color = Random.ColorHSV();
                    voxel.SetActive(false);

                    Vector3 newPos = new Vector3();

                    newPos.x = x * voxelSize;
                    newPos.y = y * voxelSize;
                    newPos.z = z * voxelSize;

                    voxel.transform.localPosition = newPos;
                    voxel.transform.localScale = new Vector3(voxelSize, voxelSize, voxelSize);
                }
            }
        }
	}
	
	// Update is called once per frame
	void Update () {
		
	}
}
