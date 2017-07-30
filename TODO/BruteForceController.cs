using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using mattatz.VoxelSystem;
using System;

public class BruteForceController : MonoBehaviour {

    private GameObject[, ,] voxelspace;

    private List<mattatz.VoxelSystem.Voxel> voxelsPos;

    private Voxel[,,] voxelList;

    public int maxLength = 3;

    public int voxelLength = 25; // x
    public int voxelHeight = 25; // y
    public int voxelDepth = 25; // z

    private float voxelSize;
    private float voxelSizeHalf;

    // Use this for initialization
    void Start ()
    {
        voxelspace = new GameObject[voxelLength, voxelHeight, voxelDepth];
        voxelList = new Voxel[voxelLength, voxelHeight, voxelDepth];

        voxelSize = ((float)maxLength / (float)voxelLength);
        voxelSizeHalf = voxelSize / 2;

        //MeshFilter meshToVoxelize = GetComponent<MeshFilter>();

        //voxelsPos = Voxelizer.Voxelize(meshToVoxelize.mesh, voxelLength);

        // Populate voxelList
        for (int x = 1; x <= voxelLength; x++)
        {
            for (int y = 1; y <= voxelHeight; y++)
            {
                for (int z = 1; z <= voxelDepth; z++)
                {
                    string label = "Test Label";
                    unsafe
                    { 
                        fixed (char* lab = label)
                        {
                            if (x % 5 == 0 || y % 6 == 0 || z % 7 == 0)
                                voxelList[x - 1, y - 1, z - 1] = new Voxel(lab, new Color32(0, 255, 0, 255));
                            else
                                voxelList[x - 1, y - 1, z - 1] = new Voxel(lab, new Color32(0, 0, 0, 0));
                        }
                    }
                }
            }
        }

        // Display voxelList
        for (int x = 1; x <= voxelLength; x++)
        {
            for (int y = 1; y <= voxelHeight; y++)
            {
                for (int z = 1; z <= voxelDepth; z++)
                {
                    GameObject voxel = GameObject.CreatePrimitive(PrimitiveType.Cube);
                    MeshRenderer renderer = voxel.gameObject.GetComponent<MeshRenderer>();

                    renderer.material.color = voxelList[x - 1, y - 1, z - 1].Colour;

                    if (voxelList[x - 1, y - 1, z - 1].Colour.a == 0)
                    {
                        voxel.SetActive(false);
                    } else
                    {
                        voxel.SetActive(true);
                    }

                    unsafe
                    {
                        Debug.Log("Address of Label = " + new IntPtr(voxelList[x - 1, y - 1, z - 1].Label));
                        voxel.name = new string(voxelList[x - 1, y - 1, z - 1].Label);
                    }

                    Vector3 newPos = new Vector3();

                    newPos.x = x * voxelSize;
                    newPos.y = y * voxelSize;
                    newPos.z = z * voxelSize;

                    voxel.transform.localPosition = newPos;
                    voxel.transform.localScale = new Vector3(voxelSize, voxelSize, voxelSize);

                    //voxelspace[x - 1, y - 1, z - 1] = voxel;
                }
            }
        }
        /*
        voxelsPos.ForEach(voxel => {

            Vector3 pos = voxel.position;

            if((int)pos.x > 0 && (int)pos.y > 0 && (int)pos.z > 0
            && pos.x < voxelLength && pos.y < voxelLength && pos.z < voxelLength)
            {
                Debug.Log(pos);
                voxelspace[((int)pos.x - 1), ((int)pos.y - 1), ((int)pos.z - 1)].SetActive(true);
            } else
            {
                Debug.Log("rejected a voxel pos");
            }
        });*/
    }
	
	// Update is called once per frame
	void Update () {
		
	}
}
