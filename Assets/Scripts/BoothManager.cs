using UnityEngine;
using UnityEngine.UI;
using System;
using System.IO;
using System.Collections;

public class BoothManager : MonoBehaviour
{
    public event Action OnPhotosFinished;
    [Tooltip("Activates the photo timer, if it is disabled no photos will be taken")]
    public bool boothMode = false;
    //[Tooltip("Set this if the scene is used on its own otherwise it will need the BoothSceneManager to be activated")]
    //public bool singleMode = false;
    [HideInInspector]
    public bool boothActive = false;
    public bool printPhotos = false;
    public string screenshotsPath;
    [Tooltip("Filename of the template used for the stitched photos and print.")]
    public string template = "PhotoBoothPrint.png";
    private string stitchedPhoto;

    public Text countdownText;
    [SerializeField]
    private Image fadeImage;
    private Flash flash;
    Camera screenshotCamera;
    private int photoNum = 4;

    public int pictureWidth = 925;
    public int pictureHeight = 1275;
    private int sideOffset = 50;
    private int topOffset = 50;
    private int middleOffset = 50;

    public float initialPhotoDelay = 60.0f;
    public float photoDelay = 15.0f;
    private float timeLeft;

    private bool photoSheduled = false;
    private int photosTaken = 0;

    private Texture2D stitchedTexture, photoTexture;
    private RenderTexture preview;
    //private RenderTexture photo;

    private int[] stitchPositions;
    //private Material photoMat;
    //private Material previewMat;
    private GameObject previewQuad, photoQuad;
    private Vector3[] previewPositions;
    
    // Use this for initialization
    void Start()
    {

        flash = gameObject.GetComponent<Flash>();                                           //the script to handle the flash for each photo

        screenshotCamera = gameObject.GetComponent<Camera>();                               //the camera that takes the photo
        screenshotCamera.aspect = (float)pictureWidth / (float)pictureHeight;               //adjust the aspect ratio of the camera according to the resulting picture

        preview = new RenderTexture(pictureWidth, pictureHeight, 24);                       //the rendertexture to show the live preview

        screenshotCamera.targetTexture = preview;   

        previewPositions = new Vector3[photoNum];                                           //the positions of the live preview for each of the photos
        
        screenshotsPath = Application.dataPath.Replace("Assets","") + "BoothPhotos/";       //path to the folder, where the resulting photos will be stored

        createInitialTextures();

        setStitchPositions();
        setPreviewPositions();

        setPreviewQuads();
        timeLeft = initialPhotoDelay;

        if(boothMode)
            startPhotos();

    }

    //calculate the positions of each photo taken on the resulting, stitched image
    void setStitchPositions()
    {
        stitchPositions = new int[photoNum * 2];

        stitchPositions[0] = sideOffset;
        stitchPositions[1] = stitchedTexture.height - topOffset - pictureHeight;
        stitchPositions[2] = sideOffset + pictureWidth + middleOffset;
        stitchPositions[3] = stitchedTexture.height - topOffset - pictureHeight;
        stitchPositions[4] = sideOffset;
        stitchPositions[5] = stitchedTexture.height - topOffset - 2 * pictureHeight - middleOffset;
        stitchPositions[6] = sideOffset + pictureWidth + middleOffset;
        stitchPositions[7] = stitchedTexture.height - topOffset - 2 * pictureHeight - middleOffset;
    }

    void setPreviewPositions()
    {
        previewPositions[0] = new Vector3(-0.243f, 0.27f, -0.001f);
        previewPositions[1] = new Vector3(0.243f, 0.27f, -0.001f);
        previewPositions[2] = new Vector3(-0.243f, -0.17f, -0.001f);
        previewPositions[3] = new Vector3(0.243f, -0.17f, -0.001f);
    }

    void setPreviewQuads()
    {
        previewQuad = GameObject.Find("PreviewQuad");
        previewQuad.GetComponent<MeshRenderer>().material.shader = Shader.Find("Unlit/Texture");
        previewQuad.GetComponent<MeshRenderer>().material.mainTexture = preview;

        photoQuad = GameObject.Find("PhotoQuad");
        photoQuad.GetComponent<MeshRenderer>().material.shader = Shader.Find("Unlit/Texture");
        photoQuad.GetComponent<MeshRenderer>().material.mainTexture = stitchedTexture;

        previewQuad.transform.localPosition = previewPositions[photosTaken];
    }

    public static Texture2D LoadPNG(string filePath)
    {
        Texture2D tex = null;
        byte[] fileData;

        if (File.Exists(filePath))
        {
            fileData = File.ReadAllBytes(filePath);
            tex = new Texture2D(2, 2, TextureFormat.RGB24, false);
            tex.LoadImage(fileData);                                    //..this will auto-resize the texture dimensions.
        }
        return tex;
    }

    void createInitialTextures()
    {
        stitchedTexture = new Texture2D(1, 1, TextureFormat.RGB24, false);
        stitchedTexture = LoadPNG(Application.dataPath + "/Textures/" + template);

        photoTexture = new Texture2D(pictureWidth, pictureHeight, TextureFormat.RGB24, false);
    }

    void LateUpdate()
    {
        if (boothActive)                                                        //if booth mode is enabled and we are not finished with taking photos
        {
            if (!countdownText.enabled)                                         //enable the text showing the countdown for the next photo if disabled
                countdownText.enabled = true;
            timeLeft -= Time.deltaTime;                                         //calculating the time left till the next photo is taken
            countdownText.text = Mathf.Ceil(timeLeft).ToString();               //round and convert to string to show it in the preview (not the cheapest solution)
            if (photoSheduled)                                                  //if a photo is triggered save the current rendertexture
            {
                takePhoto();                
                timeLeft = photoDelay;
                prepareNextPhoto();
            }
        }
        else
        {
            countdownText.enabled = false;                                      //if booth mode is not active or we are finished taking photos, don't show any countdown
        }

        if (KeyInputs.takeBoothPhoto)                                           
        {
            takePhoto();
            savePhoto();
            KeyInputs.takeBoothPhoto = false;
        }
    }

    public void startPhotos()
    {
        if (boothMode)
        {
           photoTimer();
           boothActive = true;
        }

    }

    void takePhoto()
    {
            //RenderTexture renderTexture = new RenderTexture(pictureWidth, pictureHeight, 24);
            //screenshotCamera.targetTexture = renderTexture;
            
            //screenshotCamera.Render();
            RenderTexture.active = preview;
            photoTexture.ReadPixels(new Rect(0, 0, pictureWidth, pictureHeight), 0, 0);

            //screenshotCamera.targetTexture = preview;
            RenderTexture.active = null; // JC: added to avoid errors
            //Destroy(renderTexture);         
    }

    void prepareNextPhoto()
    {
            stitchPhoto();
            photosTaken++;
            photoSheduled = false;

            if (photosTaken == photoNum)
            {
                CancelInvoke("shedulePhoto");
                saveStitchedPhoto();
                screenshotCamera.targetTexture = null;
                previewQuad.SetActive(false);
                boothActive = false;
            if (OnPhotosFinished != null)
                OnPhotosFinished();
            if (printPhotos)
                Invoke("printPhoto", 2.0f);
            }
            else
            {
                previewQuad.transform.localPosition = previewPositions[photosTaken];
            }
    }

    void stitchPhoto()
    {
        Color[] pixels = photoTexture.GetPixels(0,0,pictureWidth, pictureHeight);
        stitchedTexture.SetPixels(stitchPositions[2* photosTaken], stitchPositions[2 * photosTaken + 1], pictureWidth, pictureHeight, pixels);
        stitchedTexture.Apply();
    }

    void savePhoto()
    {
        byte[] bytes = photoTexture.EncodeToPNG();
        string filename = System.DateTime.Now.ToString("yyyyMMdd_hhmmss") + "_MREP_BoothPhoto.png";
        System.IO.File.WriteAllBytes(filename, bytes);
        Debug.Log("Saved BoothPhoto: " + filename);
    }

    void saveStitchedPhoto()
    {
        byte[] bytes = stitchedTexture.EncodeToPNG();
        stitchedPhoto = screenshotsPath + System.DateTime.Now.ToString("yyyyMMdd_hhmmss") + "_MREP_StitchedPhoto.png";
        System.IO.File.WriteAllBytes(stitchedPhoto, bytes);
    }

    void printPhoto()
    {
        string filename = stitchedPhoto.Replace("/",@"\");
        
        //string command = filename + " /print=\"Brother DCP-165C Printer\"";     //use this for IrfanView
        string command =  "/pt " + filename + " \"Canon SELPHY CP910\"";    //use this for mspaint

        if (File.Exists(filename))
        {
            System.Diagnostics.Process.Start("i_view32.exe", command);    //use this for IrfanView
            //System.Diagnostics.Process.Start("mspaint.exe", command);       //use this for mspaint
        }

    }

    void photoTimer()
    {
        InvokeRepeating("shedulePhoto", initialPhotoDelay, photoDelay);
        timeLeft = initialPhotoDelay;
        Debug.Log("Photo timer started! First photo in: " + initialPhotoDelay + " seconds");
    }

    void shedulePhoto()
    {
        flash.triggerFlash();
        photoSheduled = true;
    }

}