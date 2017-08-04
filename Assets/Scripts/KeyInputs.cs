using UnityEngine;
using System.Collections;
using WindowsInput;

/// <summary>
/// Handels the Keyboard inputs to activate/toggle certain functions.
/// </summary>
public class KeyInputs : MonoBehaviour {

    public static bool voxelSpaceBoundsVisible = false;
    public static bool trackerBoundsVisible = false;
    public static bool kinectBoundsVisible = false;
    public static bool kinectVoxelsVisible = true;
    public static bool bodyVisible = false;
    public static bool showSkeleton = false;
    public static bool assignVoxel = false;
    public static bool updateStopped = false;
    public static bool automaticScreencapture = false;
    public static bool screenCapture = false;
    public static bool takeScreenshot = false;
    public static bool takeBoothPhoto = false;
    public static bool takeThesisPhoto = false;

    private string path;

    private MREPManager manager;
    private VoxelRecording recorder;
    private OVRCameraRig rig;

    private BodyVoxelObject body;

    private Vector3[] voxelSpaceBoundsVerts;

    private Vector3 trackerPosition = Vector3.zero;
    private Quaternion trackerRotation = Quaternion.identity; // tracker rotation
    private Vector3 trackerNearPlaneCentre = Vector3.zero;
    private float trackerHfov;
    private float trackerVfov;
    private float trackerNearZ;
    private float trackerFarZ;
    private Vector3[] trackerNearPlane;
    private Vector3[] trackerFarPlane;

    private static Material lineMaterial;

    // Use this for initialization
    void OnEnable () {

        manager = GameObject.FindObjectOfType<MREPManager>();
        recorder = GameObject.FindObjectOfType<VoxelRecording>();
        rig = GameObject.FindObjectOfType<OVRCameraRig>();
        body = GameObject.FindObjectOfType<BodyVoxelObject>();

        path = Application.dataPath.Replace("Assets", "") + "Screenshots/";

        //init voxelSpaceBounds values
        voxelSpaceBoundsVerts = new Vector3 [] {
             new Vector3(-1.28f, 0.0f, 0.0f), new Vector3(1.28f, 0.0f, 0.0f),
             new Vector3(1.28f, 2.56f, 0.0f), new Vector3(-1.28f, 2.56f, 0.0f),
             new Vector3(-1.28f, 0.0f, 2.56f), new Vector3(1.28f, 0.0f, 2.56f),
             new Vector3(1.28f, 2.56f, 2.56f), new Vector3(-1.28f, 2.56f, 2.56f) };

        //init trackerFrustum values
        trackerNearPlane = new Vector3[4];
        trackerFarPlane = new Vector3[4];

        Shader shader = Shader.Find("Custom/VertexColor");
        lineMaterial = new Material(shader);

        if(automaticScreencapture)
            StartScreencapture();

    }

    void Start()
    {
       //Invoke("calibrateOculus", 0.05f);
       calibrateOculus();
    }

    void OnApplicationQuit()
    {
        if (automaticScreencapture)
            StopScreencapture();
    }

    private void updateTrackerFrustum()
    {
        GameObject tracker = GameObject.Find("OVRcameraModel");
        OVRTracker.Frustum frustum = OVRManager.tracker.GetFrustum();
        trackerHfov = frustum.fov.x * (float)Mathf.PI / 180.0f;
        trackerVfov = frustum.fov.y * (float)Mathf.PI / 180.0f;
        trackerNearZ = frustum.nearZ;
        trackerFarZ = frustum.farZ;
        trackerPosition = tracker.GetComponent<Transform>().position;
        trackerRotation = tracker.GetComponent<Transform>().rotation;

        calulateTrackerFrustumVerts();
    }

    private void calulateTrackerFrustumVerts()
    {
        // precalculate trig stuff
        float hor = Mathf.Tan(trackerHfov / 2);
        float ver = Mathf.Tan(trackerVfov / 2);
        float nearX = trackerNearZ * hor;
        float nearY = trackerNearZ * ver;
        float farX = trackerFarZ * hor;
        float farY = trackerFarZ * ver;

        // tracker frustum verts
        trackerNearPlane[0] = new Vector3(-nearX, nearY, -trackerNearZ);
        trackerNearPlane[1] = new Vector3(nearX, nearY, -trackerNearZ);
        trackerNearPlane[2] = new Vector3(nearX, -nearY, -trackerNearZ);
        trackerNearPlane[3] = new Vector3(-nearX, -nearY, -trackerNearZ);

        trackerFarPlane[0] = new Vector3(-farX, farY, -trackerFarZ);
        trackerFarPlane[1] = new Vector3(farX, farY, -trackerFarZ);
        trackerFarPlane[2] = new Vector3(farX, -farY, -trackerFarZ);
        trackerFarPlane[3] = new Vector3(-farX, -farY, -trackerFarZ);

        //rotate the frustum verts by the camera rotation
        for (int i = 0; i < 4; i++)
        {
            trackerNearPlane[i] = trackerRotation * trackerNearPlane[i];
            trackerFarPlane[i] = trackerRotation * trackerFarPlane[i];
        }
        trackerNearPlaneCentre = trackerRotation * trackerNearPlaneCentre;

        // translate the frustum verts to the camera
        for (int i = 0; i < 4; i++)
        {
            trackerNearPlane[i] += trackerPosition;
            trackerFarPlane[i] += trackerPosition;
        }
        trackerNearPlaneCentre += trackerPosition;
    }

    public void calibrateOculus()
    {
        rig.trackingSpace.FromOVRPose(OVRManager.tracker.GetPose().Inverse());
        rig.trackingSpace.Rotate(manager.trackerRotation);
        rig.trackingSpace.Translate(manager.trackerPosition); 

        Debug.Log("Oculus HMD Calibrated");
    }

    // Update is called once per frame
    void Update() {
        if (Input.GetKeyDown(KeyCode.B)) bodyVisible = !bodyVisible;
        if (Input.GetKeyDown(KeyCode.C)) recorder.startRecording();
        if (Input.GetKeyDown(KeyCode.D)) takeBoothPhoto = true;
        if (Input.GetKeyDown(KeyCode.F)) takeThesisPhoto = true;
        if (Input.GetKeyDown(KeyCode.K)) kinectBoundsVisible = !kinectBoundsVisible;
        if (Input.GetKeyDown(KeyCode.L)) recorder.loadLastRecording();
        if (Input.GetKeyDown(KeyCode.N)) kinectVoxelsVisible = !kinectVoxelsVisible;
        if (Input.GetKeyDown(KeyCode.O)) calibrateOculus();
        if (Input.GetKeyDown(KeyCode.P)) updateStopped = !updateStopped;
        if (Input.GetKeyDown(KeyCode.V)) voxelSpaceBoundsVisible = !voxelSpaceBoundsVisible;
        if (Input.GetKeyDown(KeyCode.X)) recorder.stopRecording();
        if (Input.GetKeyDown(KeyCode.Z)) recorder.snapShot();

        if (Input.GetKeyDown(KeyCode.A))
        {
            assignVoxel = !assignVoxel;
            body.clearBodyParts();
        }
        if (Input.GetKeyDown(KeyCode.S))
        {
            takeScreenshot = true;
            saveScreenshot();
        }
        if (Input.GetKeyDown(KeyCode.T))
        {
            updateTrackerFrustum();
            trackerBoundsVisible = !trackerBoundsVisible;
        }
    }

    void OnPostRender()
    {
        if (voxelSpaceBoundsVisible)
        {
            lineMaterial.SetPass(0);
            GL.Begin(GL.LINES);
            GL.Color(Color.blue);

            for (int i = 0; i < 4; i++){
                GL.Vertex3(voxelSpaceBoundsVerts[i].x, voxelSpaceBoundsVerts[i].y, voxelSpaceBoundsVerts[i].z);
                GL.Vertex3(voxelSpaceBoundsVerts[(i+1)%4].x, voxelSpaceBoundsVerts[(i + 1) % 4].y, voxelSpaceBoundsVerts[(i + 1)% 4].z);

                GL.Vertex3(voxelSpaceBoundsVerts[i+4].x, voxelSpaceBoundsVerts[i+4].y, voxelSpaceBoundsVerts[i+4].z);
                GL.Vertex3(voxelSpaceBoundsVerts[4+ ((i + 1) % 4)].x, voxelSpaceBoundsVerts[4 + ((i + 1) % 4)].y, voxelSpaceBoundsVerts[4 + ((i + 1) % 4)].z);

                GL.Vertex3(voxelSpaceBoundsVerts[i].x, voxelSpaceBoundsVerts[i].y, voxelSpaceBoundsVerts[i].z);
                GL.Vertex3(voxelSpaceBoundsVerts[4+i].x, voxelSpaceBoundsVerts[4 + i].y, voxelSpaceBoundsVerts[4 + i].z);
            }

            GL.End();
        }

        if(trackerBoundsVisible)
        {
            lineMaterial.SetPass(0);
            GL.Begin(GL.LINES);
            GL.Color(Color.red);

            for (int i = 0; i < 4; i++)
            {
                //draw nearPlane lines
                GL.Vertex3(trackerNearPlane[i].x, trackerNearPlane[i].y, trackerNearPlane[i].z);
                GL.Vertex3(trackerNearPlane[(i + 1) % 4].x, trackerNearPlane[(i + 1) % 4].y, trackerNearPlane[(i + 1) % 4].z);

                //draw farPlane lines
                GL.Vertex3(trackerFarPlane[i].x, trackerFarPlane[i].y, trackerFarPlane[i].z);
                GL.Vertex3(trackerFarPlane[(i + 1) % 4].x, trackerFarPlane[(i + 1) % 4].y, trackerFarPlane[(i + 1) % 4].z);

                //draw frustum struts
                GL.Vertex3(trackerPosition.x, trackerPosition.y, trackerPosition.z);
                GL.Vertex3(trackerFarPlane[i].x, trackerFarPlane[i].y, trackerFarPlane[i].z);
            }

            GL.End();
        }

        if (kinectBoundsVisible)
        {
            lineMaterial.SetPass(0);
            GL.Begin(GL.LINES);
            GL.Color(Color.green);

            for (int i = 0; i < 4; i++)
            {
                //draw nearPlane lines
                GL.Vertex3(manager.kinectNearPlane[i].x, manager.kinectNearPlane[i].y, manager.kinectNearPlane[i].z);
                GL.Vertex3(manager.kinectNearPlane[(i + 1) % 4].x, manager.kinectNearPlane[(i + 1) % 4].y, manager.kinectNearPlane[(i + 1) % 4].z);

                //draw farPlane lines
                GL.Vertex3(manager.kinectFarPlane[i].x, manager.kinectFarPlane[i].y, manager.kinectFarPlane[i].z);
                GL.Vertex3(manager.kinectFarPlane[(i + 1) % 4].x, manager.kinectFarPlane[(i + 1) % 4].y, manager.kinectFarPlane[(i + 1) % 4].z);

                //draw frustum struts
                GL.Vertex3(manager.kinectPosition.x, manager.kinectPosition.y, manager.kinectPosition.z);
                GL.Vertex3(manager.kinectFarPlane[i].x, manager.kinectFarPlane[i].y, manager.kinectFarPlane[i].z);
            }

            GL.End();
        }
    }

    public void StartScreencapture()
    {
        if (screenCapture) { 
            Debug.Log("Screencapture already started");
        }  else {
            screenCapture = true;
            Debug.Log("Screencapture started");
            InputSimulator.SimulateModifiedKeyStroke(VirtualKeyCode.MENU, VirtualKeyCode.F9);
        }
        
    }

    public void StopScreencapture()
    {
        if (!screenCapture)
            Debug.Log("Screencapture already stopped");
        else {
            screenCapture = false;
            Debug.Log("Screencapture stopped");
            InputSimulator.SimulateModifiedKeyStroke(VirtualKeyCode.MENU, VirtualKeyCode.F9);
        }
        
    }
    void saveScreenshot()
    {
        
        string filename = System.DateTime.Now.ToString("yyyyMMdd_hhmmss") + "_MREP_Screenshot.png";
        Application.CaptureScreenshot(path + filename, 1);
        takeScreenshot = false;
        Debug.Log(filename + " Screenshot saved!");
    }
}