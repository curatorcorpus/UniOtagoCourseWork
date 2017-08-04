using UnityEngine;
using UnityEngine.SceneManagement;
using System.Collections;
using System.Collections.Generic;
using VRStandardAssets;
using VRStandardAssets.Utils;

public class BoothSceneManager : MonoBehaviour {

    private int nextSceneId;
    private int sceneType;                                              // type of the current scene. 1 is welcome, 2 is mainscene, 3 is thankyou
    private Scene nextScene;
    private MenuButton[] buttons;                                       // The button that has to be activated to switch to the next scene

    [SerializeField] private Object welcomeSceneAsset;
    [SerializeField] private Object thankYouSceneAsset;
    [SerializeField] private List<Object> mainSceneAssets;

    private string nextMainScene;
    //private Scene welcomeScene;
    //private Scene thankYouScene;
    //private List<Scene> mainScenes = new List<Scene>();

    private GameObject ovrCameraRig;
    private GameObject guiReticle;
    private BoothManager boothmanager;
    private VRCameraFade fader;
    private VREyeRaycaster raycaster;
    private KeyInputs keyinput;
    private VirtualBodyObject[] recordings;

    private AsyncOperation async;

    void OnEnable()
    {
        //setupScene = "Setup";
        //welcomeScene = "Welcome";

        //mainScenes = new List<string>();
        //mainScenes.Add("VoxelRendering");
        //mainScenes.Add("SceneArne");
        //mainScenes.Add("SceneGorilla13");

        //thankYouScene = "ThankYou";
        Debug.Log("datapath: " + Application.dataPath);

        nextSceneId = 0;
        sceneType = 0;
    }   
                                                   
    void Start () {

        //getScenes();
        ovrCameraRig = FindObjectOfType<OVRCameraRig>().gameObject;
        fader = FindObjectOfType<VRCameraFade>();
        guiReticle = GameObject.Find("GUIReticle");
        raycaster = FindObjectOfType<VREyeRaycaster>();

        buttons = GameObject.FindObjectsOfType<MenuButton>();
        for (int i = 0; i < buttons.Length; i++)
        {
            buttons[i].OnButtonSelected += switchScene;

        }
        DontDestroyOnLoad(this);
        DontDestroyOnLoad(ovrCameraRig);

    }

    private void OnLevelWasLoaded()
    {
        buttons = GameObject.FindObjectsOfType<MenuButton>();
        for(int i = 0; i < buttons.Length; i++)
        {
            buttons[i].OnButtonSelected += switchScene;
            
        }

    }

    void OnApplicationQuit()
    {
        if (keyinput != null)
        {
            keyinput.StopScreencapture();
        }
    }

    private void switchScene(MenuButton button)
    {
        switch (sceneType)
        {
            case 0:
                //fader.FadeOut(false);
                loadIntro();
                break;
            case 1:
                //fader.FadeOut(false);
                if(button.name=="YesSlider")
                    ActivateNextMainScene(true);
                if(button.name=="NoSlider")
                    ActivateNextMainScene(false);
                break;
            //case 2:
            //    ActivateOutro();
            //    break;
            //case 3:
            //    ActivateIntro();
            //    break;
        }
    }

    private void loadIntro()
    {
        fader.FadeIn(false);
        SceneManager.LoadScene(welcomeSceneAsset.name, LoadSceneMode.Single);
        nextMainScene = mainSceneAssets[nextSceneId].name;
        async = SceneManager.LoadSceneAsync(nextMainScene, LoadSceneMode.Additive);
        StartCoroutine(prepareMainScene());
        SceneManager.LoadSceneAsync(thankYouSceneAsset.name, LoadSceneMode.Additive);
        sceneType = 1;
        ovrCameraRig.transform.position = new Vector3(100.0f, 1.0f, 0f);
        nextScene = SceneManager.GetSceneByName(nextMainScene);
        guiReticle.SetActive(true);
        raycaster.enabled = true;
        fader.FadeOut(false);
        //Scene welcome = SceneManager.GetSceneByName(welcomeSceneAsset.name);
        if (nextSceneId < mainSceneAssets.Count-1)
            nextSceneId++;
        else nextSceneId = 0;

        //if (welcome.IsValid())
        //{
        //    ovrCameraRig.transform.position = new Vector3(100.0f, 1.0f, 0f);
        //}
    }

    private IEnumerator prepareMainScene()
    {
        while (!async.isDone)
        {
            yield return new WaitForSeconds(0.1f);
        }
        recordings = FindObjectsOfType<VirtualBodyObject>();

        for (int i = 0; i < recordings.Length; i++)
        {
            boothmanager = GameObject.FindObjectOfType<BoothManager>();
            boothmanager.boothMode = false;
            recordings[i].Enable = false;
            recordings[i].play = false;
        }
    }

    private void ActivateNextMainScene(bool screenCapture)
    {

        if (nextScene.IsValid())
        {
            
            SceneManager.SetActiveScene(nextScene);
            Debug.Log("Current Scene: " + nextScene.name);
            //recordings = FindObjectsOfType<VirtualBodyObject>();
            activateRecordings();
            ovrCameraRig.transform.position = new Vector3(0.0f, 1.0f,0f);
            keyinput = FindObjectOfType<KeyInputs>();
            keyinput.calibrateOculus();

            if (screenCapture)
            {
                keyinput.StartScreencapture();
            }

            guiReticle.SetActive(false);            
            raycaster.enabled = false;
            fader.FadeIn(false);
            
            boothmanager.OnPhotosFinished += finishLoop;
            boothmanager.boothMode = true;
            boothmanager.startPhotos();
            sceneType = 2;
            //SceneManager.UnloadScene(name);
        }
    }

    private void activateRecordings()
    {
        for(int i = 0; i < recordings.Length; i++)
        {
            recordings[i].Enable = true;
            recordings[i].play = true;
        }
    }

    private void finishLoop()
    {
        StartCoroutine(ActivateOutro());

    }

    private IEnumerator ActivateIntro()
    {
        yield return new WaitForSeconds(10);

        if (fader.IsFading)
            yield break;

        // Wait for the camera to fade out.
        yield return StartCoroutine(fader.BeginFadeOut(true));
        yield return new WaitForSeconds(10);
        loadIntro();
    }

    private IEnumerator ActivateOutro()
    {
        yield return new WaitForSeconds(2);
        if (fader.IsFading)
            yield break;

        // Wait for the camera to fade out.
        yield return StartCoroutine(fader.BeginFadeOut(true));
        showOutro();

    }

    private void showOutro()
    {
        keyinput.StopScreencapture();
        sceneType = 3;
        //fader.FadeIn(false);
        ovrCameraRig.transform.position = new Vector3(-100.0f, 1.0f, 0f);
        Scene thankYouScene = SceneManager.GetSceneByName(thankYouSceneAsset.name);
        SceneManager.SetActiveScene(thankYouScene);
        fader.FadeIn(false);
        StartCoroutine(ActivateIntro());
    }


}
