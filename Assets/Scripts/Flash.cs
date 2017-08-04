using UnityEngine;
using UnityEngine.UI;
using System.Collections;

/// <summary>
///Handles the flash for the boothcamera.
///It uses the FadePanel to display a fading white image.
/// </summary>
public class Flash : MonoBehaviour {

    private Color32 flashColor;
    private float duration = 0.5f; 
    [SerializeField] private Image m_FadeImage;
    // Use this for initialization
    void Start () {

        flashColor = Color.white;
        m_FadeImage = GameObject.Find("FadePanel").GetComponent<Image>();
    }
	
    public void triggerFlash()
    {
        StartCoroutine(flash());
    }

    private IEnumerator flash()
    {
        Color32 oldcolor = m_FadeImage.color;
        m_FadeImage.color = flashColor;
        
        //yield return StartCoroutine(fade(oldcolor, flashColor));
        yield return StartCoroutine(fade(flashColor, oldcolor));
        //m_FadeImage.color = oldcolor;

    }
    private IEnumerator fade(Color32 startCol, Color32 endCol)
    {
        float timer = 0f;
        while (timer <= duration)
        {
            // Set the colour based on the normalised time.
            m_FadeImage.color = Color.Lerp(startCol, endCol, timer / duration);

            // Increment the timer by the time between frames and return next frame.
            timer += Time.deltaTime;
            yield return null;
        }
    }
}
