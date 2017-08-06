using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class VoxelSpace
{
    private Voxel[,,] voxelSpace;
    private List<Voxelizer.Voxel> voxelPos;

    private int voxelLength; // x
    private int voxelHeight; // y
    private int voxelDepth;  // z
    private int voxelSpaceHalf;

    private float voxelSize;
    private float voxelSizeHalf;

    private int volume = 257;
    private int maxLength = 3;

    // CONSTRUCTORS
    public VoxelSpace()
    {
        if (volume % 2 == 0)
            throw new System.Exception("This voxelspace doesn't support for even voxel space!");

        voxelLength = volume;
        voxelHeight = volume;
        voxelDepth = volume;

        voxelSize = ((float)maxLength / (float)voxelLength);
        voxelSizeHalf = voxelSize / 2;
        voxelSpaceHalf = voxelLength / 2;

        setupVoxelSpace();
    }

    // ACCESSOR METHODS
    public float VoxelSize
    {
        get { return voxelSize; }
        set { voxelSize = value; }
    }

    public int VoxelSpaceHalf
    {
        get { return voxelSpaceHalf; }
        set { voxelSpaceHalf = value; }
    }

    public float VoxelSizeHalf
    {
        get { return voxelSizeHalf; }
        set { voxelSizeHalf = value; }
    }

    public Voxel[,,] getVoxelSpace()
    {
        return voxelSpace;
    }

    // PUBLIC METHODS
    public Vector3 getPosition(int i, int j, int k)
    {
        return new Vector3(i * voxelSize, j * voxelSize, k * voxelSize);
    }

    public Vector3 getIndices(int i, int j, int k)
    {
        return new Vector3(voxelSpaceHalf + i, voxelSpaceHalf + j, voxelSpaceHalf + k);
    }

    public Vector3 getPosToIndices(Vector3 pos)
    {
        return new Vector3((int)pos.x + voxelSpaceHalf, (int)pos.y + voxelSpaceHalf, (int)pos.z + voxelSpaceHalf);
    }

    public Voxel getVoxel(int i, int j, int k)
    {
        Vector3 index = getIndices(i, j, k);

        return voxelSpace[(int)index.x, (int)index.y, (int)index.z];
    }

    public int addMeshToVoxelSpace(Mesh mesh, float scale)
    {
        voxelPos = Voxelizer.Voxelize(mesh, volume/2);

        voxelPos.ForEach(voxel => {

            Vector3 pos = voxel.position / scale;

            if (pos.x > -voxelSpaceHalf && pos.y > -voxelSpaceHalf && pos.z > -voxelSpaceHalf &&
                pos.x < voxelSpaceHalf && pos.y < voxelSpaceHalf && pos.z < voxelSpaceHalf)
            {
                Vector3 indices = getPosToIndices(pos);

                voxelSpace[(int)indices.x, (int)indices.y, (int)indices.z].DataExists = true;
            }
            else
            {
                Debug.Log("Voxel Position Rejected!");
            }
        });

        return voxelPos.Count;
    }

    // PRIVATE METHODS
    private void setupVoxelSpace()
    {
        voxelSpace = new Voxel[voxelLength, voxelHeight, voxelDepth];

        for (int i = -voxelSpaceHalf; i <= voxelSpaceHalf; i++)
        {
            for (int j = -voxelSpaceHalf; j <= voxelSpaceHalf; j++)
            {
                for (int k = -voxelSpaceHalf; k <= voxelSpaceHalf; k++)
                {
                    voxelSpace[voxelSpaceHalf + i, voxelSpaceHalf + j, voxelSpaceHalf + k] = new Voxel();
                }
            }
        }
    }
}