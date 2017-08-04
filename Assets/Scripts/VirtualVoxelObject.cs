using UnityEngine;
using System;
using System.Collections;
using System.Collections.Generic;

public class VirtualVoxelObject : VoxelObject {

    GameObject virtualModel;            //the model to voxelize
    MeshRenderer[] virtualChilds;
    List<Vector3> modelPos;             //the positions of the model seen by the kinect in world coordinates
    List<int> rayIndices;
    List<Vector3> originPos;
    List<Color32> originCol;
    List<Vector3> tmpNormals;
    List<float> distances;
    List<float> radial;
    float rate = 1f / 30f;
    enum intersection { OUTSIDE, INTERSECT, INSIDE };
    int layer, layerMask;
    static System.Random random = new System.Random();

    new void Start()
    {
        base.Start();
        modelPos = new List<Vector3>();
        rayIndices = new List<int>();
        normals = new List<Vector3>(3*maxMeshSize);
        tmpNormals = new List<Vector3>(meshObjects.Count * maxMeshSize);
        originPos = new List<Vector3>(3 * maxMeshSize);
        originCol = new List<Color32>(3 * maxMeshSize);
        distances = new List<float>();
        radial = new List<float>();
        virtualModel = getModel();

        layer = 8;                      //layer for the virtual objects, only the meshes in this layer will be hit by a raycast
        layerMask = 1 << 8;             //bit mask to test against layer 8 (voxelize) 

        if (virtualModel == null)
            Debug.Log("No model found to voxelize. Please attach model with the tag VirtualObjectModel");
        else
        {
            //Debug.Log(virtualModel.name + "   " + gameObject.name);
            
            float start = Time.realtimeSinceStartup;
            //voxelizeVirtualModel();
            voxelizeVirtualModelWithRay();

            //backfaceCulling();
            //frustumCulling();
            //selfOcclusion();

            float end = Time.realtimeSinceStartup;
            //Debug.Log("raycasted in: " + (end-start));

            originPos.Clear();
            originPos.AddRange(positions);
            originCol.Clear();
            originCol.AddRange(colors);

            virtualModel.transform.hasChanged = false;
            updated = true;
            enableMeshes();

            calculateDistances();
            calculateRadialDistance();
            InvokeRepeating("noise", 2, rate);

        }

    }

    void Update()
    {
        //time = Time.time;
        if (virtualModel.transform.hasChanged)
        {
            //voxelizeVirtualModel();

           // backfaceCulling();
            //frustumCulling();
            //selfOcclusion();

            voxelizeVirtualModelWithRay();

            originPos.Clear();
            originPos.AddRange(positions);
            originCol.Clear();
            originCol.AddRange(colors);

            calculateDistances();
            calculateRadialDistance();

            virtualModel.transform.hasChanged = false;
            updated = true;
        }
        //else
        //{
        //    //if (!KeyInputs.updateStopped)
        //    //{
        //        noise();
        //        latestTime = time;
        //        updated = true;
        //    //}

        //}
    }

    new void LateUpdate()
    {
        if (updated)
        {
            voxelToMesh();
            updated = false;
        }
    }

    new void voxelToMesh()
    {
        int count = positions.Count;
        int rest = count;

        decimal neededMeshes = Math.Ceiling((decimal)count / (decimal)maxMeshSize);
        neededMeshes = Math.Min(neededMeshes, meshObjects.Count);                                     // workaround for fixed number of meshobjects, if too many voxel they are discarded

        for (int i = 0; i < meshObjects.Count; i++)
        {
            if (i < neededMeshes)
            {
                meshObjects[i].gameObject.SetActive(true);
                mirroredMeshObjects[i].gameObject.SetActive(true);
                
                tmpPos.Clear();
                tmpMpos.Clear();
                tmpCols.Clear();
                tmpNormals.Clear();

                tmpPos.AddRange(positions);
                tmpMpos.AddRange(mirroredPositions);
                tmpCols.AddRange(colors);
                tmpNormals.AddRange(normals);

                if (rest - maxMeshSize <= 0)
                {
                    tmpPos.RemoveRange(0, count - rest);
                    tmpCols.RemoveRange(0, count - rest);
                    tmpMpos.RemoveRange(0, count - rest);
                    tmpNormals.RemoveRange(0, count - rest);

                    meshObjects[i].updateMesh(tmpPos, tmpCols, tmpNormals);
                    mirroredMeshObjects[i].updateMesh(tmpMpos, tmpCols, tmpNormals);

                }
                else
                {
                    tmpPos.RemoveRange(0, i * maxMeshSize);
                    tmpPos.RemoveRange(maxMeshSize, tmpPos.Count - maxMeshSize);
                    tmpCols.RemoveRange(0, i * maxMeshSize);
                    tmpCols.RemoveRange(maxMeshSize, tmpCols.Count - maxMeshSize);
                    tmpMpos.RemoveRange(0, i * maxMeshSize);
                    tmpMpos.RemoveRange(maxMeshSize, tmpMpos.Count - maxMeshSize);
                    tmpNormals.RemoveRange(0, i * maxMeshSize);
                    tmpNormals.RemoveRange(maxMeshSize, tmpNormals.Count - maxMeshSize);

                    meshObjects[i].updateMesh(tmpPos, tmpCols, tmpNormals);
                    mirroredMeshObjects[i].updateMesh(tmpMpos, tmpCols, tmpNormals);

                }
                rest -= maxMeshSize;
            }
            else
            {
                meshObjects[i].gameObject.SetActive(false);
                mirroredMeshObjects[i].gameObject.SetActive(false);
            }

        }
    }

    /// <summary>
    /// returns the GameObject that holds the meshes we want to voxelize (needs the tag "VirtualObjectModel") or null if there is none attached
    /// </summary>
    /// <returns></returns>
    GameObject getModel()
    {
        Transform[] childTansformations = gameObject.GetComponentsInChildren<Transform>();
        Transform child;
        for(int i = 0; i<childTansformations.Length; i++)
        {
            child = childTansformations[i];
            if (child.tag == "VirtualObjectModel")
               return child.gameObject;
        }
        return null;
    }

    void calculateDistances()
    {
        float distance;
        distances.Clear();

        for (int i = 0; i < positions.Count; i++)
        {
            distance = (positions[i] - manager.kinectPosition).magnitude;       //squared magnitude is faster, maybe  enough?
            distances.Add(distance);
        }
    }

    void calculateRadialDistance()
    {

        radial.Clear();

        Vector3 p, nc, fc;
        nc = manager.kinectNearPlaneCentre;
        fc = manager.kinectFarPlaneCentre;
        float denominator = (fc - nc).magnitude;
        float nominator, radD;

        for(int i = 0; i < positions.Count; i++)
        {
            p = positions[i];
            nominator = Vector3.Cross((p - nc), (p - fc)).magnitude;
            radD = nominator / denominator;
            radial.Add(radD);
        }
        

    }

    void raycastVirtualModel()
    {

        virtualChilds = virtualModel.GetComponentsInChildren<MeshRenderer>();

        if (virtualChilds.Length > 1)
            raycastMultiple();
        else raycast();

    }

    void raycastMultiple()
    {
            Mesh mesh;
            //Vector3 pixelPos;
            Ray r;
            RaycastHit hit = new RaycastHit();
            Transform objtransform;
            childsToVoxelizeLayer();
                    
            for (int i = 0; i < manager.kinectWidth; i++)                                            //x resolution of the kinect image
            {
                for (int j = 0; j < manager.kinectHeight; j++)                                       //y resolution
                {
                //pixelPos = new Vector3(i, j, 0);
                //r = kinect.ScreenPointToRay(pixelPos);                                             //ray through i,j pixel

                    r = manager.kinectRays[i][j];


                    if (Physics.Raycast(r, out hit, 4.5f, layerMask))                                //raycast for the ray and the collider of the virtual object, true if it hits
                    {

                            mesh = hit.collider.gameObject.GetComponent<MeshFilter>().mesh;
                            objtransform = hit.collider.gameObject.transform;
                            int[] meshTris = mesh.triangles;
                            Color32[] meshCols = mesh.colors32;
                            Vector3[] meshNormals = mesh.normals;

                            //Debug.DrawRay(r.origin,r.direction,Color.red, 100.0f);
                            modelPos.Add(hit.point);
                            rayIndices.AddRange(new int[] { i, j });
                            tmpNormals.Add(objtransform.TransformDirection(meshNormals[meshTris[hit.triangleIndex * 3]]));           //normal of the first vertex of the hit triangle
                            tmpCols.Add(meshCols[meshTris[hit.triangleIndex * 3]]);                                                  //color of the first vertex of the hit triangle

                    }
                }
            }              

        childsBackToDefaultLayer();
    }

    void raycast()
    {
        MeshRenderer child;
        Mesh mesh;
        //Vector3 pixelPos;
        Ray r;
        RaycastHit hit = new RaycastHit();

        child = virtualChilds[0];
        Transform objtransform = child.gameObject.transform;
        mesh = child.gameObject.GetComponent<MeshFilter>().mesh;
        int[] meshTris = mesh.triangles;
        Color32[] meshCols = mesh.colors32;
        Vector3[] meshNormals = mesh.normals;

        Collider coll = child.gameObject.GetComponent<Collider>();
        if (coll == null)
            coll = child.gameObject.AddComponent<MeshCollider>();
        coll.enabled = true;

        for (int i = 0; i < manager.kinectWidth; i++)                             //x resolution of the kinect image
        {
            for (int j = 0; j < manager.kinectHeight; j++)                        //y resolution
            {
                //pixelPos = new Vector3(i, j, 0);
                //r = kinect.ScreenPointToRay(pixelPos);                          //ray through i,j pixel

                r = manager.kinectRays[i][j];

                if (coll.Raycast(r, out hit, 4.5f))                              //raycast for the ray and the collider of the virtual object, true if it hits
                {
                    //Debug.DrawRay(r.origin,r.direction,Color.red, 100.0f);
                    modelPos.Add(hit.point);

                    tmpNormals.Add(objtransform.TransformDirection(meshNormals[meshTris[hit.triangleIndex * 3]]));           //normal of the first vertex of the hit triangle transformed to world coordinates
                    //tmpCols.Add(Color.white);
                    tmpCols.Add(meshCols[meshTris[hit.triangleIndex * 3]]);                   //color of the first vertex of the hit triangle
                }
            }
        }
        coll.enabled = false;

    }

    void childsToVoxelizeLayer()
    {
        for(int i = 0; i<virtualChilds.Length; i++)
        {
            virtualChilds[i].gameObject.layer = layer;
            Collider coll = virtualChilds[i].gameObject.GetComponent<Collider>();
            if(coll == null)
            {
               coll = virtualChilds[i].gameObject.AddComponent<MeshCollider>();
            }
            coll.enabled = true;
        }
    }

    void childsBackToDefaultLayer()
    {
        for (int i = 0; i < virtualChilds.Length; i++)
        {
            virtualChilds[i].gameObject.layer = 0;
            Collider coll = virtualChilds[i].gameObject.GetComponent<Collider>();
            coll.enabled = false;
        }
    }

    void mapToVoxelSpace(Vector3 pos, int index)
    {
        int x, y, z;
        Vector3 voxelPosition = new Vector3(1000, 1000, 1000);

        x = (int)Math.Truncate(pos.x / manager.voxelSize + manager.voxelspace.width/2);
        y = (int)Math.Truncate(pos.y / manager.voxelSize);
        z = (int)Math.Truncate(pos.z / manager.voxelSize);

        if (x > 0 && x < manager.voxelspace.width && y > 0 && y < manager.voxelspace.height && z > 0 && z < manager.voxelspace.depth && !manager.voxelspace.voxelSet[x, y, z])
        {            
            voxelPosition = new Vector3(x * manager.voxelSize, y * manager.voxelSize, z * manager.voxelSize) + manager.voxelspace.origin;                //translate from voxelspace coordinates to world coordinates
            
            positions.Add(voxelPosition);
            mirroredPositions.Add(new Vector3(voxelPosition.x, voxelPosition.y, -voxelPosition.z));
            normals.Add(tmpNormals[index]);
            colors.Add(tmpCols[index]);
            manager.voxelspace.voxelSet[x, y, z] = true;
        }
    }

    void voxelizeVirtualModelWithRay()
    {
        manager.voxelspace.voxelSet = new bool[manager.voxelspace.width, manager.voxelspace.height, manager.voxelspace.depth];

        positions.Clear();
        mirroredPositions.Clear();
        colors.Clear();
        normals.Clear();
        modelPos.Clear();
        tmpNormals.Clear();
        tmpCols.Clear();

        raycastVirtualModel();

        //positions = modelPos;
        //normals = tmpNormals;
        for (int i = 0; i < modelPos.Count; i++)
        {
            mapToVoxelSpace(modelPos[i], i);
        }
    }


    void voxelizeVirtualModel()
    {
        //Debug.Log("start voxelize: " + virtualModel.name);
        manager.voxelspace.positions.Clear();
        manager.voxelspace.mirroredPositions.Clear();
        manager.voxelspace.colors.Clear();
        manager.voxelspace.normals.Clear();

        manager.voxelspace.voxelSet = new bool[manager.voxelspace.width,manager.voxelspace.height,manager.voxelspace.depth];

        MeshRenderer[] virtualchilds = virtualModel.GetComponentsInChildren<MeshRenderer>();
        MeshRenderer child;
        for(int i = 0; i<virtualchilds.Length; i++)
        {
            child = virtualchilds[i];
            //Debug.Log("child: " + child.gameObject);
            manager.voxelspace.fillGridFromObject(child.gameObject);
            //Debug.Log("positions: " + manager.voxelspace.positions.Count);
        }
        positions.Clear();
        mirroredPositions.Clear();
        colors.Clear();
        normals.Clear();
        positions.AddRange(manager.voxelspace.positions);
        mirroredPositions.AddRange(manager.voxelspace.mirroredPositions);
        colors.AddRange(manager.voxelspace.colors);
        normals.AddRange(manager.voxelspace.normals);
        //Debug.Log("end voxelize: " + virtualModel.name);
    }

    void enableMeshes()
    {
        MeshRenderer[] children = virtualModel.GetComponentsInChildren<MeshRenderer>();
        for(int i = 0; i < children.Length; i++)
        {
            children[i].enabled = false;
        }
    }

    void backfaceCulling()
    {

        Vector3 kinectPos = manager.kinectPosition;
        Vector3 view;
        int voxel = positions.Count;
        Vector3 notSet = new Vector3(1000, 1000, 1000);
        for (int i = 0; i < voxel; i++)
        {
            view = positions[i] - kinectPos;
            if (Vector3.Dot(view, normals[i]) > 0)
            {
                positions[i] = notSet;
                mirroredPositions[i] = notSet;
            }
        }
    }

    void frustumCulling()
    {
        
        int voxel = positions.Count;
        int result = (int)intersection.INSIDE;
        //Transform kinectTransform = manager.calculateKinectTransform();
        //Matrix4x4 kinectProjection = Matrix4x4.Perspective(manager.kinectVfov, manager.kinectAspect, manager.kinectNearZ, manager.kinectFarZ);
        //Vector4 posWorld, posCamera, posClipping;
        Vector3 notSet = new Vector3(1000, 1000, 1000);
        for (int i = 0; i < voxel; i++)
        {
            result = PointinFrustum(positions[i]);
            if (result == (int)intersection.OUTSIDE)
            {
                 positions[i] = notSet;
                 mirroredPositions[i] = notSet;
            }
            
            //posWorld = new Vector4(positions[i].x, positions[i].y, positions[i].z, 1);
            //posCamera = kinectTransform.InverseTransformPoint(posWorld);                     //view matrix of the kinect: inverse transformation, posCamspace position in cameraspace/eye coordinates
            //posClipping = kinectProjection * posCamera;                                      //posClipspace position in clipping coordinates

            //if(posClipping.x<-posClipping.w && posClipping.x>posClipping.w || posClipping.y < -posClipping.w && posClipping.y > posClipping.w || posClipping.z < -posClipping.w && posClipping.z > posClipping.w)
            //{
            //    //Debug.Log("cliipped");
            //    positions[i] = notSet;
            //}
        }
    }

    //void selfOcclusion()
    //{
    //    int voxel = positions.Count;
    //    //Matrix4x4 kinectTransform = manager.calculateKinectTransform();
    //    Vector4 anfang = Vector4.zero;
    //    Vector3 ende = new Vector3(0.0f, 0.01f, 0.01f);
    //    //anfang = manager.kinect.WorldToViewportPoint(anfang);
    //    ende = manager.kinect.WorldToViewportPoint(ende);
    //    float bla = anfang.x;
    //    float ble = ende.x;
    //    Matrix4x4 kinectTransformation = manager.kinect.worldToCameraMatrix;
    //    anfang = kinectTransformation.MultiplyPoint(anfang);
    //    //Debug.Log("anfang: " + (anfang.y - ende.y) + " ende: " + anfang);


    //    Matrix4x4 kinectProjection = Matrix4x4.Perspective(manager.kinectVfov, manager.kinectAspect, manager.kinectNearZ, manager.kinectFarZ);
    //    Vector2[,] zbuffer = new Vector2[400, 400];             //x value is z in world coordinates from the camera, y value is index in positions
    //    Vector3 notSet = new Vector3(1000, 1000, 1000);
    //    Vector3 viewPoint;
    //    int x, y;
    //    int count = 0;
    //    float z;
    //    Vector2 buffer;
    //    //Debug.Log(manager.kinect.transform.position);
    //    //Debug.Log(zbuffer[1, 1].x == 0);
    //    //Vector4 posWorld, posCamera, posClipping, posNDC;
    //    //float min = 0;
    //    for (int i = 0; i < voxel; i++)
    //    {
    //        viewPoint = manager.kinect.WorldToViewportPoint(positions[i]);
    //        x = (int)(viewPoint.x * 400);
    //        y = (int)(viewPoint.y * 400);
    //        z = viewPoint.z;
    //        if (x < 0 || x > 255 || y < 0 || y > 211 || positions[i].z == 1000)
    //            continue;
    //        buffer = zbuffer[x, y];

    //        if (buffer.x == 0)
    //        {
    //            //for(int j = x - 2; j <= x + 2; j++)
    //            //{
    //            //    for(int l = y - 2; l <= y + 2; l++)
    //            //    {
    //            //        zbuffer[j, l].x = z;
    //            //        zbuffer[j, l].y = i;
    //            //    }
    //            //}
    //            zbuffer[x, y].x = z;
    //            zbuffer[x, y].y = i;
    //            continue;
    //        }

    //        if (buffer.x > z)
    //        {
    //            //for (int j = x - 2; j <= x + 2; j++)
    //            //{
    //            //    for (int l = y - 2; l <= y + 2; l++)
    //            //    {
    //            //        zbuffer[j, l].x = z;
    //            //        zbuffer[j, l].y = i;
    //            //    }
    //            //}
    //            zbuffer[x, y].x = z;
    //            zbuffer[x, y].y = i;
    //            count++;
    //            positions[(int)buffer.y] = notSet;
    //            mirroredPositions[(int)buffer.y] = notSet;
    //            continue;
    //        }
    //        if (buffer.x < z)
    //        {
    //            count++;
    //            positions[i] = notSet;
    //            mirroredPositions[i] = notSet;

    //        }


    //        //posWorld = new Vector4(positions[i].x, positions[i].y, positions[i].z, 1);
    //        ////posCamera =  posWorld;
    //        //posCamera = kinectTransformation.MultiplyPoint(posWorld);
    //        ////posCamera.w = 1.0f;
    //        //posClipping = kinectProjection.MultiplyPoint(posCamera);
    //        ////posNDC = posClipping / posClipping.w;
    //        //if (i == 500)
    //        //{
    //        ////    colors[i] = Color.red;
    //        //   Debug.Log("world: " + posWorld + "camera: " + posCamera + "clipping: " + posClipping + "ndc: " + posNDC);
    //        ////    Debug.Log("world: " + positions[i] + "viewport: " + manager.calculateKinectTransform(positions[i]));
    //        //}

    //        ////float ndc = posClipping.x / -posClipping.w;
    //        ////float ndc = posCamera.y;
    //        ////if (ndc > min)
    //        //// min = ndc;

    //    }
    //    //Debug.Log("notSet " + count);

    //}

    int PointinFrustum(Vector3 p)
    {
        int result = (int)intersection.INSIDE;
        float D, distance;
        for (int i = 0; i < 6; i++)
        {
            D = Vector3.Dot(-manager.planes[i].normal, manager.planes[i].point);
            distance = Vector3.Dot(manager.planes[i].normal, p) + D;
            if (distance < 0)
                return (int)intersection.OUTSIDE;
        }
        return (result);

    }

    public static double NextGaussianDouble(double stdDev)
    {
        double u, v, S;

        do
        {
            u = 2.0 * UnityEngine.Random.value - 1.0;
            v = 2.0 * UnityEngine.Random.value - 1.0;
            S = u * u + v * v;
        }
        while (S >= 1.0);

        double fac = Math.Sqrt(-2.0 * Math.Log(S) / S);
        return stdDev * u * fac;
    }

    void noise()
    {
        //noisePos.Clear();
        //noisePos.AddRange(positions);
        //int start = UnityEngine.Random.Range(0, 3);
        Vector3 kinectLookAt = manager.kinectLookAt;         //better the view ray for each position
        double gauss, stdDev;
        positions.Clear();
        positions.AddRange(originPos);
        colors.Clear();
        colors.AddRange(originCol);
        for(int i= 0; i<positions.Count; i++)
        {
            if (positions[i].x == 1000)                       //do not calculate noise for voxel that are not set
                continue;

            //d = distances[i];
            //prob = (d - 1) * (d - 1) - 0.5f;
            //changeVoxel(prob, i);

            //--------------------------------gauss----------------------------------------------------
            stdDev = 7.0 / 9.0 * distances[i] + 1.0/30.0;                //standard deviation depending on distance in mm
            stdDev = stdDev / 1000.0;                                    //in m
            gauss = NextGaussianDouble(stdDev) * 3;
            Vector3 step = (float)gauss * kinectLookAt.normalized;
            step = translateToVoxelSpace(step);


            //gauss = NextGaussianDouble(0.003);
            //gauss = translateToVoxelSpace(gauss);
            
            //if (gauss < 0)
            //    Debug.Log("negativ");
            //positions[i] = originPos[i] + new Vector3(0.0f, (float)gauss, 0.0f);


            positions[i] = originPos[i] + step;
            //------------------------------------------------------------------------------------------

            mirroredPositions[i] = new Vector3(positions[i].x, positions[i].y, -positions[i].z);

            //positions[i + 15] = originPos[i + 10] + new Vector3(UnityEngine.Random.Range(-1, 2) / 100.0f, 0.0f, 0.0f);
            //positions[i + 30] = originPos[i + 20] + new Vector3(0.0f, 0.0f, UnityEngine.Random.Range(-1, 2) / 100.0f);
            //for (int j = i; j<i+10; j++)
            //{
            //    colors[j] = originCol[j + 2];
            //}
            //positions[i] = originPos[i] + new Vector3(UnityEngine.Random.Range(-1, 2) / 100.0f, UnityEngine.Random.Range(-1, 2) / 100.0f, UnityEngine.Random.Range(-1, 2) / 100.0f);
        }

        updated = true;
    }

    void modelNoise()
    {
        manager.voxelspace.voxelSet = new bool[manager.voxelspace.width, manager.voxelspace.height, manager.voxelspace.depth];
        double gauss, stdDev;
        positions.Clear();
        mirroredPositions.Clear();
        colors.Clear();
        normals.Clear();
        modelPos.Clear();
        modelPos.AddRange(originPos);
        Ray lookAt;

        for (int i = 0; i < tmpCols.Count; i++)
        {
            lookAt = manager.kinectRays[rayIndices[i]][rayIndices[i+1]];
            //--------------------------------gauss----------------------------------------------------
            stdDev = 7.0 / 9.0 * distances[i] + 1.0 / 30.0;                //standard deviation depending on distance in mm
            stdDev = stdDev / 1000.0;                                    //in m
            gauss = NextGaussianDouble(stdDev) * 2;
            //gauss = translateToVoxelSpace(gauss);
            Vector3 step = (float)gauss * lookAt.direction.normalized;
            //step = translateToVoxelSpace(step);
            //if (gauss < 0)
            //    Debug.Log("negativ");
            //positions[i] = originPos[i] + new Vector3(0.0f, (float)gauss, 0.0f);
            modelPos[i] = originPos[i] + step;
            mapToVoxelSpace(modelPos[i], i);

            //------------------------------------------------------------------------------------------

            //mirroredPositions[i] = new Vector3(positions[i].x, positions[i].y, -positions[i].z);

            //positions[i + 15] = originPos[i + 10] + new Vector3(UnityEngine.Random.Range(-1, 2) / 100.0f, 0.0f, 0.0f);
            //positions[i + 30] = originPos[i + 20] + new Vector3(0.0f, 0.0f, UnityEngine.Random.Range(-1, 2) / 100.0f);
            //for (int j = i; j<i+10; j++)
            //{
            //    colors[j] = originCol[j + 2];
            //}
            //positions[i] = originPos[i] + new Vector3(UnityEngine.Random.Range(-1, 2) / 100.0f, UnityEngine.Random.Range(-1, 2) / 100.0f, UnityEngine.Random.Range(-1, 2) / 100.0f);
        }

        updated = true;
    }

    Vector3 translateToVoxelSpace(Vector3 step)
    {
        Vector3 result;
        if (Math.Abs(step.x) < manager.voxelSize)
            result.x = 0.0f;
        else result.x = (float) Math.Truncate(step.x / manager.voxelSize) * manager.voxelSize;

        if (Math.Abs(step.y) < manager.voxelSize)
            result.y = 0.0f;
        else result.y = (float)Math.Truncate(step.y / manager.voxelSize) * manager.voxelSize;

        if (Math.Abs(step.z) < manager.voxelSize)
            result.z = 0.0f;
        else result.z = (float)Math.Truncate(step.z / manager.voxelSize) * manager.voxelSize;

        return result;
    }
    double translateToVoxelSpace(double gauss)
    {
        double result;
        if (Math.Abs(gauss) < manager.voxelSize)
            result = 0.0f;
        else
            result = Math.Truncate(gauss / manager.voxelSize) * manager.voxelSize;
            
        return result;
    }
    void changeVoxel(float prob, int index)
    {
        double rand = random.NextDouble();
        if(rand < prob)
        {
            //positions[index] = originPos[index] + new Vector3(0.0f, UnityEngine.Random.Range(-1, 2) / 125.0f, 0.0f);
            colors[index] = Color.red;
        }

    }
}
