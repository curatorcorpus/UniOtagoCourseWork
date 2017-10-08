using System;
using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System.Runtime.InteropServices;
using System.Threading;

public class VoxelizerThread
{	
	public void VoxelizeMesh(ref Mesh mesh, Color32 clr, Matrix4x4 matrix)
	{

		Debug.Log(mesh.triangles.Length);
		var x = 1;
		/*
		int iCount = 0;
		int niCount = 0;
		
		float voxelSizeHalf = voxelSize / 2;
		
		Vector3 voxelExtends = new Vector3(voxelSizeHalf, voxelSizeHalf, voxelSizeHalf);
		
		// Take each triangle in the mesh
		for (int i = 0; i < mesh.triangles.Length; i += 3)
		{
			// Get the triangles three points
			Vector3 p1 = mesh.vertices[mesh.triangles[i + 0]];
			Vector3 p2 = mesh.vertices[mesh.triangles[i + 1]];
			Vector3 p3 = mesh.vertices[mesh.triangles[i + 2]];

			// Create the axis aligned bounding box around the triangle
			float minX = MathUtils.ClipToVoxelGrid(Mathf.Min(p1.x, p2.x, p3.x), voxelSize, true);
			float minY = MathUtils.ClipToVoxelGrid(Mathf.Min(p1.y, p2.y, p3.y), voxelSize, true); 
			float minZ = MathUtils.ClipToVoxelGrid(Mathf.Min(p1.z, p2.z, p3.z), voxelSize, true);

			float maxX = MathUtils.ClipToVoxelGrid(Mathf.Max(p1.x, p2.x, p3.x), voxelSize, false);
			float maxY = MathUtils.ClipToVoxelGrid(Mathf.Max(p1.y, p2.y, p3.y), voxelSize, false);
			float maxZ = MathUtils.ClipToVoxelGrid(Mathf.Max(p1.z, p2.z, p3.z), voxelSize, false);
			
			// Scan the bounding box by increments of voxelvoxelSize
			for (float x = minX + voxelSizeHalf; x < maxX; x += voxelSize)
			{
				for (float y = minY + voxelSizeHalf; y < maxY; y += voxelSize)
				{
					for (float z = minZ + voxelSizeHalf; z < maxZ; z += voxelSize)
					{
						Vector3 currentVoxel = new Vector3(x, y, z);
						
						if (MathUtils.IntersectsBox(p1, p2, p3, currentVoxel, voxelExtends))
						{
							this.Add(matrix.MultiplyPoint3x4(currentVoxel), clr);
							iCount++;
						}
//	                    UnityEngine.Debug.Log("mX: " + minX + " MX: " + maxX + " mY: " + minY + " MY: " + maxY + " mZ: " + 
//	                                      minZ +" MZ: " + maxZ + " x: " + x + " y: " + y + " z: " + z);
						niCount++;
					}
				}
			}
		}
		
		UnityEngine.Debug.Log("Intersect count: " + iCount);
		UnityEngine.Debug.Log("Not interesect count: " + niCount);
		UnityEngine.Debug.Log("Total count: " + (iCount + niCount));*/
	}
}
