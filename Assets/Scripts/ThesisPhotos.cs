using UnityEngine;
using System.Collections;

public class ThesisPhotos : MonoBehaviour {

    Camera thesisCamera;
    private Texture2D photoTexture;
    public int pictureWidth = 1920;
    public int pictureHeight = 1080;


    // Use this for initialization
    void Start () {
        thesisCamera = gameObject.GetComponent<Camera>();
        thesisCamera.aspect = (float)pictureWidth / (float)pictureHeight;

        photoTexture = new Texture2D(pictureWidth, pictureHeight, TextureFormat.RGB24, false);
    }
	
    void LateUpdate()
    {
        if (KeyInputs.takeThesisPhoto)
        {
            takePhoto();
            savePhoto();
            KeyInputs.takeThesisPhoto = false;
        }
    }

    void takePhoto()
    {
        RenderTexture renderTexture = new RenderTexture(pictureWidth, pictureHeight, 24);
        thesisCamera.targetTexture = renderTexture;

        thesisCamera.Render();
        RenderTexture.active = renderTexture;
        photoTexture.ReadPixels(new Rect(0, 0, pictureWidth, pictureHeight), 0, 0);

        thesisCamera.targetTexture = null;
        RenderTexture.active = null; // JC: added to avoid errors
        Destroy(renderTexture);
    }

    void savePhoto()
    {
        byte[] bytes = photoTexture.EncodeToPNG();
        string filename = System.DateTime.Now.ToString("yyyyMMdd_hhmmss") + "_MREP_ThesisPhoto.png";
        System.IO.File.WriteAllBytes(filename, bytes);
        Debug.Log("Saved ThesisPhoto: " + filename);
    }
}
