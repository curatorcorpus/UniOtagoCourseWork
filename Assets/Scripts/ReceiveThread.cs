using UnityEngine;
using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;

namespace Assets.Scripts
{
    class ReceiveThread
    {
        UdpClient client;
        IPEndPoint localhost;

        byte[] incomingMessage = new byte[6000];
        int voxelCount;
        int voxelSize = 10;
       
        int oldest = 0;

        private int port;

        private MREPManager manager;
        private Vector3 voxelspaceOrigin;
        private System.Threading.Thread m_Thread = null;
        public bool isRunning = false;
        private bool m_newFrame = false;
        private object m_Handle = new object();
        private object frameHandle = new object();
        KinectFrame[] bufferedFrames = new KinectFrame[2];
        private KinectFrame m_latestFrame = new KinectFrame();

        public class KinectFrame
        {    
            public ulong timestamp = 0;
            public int voxelCount = 0;
            public List<Vector3> backgroundVoxelPositions = new List<Vector3>(100000);
            public List<Color32> backgroundVoxelColors = new List<Color32>(100000);
            public List<Vector3> mirroredBackgroundPositions = new List<Vector3>(100000);
            public List<Vector3> bodyVoxelPositions = new List<Vector3>(100000);
            public List<Color32> bodyVoxelColors = new List<Color32>(100000);
            public List<Vector3> mirroredBodyPositions = new List<Vector3>(100000);
        }

        public KinectFrame latestFrame
        {
            get
            {
                KinectFrame tmp;
                lock (frameHandle)
                {
                    tmp = m_latestFrame;
                }
                return tmp;
            }
            set
            {
                lock (frameHandle)
                {
                    m_latestFrame = value;
                }
            }
        }


        public bool newFrame
        {
            get
            {
                bool tmp;
                lock (m_Handle)
                {
                    tmp = m_newFrame;
                }
                return tmp;
            }
            set
            {
                lock (m_Handle)
                {
                    m_newFrame = value;
                }
            }
        }

        public ReceiveThread(int port)
        {
            this.port = port;
        }

        public void Start()
        {
            manager = GameObject.Find("MREPManager").GetComponent<MREPManager>();
            voxelspaceOrigin = manager.voxelspace.origin;
            for (int i = 0; i < bufferedFrames.Length; i++)
            {

                KinectFrame frame = new KinectFrame();
                bufferedFrames[i] = frame;
                
            }
            initSocket();
            m_Thread = new System.Threading.Thread(Run);
            m_Thread.Start();
        }

        public void Abort()
        {
            client.Close();
            client = null;
            m_Thread.Abort();
        }

        protected void ThreadFunction() {

            ulong timestamp = 0;
            bool found = false;
            int current = 0;

            int x, y, z;
            Vector3 position, mirroredPosition;
            Color32 color;
            byte id;

            incomingMessage = client.Receive(ref localhost);

            if(newFrame == false)
            {
                timestamp = BitConverter.ToUInt64(incomingMessage, 0);
                voxelCount = BitConverter.ToInt32(incomingMessage, 12);

                for (int i = 0; i < bufferedFrames.Length; i++)
                {
                    if (bufferedFrames[i].timestamp == timestamp)
                    {
                        current = i;
                        found = true;
                        break;
                    }
                }

                if (found == false && (timestamp != bufferedFrames[current].timestamp))
                {
                    KinectFrame frame = new KinectFrame();
                    frame.timestamp = timestamp;

                    current = oldest;
                    oldest = 1 - oldest;
                    bufferedFrames[current] = frame;
                }

                for (int i = 16; i < incomingMessage.Length; i += voxelSize)
                {

                    x = BitConverter.ToInt16(incomingMessage, i);
                    y = BitConverter.ToInt16(incomingMessage, i + 2);
                    z = BitConverter.ToInt16(incomingMessage, i + 4);

                    position = new Vector3(x * manager.voxelSize, y * manager.voxelSize, z * manager.voxelSize) + voxelspaceOrigin; //translate from voxelspace coordinates to world coordinates
                    mirroredPosition = new Vector3(position.x, position.y, -position.z);
                    color = new Color32(incomingMessage[i + 6], incomingMessage[i + 7], incomingMessage[i + 8], 1);
                    id = incomingMessage[i + 9];

                    if (id >= 1 && id <= 6)  // voxel belongs ot a body
                    {
                        bufferedFrames[current].bodyVoxelPositions.Add(position);
                        bufferedFrames[current].bodyVoxelColors.Add(color);
                        bufferedFrames[current].mirroredBodyPositions.Add(mirroredPosition);
                    }
                    else    // voxel belongs to the background
                    {
                        bufferedFrames[current].backgroundVoxelPositions.Add(position);
                        bufferedFrames[current].backgroundVoxelColors.Add(color);
                        bufferedFrames[current].mirroredBackgroundPositions.Add(mirroredPosition);
                    }

                    bufferedFrames[current].voxelCount++;
                }

                if (bufferedFrames[current].voxelCount == voxelCount && newFrame == false)
                {
                    bufferedFrames[oldest].bodyVoxelPositions.Clear();
                    bufferedFrames[oldest].bodyVoxelColors.Clear();
                    bufferedFrames[oldest].mirroredBodyPositions.Clear();

                    bufferedFrames[oldest].backgroundVoxelPositions.Clear();
                    bufferedFrames[oldest].backgroundVoxelColors.Clear();
                    bufferedFrames[oldest].mirroredBackgroundPositions.Clear();

                    latestFrame = bufferedFrames[current];
                    
                    newFrame = true;
                }
            }
           
        }

        public bool Update()
        {
            if (newFrame)
            {
                return true;
            }
            return false;
        }

        private void Run()
        {
            while (true)
            {
                ThreadFunction();
            }
        }

        void initSocket()
        {
            // set up the networking
            client = new UdpClient(port);
            client.Client.ReceiveTimeout = 500;
        }
    }
}
