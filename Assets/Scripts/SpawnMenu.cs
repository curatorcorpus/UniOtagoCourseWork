using UnityEngine;
using UnityEngine.UI;

using HoloToolkit.Unity.InputModule;
using HoloToolkit.Unity.SpatialMapping;

using System;
using System.IO;
using System.Threading;

#if ENABLE_WINMD_SUPPORT
using Windows;
using Windows.Storage;
using Windows.Storage.Streams;
using Windows.Storage.Pickers;
using UnityEngine.WSA;
#endif

public class SpawnMenu : MonoBehaviour, IInputHandler
{
    public Canvas canvas;

    private bool newModel = false;
    private string filePath = "";

    private GameObject loadedObject;
    private Text text;

#if ENABLE_WINMD_SUPPORT
    private FileOpenPicker openPicker;
#endif

    private void Start()
    {
        text = canvas.GetComponent<Text>();
    }

    private void Update()
    {
        if (newModel)
        {
            loadedObject = OBJLoader.LoadOBJFile(filePath);
            loadedObject.AddComponent<MeshCollider>();
            loadedObject.AddComponent<TapToPlace>();
            loadedObject.GetComponent<TapToPlace>().IsBeingPlaced = true;
            text.text = "spawned " + filePath;

            newModel = false;
        }
    }

    public void OnInputDown(InputEventData e)
    {
#if ENABLE_WINMD_SUPPORT
            UnityEngine.WSA.Application.InvokeOnUIThread(OpenFileAsync, false);
#else
        text.text = "NON-UWP Device Not Implemented!";
#endif
        //text.text = UnityEngine.Application.dataPath;
    }

    public void OnInputUp(InputEventData e)
    {
    }

#if ENABLE_WINMD_SUPPORT
    public async void OpenFileAsync()
    {
         // UnityEngine.Debug.LogFormat( "OpenFileAsync() on Thread: {0}", Thread.CurrentThread.ManagedThreadId );

         openPicker = new FileOpenPicker();
 
        //openPicker.ViewMode = PickerViewMode.Thumbnail;
        //openPicker.SuggestedStartLocation = PickerLocationId.Objects3D;
        openPicker.FileTypeFilter.Add(".obj");
        //openPicker.FileTypeFilter.Add("*");
     
        StorageFile file = await openPicker.PickSingleFileAsync();
        UnityEngine.WSA.Application.InvokeOnAppThread( new AppCallbackItem( () => 
        {
            if(file != null)
                filePath = file.Path;
            else 
                text.text = "No file Picked";
        } ), true );
        newModel = true;
    }
#endif
}