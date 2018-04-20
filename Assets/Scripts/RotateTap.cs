using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using HoloToolkit.Unity.InputModule;

public class RotateTap : MonoBehaviour, IInputHandler {
    public bool left;
    public GameObject o;

    public void OnInputDown(InputEventData e)
    {
        if (left)
        {
            o.transform.Rotate(0, 0.5f, 0);
        }
        else
        {
            o.transform.Rotate(0, -0.5f, 0);
        }
        e.Use();
    }

    public void OnInputUp(InputEventData e)
    {

    }


}
