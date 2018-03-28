using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class GainMovement : MonoBehaviour {
    public float xM = 1f;
    public float yM = 1f;
    private float x;
    private float y;
    private GameObject space;
	// Use this for initialization
	void Start () {
		space = GameObject.FindGameObjectWithTag("Finish");
        x = space.transform.position.x;
        y = space.transform.position.y;
	}
	
	// Update is called once per frame
	void Update () {
        float xDiff = x - space.transform.position.x;
        float yDiff = y - space.transform.position.y;
        space.transform.position = space.transform.position + new Vector3(xM * xDiff, yM * yDiff, 0);
        x = space.transform.position.x;
        y = space.transform.position.y;
    }
}
