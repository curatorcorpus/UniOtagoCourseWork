using UnityEngine;

public class ModelFileFinder : MonoBehaviour
{

    protected string m_textPath;

    protected FileBrowser m_fileBrowser;

    [SerializeField]
    protected Texture2D m_directoryImage,
                        m_fileImage;

    private void StartUp()
    {
        //m_fileBrowser = new FileBrowser(new Rect(0, 0, 500, 500), "test", new FinishedCallback("Assets"));
    }

    protected void OnGUI()
    {
        if (m_fileBrowser != null)
        {
            m_fileBrowser.OnGUI();
        }
        else
        {
            OnGUIMain();
            Debug.Log("working");
        }
    }

    protected void OnGUIMain()
    {

        UnityEngine.GUILayout.BeginHorizontal();
        UnityEngine.GUILayout.Label("Text File", GUILayout.Width(100));
        UnityEngine.GUILayout.FlexibleSpace();
        UnityEngine.GUILayout.Label(m_textPath ?? "none selected");
        if (GUILayout.Button("...", GUILayout.ExpandWidth(false)))
        {
            m_fileBrowser = new FileBrowser(
                new Rect(100, 100, 600, 500),
                "Choose Text File",
                FileSelectedCallback
            );
            m_fileBrowser.SelectionPattern = "*.txt";
            m_fileBrowser.DirectoryImage = m_directoryImage;
            m_fileBrowser.FileImage = m_fileImage;
        }
        GUILayout.EndHorizontal();
    }

    protected void FileSelectedCallback(string path)
    {
        m_fileBrowser = null;
        m_textPath = path;
    }
}