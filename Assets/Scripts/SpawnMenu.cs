using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using HoloToolkit.Unity.InputModule;
using System.IO;

public class SpawnMenu : MonoBehaviour, IInputHandler
{
    public string location;
    public GameObject spawn;
    private bool loaded = false;

    private GameObject[] items;

    private float y = 1.25f;

    public void OnInputDown(InputEventData e)
    {
        if (loaded)
        {
            foreach(GameObject o in items)
            {
                Destroy(o);
            }
            loaded = false;
            y = 1.25f;
            return;
        }
        string[] files = Directory.GetFiles(location);
        items = new GameObject[files.Length];
        int count = 0;
        foreach(string s in files)
        {
            if (Path.GetExtension(s) == ".obj")
            {
                GameObject o = Instantiate(spawn);
                o.transform.parent = gameObject.transform;
                o.transform.localPosition = new Vector3(0, y, 0);
                y += 1.25f;
                o.GetComponent<LoadModel>().file =s;
                items[count++] = o;
            }
        }
        loaded = true;
    }

    public void OnInputUp(InputEventData e)
    {

    }
}
