using UnityEngine;
using System.Collections;
using System.Collections.Generic;


public class BodyVoxelObject : VoxelObject {

    [HideInInspector]
    public bool capture = false;

    private Skeleton skeleton;
    private List<BodyPart> bodyparts;
    private MREPManager.Plane[] planes;

    new void Start()
    {
        //base.Start();
        manager = GameObject.FindObjectOfType<MREPManager>();
        maxMeshSize = manager.maxMeshSize;

        planes = new MREPManager.Plane[6];

        skeleton = GameObject.FindObjectOfType<Skeleton>();
        bodyparts = new List<BodyPart>(20);
        bodyparts.Add(GameObject.Find("LowerTorso").GetComponent<BodyPart>());
        bodyparts.Add(GameObject.Find("Neck").GetComponent<BodyPart>());
        bodyparts.Add(GameObject.Find("Head").GetComponent<BodyPart>());
        bodyparts.Add(GameObject.Find("LeftShoulder").GetComponent<BodyPart>());
        bodyparts.Add(GameObject.Find("LeftUpperArm").GetComponent<BodyPart>());
        bodyparts.Add(GameObject.Find("LeftForearm").GetComponent<BodyPart>());
        bodyparts.Add(GameObject.Find("LeftHand").GetComponent<BodyPart>());
        bodyparts.Add(GameObject.Find("RightShoulder").GetComponent<BodyPart>());
        bodyparts.Add(GameObject.Find("RightUpperArm").GetComponent<BodyPart>());
        bodyparts.Add(GameObject.Find("RightForearm").GetComponent<BodyPart>());
        bodyparts.Add(GameObject.Find("RightHand").GetComponent<BodyPart>());
        bodyparts.Add(GameObject.Find("LeftHip").GetComponent<BodyPart>());
        bodyparts.Add(GameObject.Find("LeftThigh").GetComponent<BodyPart>());
        bodyparts.Add(GameObject.Find("LeftCalf").GetComponent<BodyPart>());
        bodyparts.Add(GameObject.Find("LeftFoot").GetComponent<BodyPart>());
        bodyparts.Add(GameObject.Find("RightHip").GetComponent<BodyPart>());
        bodyparts.Add(GameObject.Find("RightThigh").GetComponent<BodyPart>());
        bodyparts.Add(GameObject.Find("RightCalf").GetComponent<BodyPart>());
        bodyparts.Add(GameObject.Find("RightFoot").GetComponent<BodyPart>());
        bodyparts.Add(GameObject.Find("UpperTorso").GetComponent<BodyPart>());

        tmpPos = new List<Vector3>(maxMeshSize);
        tmpMpos = new List<Vector3>(maxMeshSize);
        tmpCols = new List<Color32>(maxMeshSize);

        positions = new List<Vector3>(maxMeshSize);
        mirroredPositions = new List<Vector3>(maxMeshSize);
        colors = new List<Color32>(maxMeshSize);

    }

    //void Update()
    //{
        //test();
        //bodyparts[3].Positions.Clear();
        //bodyparts[3].Positions.AddRange(tmpPos);
        //bodyparts[3].Colors.Clear();
        //bodyparts[3].Colors.AddRange(tmpCols);
        //bodyparts[3].MirroredPositions.Clear();
        //bodyparts[3].MirroredPositions.AddRange(tmpMpos);
        //bodyparts[3].updated = true;
        
        //if (KeyInputs.assignVoxel)
        //{
        //    assignVoxeltoBodyParts();
        //}          
        //else {
        //    bodyparts[0].Positions = positions;
        //    bodyparts[0].Colors = colors;
        //    bodyparts[0].MirroredPositions = mirroredPositions;
        //    bodyparts[0].updated = true;
        //}
    //}

    new void LateUpdate()
    {
        if (updated)
        {
            capture = true;
            if (KeyInputs.assignVoxel)
            {
                assignVoxeltoBodyParts();
            }
            else
            {
                bodyparts[0].Positions.Clear();
                bodyparts[0].Colors.Clear();
                bodyparts[0].MirroredPositions.Clear();
                bodyparts[0].Positions.AddRange(positions);
                bodyparts[0].Colors.AddRange(colors);
                bodyparts[0].MirroredPositions.AddRange(mirroredPositions);
                bodyparts[0].updated = true;
            }

            updated = false;
        }

        
        //base.LateUpdate();

        //assign to BodyParts, BodyParts.updated = true, don't call base.LateUpdate()

    }

    //void assignVoxeltoBodyParts()
    //{
    //    Mesh box;
    //    bool result;
    //    Transform t;

    //    for(int i = 1; i <= bodyparts.Count; i++)
    //    {
    //        box = skeleton.joints[i].GetComponent<MeshFilter>().mesh;
    //        t = skeleton.joints[i].transform;
    //        calculatePlanes(box, t);

    //        bodyparts[i - 1].Positions.Clear();
    //        bodyparts[i - 1].Colors.Clear();
    //        bodyparts[i - 1].MirroredPositions.Clear();

    //        for (int j = 0; j < positions.Count; j++)
    //        {
    //            result = PointInBox(positions[j]);
    //            if(result)
    //            {
    //                bodyparts[i - 1].Positions.Add(positions[j]);
    //                bodyparts[i - 1].Colors.Add(colors[j]);
    //                bodyparts[i - 1].MirroredPositions.Add(mirroredPositions[j]);
    //                //Debug.Log("inside!!");
    //                //positions.RemoveAt(j);
    //            }
    //        }
    //        bodyparts[i - 1].updated = true;
    //    }
    //}

    void assignVoxeltoBodyParts()
    {
        bool result;
        Transform t;
        Vector3 dir, pos;
        Bone parent;

        for (int i = 1; i <= bodyparts.Count; i++)
        {
            t = skeleton.joints[i].transform;
            dir = skeleton.bones[i].direction;
            parent = skeleton.bones[skeleton.bones[i].parent];
            pos = parent.origin;

            bodyparts[i - 1].Positions.Clear();
            bodyparts[i - 1].Colors.Clear();
            bodyparts[i - 1].MirroredPositions.Clear();
            //bodyparts[i - 1].transform.position = skeleton.joints[i].transform.position;
            //bodyparts[i - 1].transform.rotation = skeleton.joints[i].transform.rotation;
            //bodyparts[i - 1].transform.localScale = skeleton.joints[i].transform.localScale;

            float lengthsq = t.localScale.y * t.localScale.y;
            float radiussq = (t.localScale.x / 2) * (t.localScale.x / 2);

            for (int j = 0; j < positions.Count; j++)
            {
                result = PointInCylinder(pos, dir, lengthsq, radiussq, positions[j]);
                if (result)
                {
                    //bodyparts[i - 1].Positions.Add(t.InverseTransformPoint(positions[j]));
                    bodyparts[i - 1].Positions.Add(positions[j]);
                    bodyparts[i - 1].Colors.Add(colors[j]);
                    bodyparts[i - 1].MirroredPositions.Add(mirroredPositions[j]);

                    //Debug.Log("inside!!");
                    //positions.RemoveAt(j);
                }
            }
            bodyparts[i - 1].updated = true;
        }
    }

    public void clearBodyParts()
    {
        for(int i = 0; i < bodyparts.Count; i++)
        {
            bodyparts[i].Positions.Clear();
            bodyparts[i].Colors.Clear();
            bodyparts[i].MirroredPositions.Clear();
            bodyparts[i].setMeshesToActive();
            bodyparts[i].updated = true;
        }

    }

    void calculatePlanes(Mesh box, Transform t)
    {
        for(int i = 0; i < 6; i++)
        {
            planes[i].point = t.TransformPoint(box.vertices[i * 4]);
            planes[i].normal = t.TransformDirection(box.normals[i * 4].normalized);
        }
    }

    /// <summary>
    /// tests if a point is inside a cuboid
    /// </summary>
    /// <param name="p"></param>
    /// <param name="planes"></param>
    /// <returns></returns>
    bool PointInBox(Vector3 p)
    {
        Vector3 toPoint;

        for (int i = 0; i < 6; i++)
        {
            toPoint = p - planes[i].point;
            
            if (Vector3.Dot(toPoint, planes[i].normal) > 0)
                return false;
        }
        return true;

    }

    /// <summary>
    /// tests if a point lays inside a cylinder
    /// </summary>
    /// <returns></returns>
    bool PointInCylinder(Vector3 pt1, Vector3 dir, float lengthsq, float radius_sq, Vector3 testpt)
    {
        //float dx, dy, dz;	    // vector d  from line segment point 1 to point 2
        float pdx, pdy, pdz; 	// vector pd from point 1 to test point
        float dot, dsq;

        //dx = pt2.x - pt1.x;     // translate so pt1 is origin.  Make vector from
        //dy = pt2.y - pt1.y;     // pt1 to pt2.  Need for this is easily eliminated
        //dz = pt2.z - pt1.z;

        pdx = testpt.x - pt1.x;		// vector from pt1 to test point.
	    pdy = testpt.y - pt1.y;
	    pdz = testpt.z - pt1.z;

        dot = pdx * dir.x + pdy * dir.y + pdz * dir.z;

        if (dot < 0.0f || dot > lengthsq)
        {
            return false;
        }
        else
        {
            dsq = (pdx * pdx + pdy * pdy + pdz * pdz) - dot * dot / lengthsq;

            if (dsq > radius_sq)
            {
                return false;
            }
            else
            {
                return true;       // return distance squared to axis
            }
        }
    }

}
