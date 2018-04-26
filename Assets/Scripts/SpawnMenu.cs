using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using HoloToolkit.Unity.InputModule;
using System.IO;
using UnityEngine.UI;
using System.Diagnostics;
using System.Runtime.CompilerServices;
using System.Threading;
using System;
using UnityEngine.WSA;

#if ENABLE_WINMD_SUPPORT
using Windows.Storage;
using Windows.Storage.Streams;
using Windows.Storage.Pickers;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using HoloToolkit.Unity.InputModule;
using System.IO;
using UnityEngine.UI;
using System.Diagnostics;
using System.Runtime.CompilerServices;
using System.Threading;
using System;
using UnityEngine.WSA;
#endif

public class SpawnMenu : MonoBehaviour, IInputHandler
{
    public Canvas canvas;
    public Text label, text;

#if ENABLE_WINMD_SUPPORT
    private FileOpenPicker openPicker;
#endif

    void Start()
    {
        text = canvas.GetComponent<Text>();
      //  UnityEngine.Debug.LogFormat("UnityThread: {0}", Thread.CurrentThread.ManagedThreadId);
    }

    public void OnInputDown(InputEventData e)
    {
#if ENABLE_WINMD_SUPPORT
        UnityEngine.WSA.Application.InvokeOnUIThread(OpenFileAsync, false);  
#else
        text.text = "ENABLE_WINMD_SUPPORT FALSE";
#endif
        //text.text = UnityEngine.Application.dataPath;
    }

    public void OnInputUp(InputEventData e)
    {

    }

    void ThreadCallback()
    {
        UnityEngine.Debug.LogFormat("ThreadCallback() on thread: \t{0}", Thread.CurrentThread.ManagedThreadId);
    }

#if ENABLE_WINMD_SUPPORT
    public async void OpenFileAsync()
    {  
       // UnityEngine.Debug.LogFormat( "OpenFileAsync() on Thread: {0}", Thread.CurrentThread.ManagedThreadId );

        openPicker = new FileOpenPicker();
 
        //openPicker.ViewMode = PickerViewMode.Thumbnail;
        //openPicker.SuggestedStartLocation = PickerLocationId.Objects3D;
        //openPicker.FileTypeFilter.Add(".fbx");
        openPicker.FileTypeFilter.Add("*");
     
        StorageFile file = await openPicker.PickSingleFileAsync();
        string labelText = String.Empty;
        if ( file != null )
        {
            // Application now has read/write access to the picked file 
            labelText = "Picked file: " + file.DisplayName;
        }
        else
        {
            // The picker was dismissed with no selected file 
            labelText = "File picker operation cancelled";
        }
        
        UnityEngine.Debug.Log( labelText );

     //   UnityEngine.WSA.Application.InvokeOnAppThread( ThreadCallback, false );

     //   UnityEngine.WSA.Application.InvokeOnAppThread( new AppCallbackItem( () => { label.text = labelText; } ), false );
    }

#endif

}
/*
public class SpawnMenu : MonoBehaviour, IInputHandler
{
    public Canvas canvas;

    public void Start()
    {
        Text text = canvas.GetComponent<Text>();
        text.text = "Started (Editor)";
    }

    public void OpenFile()
    {
    }

    public void OnInputDown(InputEventData e)
    {
        Text text = canvas.GetComponent<Text>();
        text.text = UnityEngine.Application.dataPath;
    }

    public void OnInputUp(InputEventData e)
    {

    }
}*/

/*
public class SpawnMenu : MonoBehaviour, IInputHandler
{
    public Canvas canvas;
    public string location;
    public GameObject spawn;
    private bool loaded = false;

    private GameObject[] items;

    private float y = 1.25f;

    public void OnInputDown(InputEventData e)
    {
        Text text = canvas.GetComponent<Text>();
        text.text = Application.dataPath;





          if (loaded)
        {
            foreach(GameObject o in items)
            {
                Destroy(o);
            }
            loaded = false;
            y = 1.25f;
            return;
        }
        string[] files = Directory.GetFiles(location);
        items = new GameObject[files.Length];
        int count = 0;
        foreach(string s in files)
        {
            if (Path.GetExtension(s) == ".obj")
            {
                GameObject o = Instantiate(spawn);
                o.transform.parent = gameObject.transform;
                o.transform.localPosition = new Vector3(0, y, 0);
                y += 1.25f;
                o.GetComponent<LoadModel>().file =s;
                items[count++] = o;
            }
        }
        loaded = true;
    }

    public void OnInputUp(InputEventData e)
    {

    }
}*/
