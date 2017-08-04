using UnityEngine;
using System.Collections;
using System.Collections.Generic;

public class Bone
{

    public int parent; //type of parent, doubles as id in list?
    public Vector3 origin;
    public Vector3 end;
    public Vector3 direction; //Start to end
    public Vector3 dimensions; //set these manually?


    public Bone()
    {

    }
    public Bone(int parent, Vector3 origin, Vector3 end, Vector3 dimensions)
    {
        this.parent = parent;
        this.origin = origin;
        //this.end = parent.start;
        //this.direction = origin - parent.origin;
        this.dimensions = dimensions;
    }
}


public class Skeleton : MonoBehaviour {
    
    public Bone[] bones = new Bone[25];

    public List<GameObject> joints;
    public List<GameObject> mirroredJoints;
    private SkeletonRendering skeletonRendering;
    private MREPManager manager;
    //private int type;

    // Use this for initialization
    void Start () {

        manager = GameObject.FindObjectOfType<MREPManager>();

        //initGameObjects();

        joints = new List<GameObject>(25);
        skeletonRendering = GameObject.FindGameObjectWithTag("SkeletonRendering").GetComponent<SkeletonRendering>();
        joints.Add(GameObject.Find("SpineBase"));
        joints.Add(GameObject.Find("SpineMid"));
        joints.Add(GameObject.Find("NeckJoint"));
        joints.Add(GameObject.Find("HeadJoint"));
        joints.Add(GameObject.Find("ShoulderLeft"));
        joints.Add(GameObject.Find("ElbowLeft"));
        joints.Add(GameObject.Find("WristLeft"));
        joints.Add(GameObject.Find("HandLeft"));
        joints.Add(GameObject.Find("ShoulderRight"));
        joints.Add(GameObject.Find("ElbowRight"));
        joints.Add(GameObject.Find("WristRight"));
        joints.Add(GameObject.Find("HandRight"));
        joints.Add(GameObject.Find("HipLeft"));
        joints.Add(GameObject.Find("KneeLeft"));
        joints.Add(GameObject.Find("AnkleLeft"));
        joints.Add(GameObject.Find("FootLeft"));
        joints.Add(GameObject.Find("HipRight"));
        joints.Add(GameObject.Find("KneeRight"));
        joints.Add(GameObject.Find("AnkleRight"));
        joints.Add(GameObject.Find("FootRight"));
        joints.Add(GameObject.Find("SpineShoulder"));
        joints.Add(GameObject.Find("HandTipLeft"));
        joints.Add(GameObject.Find("ThumbLeft"));
        joints.Add(GameObject.Find("HandTipRight"));
        joints.Add(GameObject.Find("ThumbRight"));

        mirroredJoints = new List<GameObject>(25);

        mirroredJoints.Add(GameObject.Find("MirroredSpineBase"));
        mirroredJoints.Add(GameObject.Find("MirroredSpineMid"));
        mirroredJoints.Add(GameObject.Find("MirroredNeckJoint"));
        mirroredJoints.Add(GameObject.Find("MirroredHeadJoint"));
        mirroredJoints.Add(GameObject.Find("MirroredShoulderLeft"));
        mirroredJoints.Add(GameObject.Find("MirroredElbowLeft"));
        mirroredJoints.Add(GameObject.Find("MirroredWristLeft"));
        mirroredJoints.Add(GameObject.Find("MirroredHandLeft"));
        mirroredJoints.Add(GameObject.Find("MirroredShoulderRight"));
        mirroredJoints.Add(GameObject.Find("MirroredElbowRight"));
        mirroredJoints.Add(GameObject.Find("MirroredWristRight"));
        mirroredJoints.Add(GameObject.Find("MirroredHandRight"));
        mirroredJoints.Add(GameObject.Find("MirroredHipLeft"));
        mirroredJoints.Add(GameObject.Find("MirroredKneeLeft"));
        mirroredJoints.Add(GameObject.Find("MirroredAnkleLeft"));
        mirroredJoints.Add(GameObject.Find("MirroredFootLeft"));
        mirroredJoints.Add(GameObject.Find("MirroredHipRight"));
        mirroredJoints.Add(GameObject.Find("MirroredKneeRight"));
        mirroredJoints.Add(GameObject.Find("MirroredAnkleRight"));
        mirroredJoints.Add(GameObject.Find("MirroredFootRight"));
        mirroredJoints.Add(GameObject.Find("MirroredSpineShoulder"));
        mirroredJoints.Add(GameObject.Find("MirroredHandTipLeft"));
        mirroredJoints.Add(GameObject.Find("MirroredThumbLeft"));
        mirroredJoints.Add(GameObject.Find("MirroredHandTipRight"));
        mirroredJoints.Add(GameObject.Find("MirroredThumbRight"));

        initBones();
    }

    //void initGameObjects()
    //{
    //    if (KeyInputs.showSkeleton)
    //        type = (int)PrimitiveType.Cube;
    //    else
    //        type = (int)PrimitiveType.Cylinder;
    //}

    void initBones()
    {
        bones[0] = new Bone(-1, new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0.1f, 0.4f, 0.1f)); // SpineBase
        bones[1] = new Bone(0, new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0.1f, 0.4f, 0.1f)); // SpineMid
        bones[2] = new Bone(20, new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0.1f, 0.4f, 0.1f)); // Neck
        bones[3] = new Bone(2, new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0.1f, 0.4f, 0.1f)); // Head

        bones[4] = new Bone(20, new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0.1f, 0.4f, 0.1f)); // ShoulderLeft
        bones[5] = new Bone(4, new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0.1f, 0.4f, 0.1f)); // ElbowLeft
        bones[6] = new Bone(5, new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0.1f, 0.4f, 0.1f)); // WristLeft
        bones[7] = new Bone(6, new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0.1f, 0.4f, 0.1f)); // HandLeft

        bones[8] = new Bone(20, new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0.1f, 0.4f, 0.1f)); // ShoulderRight
        bones[9] = new Bone(8, new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0.1f, 0.4f, 0.1f)); // ElbowRight
        bones[10] = new Bone(9, new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0.1f, 0.4f, 0.1f)); // WristRight
        bones[11] = new Bone(10, new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0.1f, 0.4f, 0.1f)); // HandRight

        bones[12] = new Bone(0, new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0.1f, 0.4f, 0.1f)); // HipLeft
        bones[13] = new Bone(12, new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0.1f, 0.4f, 0.1f)); // KneeLeft
        bones[14] = new Bone(13, new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0.1f, 0.4f, 0.1f)); // AnkleLeft
        bones[15] = new Bone(14, new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0.1f, 0.4f, 0.1f)); // FootLeft

        bones[16] = new Bone(0, new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0.1f, 0.4f, 0.1f)); // HipRight
        bones[17] = new Bone(16, new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0.1f, 0.4f, 0.1f)); // KneeRight
        bones[18] = new Bone(17, new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0.1f, 0.4f, 0.1f)); // AnkleRight
        bones[19] = new Bone(18, new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0.1f, 0.4f, 0.1f)); // FootRight

        bones[20] = new Bone(1, new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0.1f, 0.4f, 0.1f)); // SpineShoulder

        bones[21] = new Bone(7, new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0.1f, 0.4f, 0.1f)); // HandTipLeft
        bones[22] = new Bone(6, new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0.1f, 0.4f, 0.1f)); // ThumbLeft

        bones[23] = new Bone(11, new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0.1f, 0.4f, 0.1f)); // HandTipRight
        bones[24] = new Bone(10, new Vector3(0, 0, 0), new Vector3(0, 0, 0), new Vector3(0.1f, 0.4f, 0.1f)); // ThumbRight

    }

    // Update is called once per frame
    void Update () {
        if (KeyInputs.bodyVisible != joints[1].GetComponent<MeshRenderer>().enabled)
        {
            toggleBodyVisible();
        }
        if (skeletonRendering.jointPositionsUpdated)
            //updateBodies();
            updateBounds();
    }

    void updateBounds()
    {
        for (int i = 0; i < skeletonRendering.jointTypes.Count; i++)
        {
            int t = skeletonRendering.jointTypes[i];
            bones[t].origin = skeletonRendering.jointPositions[i];

            //Bone bone = bones[i];
            //Bone parent;
            
            //bone.origin = skeletonRendering.jointPositions[i];

            //if (bone.parent != null)
            //{
            //    parent = bone.parent;
            //    bone.direction = bone.origin - parent.origin;
            //}


            //jointObjects[i].transform.rotation = Quaternion.FromToRotation(Vector3.up, bone.direction);
            ////joints[i].transform.localRotation = skeletonRendering.[i];
            //Vector3 oldScale = jointObjects[i].transform.localScale;
            //jointObjects[i].transform.localScale = new Vector3(oldScale.x, bone.direction.magnitude, oldScale.z);
            //jointObjects[i].transform.localPosition = skeletonRendering.jointPositions[i];

        }

        Bone bone, parent;

        for (int i = 1; i < 21; i++)  //skip spinebase, handtips etc
        {
            bone = bones[i];
            parent = bones[bone.parent];
            bone.direction = bone.origin - parent.origin;

            joints[i].transform.rotation = Quaternion.FromToRotation(Vector3.up, bone.direction);
            Vector3 oldScale = joints[i].transform.localScale;
            if (i == 3) //head
            {
                joints[i].transform.localScale = new Vector3(0.25f, 0.3f, 0.5f);
                //joints[i].transform.localPosition = bone.origin;
            }
            else if(i == 7) //hand
            {
                joints[i].transform.localScale = new Vector3(0.2f, 0.25f, 0.2f);
                //joints[i].transform.localPosition = bone.origin;
            }
            else if(i == 11) //hand
            {
                joints[i].transform.localScale = new Vector3(0.2f, 0.25f, 0.2f);
                //joints[i].transform.localPosition = bone.origin;
            }
            else if(i == 15) //foot
            {
                joints[i].transform.localScale = new Vector3(0.15f, 0.2f, 0.15f);
            }
            else if (i == 19) //foot
            {
                joints[i].transform.localScale = new Vector3(0.15f, 0.2f, 0.15f);
            }
            else
            {
                joints[i].transform.localScale = new Vector3(oldScale.x, bone.direction.magnitude, oldScale.z);   
            }

            Vector3 pos = parent.origin;
            joints[i].transform.localPosition = pos;

            Vector3 mirroredDirection = new Vector3(bone.direction.x, bone.direction.y, -bone.direction.z);
            mirroredJoints[i].transform.rotation = Quaternion.FromToRotation(Vector3.up, mirroredDirection); 

            mirroredJoints[i].transform.localPosition = new Vector3(pos.x, pos.y, -pos.z) + manager.mirrorOffset;
            //mirroredJoints[i].transform.Translate(manager.mirrorOffset);
            mirroredJoints[i].transform.localScale = joints[i].transform.localScale;

        }
        //joints[3].transform.localPosition = bones[3].origin; //head
        //joints[11].transform.localPosition = bones[11].origin; //right hand
        //joints[7].transform.localPosition = bones[7].origin; //left hand

        skeletonRendering.jointPositionsUpdated = false;
    }

    void updateBodies()
    {
        if(skeletonRendering.jointPositions.Count > 25)
            Debug.Log("TOO LONG: " + skeletonRendering.jointPositions.Count);

        for (int i = 0; i < 25; i++)
        {
            joints[i].transform.localRotation = skeletonRendering.jointOrientations[i];
            joints[i].transform.localPosition = skeletonRendering.jointPositions[i];
            
            
        }
        skeletonRendering.jointPositionsUpdated = false;
    }

    void toggleBodyVisible()
    {
        bool visible = KeyInputs.bodyVisible;
        for (int i = 1; i < 21; i++)
        {
            joints[i].GetComponent<MeshRenderer>().enabled = visible;
            mirroredJoints[i].GetComponent<MeshRenderer>().enabled = visible;
        }

    }

}
