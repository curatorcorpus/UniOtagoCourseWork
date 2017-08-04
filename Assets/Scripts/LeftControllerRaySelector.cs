using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class LeftControllerRaySelector : MonoBehaviour {

    private int virtualObjects = 0;
    private int rayCastPresses = 0;
    private int correctGuesses = 0;
    private int wrongGuesses   = 0;

    private bool castRay;
    private Ray theRay;

    public LayerMask theMask;
    public LineRenderer rayLine;

	// Use this for initialization
	void Start () {
        castRay = false;

    }
	
	// Update is called once per frame
	void Update () {

        // change line color to yellow
        if (OVRInput.GetDown(OVRInput.Button.PrimaryIndexTrigger))
        {
            castRay = true;

            rayLine.startColor = Color.yellow;
            rayLine.endColor = Color.yellow;

            rayCastPresses++;
        }

        // change line color back to default blue
        if (OVRInput.GetUp(OVRInput.Button.PrimaryIndexTrigger))
        {
            castRay = false;

            rayLine.startColor = Color.blue;
            rayLine.endColor = Color.blue;
        }
    }

    void FixedUpdate()
    {
        RaycastHit hitInfo;

        // cast ray
        if (castRay)
        {
            Vector3 forward = transform.TransformDirection(Vector3.forward) * 10;
            Debug.DrawRay(transform.position, forward, Color.blue);

            if (Physics.Raycast(transform.position, forward, out hitInfo))
            {
                GameObject hitObject = hitInfo.collider.gameObject;
                Debug.Log(hitObject);
                if(hitObject.name == "chairCombinedDynamicPaintblend")
                {
                    hitObject.SetActive(false);
                    correctGuesses++;
                }
            }

            summary();
        }
    }

    private void summary()
    {
        Debug.Log("Number of ray triggers: " + rayCastPresses);
        Debug.Log("Correct Guesses: " + correctGuesses);
        //Debug.Log("");
    }
}
