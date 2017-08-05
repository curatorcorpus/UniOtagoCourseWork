using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System;

public class MeshObject : MonoBehaviour
{
    private static int MAX_VERTS = 65534;

    private Mesh mesh;

    private List<Vector3> vertices;
    private List<Color32> colors;
    private List<Vector3> normals;
    private List<int> index;

    private int[] indices;

    private static Vector3 voxelSpaceSize;

    // Use this for initialization
    void Start ()
    {/*
        voxelSpaceSize = new Vector3();

        emptyverts = new List<Vector3>(manager.maxMeshSize);
        emptycols = new List<Color32>(manager.maxMeshSize);
        vertices = new List<Vector3>(manager.maxMeshSize);
        colors = new List<Color32>(manager.maxMeshSize);
        normals = new List<Vector3>(manager.maxMeshSize);
        index = new List<int>(manager.maxMeshSize);
        indices = new int[manager.maxMeshSize];*//*
        fillArrays();

        mesh = new Mesh();
        mesh.name = "Mesh";      
        mesh.SetVertices(emptyverts);
        mesh.SetColors(emptycols);
        mesh.SetIndices(indices, MeshTopology.Points, 0);       
        mesh.bounds = new Bounds(Vector3.zero, voxelSpaceSize*2);

        gameObject.GetComponent<MeshFilter>().mesh = mesh;*/
    }

    void fillArrays()
    {
        Vector3 notSet = new Vector3(1000, 1000, 1000);
        /*
        for(int i = 0; i < manager.maxMeshSize; i++)
        {
            emptyverts.Add(notSet);
            emptycols.Add(Color.black);
            index.Add(i);
            indices[i] = i;
        }*/
    }

    public void updateMesh(Voxel voxel)
    {
        /*
        if(voxel != null)
        {
            vertices.Clear();
            colors.Clear();
        }

        mesh.Clear();
        mesh.SetVertices(verts);
        mesh.SetColors(cols);
        mesh.SetIndices(index.GetRange(0,verts.Count).ToArray(), MeshTopology.Points, 0);*/
    }

    /// <summary>
    /// updates the Mesh with the given vertices and colors
    /// </summary>
    /// <param name="verts"></param>
    /// <param name="cols"></param>
    public void updateMesh(List<Vector3> verts, List<Color32> cols)
    {/*
        if(vertices != null)
        {
            vertices.Clear();
            colors.Clear();
        }

        mesh.Clear();
        mesh.SetVertices(verts);
        mesh.SetColors(cols);
        mesh.SetIndices(index.GetRange(0,verts.Count).ToArray(), MeshTopology.Points, 0);*/
    }

    public void updateMesh(List<Vector3> verts, List<Color32> cols, List<Vector3> norms)
    {
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
        mesh.SetIndices(index.GetRange(0, verts.Count).ToArray(), MeshTopology.Points, 0);
    }
}

