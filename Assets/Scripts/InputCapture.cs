using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.EventSystems;
using HoloToolkit.Unity.InputModule;

public class InputCapture : MonoBehaviour, IManipulationHandler {
    public bool rotate = false;
    public GameObject o;
    private float rot = 0;
    
    public void OnManipulationStarted(ManipulationEventData eventData)
    {
        Debug.LogFormat("OnManipulationStarted\r\nSource: {0}  SourceId: {1}\r\nCumulativeDelta: {2} {3} {4}",
            eventData.InputSource,
            eventData.SourceId,
            eventData.CumulativeDelta.x,
            eventData.CumulativeDelta.y,
            eventData.CumulativeDelta.z);
        eventData.Use(); // Mark the event as used, so it doesn't fall through to other handlers.
    }

    public void OnManipulationUpdated(ManipulationEventData eventData)
    {
            Debug.LogFormat("OnManipulationUpdated\r\nSource: {0}  SourceId: {1}\r\nCumulativeDelta: {2} {3} {4}",
                eventData.InputSource,
                eventData.SourceId,
                eventData.CumulativeDelta.x,
                eventData.CumulativeDelta.y,
                eventData.CumulativeDelta.z);
        if (!rotate)
        {
            o.transform.localPosition += eventData.CumulativeDelta / 3;
        }
        else
        {
            rot += eventData.CumulativeDelta.x + eventData.CumulativeDelta.z;
            o.transform.rotation = Quaternion.Euler(0, rot, 0);
        }

            eventData.Use(); // Mark the event as used, so it doesn't fall through to other handlers.
    }

    public void OnManipulationCompleted(ManipulationEventData eventData)
    {
        Debug.LogFormat("OnManipulationCompleted\r\nSource: {0}  SourceId: {1}\r\nCumulativeDelta: {2} {3} {4}",
            eventData.InputSource,
            eventData.SourceId,
            eventData.CumulativeDelta.x,
            eventData.CumulativeDelta.y,
            eventData.CumulativeDelta.z);

        eventData.Use(); // Mark the event as used, so it doesn't fall through to other handlers.
    }

    public void OnManipulationCanceled(ManipulationEventData eventData)
    {
        Debug.LogFormat("OnManipulationCanceled\r\nSource: {0}  SourceId: {1}\r\nCumulativeDelta: {2} {3} {4}",
            eventData.InputSource,
            eventData.SourceId,
            eventData.CumulativeDelta.x,
            eventData.CumulativeDelta.y,
            eventData.CumulativeDelta.z);

        eventData.Use(); // Mark the event as used, so it doesn't fall through to other handlers.
    }
}
