using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Rotate : MonoBehaviour {

	// Use this for initialization
	void Start () {
		
	}
	
	// Update is called once per frame
	void Update () {
        OVRInput.Update();
        Vector2 dir = OVRInput.Get(OVRInput.Axis2D.Any, OVRInput.Controller.LTouch);
        if (Input.GetKey(KeyCode.RightArrow))
        {
            transform.Rotate(new Vector3(0, 0.75f, 0));
        }else if (Input.GetKey(KeyCode.LeftArrow))
        {
            transform.Rotate(new Vector3(0, -0.75f, 0));
        }
        else if (Input.GetKey(KeyCode.UpArrow))
        {
            transform.position = transform.position + 0.01f*transform.forward;
        }
        else if (Input.GetKey(KeyCode.DownArrow))
        {
            transform.position = transform.position - 0.01f * transform.forward;
        }
        else if (Input.GetKey(KeyCode.X))
        {
            GameObject.FindGameObjectWithTag("Finish").transform.localPosition = GameObject.FindGameObjectWithTag("Finish").transform.localPosition + new Vector3(0, 0.01f, 0);
        }
        else if (Input.GetKey(KeyCode.Z))
        {
            GameObject.FindGameObjectWithTag("Finish").transform.localPosition = GameObject.FindGameObjectWithTag("Finish").transform.localPosition + new Vector3(0, -0.01f, 0);
        }
        Vector3 pos = GameObject.FindGameObjectWithTag("MainCamera").transform.localPosition;
        if (abs(pos.x) < 0.5 && abs(pos.z) < 1.0 && abs(pos.y) < 1.45 && abs(pos.y) > 0.85)
        {
            transform.position = transform.position + 0.01f * dir.y * transform.forward;
            transform.Rotate(new Vector3(0, 0.5f * dir.x, 0));
        }
    }

    float abs(float a)
    {
        return Mathf.Abs(a);
    }
}
