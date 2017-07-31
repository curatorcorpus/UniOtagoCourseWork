using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public unsafe class Voxel
{
    private bool dataExists;

    private char* _label;
    private Color32 _colour;

    public Voxel()
    {
        dataExists = false;
        _label = null;
        _colour = new Color32(0, 128, 192, 255);
    }

    public Voxel(char* label, Color32 colour)
    {
        _label = label;
        _colour = colour;
    }

    public bool DataExists
    {
        get { return dataExists;  }
        set { dataExists = value;  }
    }

    public char* Label
    {
        get { return _label; }
        set { _label = value; }
    }

    public Color32 Colour
    {
        get { return _colour; }
        set { _colour = value; }
    }
}

