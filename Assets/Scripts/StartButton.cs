using UnityEngine;
using System;
using System.Collections;

/// <summary>
/// Used in the Setup Scene to start Booth expierence.
/// </summary>
public class StartButton : MonoBehaviour {

    public event Action OnKeyPressed;

    void Update()
    {
        if (Input.GetKeyDown(KeyCode.J)) { 

                OnKeyPressed();
        } 
    }
    	
}
