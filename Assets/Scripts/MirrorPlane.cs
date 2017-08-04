using UnityEngine;
using System.Collections;

/// <summary>
/// Represents the virtual mirror plane between both voxelspaces.
/// Only "valid" for moved in Z direction.
/// </summary>
public class MirrorPlane : MonoBehaviour {

    protected MREPManager manager;

    // Use this for initialization
    void Start () {
        manager = GameObject.FindObjectOfType<MREPManager>();
        gameObject.transform.Translate(manager.mirrorOffset.x, manager.mirrorOffset.y, (float)(0.5 * manager.mirrorOffset.z));
    }
	
}
