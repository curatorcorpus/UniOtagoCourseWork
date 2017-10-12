using UnityEngine;
using System;
using System.IO;
using System.Collections.Generic;
using System.Runtime.Serialization.Formatters.Binary;

[Serializable]
public struct SerializableVector3
{
    public float x, y, z;
     
    public SerializableVector3(float x, float y, float z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
     
    public static implicit operator Vector3(SerializableVector3 position)
    {
        return new Vector3(position.x, position.y, position.z);
    }
     
    public static implicit operator SerializableVector3(Vector3 position)
    {
        return new SerializableVector3(position.x, position.y, position.z);
    }
}

[Serializable]
public struct SerializableColor32
{
    public byte r, g, b;
     
    public SerializableColor32(byte r, byte g, byte b)
    {
        this.r = r;
        this.g = g;
        this.b = b;
    }
     
    public static implicit operator Color32(SerializableColor32 color)
    {
        return new Color32(color.r, color.g, color.b, 1);
    }
     
    public static implicit operator SerializableColor32(Color32 color)
    {
        return new SerializableColor32(color.r, color.g, color.b);
    }
}

[Serializable]
public class VoxelData
{
    private List<SerializableVector3> voxels;
    private List<SerializableColor32> colors;

    private List<SerializableVector3> Voxels
    {
        get { return voxels; }
        set { this.voxels = value; }
    }
    
    private List<SerializableColor32> Colors
    {
        get { return colors; }
        set { this.colors = value; }
    }

    public VoxelData()
    {
        this.voxels = new List<SerializableVector3>();
        this.colors = new List<SerializableColor32>();
    }
    
    public VoxelData(List<SerializableVector3> voxels, List<SerializableColor32> colors)
    {
        this.voxels = voxels;
        this.colors = colors;
    }

    public void AddToVoxelList(SerializableVector3 voxelPos)
    {
        voxels.Add(voxelPos);
    }
    
    public void AddToColorList(SerializableColor32 voxelColors)
    {
        colors.Add(voxelColors);
    }
}

public class VoxelSerializer
{   
    private static string filePath = "Assets/Resources/Saves/";
    
    public static void saveModel(string modelName, VoxelData voxelData)
    {
        // check if directory exists.
        if (!Directory.Exists(filePath))
            Directory.CreateDirectory(filePath);

        string filename = modelName + "_" + System.DateTime.Now.ToString("yyyyMMdd_hhmmss");
        
        // create serializer.
        BinaryFormatter formatter = new BinaryFormatter();
        
        // create binary save file.
        FileStream saveFile = File.Create(filePath + filename + ".arnold");
        
        // serialize list of ver
        formatter.Serialize(saveFile, voxelData);
        saveFile.Close();
        
        Debug.Log("Clip saved to: " + filename);
    }

    public static void loadModel()
    {
        
    }
}