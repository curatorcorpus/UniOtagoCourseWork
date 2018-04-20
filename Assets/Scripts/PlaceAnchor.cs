using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using HoloToolkit.Unity.InputModule;
using HoloToolkit.Unity.SpatialMapping;
using HoloToolkit.Unity;
using UnityEngine.XR.WSA;

public class PlaceAnchor : MonoBehaviour,IInputClickHandler {
    public GameObject small;
    public GameObject large;
    int anchs=0;

    // Use this for initialization
    void Start () {
        InputManager.Instance.PushFallbackInputHandler(gameObject);
    }
	
	// Update is called once per frame
	void Update () {
		
	}

    public virtual void OnInputClicked(InputClickedEventData eventData)
    {
        Debug.Log("Clicked");
        Transform cameraTransform = CameraCache.Main.transform;
        Vector3 placementPosition = GetPlacementPosition(cameraTransform.position, cameraTransform.forward, 5.0f);
        Debug.Log(placementPosition);
        if (anchs < 2)
        {
             Debug.Log("Anchs<2");
                small.GetComponent<AnchorStorage>().addAnchor(placementPosition);
                anchs++;
        }
        else if (anchs <50)
        {
            Debug.Log("Large");
            large.GetComponent<AnchorStorage>().addAnchor(placementPosition);
            anchs++;
        }
        Debug.Log(anchs);
        eventData.Use();
    }

    /// <summary>
    /// If we're using the spatial mapping, check to see if we got a hit, else use the gaze position.
    /// </summary>
    /// <returns>Placement position in front of the user</returns>
    private static Vector3 GetPlacementPosition(Vector3 headPosition, Vector3 gazeDirection, float defaultGazeDistance)
    {
        RaycastHit hitInfo;
        if (SpatialMappingRaycast(headPosition, gazeDirection, out hitInfo))
        {
            return hitInfo.point;
        }
        return GetGazePlacementPosition(headPosition, gazeDirection, defaultGazeDistance);
    }

    /// <summary>
    /// Does a raycast on the spatial mapping layer to try to find a hit.
    /// </summary>
    /// <param name="origin">Origin of the raycast</param>
    /// <param name="direction">Direction of the raycast</param>
    /// <param name="spatialMapHit">Result of the raycast when a hit occurred</param>
    /// <returns>Whether it found a hit or not</returns>
    private static bool SpatialMappingRaycast(Vector3 origin, Vector3 direction, out RaycastHit spatialMapHit)
    {
        if (SpatialMappingManager.Instance != null)
        {
            RaycastHit hitInfo;
            if (Physics.Raycast(origin, direction, out hitInfo, 30.0f, SpatialMappingManager.Instance.LayerMask))
            {
                spatialMapHit = hitInfo;
                return true;
            }
        }
        spatialMapHit = new RaycastHit();
        return false;
    }

    /// <summary>
    /// Get placement position either from GazeManager hit or in front of the user as backup
    /// </summary>
    /// <param name="headPosition">Position of the users head</param>
    /// <param name="gazeDirection">Gaze direction of the user</param>
    /// <param name="defaultGazeDistance">Default placement distance in front of the user</param>
    /// <returns>Placement position in front of the user</returns>
    private static Vector3 GetGazePlacementPosition(Vector3 headPosition, Vector3 gazeDirection, float defaultGazeDistance)
    {
        if (GazeManager.Instance.HitObject != null)
        {
            return GazeManager.Instance.HitPosition;
        }
        return headPosition + gazeDirection * defaultGazeDistance;
    }
}
