using UnityEngine;
using System.Collections;
using System.Collections.Generic;
/// <summary>
/// VoxelSpace defines a grid of <see cref="Voxel"/> with a given voxel size and numbers of voxel in widht, height and depth
/// </summary>
public class VoxelSpace {

    public bool[,,] voxelSet;
    public int width;
    public int height;
    public int depth;
    public float voxelSize;
    public Vector3 origin;
    public List<Vector3> positions;
    public List<Vector3> mirroredPositions;
    public List<Color32> colors;
    public List<Vector3> normals;
    //public List<Voxel> voxel;
    /// <summary>
    /// Creates a new Voxelspace
    /// </summary>
    /// <param name="width">Number of Voxel in width</param>
    /// <param name="height">Number of Voxel in height</param>
    /// <param name="depth">Number of Voxel in depth</param>
    /// <param name="voxelSize">Size of one Voxel</param>
    /// <param name="origin">Origin of VoxelSpace in world coordinates</param>
    public VoxelSpace(int width, int height, int depth, float voxelSize, Vector3 origin)
    {
        this.origin = origin - new Vector3(voxelSize, voxelSize, voxelSize);
        this.width = width + 2;
        this.height = height + 2;
        this.depth = depth + 2;
        this.voxelSize = voxelSize;
        this.positions = new List<Vector3>(width * height * depth);
        this.mirroredPositions = new List<Vector3>(width * height * depth);
        this.colors = new List<Color32>(width * height * depth);
        this.normals = new List<Vector3>(width * height * depth);
        //this.voxel = new List<Voxel>(width * height * depth);
        voxelSet = new bool[width, height, depth];

        //initalize every Voxel with id = 0 (not set) and color black

        //for(int x = 0; x < width; x++)
        //{
        //    for(int y = 0; y < height; y++)
        //    {
        //        for(int z = 0; z < depth; z++)
        //        {
        //            grid[x, y, z] = new Voxel();
        //        }
        //    }
        //}
    }

    public void setOrigin(Vector3 origin)
    {
        this.origin = origin;
    }

    /// <summary>
    /// Sets the color and id of Voxel at Postion x,y,z 
    /// </summary>
    /// <param name="x"></param>
    /// <param name="y"></param>
    /// <param name="z"></param>
    /// <param name="r"></param>
    /// <param name="g"></param>
    /// <param name="b"></param>
    /// <param name="id"></param>
    //public void setVoxelAt(int x, int y, int z, byte r, byte g, byte b, byte id)
    //{
    //    grid[x, y, z].setColor(r, g, b);
    //    grid[x, y, z].id = id;
    //}

    public Vector3 getCenter()
    {
        return origin + new Vector3(width / 2 * voxelSize, height / 2 * voxelSize, depth / 2 * voxelSize);
    }

    public Vector3 getSpaceCenter(int x, int y, int z)
    {
        return new Vector3(voxelSize * (x + 1 / 2 - width / 2), voxelSize * (y + 1 / 2 - height / 2), voxelSize * (z + 1 / 2 - depth / 2));
        //return new Vector3(cubeSize * (x - width / 2), cubeSize * (y - height / 2), cubeSize * (z - depth / 2));
    }
           private float minOfProjectionOnAxis(Vector3[] solid, Vector3 axis)
        {

            float min = float.MaxValue;
            float dotProd;

            for (int i = 0; i < solid.Length; ++i)
            {
                dotProd = Vector3.Dot(solid[i], axis);
                if (dotProd < min)
                    min = dotProd;
            }

            return min;
        }

        private float maxOfProjectionOnAxis(Vector3[] solid, Vector3 axis)
        {

            float max = float.MinValue;
            float dotProd;

            for (int i = 0; i < solid.Length; ++i)
            {
                dotProd = Vector3.Dot(solid[i], axis);
                if (dotProd > max)
                    max = dotProd;
            }

            return max;
        }

        private bool projectionsIntersectOnAxis(Vector3[] solidA, Vector3[] solidB, Vector3 axis)
        {
            float minSolidA = minOfProjectionOnAxis(solidA, axis);
            float maxSolidA = maxOfProjectionOnAxis(solidA, axis);
            float minSolidB = minOfProjectionOnAxis(solidB, axis);
            float maxSolidB = maxOfProjectionOnAxis(solidB, axis);

            if (minSolidA > maxSolidB)
                return false;
            if (maxSolidA < minSolidB)
                return false;

            return true;

        }

        private Vector3[] getAABCCorners(int x, int y, int z)
        {
            Vector3 center = new Vector3(x * voxelSize + voxelSize / 2f, y * voxelSize + voxelSize / 2f, z * voxelSize + voxelSize / 2f);     //CHECK this, all of this
            Vector3[] corners = new Vector3[8];

            corners[0] = new Vector3(center.x + voxelSize, center.y - voxelSize, center.z + voxelSize) + origin;
            corners[1] = new Vector3(center.x + voxelSize, center.y - voxelSize, center.z - voxelSize) + origin;
            corners[2] = new Vector3(center.x - voxelSize, center.y - voxelSize, center.z - voxelSize) + origin;
            corners[3] = new Vector3(center.x - voxelSize, center.y - voxelSize, center.z + voxelSize) + origin;
            corners[4] = new Vector3(center.x + voxelSize, center.y + voxelSize, center.z + voxelSize) + origin;
            corners[5] = new Vector3(center.x + voxelSize, center.y + voxelSize, center.z - voxelSize) + origin;
            corners[6] = new Vector3(center.x - voxelSize, center.y + voxelSize, center.z - voxelSize) + origin;
            corners[7] = new Vector3(center.x - voxelSize, center.y + voxelSize, center.z + voxelSize) + origin;

            return corners;

        }

        private bool triangleIntersectAABC(Vector3[] triangle, int x, int y, int z)
        {
            Vector3[] aabcCorners;
            Vector3 triangleEdgeA, triangleEdgeB, triangleEdgeC, triangleNormal;
            Vector3 aabcEdgeA = new Vector3(1, 0, 0);
            Vector3 aabcEdgeB = new Vector3(0, 1, 0);
            Vector3 aabcEdgeC = new Vector3(0, 0, 1);

            aabcCorners = getAABCCorners(x, y, z);

            triangleEdgeA = triangle[1] - triangle[0];
            triangleEdgeB = triangle[2] - triangle[1];
            triangleEdgeC = triangle[0] - triangle[2];

            if (!projectionsIntersectOnAxis(aabcCorners, triangle, Vector3.Cross(triangleEdgeA, aabcEdgeA))) return false;
            if (!projectionsIntersectOnAxis(aabcCorners, triangle, Vector3.Cross(triangleEdgeA, aabcEdgeB))) return false;
            if (!projectionsIntersectOnAxis(aabcCorners, triangle, Vector3.Cross(triangleEdgeA, aabcEdgeC))) return false;
            if (!projectionsIntersectOnAxis(aabcCorners, triangle, Vector3.Cross(triangleEdgeB, aabcEdgeA))) return false;
            if (!projectionsIntersectOnAxis(aabcCorners, triangle, Vector3.Cross(triangleEdgeB, aabcEdgeB))) return false;
            if (!projectionsIntersectOnAxis(aabcCorners, triangle, Vector3.Cross(triangleEdgeB, aabcEdgeC))) return false;
            if (!projectionsIntersectOnAxis(aabcCorners, triangle, Vector3.Cross(triangleEdgeC, aabcEdgeA))) return false;
            if (!projectionsIntersectOnAxis(aabcCorners, triangle, Vector3.Cross(triangleEdgeC, aabcEdgeB))) return false;
            if (!projectionsIntersectOnAxis(aabcCorners, triangle, Vector3.Cross(triangleEdgeC, aabcEdgeC))) return false;

            triangleNormal = Vector3.Cross(triangleEdgeA, triangleEdgeB);
            if (!projectionsIntersectOnAxis(aabcCorners, triangle, triangleNormal)) return false;

            if (!projectionsIntersectOnAxis(aabcCorners, triangle, aabcEdgeA)) return false;
            if (!projectionsIntersectOnAxis(aabcCorners, triangle, aabcEdgeB)) return false;
            if (!projectionsIntersectOnAxis(aabcCorners, triangle, aabcEdgeC)) return false;

            return true;
        }

        public void fillGridFromObject(GameObject gameObject)
        {
            Mesh objMesh = gameObject.GetComponent<MeshFilter>().mesh;
            //objMesh.RecalculateNormals();
            Transform objTransfom = gameObject.transform;
            Vector3[] triangle = new Vector3[3];
            
            Vector3[] meshVerts = objMesh.vertices;
            int[] meshTris = objMesh.triangles;
            //Debug.Log("Topology: "  + objMesh.GetTopology(0));
            Color32[] meshCols = objMesh.colors32;
            Vector3[] meshNormals = objMesh.normals;
            int triangleCount = meshTris.Length / 3;
            int startX, startY, startZ;
            int endX, endY, endZ;
            
            //Debug.Log("colors: " + meshCols.Length);
            //Debug.Log("Time: " + start);

            //for each triangle perform SAT intersection check with the cubes and the triangles AABB (axis aligned bouding box)

            for (int i = 0; i < triangleCount; ++i)
            {
                triangle[0] = objTransfom.TransformPoint(meshVerts[meshTris[i * 3]]);       //first vertex from triangle transformed in world coordinates
                triangle[1] = objTransfom.TransformPoint(meshVerts[meshTris[i * 3 + 1]]);
                triangle[2] = objTransfom.TransformPoint(meshVerts[meshTris[i * 3 + 2]]);

                //find the triangle AABB, select a subgrid??
                startX = (int)Mathf.Floor((Mathf.Min(triangle[0].x, triangle[1].x, triangle[2].x) - origin.x) / voxelSize);
                startY = (int)Mathf.Floor((Mathf.Min(triangle[0].y, triangle[1].y, triangle[2].y) - origin.y) / voxelSize);
                startZ = (int)Mathf.Floor((Mathf.Min(triangle[0].z, triangle[1].z, triangle[2].z) - origin.z) / voxelSize);

                endX = (int)Mathf.Ceil((Mathf.Max(triangle[0].x, triangle[1].x, triangle[2].x) - origin.x) / voxelSize);
                endY = (int)Mathf.Ceil((Mathf.Max(triangle[0].y, triangle[1].y, triangle[2].y) - origin.y) / voxelSize);
                endZ = (int)Mathf.Ceil((Mathf.Max(triangle[0].z, triangle[1].z, triangle[2].z) - origin.z) / voxelSize);

                for (int x = startX; x < endX; ++x)
                {
                    for (int y = startY; y < endY; ++y)
                    {
                        for (int z = startZ; z < endZ; ++z)
                        {
                        if (x<0 || x>=width || y<0 || y>=height || z<0 || z >= depth)   //continue if outside of the voxelspace
                            continue;
                            if (!voxelSet[x, y, z] && triangleIntersectAABC(triangle, x, y, z))
                            {
                               Vector3 cubeCenter = getSpaceCenter(x, y, z) + getCenter() + new Vector3(voxelSize / 2f, voxelSize / 2f, voxelSize / 2f);
                               positions.Add(cubeCenter);
                               mirroredPositions.Add(new Vector3(cubeCenter.x, cubeCenter.y, -cubeCenter.z));
                               if (meshCols.Length > 0)
                                colors.Add(meshCols[meshTris[i * 3]]);                   //color of the first vertex of the triangle
                               if (meshNormals.Length > 0)
                               {
                                    Vector3 n1, n2, n3;
                                    n1 = objTransfom.TransformDirection(meshNormals[meshTris[i * 3]]);
                                    n2 = objTransfom.TransformDirection(meshNormals[meshTris[i * 3 + 1]]);
                                    n3 = objTransfom.TransformDirection(meshNormals[meshTris[i * 3 + 2]]);
                                    //Vector3 normal = (meshNormals[meshTris[i * 3]]);
                                    Vector3 normal = (n1 + n2 + n3) / 3.0f;
                                    //colors.Add(new Color(normal.x, normal.y, normal.z, 1));                    //color of the first vertex of the triangle
                                    normals.Add(normal);                                                         //normal of the first vertex of the triangle
                               }
                               voxelSet[x, y, z] = true;
                            }
                        }
                    }
                }
            }
        }
}
