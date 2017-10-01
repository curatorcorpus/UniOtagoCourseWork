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
        this.root = new OctreeNode<TType>(position, voxelSize);

	    this.voxelSize = voxelSize;
        this.voxelSpaceSize = voxelSpaceSize;
	    
        this.maxPoint = voxelSpaceSize / 2;
        this.count = 0;
    }

    // PUBLIC METHODS
    /*
     * Method for adding a new node to data structure.
     */
    public void add(Vector3 pos, Color32 color)
    {
        if (pos.x <= maxPoint && pos.x >= -maxPoint &&
            pos.y <= maxPoint && pos.y >= -maxPoint &&
            pos.z <= maxPoint && pos.z >= -maxPoint)
        {
            root.add(pos, color, voxelSize);
            count++;
        }

        // any position outside mentioned boundary is just inserted at the boundary points.
        else
        {
            UnityEngine.Debug.Log("position " + pos + " wasn't inserted because the max size is " + voxelSpaceSize);
            UnityEngine.Debug.Log("The maximum boundaries are: ");
           // printBoundaries();
        }
    }

    public static bool IntersectsBox( Vector3 a, Vector3 b, Vector3 c, Vector3 boxCenter, Vector3 boxExtents )
	{
		// Translate triangle as conceptually moving AABB to origin
		var v0 = ( a - boxCenter );
		var v1 = ( b - boxCenter );
		var v2 = ( c - boxCenter );
	
		// Compute edge vectors for triangle
		var f0 = ( v1 - v0 );
		var	f1 = ( v2 - v1 );
		var	f2 = ( v0 - v2 );
	
		#region Test axes a00..a22 (category 3)
	
		// Test axis a00
		var a00 = new Vector3( 0, -f0.z, f0.y );
		var p0 = Vector3.Dot( v0, a00 );
		var p1 = Vector3.Dot( v1, a00 );
		var p2 = Vector3.Dot( v2, a00 );
		var r = boxExtents.y * Math.Abs( f0.z ) + boxExtents.z * Math.Abs( f0.y );
		if( Math.Max( -fmax( p0, p1, p2 ), fmin( p0, p1, p2 ) ) > r )
		{
			return false;
		}
	
		// Test axis a01
		var a01 = new Vector3( 0, -f1.z, f1.y );
		p0 = Vector3.Dot( v0, a01 );
		p1 = Vector3.Dot( v1, a01 );
		p2 = Vector3.Dot( v2, a01 );
		r = boxExtents.y * Math.Abs( f1.z ) + boxExtents.z * Math.Abs( f1.y );
		if( Math.Max( -fmax( p0, p1, p2 ), fmin( p0, p1, p2 ) ) > r )
		{
			return false;
		}
	
		// Test axis a02
		var a02 = new Vector3( 0, -f2.z, f2.y );
		p0 = Vector3.Dot( v0, a02 );
		p1 = Vector3.Dot( v1, a02 );
		p2 = Vector3.Dot( v2, a02 );
		r = boxExtents.y * Math.Abs( f2.z ) + boxExtents.z * Math.Abs( f2.y );
		if( Math.Max( -fmax( p0, p1, p2 ), fmin( p0, p1, p2 ) ) > r )
		{
			return false;
		}
	
		// Test axis a10
		var a10 = new Vector3( f0.z, 0, -f0.x );
		p0 = Vector3.Dot( v0, a10 );
		p1 = Vector3.Dot( v1, a10 );
		p2 = Vector3.Dot( v2, a10 );
		r = boxExtents.x * Math.Abs( f0.z ) + boxExtents.z * Math.Abs( f0.x );
		if( Math.Max( -fmax( p0, p1, p2 ), fmin( p0, p1, p2 ) ) > r )
		{
			return false;
		}
	
		// Test axis a11
		var a11 = new Vector3( f1.z, 0, -f1.x );
		p0 = Vector3.Dot( v0, a11 );
		p1 = Vector3.Dot( v1, a11 );
		p2 = Vector3.Dot( v2, a11 );
		r = boxExtents.x * Math.Abs( f1.z ) + boxExtents.z * Math.Abs( f1.x );
		if( Math.Max( -fmax( p0, p1, p2 ), fmin( p0, p1, p2 ) ) > r )
		{
			return false;
		}
	
		// Test axis a12
		var a12 = new Vector3( f2.z, 0, -f2.x );
		p0 = Vector3.Dot( v0, a12 );
		p1 = Vector3.Dot( v1, a12 );
		p2 = Vector3.Dot( v2, a12 );
		r = boxExtents.x * Math.Abs( f2.z ) + boxExtents.z * Math.Abs( f2.x );
		if( Math.Max( -fmax( p0, p1, p2 ), fmin( p0, p1, p2 ) ) > r )
		{
			return false;
		}
	
		// Test axis a20
		var a20 = new Vector3( -f0.y, f0.x, 0 );
		p0 = Vector3.Dot( v0, a20 );
		p1 = Vector3.Dot( v1, a20 );
		p2 = Vector3.Dot( v2, a20 );
		r = boxExtents.x * Math.Abs( f0.y ) + boxExtents.y * Math.Abs( f0.x );
		if( Math.Max( -fmax( p0, p1, p2 ), fmin( p0, p1, p2 ) ) > r )
		{
			return false;
		}
	
		// Test axis a21
		var a21 = new Vector3( -f1.y, f1.x, 0 );
		p0 = Vector3.Dot( v0, a21 );
		p1 = Vector3.Dot( v1, a21 );
		p2 = Vector3.Dot( v2, a21 );
		r = boxExtents.x * Math.Abs( f1.y ) + boxExtents.y * Math.Abs( f1.x );
		if( Math.Max( -fmax( p0, p1, p2 ), fmin( p0, p1, p2 ) ) > r )
		{
			return false;
		}
	
		// Test axis a22
		var a22 = new Vector3( -f2.y, f2.x, 0 );
		p0 = Vector3.Dot( v0, a22 );
		p1 = Vector3.Dot( v1, a22 );
		p2 = Vector3.Dot( v2, a22 );
		r = boxExtents.x * Math.Abs( f2.y ) + boxExtents.y * Math.Abs( f2.x );
		if( Math.Max( -fmax( p0, p1, p2 ), fmin( p0, p1, p2 ) ) > r )
		{
			return false;
		}
	
		#endregion 
	
		#region Test the three axes corresponding to the face normals of AABB b (category 1)
	
		// Exit if...
		// ... [-extents.x, extents.x] and [min(v0.x,v1.x,v2.x), max(v0.x,v1.x,v2.x)] do not overlap
		if( fmax( v0.x, v1.x, v2.x ) < -boxExtents.x || fmin( v0.x, v1.x, v2.x ) > boxExtents.x )
		{
			return false;
		}
	
		// ... [-extents.y, extents.y] and [min(v0.y,v1.y,v2.y), max(v0.y,v1.y,v2.y)] do not overlap
		if( fmax( v0.y, v1.y, v2.y ) < -boxExtents.y || fmin( v0.y, v1.y, v2.y ) > boxExtents.y )
		{
			return false;
		}
				
		// ... [-extents.z, extents.z] and [min(v0.z,v1.z,v2.z), max(v0.z,v1.z,v2.z)] do not overlap
		if( fmax( v0.z, v1.z, v2.z ) < -boxExtents.z || fmin( v0.z, v1.z, v2.z ) > boxExtents.z )
		{
			return false;
		}
	
		#endregion 
	
		#region Test separating axis corresponding to triangle face normal (category 2)
	
		var planeNormal = Vector3.Cross( f0, f1 );
		var planeDistance = Vector3.Dot( planeNormal, v0 );
	
		// Compute the projection interval radius of b onto L(t) = b.c + t * p.n
		r = boxExtents.x * Math.Abs( planeNormal.x )
			+ boxExtents.y * Math.Abs( planeNormal.y )
			+ boxExtents.z * Math.Abs( planeNormal.z );
	
		// Intersection occurs when plane distance falls within [-r,+r] interval
		if( planeDistance > r )
		{
			return false;
		}
	
		#endregion
	
		return true;
	}

	private static float fmin( float a, float b, float c )
	{
		return Math.Min( a, Math.Min( b, c ) );
	}
	
	private static float fmax( float a, float b, float c )
	{
		return Math.Max( a, Math.Max( b, c ) ); 
	}
    
	float round(float axisBound, float voxelSize, bool down)
	{
		float difference = axisBound % voxelSize;

		if (down)
			return (axisBound - difference);
		
		return ((axisBound - difference) + voxelSize);
	}

    public void voxelizeMesh(ref Mesh mesh, Color32 clr, Matrix4x4 matrix)
    {
	    int i_count = 0;
	    int ni_count = 0;
	    
        // Take each triangle in the mesh
        for (int i = 0; i < mesh.triangles.Length; i += 3)
        {
            // Get the triangles three points
            Vector3 p1 = mesh.vertices[mesh.triangles[i + 0]];
            Vector3 p2 = mesh.vertices[mesh.triangles[i + 1]];
            Vector3 p3 = mesh.vertices[mesh.triangles[i + 2]];

            // Create the axis aligned bounding box around the triangle
            float minX = round(Mathf.Min(p1.x, p2.x, p3.x), voxelSize, true);
            float maxX = round(Mathf.Max(p1.x, p2.x, p3.x), voxelSize, false);
            float minY = round(Mathf.Min(p1.y, p2.y, p3.y), voxelSize, true); 
            float maxY = round(Mathf.Max(p1.y, p2.y, p3.y), voxelSize, false);
            float minZ = round(Mathf.Min(p1.z, p2.z, p3.z), voxelSize, true);
            float maxZ = round(Mathf.Max(p1.z, p2.z, p3.z), voxelSize, false);

	        float voxelSizeHalf = voxelSize / 2;
	        Vector3 voxelExtends = new Vector3(voxelSizeHalf, voxelSizeHalf, voxelSizeHalf);
	        
            // Scan the bounding box by increments of voxelvoxelSize
            for (float x = minX + voxelSizeHalf; x < maxX; x += voxelSize)
            {
	            for (float y = minY + voxelSizeHalf; y < maxY; y += voxelSize)
	            {
		            for (float z = minZ + voxelSizeHalf; z < maxZ; z += voxelSize)
		            {
			            Vector3 currentVoxel = new Vector3(x, y, z);
			            
			            if (IntersectsBox(p1, p2, p3, currentVoxel, voxelExtends))
			            {
				            this.add(matrix.MultiplyPoint3x4(currentVoxel), clr);
				            i_count++;
			            }
//	                    UnityEngine.Debug.Log("mX: " + minX + " MX: " + maxX + " mY: " + minY + " MY: " + maxY + " mZ: " + 
//	                                      minZ +" MZ: " + maxZ + " x: " + x + " y: " + y + " z: " + z);
			            ni_count++;
                    }
                }
            }
        }
	    
	    UnityEngine.Debug.Log("Intersect count: " + i_count);
	    UnityEngine.Debug.Log("Not interesect count: " + ni_count);
	    UnityEngine.Debug.Log("Total count: " + (i_count + ni_count));
    }

    /*
     * Method for traversing entire Octree data structure.
     */
    public List<Vector3> getAllPoints()
    {
        if(root == null)
        {
            return null;
        }

        return root.getPositions();
    }

    public List<Color32> getAllColors()
    {
        if (root == null)
        {
            return null;
        }

        return root.getColors();
    }

    public void printBoundaries()
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