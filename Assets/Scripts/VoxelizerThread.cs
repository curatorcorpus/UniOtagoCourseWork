using System;
using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using System.Threading;

public class VoxelizerThread
{
    private bool finished = false;
	private bool locked = false;
    private float voxelSize;
    private int[] tris;

    private Matrix4x4 matrix;
    private Stack<Vector3> voxelsToAdd;
    private Thread thread;
    private Vector3[] verts;

    // GETTERS AND SETTERS.
    public bool Finished
    {
        get { return finished; }
        set { this.finished = value; }
    }
	public bool Locked
	{
		get { return locked; }
		set { this.locked = value; }
	}
	public Stack<Vector3> VoxelsToAdd
    {
        get { return voxelsToAdd; }
        set { this.voxelsToAdd = value; }
    }
    public Thread Thread
    {
        get { return thread; }
        set { this.thread = value; }
    }

    public VoxelizerThread(ref Vector3[] verts, ref int[] tris, Matrix4x4 matrix, float voxelSize)
    {
        this.verts = verts;
        this.tris = tris;
        this.matrix = matrix;
        this.voxelSize = voxelSize;
        this.voxelsToAdd = new Stack<Vector3>();

        thread = new Thread(new ThreadStart(this.VoxelizeMesh));
    }

    // Thread methods / properties
    public void Start()
    {
        thread.Start();
    }

    public bool IsAlive
    {
        get { return thread.IsAlive; }
    }

    private void VoxelizeMesh()
    { 
		int iCount = 0;
		int niCount = 0;

		Vector3 voxelExtends = new Vector3(voxelSize, voxelSize, voxelSize);

        // Take each triangle in the mesh
        for (int i = 0; i < tris.Length; i += 3)
		{
			// Get the triangles three points
			Vector3 p1 = verts[tris[i + 0]];
			Vector3 p2 = verts[tris[i + 1]];
			Vector3 p3 = verts[tris[i + 2]];

//            p1 = matrix.MultiplyPoint(p1);
//            p2 = matrix.MultiplyPoint(p2);
//            p3 = matrix.MultiplyPoint(p3);

            // Create the axis aligned bounding box around the triangle
            float minX = MathUtils.ClipToVoxelGrid(Mathf.Min(p1.x, p2.x, p3.x), voxelSize, true);
			float minY = MathUtils.ClipToVoxelGrid(Mathf.Min(p1.y, p2.y, p3.y), voxelSize, true); 
			float minZ = MathUtils.ClipToVoxelGrid(Mathf.Min(p1.z, p2.z, p3.z), voxelSize, true);

			float maxX = MathUtils.ClipToVoxelGrid(Mathf.Max(p1.x, p2.x, p3.x), voxelSize, false);
			float maxY = MathUtils.ClipToVoxelGrid(Mathf.Max(p1.y, p2.y, p3.y), voxelSize, false);
			float maxZ = MathUtils.ClipToVoxelGrid(Mathf.Max(p1.z, p2.z, p3.z), voxelSize, false);
			
			// Scan the bounding box by increments of voxelvoxelSize
			for (float x = minX; x < maxX; x += voxelSize)
			{
				for (float y = minY; y < maxY; y += voxelSize)
				{
					for (float z = minZ; z < maxZ; z += voxelSize)
					{
						Vector3 currentVoxel = new Vector3(x, y, z);
						
//						if (MathUtils.IntersectsBox(p1, p2, p3, currentVoxel, voxelExtends))
//						{
//                            voxelsToAdd.Push(currentVoxel);
//                            iCount++;
//						}
						if (MathUtils.IntersectsBox(p1, p2, p3, currentVoxel, voxelExtends))
						{
							voxelsToAdd.Push(matrix.MultiplyPoint3x4(currentVoxel));
							iCount++;
						}
						niCount++;
					}
				}
			}
		}
	/*	
		UnityEngine.Debug.Log("Intersect count: " + iCount);
		UnityEngine.Debug.Log("Not interesect count: " + niCount);
		UnityEngine.Debug.Log("Total count: " + (iCount + niCount));
*/
        finished = true;
	}
}
