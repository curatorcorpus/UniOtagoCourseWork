using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class RightControllerRaySelector : MonoBehaviour {


    private bool castRay;

    private RaycastHit hitInfo;
    private Ray theRay;

    public LayerMask theMask;
    public LineRenderer rayLine;

	// Use this for initialization
	void Start () {

        castRay = false;
        theRay = new Ray(transform.position, Vector3.forward);
    }
	
	// Update is called once per frame
	void Update () {

        // change line color to yellow
        if (OVRInput.GetDown(OVRInput.Button.SecondaryIndexTrigger)) 
        {
            castRay = true;

            rayLine.startColor = Color.yellow;
            rayLine.endColor = Color.yellow;
        }

        // change line color back to default blue
        if(OVRInput.GetUp(OVRInput.Button.SecondaryIndexTrigger))
        {
            castRay = false;

            rayLine.startColor = Color.blue;
            rayLine.endColor = Color.blue;
        }
    }

    void FixedUpdate()
    {
        // cast ray
        if (castRay)
        {
            //Debug.DrawRay(transform.position, transform.TransformDirection(Vector3.forward) * 1000, Color.blue);

            if (Physics.Raycast(theRay, out hitInfo))
            {
                Debug.Log(hitInfo.collider.tag == "chair");
                if (hitInfo.collider.tag == "chair")
                {
                    Debug.Log("chair detected");
                }
            }
        }
    }
}
