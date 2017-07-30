using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Voxel {

    //private char* label;
    private Color32 colour;
    
    public Voxel(string lab, Color32 colour)
    {
        //fixed (char* label = lab) { }
        //this.label = label;
        this.colour = colour;  
    }

    public Color32 Colour
    {
        get { return colour; }
        set { colour = value; }
    }
}
