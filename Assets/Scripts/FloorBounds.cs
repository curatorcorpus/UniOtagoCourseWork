using UnityEngine;
using System.Collections;


/// <summary>
/// Displays the boundaries of the current voxelspace.
/// </summary>
public class FloorBounds : MonoBehaviour {

    protected MREPManager manager;

    // Use this for initialization
    void Start () {

        manager = GameObject.FindObjectOfType<MREPManager>();
        Vector3 size = new Vector3(manager.spaceWidth * manager.voxelSize, manager.spaceHeight* manager.voxelSize, manager.spaceDepth *  manager.voxelSize);
        gameObject.transform.localScale = size;
        if (gameObject.name == "MirroredFloorBounds")
            gameObject.transform.Translate(manager.mirrorOffset.x, manager.mirrorOffset.y, -manager.mirrorOffset.z);

        
    }
	
}
