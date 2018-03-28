using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class incSpeed : MonoBehaviour {

    // Use this for initialization
    void Start()
    {
        transform.parent = GameObject.FindGameObjectWithTag("GameController").transform;
        transform.GetChild(0).localPosition = new Vector3(2.0f, 1f, 0.3f);
    }
	
	// Update is called once per frame
	void Update () {
        //transform.parent = GameObject.FindGameObjectWithTag("GameController").transform;
    }
}
