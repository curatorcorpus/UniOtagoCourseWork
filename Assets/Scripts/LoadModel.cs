using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using HoloToolkit.Unity.InputModule;
using HoloToolkit.Unity.SpatialMapping;
using System.IO;

public class LoadModel : MonoBehaviour, IInputHandler {

    public string file;
    GameObject g;

    void Start()
    {
        gameObject.GetComponentInChildren<TextMesh>().text = Path.GetFileNameWithoutExtension(file);
    }

    public void OnInputDown(InputEventData e)
    {
        GameObject o = OBJLoader.LoadOBJFile(file);
        foreach (MeshFilter m in o.GetComponentsInChildren<MeshFilter>())
        {
            if (m.name == "g")
            {
                g = m.gameObject;
            }
        }
        g.AddComponent<MeshCollider>();
        g.AddComponent<TapToPlace>();

    }

    public void OnInputUp(InputEventData e)
    {
        g.GetComponent<TapToPlace>().IsBeingPlaced = true;
    }
}
