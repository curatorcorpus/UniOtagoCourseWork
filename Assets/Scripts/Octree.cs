//using System;

using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using UnityEngine;
using UnityEngine.EventSystems;
using UnityEngine.VR.WSA;

public class Octree<TType>
{
    private OctreeNode<TType> root;

    private int count;
	
    private float voxelSpaceSize;
    private float voxelSize;
    private float maxPoint;

    // GETTERS AND SETTERS
    public int Count
    {
        get { return count; }
        //set { this.count = value;  }
    }

    public OctreeNode<TType> Root
    {
        get { return root; }
    }

    // CONSTRUCTORS
    public Octree(Vector3 position, float voxelSpaceSize, float voxelSize)
    {
        // initialize the root node
        this.root = new OctreeNode<TType>(position, voxelSpaceSize);

	    this.voxelSize = voxelSize;
        this.voxelSpaceSize = voxelSpaceSize;
	    
        this.maxPoint = voxelSpaceSize / 2;
        this.count = 0;
    }

    // PUBLIC METHODS
    /*
     * Method for adding a new node to data structure.
     */
    public void Add(Vector3 pos, Color32 color)
    {
        if (pos.x <= maxPoint && pos.x >= -maxPoint &&
            pos.y <= maxPoint && pos.y >= -maxPoint &&
            pos.z <= maxPoint && pos.z >= -maxPoint)
        {
            root.add(pos, color, voxelSize);
            count++;
            UnityEngine.Debug.Log(pos);
        }

        // any position outside mentioned boundary is just inserted at the boundary points.
        else
        {
            UnityEngine.Debug.Log("position " + pos + " wasn't inserted because the max size is " + voxelSpaceSize);
            UnityEngine.Debug.Log("The maximum boundaries are: ");
            PrintBoundaries();
        }
    }

    public void VoxelizeMesh(ref Mesh mesh, Color32 clr, Matrix4x4 matrix)
    {
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
				            this.add(matrix.MultiplyPoint3x4(currentVoxel), clr);
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
	    UnityEngine.Debug.Log("Total count: " + (iCount + niCount));
    }

    /*
     * Method for traversing entire Octree data structure.
     */
    public List<Vector3> GetAllPoints()
    {
        if(root == null)
        {
            return null;
        }

        return root.getPositions();
    }

    public List<Color32> GetAllColors()
    {
        if (root == null)
        {
            return null;
        }

        return root.getColors();
    }

    public void PrintBoundaries()
    {
        UnityEngine.Debug.Log(new Vector3(maxPoint, maxPoint, maxPoint));
        UnityEngine.Debug.Log(new Vector3(-maxPoint, maxPoint, maxPoint));
        UnityEngine.Debug.Log(new Vector3(maxPoint, -maxPoint, maxPoint));
        UnityEngine.Debug.Log(new Vector3(maxPoint, maxPoint, -maxPoint));
        UnityEngine.Debug.Log(new Vector3(-maxPoint, -maxPoint, maxPoint));
        UnityEngine.Debug.Log(new Vector3(-maxPoint, maxPoint, -maxPoint));
        UnityEngine.Debug.Log(new Vector3(maxPoint, -maxPoint, -maxPoint));
        UnityEngine.Debug.Log(new Vector3(-maxPoint, -maxPoint, -maxPoint));
    }
}