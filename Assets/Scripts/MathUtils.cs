using System;
using UnityEngine;

public class MathUtils
{
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
		if( Math.Max( -Fmax( p0, p1, p2 ), Fmin( p0, p1, p2 ) ) > r )
		{
			return false;
		}
	
		// Test axis a01
		var a01 = new Vector3( 0, -f1.z, f1.y );
		p0 = Vector3.Dot( v0, a01 );
		p1 = Vector3.Dot( v1, a01 );
		p2 = Vector3.Dot( v2, a01 );
		r = boxExtents.y * Math.Abs( f1.z ) + boxExtents.z * Math.Abs( f1.y );
		if( Math.Max( -Fmax( p0, p1, p2 ), Fmin( p0, p1, p2 ) ) > r )
		{
			return false;
		}
	
		// Test axis a02
		var a02 = new Vector3( 0, -f2.z, f2.y );
		p0 = Vector3.Dot( v0, a02 );
		p1 = Vector3.Dot( v1, a02 );
		p2 = Vector3.Dot( v2, a02 );
		r = boxExtents.y * Math.Abs( f2.z ) + boxExtents.z * Math.Abs( f2.y );
		if( Math.Max( -Fmax( p0, p1, p2 ), Fmin( p0, p1, p2 ) ) > r )
		{
			return false;
		}
	
		// Test axis a10
		var a10 = new Vector3( f0.z, 0, -f0.x );
		p0 = Vector3.Dot( v0, a10 );
		p1 = Vector3.Dot( v1, a10 );
		p2 = Vector3.Dot( v2, a10 );
		r = boxExtents.x * Math.Abs( f0.z ) + boxExtents.z * Math.Abs( f0.x );
		if( Math.Max( -Fmax( p0, p1, p2 ), Fmin( p0, p1, p2 ) ) > r )
		{
			return false;
		}
	
		// Test axis a11
		var a11 = new Vector3( f1.z, 0, -f1.x );
		p0 = Vector3.Dot( v0, a11 );
		p1 = Vector3.Dot( v1, a11 );
		p2 = Vector3.Dot( v2, a11 );
		r = boxExtents.x * Math.Abs( f1.z ) + boxExtents.z * Math.Abs( f1.x );
		if( Math.Max( -Fmax( p0, p1, p2 ), Fmin( p0, p1, p2 ) ) > r )
		{
			return false;
		}
	
		// Test axis a12
		var a12 = new Vector3( f2.z, 0, -f2.x );
		p0 = Vector3.Dot( v0, a12 );
		p1 = Vector3.Dot( v1, a12 );
		p2 = Vector3.Dot( v2, a12 );
		r = boxExtents.x * Math.Abs( f2.z ) + boxExtents.z * Math.Abs( f2.x );
		if( Math.Max( -Fmax( p0, p1, p2 ), Fmin( p0, p1, p2 ) ) > r )
		{
			return false;
		}
	
		// Test axis a20
		var a20 = new Vector3( -f0.y, f0.x, 0 );
		p0 = Vector3.Dot( v0, a20 );
		p1 = Vector3.Dot( v1, a20 );
		p2 = Vector3.Dot( v2, a20 );
		r = boxExtents.x * Math.Abs( f0.y ) + boxExtents.y * Math.Abs( f0.x );
		if( Math.Max( -Fmax( p0, p1, p2 ), Fmin( p0, p1, p2 ) ) > r )
		{
			return false;
		}
	
		// Test axis a21
		var a21 = new Vector3( -f1.y, f1.x, 0 );
		p0 = Vector3.Dot( v0, a21 );
		p1 = Vector3.Dot( v1, a21 );
		p2 = Vector3.Dot( v2, a21 );
		r = boxExtents.x * Math.Abs( f1.y ) + boxExtents.y * Math.Abs( f1.x );
		if( Math.Max( -Fmax( p0, p1, p2 ), Fmin( p0, p1, p2 ) ) > r )
		{
			return false;
		}
	
		// Test axis a22
		var a22 = new Vector3( -f2.y, f2.x, 0 );
		p0 = Vector3.Dot( v0, a22 );
		p1 = Vector3.Dot( v1, a22 );
		p2 = Vector3.Dot( v2, a22 );
		r = boxExtents.x * Math.Abs( f2.y ) + boxExtents.y * Math.Abs( f2.x );
		if( Math.Max( -Fmax( p0, p1, p2 ), Fmin( p0, p1, p2 ) ) > r )
		{
			return false;
		}
	
		#endregion 
	
		#region Test the three axes corresponding to the face normals of AABB b (category 1)
	
		// Exit if...
		// ... [-extents.x, extents.x] and [min(v0.x,v1.x,v2.x), max(v0.x,v1.x,v2.x)] do not overlap
		if( Fmax( v0.x, v1.x, v2.x ) < -boxExtents.x || Fmin( v0.x, v1.x, v2.x ) > boxExtents.x )
		{
			return false;
		}
	
		// ... [-extents.y, extents.y] and [min(v0.y,v1.y,v2.y), max(v0.y,v1.y,v2.y)] do not overlap
		if( Fmax( v0.y, v1.y, v2.y ) < -boxExtents.y || Fmin( v0.y, v1.y, v2.y ) > boxExtents.y )
		{
			return false;
		}
				
		// ... [-extents.z, extents.z] and [min(v0.z,v1.z,v2.z), max(v0.z,v1.z,v2.z)] do not overlap
		if( Fmax( v0.z, v1.z, v2.z ) < -boxExtents.z || Fmin( v0.z, v1.z, v2.z ) > boxExtents.z )
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

	public static float Fmin( float a, float b, float c )
	{
		return Math.Min( a, Math.Min( b, c ) );
	}
	
	public static float Fmax( float a, float b, float c )
	{
		return Math.Max( a, Math.Max( b, c ) ); 
	}
	
	public static float ClipToVoxelGrid(float axisBound, float voxelSize, bool down)
	{
		axisBound -= (axisBound % voxelSize);
		
		if (down)
			return axisBound;
		
		return (axisBound + voxelSize);
	}

	public static float ClosetPow2(float size)
	{
		float ceil = Mathf.Pow(2, Mathf.Ceil(Mathf.Log(size)/Mathf.Log(2)));
		float floor = Mathf.Pow(2, Mathf.Floor(Mathf.Log(size)/Mathf.Log(2)));

		if ((size - floor) < (ceil - size))
		{
			return (float) floor;
            
		}
		return (float) ceil;
	} 
	
	public static int ClosetPow2(int size)
	{
		float ceil = Mathf.Pow(2, Mathf.Ceil(Mathf.Log(size)/Mathf.Log(2)));
		float floor = Mathf.Pow(2, Mathf.Floor(Mathf.Log(size)/Mathf.Log(2)));

		if ((size - floor) < (ceil - size))
		{
			return (int) floor;
            
		}
		return (int) ceil;
	} 
}