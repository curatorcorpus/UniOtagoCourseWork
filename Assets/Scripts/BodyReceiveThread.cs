using UnityEngine;
using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;

namespace Assets.Scripts
{
    class BodyReceiveThread
    {
        UdpClient client;
        IPEndPoint localhost;
        //IPEndPoint remote;

        byte[] incomingMessage;
        int jointCount;
       
        int newest = 0;
        int oldest = 0;

        private MREPManager manager;
        private Vector3 voxelspaceOrigin;
        private System.Threading.Thread m_Thread = null;
        public bool isRunning = false;
        private bool m_newFrame = false;
        private object m_Handle = new object();
        private object frameHandle = new object();
        BodyKinectFrame[] bufferedFrames = new BodyKinectFrame[2];
        private BodyKinectFrame m_latestBodyFrame = new BodyKinectFrame();

        public class BodyKinectFrame
        {    
            public ulong timestamp = 0;
            public List<int> types = new List<int>();
            public List<Vector3> positions = new List<Vector3>();
            public List<Vector3> mirroredPositions = new List<Vector3>();
            public List<Quaternion> orientations = new List<Quaternion>();
            public bool isComplete = false;
        }


        public BodyKinectFrame latestBodyFrame
        {
            get
            {
                BodyKinectFrame tmp;
                lock (frameHandle)
                {
                    tmp = m_latestBodyFrame;
                }
                return tmp;
            }
            set
            {
                lock (frameHandle)
                {
                    m_latestBodyFrame = value;
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

        public void Start()
        {
            manager = GameObject.Find("MREPManager").GetComponent<MREPManager>();
            voxelspaceOrigin = manager.voxelspace.origin;

            for (int i = 0; i < bufferedFrames.Length; i++)
            {

                BodyKinectFrame frame = new BodyKinectFrame();
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
            int current = 255;

            int x, y, z;
            Vector3 position;

            incomingMessage = client.Receive(ref localhost);
            timestamp = BitConverter.ToUInt64(incomingMessage, 0);
            jointCount = BitConverter.ToInt32(incomingMessage, 8);

            for(int i = 0; i < bufferedFrames.Length; i++)
            {
                if(bufferedFrames[i].timestamp == timestamp)
                {
                    current = i;
                    found = true;
                    break;
                }
            }


            if (found == false && timestamp > bufferedFrames[newest].timestamp)
            {
                BodyKinectFrame frame = new BodyKinectFrame();
                frame.timestamp = timestamp;

                current = oldest;
                bufferedFrames[current] = frame;

            } else
            {
                if (timestamp < bufferedFrames[newest].timestamp)
                {
                    Debug.Log("Body Order is off.");
                    if (bufferedFrames[newest].timestamp - timestamp > 1000000000000)
                        Debug.Log("Body Timestamp overflow!");
                }
            }

            for (int i = 12; i < incomingMessage.Length; i += 23)
            {
                bufferedFrames[current].types.Add(incomingMessage[i]);

                x = BitConverter.ToInt16(incomingMessage, i + 1);
                y = BitConverter.ToInt16(incomingMessage, i + 3);
                z = BitConverter.ToInt16(incomingMessage, i + 5);
                position = new Vector3(x * manager.voxelSize, y * manager.voxelSize, z * manager.voxelSize) + voxelspaceOrigin; //translate from voxelspace coordinates to world coordinates
                bufferedFrames[current].positions.Add(position);
                //bufferedFrames[current].mirroredPositions.Add(Vector3.Scale(bufferedFrames[current].positions[bufferedFrames[current].positions.Count-1], Vector3.back));

                bufferedFrames[current].orientations.Add(new Quaternion(BitConverter.ToSingle(incomingMessage, i + 7), 
                                                                        BitConverter.ToSingle(incomingMessage, i + 11), 
                                                                        BitConverter.ToSingle(incomingMessage, i + 15), 
                                                                        BitConverter.ToSingle(incomingMessage, i + 19)
                                                                        ));
            }

            if(bufferedFrames[current].types.Count == jointCount)
            {
                newest = current;
                oldest = 1 - current;
                latestBodyFrame = bufferedFrames[current];
                newFrame = true;

                bufferedFrames[oldest].types.Clear();
                bufferedFrames[oldest].positions.Clear();
                bufferedFrames[oldest].mirroredPositions.Clear();
                bufferedFrames[oldest].orientations.Clear();
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
            client = new UdpClient(5557);
            localhost = new IPEndPoint(IPAddress.Parse("127.0.0.1"), 5557);
            //remote = new IPEndPoint(IPAddress.Parse("192.168.0.149"), 5557);
            client.Client.ReceiveTimeout = 500;
        }
    }
}
