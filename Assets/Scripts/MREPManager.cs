using UnityEngine;
using System.Collections;

public class MREPManager : MonoBehaviour {

    public VoxelSpace voxelspace;
    [Tooltip("The width of the voxelspace in voxels.")]
    public int spaceWidth = 256;
    [Tooltip("The height of the voxelspace in voxels.")]
    public int spaceHeight = 256;
    [Tooltip("The depth of the voxelspace in voxels.")]
    public int spaceDepth = 256;
    [Tooltip("The size of each voxel in meters.")]
    public float voxelSize = 0.008f;

    [HideInInspector]
    public int maxMeshSize = 65000;
    [Tooltip("The origin of the voxelpace, located at the lower front corner on the right.")]
    public Vector3 voxelspaceOrigin = new Vector3(0f, 0f, 0f);
    public Vector3 trackerPosition = new Vector3(-0.08f, 1.79f, 0f);
    public Vector3 trackerRotation = new Vector3(0.0f, 0.0f, 0.0f);
    public Vector3 kinectPosition = new Vector3(0.0f, 2.33f, 0.0315f);
    public Vector3 mirrorOffset = new Vector3(0f, 0f, 0.4f);

    [HideInInspector]
    public Quaternion kinectRotation;
    [HideInInspector]
    public Vector3 kinectNearPlaneCentre, kinectFarPlaneCentre, kinectLookAt;
    [HideInInspector]
    public float kinectHfov, kinectVfov, kinectNearZ, kinectFarZ, kinectAspect, nearX, nearY, farX, farY;
    [HideInInspector]
    public Vector3[] kinectNearPlane, kinectFarPlane;
    [HideInInspector]
    public Matrix4x4 kinectTransform, kinectProjection;
    [HideInInspector]

    public struct Plane
    {
        public Vector3 point;
        public Vector3 normal;
    }
    [HideInInspector]  
    public Plane[] planes;  //planes of the kinect frustum
    enum frustum { TOP, BOTTOM, LEFT, RIGHT, NEAR, FAR };

    public Ray[][] kinectRays;
    [HideInInspector]
    public int kinectWidth = 512;
    [HideInInspector]
    public int kinectHeight = 424;
    private float stepX, stepY;

    //use this for initialization to make sure it is available in every start methode of the other scripts
    void Awake()
    {
        voxelspace = new VoxelSpace(spaceWidth, spaceHeight, spaceDepth, voxelSize, voxelspaceOrigin);

        //init kinectFrustum values
        kinectRotation = Quaternion.AngleAxis(32.0f, Vector3.right);
        kinectHfov = 70.0f;
        kinectVfov = 58.3f;
        kinectNearZ = 0.5f;
        kinectFarZ = 4.5f;
        kinectAspect = (float)kinectWidth / (float)kinectHeight;
        kinectNearPlane = new Vector3[4];
        kinectFarPlane = new Vector3[4];

        kinectRays = new Ray[kinectWidth][];
        for(int i = 0; i<kinectWidth; i++)
        {
            kinectRays[i] = new Ray[kinectHeight];
        }

        calculateKinectFrustumVerts();
        calculatePlanes();
        calculateKinectRays();
    }

    void calculateKinectFrustumVerts()
    {
        // precalculate trig stuff
        kinectHfov = kinectHfov * (float)Mathf.PI / 180.0f;
        kinectVfov = kinectVfov * (float)Mathf.PI / 180.0f;
        float hor = Mathf.Tan(kinectHfov / 2);
        float ver = Mathf.Tan(kinectVfov / 2);
        nearX = kinectNearZ * hor;
        nearY = kinectNearZ * ver;
        farX = kinectFarZ * hor;
        farY = kinectFarZ * ver;

        // tracker frustum verts
        kinectNearPlane[0] = new Vector3(-nearX, nearY, kinectNearZ);
        kinectNearPlane[1] = new Vector3(nearX, nearY, kinectNearZ);
        kinectNearPlane[2] = new Vector3(nearX, -nearY, kinectNearZ);
        kinectNearPlane[3] = new Vector3(-nearX, -nearY, kinectNearZ);

        kinectFarPlane[0] = new Vector3(-farX, farY, kinectFarZ);
        kinectFarPlane[1] = new Vector3(farX, farY, kinectFarZ);
        kinectFarPlane[2] = new Vector3(farX, -farY, kinectFarZ);
        kinectFarPlane[3] = new Vector3(-farX, -farY, kinectFarZ);

        kinectNearPlaneCentre = new Vector3(0, 0, kinectNearZ);
        //rotate the frustum verts by the camera rotation
        for (int i = 0; i < 4; i++)
        {
            kinectNearPlane[i] = kinectRotation * kinectNearPlane[i];
            kinectFarPlane[i] = kinectRotation * kinectFarPlane[i];
        }
        kinectNearPlaneCentre = kinectRotation * kinectNearPlaneCentre;

        // translate the frustum verts to the camera
        for (int i = 0; i < 4; i++)
        {
            kinectNearPlane[i] += kinectPosition;
            kinectFarPlane[i] += kinectPosition;
        }
        kinectNearPlaneCentre += kinectPosition;
        kinectLookAt = kinectNearPlaneCentre - kinectPosition;
        kinectFarPlaneCentre = kinectNearPlaneCentre + (kinectFarZ - kinectNearZ)*kinectLookAt.normalized;
    }

    void calculateKinectRays()
    {
        Vector3 screenPoint, direction;
        Vector3 xDirection = kinectNearPlane[2] - kinectNearPlane[3];
        Vector3 yDirection = kinectNearPlane[0] - kinectNearPlane[3];

        stepX = 1.0f / (float)kinectWidth;
        stepY = 1.0f / (float)kinectHeight;
        Vector3 start = kinectNearPlane[3] + ((0.5f * stepX) * xDirection) + ((0.5f * stepY) * yDirection);                 //lower left corner of the nearPlane + a half pixel to go through the middle of the pixel


        for(int i = 0; i < kinectWidth; i++)
        {
            for(int j = 0; j < kinectHeight; j++)
            {
                screenPoint = start + (i*stepX) * xDirection + (j*stepY) * yDirection;
                direction = screenPoint - kinectPosition;
                kinectRays[i][j] = new Ray(kinectPosition, direction);
 
            }
        }
    }

    /// <summary>
    /// calculates the planes of the kinect frustum. A plane is specified by a point and its normal.
    /// </summary>
    /// 
    void calculatePlanes()
    {
        planes = new Plane[6];

        planes[(int)frustum.NEAR].point = kinectNearPlaneCentre;
        planes[(int)frustum.NEAR].normal = kinectLookAt.normalized;

        planes[(int)frustum.FAR].point = kinectFarPlaneCentre;
        planes[(int)frustum.FAR].normal = -kinectLookAt.normalized;

        Vector3 point, a, b, normal;
        point = kinectNearPlane[0];
        a = kinectFarPlane[0] - kinectNearPlane[0];
        b = kinectNearPlane[1] - kinectNearPlane[0];
        normal = Vector3.Cross(b, a).normalized;
        planes[(int)frustum.TOP].point = point;
        planes[(int)frustum.TOP].normal = normal;

        b = kinectNearPlane[3] - kinectNearPlane[0];
        normal = Vector3.Cross(a ,b).normalized;
        planes[(int)frustum.LEFT].point = point;
        planes[(int)frustum.LEFT].normal = normal;

        point = kinectNearPlane[2];
        a = kinectFarPlane[2] - kinectNearPlane[2];
        b = kinectNearPlane[3] - kinectNearPlane[2];
        normal = Vector3.Cross(b, a).normalized;
        planes[(int)frustum.BOTTOM].point = point;
        planes[(int)frustum.BOTTOM].normal = normal;

        b = kinectNearPlane[1] - kinectNearPlane[2];
        normal = Vector3.Cross(a, b).normalized;
        planes[(int)frustum.RIGHT].point = point;
        planes[(int)frustum.RIGHT].normal = normal;
    }
}
