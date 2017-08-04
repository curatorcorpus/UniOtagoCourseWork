using UnityEngine;
using System;
using System.IO;
using System.Collections.Generic;
using System.Runtime.Serialization.Formatters.Binary;


[Serializable]
public class Data
{
    public List<float> Xpositions = new List<float>();
    public List<float> Ypositions = new List<float>();
    public List<float> Zpositions = new List<float>();

    public List<byte> Rcolors = new List<byte>();
    public List<byte> Gcolors = new List<byte>();
    public List<byte> Bcolors = new List<byte>();
}

public class Frame
{
    public List<Vector3> positions = new List<Vector3>();
    public List<Vector3> mirroredPositions = new List<Vector3>();
    public List<Color32> colors = new List<Color32>();
}

public class VoxelRecording : MonoBehaviour {

    private bool recording = false;    
    private string lastFileName;       // path to the last recorded file

    private GameObject voxelRendering; // uses this as a parent for created VirtualBodyObjects

    private List<Data> localData;      // saves all data points into list for serialization.
    private List<Frame> localFrames;   // saves new frames with voxels into list.
    private List<Vector3> poss;        // list of voxel positions
    private List<Vector3> mposs;       // list of mirrored positions
    private List<Color32> cols;        // list of voxel colours

    private Matrix4x4 matrix; // a transformation matrix used to transform MKS voxels to MKSVoxelObject calibration.

    public string recordingsPath = "Assets/Recordings/";

    public BodyVoxelObject body;                    // object used to obtain voxel position and color.
    public BackgroundVoxelObject background;        // object used to obtain voxel position and color.
    public GameObject virtualBodyPrefab;            // used for immediately loading last recording.
    public MultiKinectVoxelObject multiKinectVoxel; // object used to obtain voxel position and color.

    public bool captureMainVoxels = false; // Checkbox to set whether the main body frames should be stored or not
    public bool captureMKVoxels = false;   // Checkbox to set whether the multi kinect frames should be stored or not
    public bool captureBackground = false; // Checkbox to set whether the main background kinect frames should be stored or not

    // Use this for initialization
    void OnEnable ()
    {
        voxelRendering = GameObject.FindObjectOfType<KinectVoxel>().gameObject;

        localData  = new List<Data>();
        localFrames = new List<Frame>();

        poss = new List<Vector3>();
        mposs = new List<Vector3>();
        cols = new List<Color32>();
    }

    /// <summary>
    /// Method to store only one frame of the user. Creates a static Snapshot.
    /// </summary>
    public void snapShot()
    {
        localFrames.Clear();
        addFrame();
        SaveData();
    }

    /// <summary>
    /// Starts the recording of the user by setting the captureBody flag.
    /// </summary>
    public void startRecording()
    {
        recording = true;
        localFrames.Clear();
        Debug.Log("Start Recording!");

        InvokeRepeating("addFrame", 0.0f, 1.0f / 30.0f);
        matrix = Matrix4x4.TRS(multiKinectVoxel.transform.localPosition, 
                               multiKinectVoxel.transform.localRotation, 
                               new Vector3(1, 1, 1));
    } 

    /// <summary>
    /// Creates a frame with the current positions and colors of the BodyVoxelObject and adds it to the list of frames.
    /// </summary>
    void addFrame()
    {
        VoxelObject voxelObject;
        Frame frame = new Frame(); // class to store current frame (voxel pos and color)

        if (captureMainVoxels)
        {
            voxelObject = body;

            frame.positions.AddRange(voxelObject.Positions);
            frame.colors.AddRange(voxelObject.Colors);
            localFrames.Add(frame);
        }
        if (captureBackground)
        {
            voxelObject = background;

            frame.positions.AddRange(voxelObject.Positions);
            frame.colors.AddRange(voxelObject.Colors);
            localFrames.Add(frame);
        }
        if (captureMKVoxels)
        {
            voxelObject = multiKinectVoxel;
            List<Vector3> tmpMksPos = new List<Vector3>(voxelObject.Positions.Count);
            for(int i = 0; i < voxelObject.Positions.Count; i++)
            {
                Vector3 alteredPos = voxelObject.Positions[i];
                alteredPos = matrix.MultiplyPoint3x4(alteredPos);

                tmpMksPos.Add(alteredPos);
            }

            frame.positions.AddRange(tmpMksPos);
            frame.colors.AddRange(voxelObject.Colors);
            localFrames.Add(frame);
        }
    }

    /// <summary>
    /// Stops the recording of the user by setting the captureBody flag. Initiates the saving of the data.
    /// </summary>
    public void stopRecording()
    {
        recording = false;
        SaveData();
        CancelInvoke();

        Debug.Log("Stop Recording!");
    }

    public void loadLastRecording()
    {
        GameObject virtualBodyObject = Instantiate(virtualBodyPrefab);
        VirtualBodyObject virtualBody = virtualBodyObject.GetComponent<VirtualBodyObject>();

        virtualBody.name = "LastRecordedBodyObject";
        virtualBody.transform.parent = voxelRendering.transform;

        if(lastFileName != null)
        {
            virtualBody.file = new GameObject(lastFileName);
            virtualBody.enableRecording = true;
        }
    }

    /// <summary>
    /// Saves all frames to a .binary file.
    /// </summary>
    public void SaveData()
    {
        if (!Directory.Exists(recordingsPath))
            Directory.CreateDirectory(recordingsPath);

        BinaryFormatter formatter = new BinaryFormatter();
        string filename = System.DateTime.Now.ToString("yyyyMMdd_hhmmss") + "_MREP_Recording";
        FileStream saveFile = File.Create(recordingsPath + filename + ".binary");
        toData();
        formatter.Serialize(saveFile, localData);

        saveFile.Close();
        lastFileName = filename;
        Debug.Log("Clip saved to: " + filename);
    }

    /// <summary>
    /// Loads the data of the file found in recordingsPath + filename and adds the containing list of frames to body.
    /// </summary>
    /// <param name="filename"></param>
    /// <param name="body"></param>
    public void LoadData(string filename, VirtualBodyObject body)
    {
        BinaryFormatter formatter = new BinaryFormatter();
        FileStream saveFile = File.Open(recordingsPath + filename, FileMode.Open);

        localData = (List<Data>)formatter.Deserialize(saveFile);

        fromData();
        body.frames.Clear();
        body.frames.AddRange(localFrames);
        body.Running = false;

        saveFile.Close();
    }

    /// <summary>
    /// Converts the Frames to Data
    /// </summary>
    void toData()
    {
        List<Vector3> positions;
        List<Color32> colors;
        Vector3 pos;
        Color32 col;

        localData.Clear();

        for (int j = 0; j < localFrames.Count; j++) {
            Data data = new Data();

            positions = localFrames[j].positions;
            colors = localFrames[j].colors;

            for (int i = 0; i < positions.Count; i++)
            {
                pos = positions[i];
                col = colors[i];

                data.Xpositions.Add(pos.x);
                data.Ypositions.Add(pos.y);
                data.Zpositions.Add(pos.z);

                data.Rcolors.Add(col.r);
                data.Gcolors.Add(col.g);
                data.Bcolors.Add(col.b);
            }
            localData.Add(data);
        }
    }

    /// <summary>
    /// Converts from Data to Frames.
    /// </summary>
    void fromData()
    {        
        Vector3 pos;
        Vector3 mpos;
        Color32 col;

        localFrames.Clear();

        for (int j = 0; j < localData.Count; j++) {
            poss.Clear();
            mposs.Clear();
            cols.Clear();

            Data data = new Data();

            data = localData[j];

            for (int i = 0; i < data.Rcolors.Count; i++)
            {
                pos.x = data.Xpositions[i];
                pos.y = data.Ypositions[i];
                pos.z = data.Zpositions[i];

                poss.Add(pos);

                mpos.x = data.Xpositions[i];
                mpos.y = data.Ypositions[i];
                mpos.z = -data.Zpositions[i];

                mposs.Add(mpos);

                col.r = data.Rcolors[i];
                col.g = data.Gcolors[i];
                col.b = data.Bcolors[i];
                col.a = 1;

                cols.Add(col);
            }

            localFrames.Add(new Frame());
            localFrames[j].positions.AddRange(poss);
            localFrames[j].mirroredPositions.AddRange(mposs);
            localFrames[j].colors.AddRange(cols);
        }
    }
}
