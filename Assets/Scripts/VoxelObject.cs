using UnityEngine;
using System;
using System.Collections;
using System.Collections.Generic;

public abstract class VoxelObject : MonoBehaviour

{

    protected int maxMeshSize;

    protected List<Vector3> tmpPos;
    protected List<Vector3> tmpMpos;
    protected List<Color32> tmpCols;
    protected List<MeshObject> meshObjects;
    protected List<MeshObject> mirroredMeshObjects;
    protected List<Vector3> positions;
    protected List<Vector3> mirroredPositions;
    protected List<Vector3> normals;
    protected List<Color32> colors;

    [HideInInspector]
    public bool updated = true;
    public List<Vector3> Positions
    {
        get { return positions; }
        set { positions = value; }
    }

    public List<Color32> Colors
    {
        get { return colors; }
        set { colors = value; }
    }
    public List<Vector3> MirroredPositions
    {
        get { return mirroredPositions; }
        set { mirroredPositions = value; }
    }

    // Use this for initialization
    protected void Start ()
    {
       /* manager = GameObject.FindObjectOfType<MREPManager>();
        maxMeshSize = manager.maxMeshSize;

        meshObjects = new List<MeshObject>(2);
        meshObjects.AddRange(gameObject.GetComponentsInChildren<MeshObject>());
        mirroredMeshObjects = new List<MeshObject>(meshObjects.Count);

        for(int i = 0; i < meshObjects.Count; i++)
        {
            GameObject mirroredObj = Instantiate(meshObjects[0].gameObject);
            mirroredObj.name = "MirroredObject" + i;
            mirroredObj.transform.SetParent(transform);
            mirroredObj.transform.Translate(manager.mirrorOffset);
            mirroredMeshObjects.Add(mirroredObj.GetComponent<MeshObject>());
        }
        
        tmpPos = new List<Vector3>(meshObjects.Count * maxMeshSize);
        tmpMpos = new List<Vector3>(meshObjects.Count * maxMeshSize);
        tmpCols = new List<Color32>(meshObjects.Count * maxMeshSize);

        positions         = new List<Vector3>(meshObjects.Count * maxMeshSize);
        colors            = new List<Color32>(meshObjects.Count * maxMeshSize);
        mirroredPositions = new List<Vector3>(meshObjects.Count * maxMeshSize);
        */
        updated = true;
    }

    protected void LateUpdate()
    {
        if (updated)
        {
            voxelToMesh();
            updated = false;
        }
       // if (gameObject.transform.hasChanged)
    }

    public void setMeshesToActive()
    {
        for(int i = 0; i < meshObjects.Count; i++)
        {
            meshObjects[i].gameObject.SetActive(true);
        }
    }

    protected void voxelToMesh()
    {
        int count = positions.Count;
        int rest = count;

        decimal neededMeshes = Math.Ceiling((decimal)count / (decimal)maxMeshSize);
        neededMeshes = Math.Min(neededMeshes, meshObjects.Count);  // workaround for fixed number of meshobjects, if too many voxel they are discarded
        
        for(int i = 0; i < meshObjects.Count; i++)
        {    
            if(i < neededMeshes)
            {
                meshObjects[i].gameObject.SetActive(true);
                mirroredMeshObjects[i].gameObject.SetActive(true);

                tmpPos.Clear();
                tmpCols.Clear();
                tmpMpos.Clear();

                tmpPos.AddRange(positions);
                tmpCols.AddRange(colors);
                tmpMpos.AddRange(mirroredPositions);
            
                if (rest - maxMeshSize <= 0)
                {
                    tmpPos.RemoveRange(0, count - rest);
                    tmpCols.RemoveRange(0, count - rest);
                    tmpMpos.RemoveRange(0, count - rest);

                    meshObjects[i].updateMesh(tmpPos, tmpCols);
                    mirroredMeshObjects[i].updateMesh(tmpMpos, tmpCols);
                } else
                {
                    tmpPos.RemoveRange(0, i * maxMeshSize);
                    tmpPos.RemoveRange(maxMeshSize, tmpPos.Count - maxMeshSize);

                    tmpCols.RemoveRange(0, i * maxMeshSize);
                    tmpCols.RemoveRange(maxMeshSize, tmpCols.Count - maxMeshSize);

                    tmpMpos.RemoveRange(0, i * maxMeshSize);
                    tmpMpos.RemoveRange(maxMeshSize, tmpMpos.Count - maxMeshSize);

                    meshObjects[i].updateMesh(tmpPos, tmpCols);
                    mirroredMeshObjects[i].updateMesh(tmpMpos, tmpCols);
                }
                rest -= maxMeshSize;
            }  else
            {
                meshObjects[i].gameObject.SetActive(false);
                mirroredMeshObjects[i].gameObject.SetActive(false);
            }       

        }
    }
}
