using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.XR.WSA;

public class AnchorStorage : MonoBehaviour {
    private Vector3[] anchs;
    public int size = 0;
    private int position = 0;
    private Vector3 invis = new Vector3(0, 0, 0);
    private Vector3 origin;

	// Use this for initialization
	void Start () {
        anchs = new Vector3[size];
        origin = gameObject.transform.localScale;

    }

    // Update is called once per frame
    void Update()
    {
        if (size < 4) { 
            if (position == 0)
            {
                gameObject.transform.localScale = invis;
            }
            else if (position == 1)
            {
                gameObject.transform.localPosition = anchs[0];
                gameObject.transform.localScale = origin;
            }
            else if (position == 2)
            {
                Vector3 look = Quaternion.LookRotation(anchs[1] - anchs[0]).eulerAngles;
                gameObject.transform.localRotation = Quaternion.Euler(look.z, look.y - 90, -look.x);
                gameObject.transform.localScale = origin;
            }
        }
        else
        {
            if (position < 6) 
            {
                gameObject.transform.localScale = invis;
            }
            else
            {
                gameObject.transform.localScale = origin;
                Vector3 pos;
                Vector3 x = (anchs[0] + anchs[1])/2;
                Vector3 y = (anchs[4] + anchs[5])/2;
                Vector3 z = (anchs[2] + anchs[3])/2;
                pos = Vector3.Cross(Vector3.Cross(x,y),z);
                Vector3 zz = anchs[2] - anchs[3];
                Vector3 xx = anchs[0] - anchs[1];
                Vector3 xprojz = Vector3.Project(x-z, zz);
                Debug.Log(x);
                Debug.Log(z+xprojz);
                Debug.Log(xx);
                Debug.Log(zz);
                Debug.Log(xprojz);
                Vector3 zprojx = Vector3.Project(z + xprojz, xx);
                Debug.Log(zprojx);
                gameObject.transform.localPosition = new Vector3(0,y.y-1.35f,xprojz.z-1.8f);
                Vector3 look = Quaternion.LookRotation(anchs[2] - gameObject.transform.localPosition).eulerAngles;
                gameObject.transform.localRotation = Quaternion.Euler(0, look.y - 90, -0);
            }
        }
		
	}

     public void addAnchor(Vector3 a)
    {
        anchs[position] = a;
        position++;
    }

    public Vector3[] getAnchs()
    {
        Vector3[] anchsSet = new Vector3[position];
        for (int i = 0; i < position; i++)
        {
            anchsSet[i] = anchs[i];
        }
        return anchsSet;
    }
}
