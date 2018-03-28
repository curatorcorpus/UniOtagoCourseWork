using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Trigger : MonoBehaviour {
	
	// Update is called once per frame
	private void Update ()
    {
        if(OVRInput.Get(OVRInput.Touch.PrimaryIndexTrigger))
        {
            Debug.Log("Workign index trigger");
        }
        if (OVRInput.Get(OVRInput.Axis1D.PrimaryHandTrigger) > 0)
        {
            Debug.Log("Workign hand trigger");
        }
    }
}
