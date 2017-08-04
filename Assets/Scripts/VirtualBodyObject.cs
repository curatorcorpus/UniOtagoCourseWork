
using UnityEngine;
using System.IO;
using System.Collections;
using System.Collections.Generic;

public class VirtualBodyObject : VoxelObject {
    [Tooltip("Enables the replay of the recording.")]
    public bool enableRecording;
    [Tooltip("Determine whether the recording should loop or played one time")]
    public bool loop;
    [Tooltip("Toggle play and pause of the recording")]
    public bool play;
    [Tooltip("The number of seconds, before the replay starts")]
    public float startDelay;
    [Tooltip("Drag and drop the file of the recording from Assets/Recordings")]
    public Object file;
    [Tooltip("Speed at which the recording is played")]
    public float frameRate = 30f;

    [HideInInspector]
    public List<Frame> frames;

    private  Frame frame;
    private float InvokeRate;
    private int currentFrame = 0;
    private bool running = false;
    private string filename;

    public bool Running{
        get { return running; }
        set { if (value && !running)
                InvokeRepeating("nextFrame", startDelay, InvokeRate);
              if(!value)
                CancelInvoke("nextFrame");
            running = value;
            }
    }
    public bool Enable
    {
        get { return enableRecording; }
        set
        {
            if(value)
            {
                Running = true;
            }                
            if(!value)
            {
                if(running && frames != null)
                {
                    resetPositions();
                    currentFrame = 0;
                }
                Running = false;
            }
            enableRecording = value;
        }
    }

    private VoxelRecording recorder;

    // Use this for initialization
    new void Start()
    {
        base.Start();
        recorder = GameObject.FindObjectOfType<VoxelRecording>();
        frames = new List<Frame>();
        if(file != null)
            readFile();
        InvokeRate = 1f / frameRate;
    }

    void OnValidate()
    {
        //Running = running;
        
        if (file != null)
        {
            if (filename != file.name)
            {
                readFile();
                currentFrame = 0;
            }
                
            if (!this.gameObject.activeSelf)
                this.gameObject.SetActive(true);
            //if (!enableRecording)
            //{
            //    //this.gameObject.SetActive(false);
            //    if (running && frames != null)
            //    {
            //        resetPositions();
            //        currentFrame = 0;
            //    }

            //    Running = false;
            //}

            Enable = enableRecording;
            if (loop)
            {
                Running = true;
            }                           
        }

        if (frameRate > 0f)
        {
            Running = false;
            InvokeRate = 1f / frameRate;
            Running = true;
        }
        else this.gameObject.SetActive(false);

    }

    public void readFile()
    {       

        if(recorder != null)
        {
            filename = file.name;
            recorder.LoadData(filename + ".binary", this);

            if(enableRecording && play)
            {
                updated = true;
                Running = true;
            }   
        }

    }

    public void resetPositions()
    {
        if (gameObject.activeSelf)
        {
            frame = frames[0];
            positions = frame.positions;
            mirroredPositions = frame.mirroredPositions;
            colors = frame.colors;
            //CancelInvoke("nextFrame");
            //updated = true;
        }

    }

    public void clearPositions()
    {
        if (gameObject.activeSelf)
        {
            //frame = frames[0];
            positions.Clear();
            mirroredPositions.Clear();
            colors.Clear();
            Running = false;
            //CancelInvoke("nextFrame");
            //updated = true;
        }
    }
    
    public void nextFrame()
    {
        if(frames != null)
        {
           if(enableRecording && play)
           {
                if (frames.Count > 0)
                {
                    if(frames.Count > 1)
                    {
                        //Debug.Log("neuer loop whoop whoop" + frames.Count);
                        frame = frames[currentFrame];
                        positions = frame.positions;
                        mirroredPositions = frame.mirroredPositions;
                        colors = frame.colors;
                        updated = true;

                        if(currentFrame == frames.Count - 1)
                        {
                            if(loop)
                            {
                                currentFrame = 0;
                            }                                
                            else{
                                Running = false;
                                gameObject.SetActive(false);
                            }                 
                         }
                         else
                            currentFrame++;
                    }
                    else
                    {
                        frame = frames[0];
                        positions = frame.positions;
                        mirroredPositions = frame.mirroredPositions;
                        colors = frame.colors;
                        //CancelInvoke("nextFrame");
                        updated = true;
                    }
                }
            }
        }       
    }
}
