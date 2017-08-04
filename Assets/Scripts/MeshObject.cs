using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System;
//using System.Linq;
/// <summary>
/// MeshObject is a GameObject with one Mesh
/// </summary>
public class MeshObject : MonoBehaviour {

    private MREPManager manager;
    private Mesh mesh;
    private List<Vector3> emptyverts;
    private List<Color32> emptycols;
    private List<Vector3> vertices;
    private List<Color32> colors;
    private List<Vector3> normals;
    private List<int> index;
    private int[] indices;
    //private static Vector3 voxelSpaceOrigin;
    private static Vector3 voxelSpaceSize;
    //private static Vector3 voxelSpaceCenter;

    // Use this for initialization
    void Start () {
        //emptyverts and emptycols are used to fill up to maxmeshsize with vertices getting clipped (hopefully, to check) and color black
        manager = GameObject.FindObjectOfType<MREPManager>();
        //voxelSpaceOrigin = manager.voxelspace.origin;
        voxelSpaceSize = new Vector3(manager.voxelspace.voxelSize * manager.voxelspace.width, manager.voxelspace.voxelSize * manager.voxelspace.height, manager.voxelspace.voxelSize * manager.voxelspace.depth);
        //voxelSpaceCenter = manager.voxelspace.getCenter();
        emptyverts = new List<Vector3>(manager.maxMeshSize);
        emptycols = new List<Color32>(manager.maxMeshSize);
        vertices = new List<Vector3>(manager.maxMeshSize);
        colors = new List<Color32>(manager.maxMeshSize);
        normals = new List<Vector3>(manager.maxMeshSize);
        index = new List<int>(manager.maxMeshSize);
        indices = new int[manager.maxMeshSize];
        fillArrays();
        //emptyverts = Enumerable.Repeat<Vector3>(new Vector3(1000, 1000, 1000), maxMeshSize).ToList<Vector3>();
        //emptycols = Enumerable.Repeat<Color32>(Color.black, maxMeshSize).ToList<Color32>();

        mesh = new Mesh();
        mesh.name = "Mesh";      
        mesh.SetVertices(emptyverts);
        mesh.SetColors(emptycols);
        //mesh.SetNormals(emptyverts);
        mesh.SetIndices(indices, MeshTopology.Points, 0);       
        //mesh.SetIndices(Enumerable.Range(0, maxMeshSize).ToArray<int>(), MeshTopology.Points, 0);
        mesh.bounds = new Bounds(Vector3.zero, voxelSpaceSize*2);

        gameObject.GetComponent<MeshFilter>().mesh = mesh;
    }

    void fillArrays()
    {
        Vector3 notSet = new Vector3(1000, 1000, 1000);
        
        for(int i = 0; i < manager.maxMeshSize; i++)
        {
            emptyverts.Add(notSet);
            emptycols.Add(Color.black);
            index.Add(i);
            indices[i] = i;
        }
    }

    /// <summary>
    /// updates the Mesh with the given vertices and colors
    /// </summary>
    /// <param name="verts"></param>
    /// <param name="cols"></param>
    public void updateMesh(List<Vector3> verts, List<Color32> cols)
    {
        //mesh.Clear();
        if(vertices != null)
        {
            vertices.Clear();
            colors.Clear();
        }
        //if(verts.Count != cols.Count)
        //    Debug.Log("Size difference! VertexCount: " + verts.Count + " ColorsCount " + cols.Count);
        //if (verts.Count > manager.maxMeshSize || cols.Count > manager.maxMeshSize)
        //    Debug.Log("Size to big! VertexCount: " + verts.Count + " ColorsCount " + cols.Count);
        //vertices.AddRange(emptyverts);
        //colors.AddRange(emptycols);
        //vertices.RemoveRange(0, verts.Count);
        //colors.RemoveRange(0, cols.Count);
        //vertices.InsertRange(0, verts);
        //colors.InsertRange(0, cols);

        //int count = verts.Count;
        //if (count < maxMeshSize)
        //{
        //    int diff = maxMeshSize - count;
        //    vertices.AddRange(emptyverts.GetRange(count, diff));
        //    colors.AddRange(emptycols.GetRange(count, diff));
        //}

        mesh.Clear();
        mesh.SetVertices(verts);
        mesh.SetColors(cols);
        
        //mesh.RecalculateBounds();
        mesh.SetIndices(index.GetRange(0,verts.Count).ToArray(), MeshTopology.Points, 0);
    }

    public void updateMesh(List<Vector3> verts, List<Color32> cols, List<Vector3> norms)
    {
        //mesh.Clear();
        if (vertices != null)
        {
            vertices.Clear();
            colors.Clear();
            normals.Clear();
        }

        mesh.Clear();
        mesh.SetVertices(verts);
        mesh.SetColors(cols);
        mesh.SetNormals(norms);
        //mesh.RecalculateBounds();
        mesh.SetIndices(index.GetRange(0, verts.Count).ToArray(), MeshTopology.Points, 0);

    }
}

