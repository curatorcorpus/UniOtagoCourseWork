using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class VoxelRecordingController : MonoBehaviour {

	public VirtualBodyObject[] recordings;

	// Use this for initialization
	void Start () {
		this.recordings = new VirtualBodyObject[5];
	}
	
	// Update is called once per frame
	void Update () {

		if(Input.GetKeyDown(KeyCode.Alpha0))
		{
		}
		else if(Input.GetKeyDown(KeyCode.Alpha1)) 
		{
		}
		else if(Input.GetKeyDown(KeyCode.Alpha2)) 
		{
		}
		else if(Input.GetKeyDown(KeyCode.Alpha3))
		{
		}
		else if(Input.GetKeyDown(KeyCode.Alpha4))
		{
		}
	}
}
