using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using HoloToolkit.Unity.InputModule;

public class ScaleTap : MonoBehaviour, IInputHandler
{
    public bool x;
    public bool y;
    public bool z;
    public bool UP;
    public GameObject o;

    public void OnInputDown(InputEventData e)
    {
        if (UP)
        {
            o.transform.localScale = o.transform.localScale + new Vector3(x ? 0.005f : 0f, y ? 0.005f : 0f, z ? 0.005f : 0f);
        }
        else
        {
            o.transform.localScale = o.transform.localScale - new Vector3(x ? 0.005f : 0f, y ? 0.005f : 0f, z ? 0.005f : 0f);
        }
    }

    public void OnInputUp(InputEventData e)
    {

    }


}
