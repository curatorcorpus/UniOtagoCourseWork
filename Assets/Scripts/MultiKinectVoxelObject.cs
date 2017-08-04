using UnityEngine;
using System.Collections;


/// <summary>
///The VoxelObject of this type is used to display the voxels captured by the MultiKinect Setup.
/// </summary>
public class MultiKinectVoxelObject : VoxelObject
{
    [HideInInspector]
    public bool capture = false;

    new void LateUpdate()
    {
        if (updated)
        {
            capture = true;
            voxelToMesh();
            updated = false;
        }

        if (gameObject.transform.hasChanged)
            mirrorTransform();
    }
}
