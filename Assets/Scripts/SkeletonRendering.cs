using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using Assets.Scripts;

public class SkeletonRendering : MonoBehaviour {

    BodyReceiveThread bodyReceiveThread;

    [HideInInspector]
    public bool jointPositionsUpdated = false;
    public List<Vector3> jointPositions;
    public List<Quaternion> jointOrientations;
    public List<int> jointTypes;
    

    // Use this for initialization
    void Start () {

        bodyReceiveThread = new BodyReceiveThread();
        bodyReceiveThread.Start();
    }

    void initGameObjects()
    {
    }

    void OnDestroy()
    {
        //end the thread
        bodyReceiveThread.Abort();
    }

    // Update is called once per frame
    void Update () {

        if (!KeyInputs.updateStopped)
        {
            // check if a new BodyFrame is avaible in the bodyReceiveThread
            if (bodyReceiveThread != null)
                if (bodyReceiveThread.Update())
                {
                    // updating the existing gameobjects with the data in bodyFrame
                    getBodyData();
                }
        }
    }

    void getBodyData()
    {
        BodyReceiveThread.BodyKinectFrame latestBodyFrame = bodyReceiveThread.latestBodyFrame;
        bodyReceiveThread.newFrame = false;

        jointPositions.Clear();
        jointOrientations.Clear();
        jointTypes.Clear();

        jointPositions.AddRange(latestBodyFrame.positions);
        jointTypes.AddRange(latestBodyFrame.types);
        jointOrientations.AddRange(latestBodyFrame.orientations);

        jointPositionsUpdated = true;
    }

   
}
