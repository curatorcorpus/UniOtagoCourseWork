using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using mattatz.VoxelSystem;

public class BruteForceController : MonoBehaviour {

    private GameObject[, ,] voxelspace;

    private List<mattatz.VoxelSystem.Voxel> voxelsPos;

    private int voxelLength = 25; // x
    private int voxelHeight = 25; // y
    private int voxelDepth = 25; // z
    private int voxelSpaceHalf;

    private float voxelSize;
    private float voxelSizeHalf;

    public int volume;
    public int maxLength = 3;

    // Use this for initialization
    void Start ()
    {
        if(volume == 0)
        {
            throw new System.Exception("Volume size is Zero!");
        }

        voxelLength = volume;
        voxelHeight = volume;
        voxelDepth = volume;

        voxelspace = new GameObject[voxelLength, voxelHeight, voxelDepth];
        voxelSize = ((float)maxLength / (float)voxelLength);
        voxelSizeHalf = voxelSize / 2;
        voxelSpaceHalf = voxelLength / 2;

        if (voxelSize % 2 == 0)
        {
            voxelSpaceHalf++;
        }

        MeshFilter meshToVoxelize = GetComponent<MeshFilter>();
        voxelsPos = Voxelizer.Voxelize(meshToVoxelize.mesh, voxelLength);

        for (int i = -voxelSpaceHalf; i <= voxelSpaceHalf; i++)
        {
            for (int j = -voxelSpaceHalf; j <= voxelSpaceHalf; j++)
            {
                for (int k = -voxelSpaceHalf; k <= voxelSpaceHalf; k++)
                {
                    GameObject voxel = GameObject.CreatePrimitive(PrimitiveType.Cube);
                    MeshRenderer shader = voxel.gameObject.GetComponent<MeshRenderer>();

                    voxel.transform.parent = gameObject.transform;

                    voxel.hideFlags = HideFlags.HideInInspector;
                    voxel.hideFlags = HideFlags.NotEditable;
                    voxel.hideFlags = HideFlags.HideInHierarchy;
                    shader.material.color = Random.ColorHSV();
                    voxel.SetActive(false);

                    Vector3 newPos = new Vector3();

                    newPos.x = i * voxelSize;
                    newPos.y = j * voxelSize;
                    newPos.z = k * voxelSize;

                    voxel.transform.localPosition = newPos;
                    voxel.transform.localScale = new Vector3(voxelSize, voxelSize, voxelSize);

                    voxelspace[voxelSpaceHalf + i, voxelSpaceHalf + j, voxelSpaceHalf + k] = voxel;
                }
            }
        }

        voxelsPos.ForEach(voxel => {

            Vector3 pos = voxel.position / 4.0f;

                        Debug.Log(pos);
            if (pos.x > -voxelSpaceHalf && pos.y > -voxelSpaceHalf && pos.z > -voxelSpaceHalf &&
                pos.x < voxelSpaceHalf && pos.y < voxelSpaceHalf && pos.z < voxelSpaceHalf)
            {
                voxelspace[((int)pos.x + voxelSpaceHalf), 
                           ((int)pos.y + voxelSpaceHalf), 
                           ((int)pos.z + voxelSpaceHalf)].SetActive(true);
            } else
            {
                Debug.Log("rejecteda  voxel pos");
            }
        });
    }
	
	// Update is called once per frame
	void Update () {
		
	}
}
