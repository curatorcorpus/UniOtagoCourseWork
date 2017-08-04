using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System;
using System.Net;
using System.Net.Sockets;
using Assets.Scripts;


public class KinectVoxel : MonoBehaviour
{
    private const int maxMeshSize         = 65000;
    private const int meshNumber          = 4;
    private const int backgroundMeshCount = 4;
    private const int bodyMeshCount       = 1;

    private const int voxelSize   = 7;
    private const int voxelNumber = 9000;
    private const int msgLength   = voxelNumber * voxelSize + 16; //plus 16 byte for timestamp, packetnumber and voxelcount

    private int[] indices;

    private ReceiveThread receiveThread;
    private VoxelObject backgroundVoxelObject;
    private VoxelObject bodyVoxelObject;

    private Dictionary<int, MultiKinectReceiveThread> mksThreads;
    private Dictionary<int, MultiKinectVoxelObject> mksvos;

    public bool ignoreMultiKinectVoxels = false; // toggle if you only want main Kinect Voxels.
    public string mksIPAddress = "192.168.2.3";
    public int expectedNumberOfMk;
    public int mREPPort;
    public int mksStartingPort;

    public int[] mksPorts;

    void OnValidate()
    {
        receiveThread = new ReceiveThread(mREPPort);
        mksPorts      = new int[expectedNumberOfMk];
        mksThreads    = new Dictionary<int, MultiKinectReceiveThread>(expectedNumberOfMk);
        mksvos        = new Dictionary<int, MultiKinectVoxelObject>(expectedNumberOfMk);

        for (int i = 0; i < expectedNumberOfMk; i++)
        {
            int port = mksStartingPort + i;

            mksPorts[i] = port;
            mksThreads[port] = new MultiKinectReceiveThread(mksIPAddress, port);
        }
    }

    void Start()
    {
        // initialized indicies
        indices = new int[maxMeshSize];
        for (int i = 0; i < maxMeshSize; i++)
        {
            indices[i] = i;
        }

        // initialize game objects
        for(int i = 0; i < expectedNumberOfMk; i++)
        {
            int currPort = mksPorts[i];
            MultiKinectVoxelObject mkvo = GameObject.Find("MultiKinectVoxelObject " + currPort)
                                                    .GetComponent<MultiKinectVoxelObject>();
            mksvos.Add(currPort, mkvo);
        }
        
        if (mksvos.Count != expectedNumberOfMk)
        {
            throw new Exception("The number of MultiKinectVoxelObject doesn't make specified expected number of Multiple Kinects.");
        }

        bodyVoxelObject       = GameObject.FindObjectOfType<BodyVoxelObject>();
        backgroundVoxelObject = GameObject.FindObjectOfType<BackgroundVoxelObject>();

        // start threads.
        receiveThread.Start();
        foreach(KeyValuePair<int, MultiKinectReceiveThread> t in mksThreads)
        {
            t.Value.Start();
        }
    }

    // Update is called once per frame
    void Update()
    {
        Boolean updateFinished = false;

        if(ignoreMultiKinectVoxels)
        {
            updateFinished = true;
        }
        else if(!KeyInputs.updateStopped)
        {
            for (int i = 0; i < expectedNumberOfMk; i++)
            {
                int currPort = mksPorts[i];
                MultiKinectReceiveThread curMKSThread = mksThreads[currPort];

                if (curMKSThread != null)
                    if (curMKSThread.Update())
                        updateFinished = updateMultiKinectVoxelObject(mksvos[currPort], curMKSThread);
            }
        }

        if (updateFinished)
        {
            if (!KeyInputs.updateStopped)
                if (receiveThread != null)
                    if (receiveThread.Update())
                        updateVoxelObjects();
        }
    }

    Boolean updateMultiKinectVoxelObject(MultiKinectVoxelObject mksVoxelObject, MultiKinectReceiveThread mksThread)
    {
        MultiKinectReceiveThread.KinectFrame latestFrame = mksThread.latestFrame;

        mksVoxelObject.Positions.Clear();
        mksVoxelObject.Colors.Clear();
        mksVoxelObject.MirroredPositions.Clear();

        mksVoxelObject.Positions.AddRange(latestFrame.backgroundVoxelPositions);
        mksVoxelObject.Colors.AddRange(latestFrame.backgroundVoxelColors);
        mksVoxelObject.MirroredPositions.AddRange(latestFrame.mirroredBackgroundPositions);

        mksVoxelObject.updated = true;

        mksThread.newFrame = false;

        return true;
    }

    void updateVoxelObjects()
    {
        ReceiveThread.KinectFrame latestFrame = receiveThread.latestFrame;

        backgroundVoxelObject.Positions.Clear();
        backgroundVoxelObject.Colors.Clear();
        backgroundVoxelObject.MirroredPositions.Clear();

        backgroundVoxelObject.Positions.AddRange(latestFrame.backgroundVoxelPositions);
        backgroundVoxelObject.Colors.AddRange(latestFrame.backgroundVoxelColors);
        backgroundVoxelObject.MirroredPositions.AddRange(latestFrame.mirroredBackgroundPositions);
        backgroundVoxelObject.updated = true;

        bodyVoxelObject.Positions.Clear();
        bodyVoxelObject.Colors.Clear();
        bodyVoxelObject.MirroredPositions.Clear();

        bodyVoxelObject.Positions.AddRange(latestFrame.bodyVoxelPositions);
        bodyVoxelObject.Colors.AddRange(latestFrame.bodyVoxelColors);
        bodyVoxelObject.MirroredPositions.AddRange(latestFrame.mirroredBodyPositions);
        bodyVoxelObject.updated = true;

        receiveThread.newFrame = false;
    }

    void OnDestroy()
    {
        //end the thread
        receiveThread.Abort();

        // loop through all threads and stop
        for (int i = 0; i < expectedNumberOfMk; i++)
        {
            int currPort = mksPorts[i];
            mksThreads[currPort].Abort();
        }

        Debug.Log("Closed");
    }
}
